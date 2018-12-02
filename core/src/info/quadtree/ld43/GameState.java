package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class GameState {
    public WorldMap worldMap;

    public Creature pc;

    public ArrayList<Creature> creatures = new ArrayList<>();
    public ArrayList<Item> items = new ArrayList<>();

    public int tick;

    public Spell selectedSpell = null;

    public void init(){
        this.worldMap = new WorldMap();

        pc = new Creature();
        pc.graphicName = "pc1";

        if (Util.randInt(3) == 0) pc.statMagic = Util.randInt(40);

        pc.statEndurance = 10;
        pc.statSpeed = 10;
        pc.statPower = 10;

        while(true){
            switch(Util.randInt(3)){
                case 0: pc.statPower += Util.randInt(6); break;
                case 1: pc.statSpeed += Util.randInt(6); break;
                case 2: pc.statEndurance += Util.randInt(6); break;
            }

            if (pc.statPower + pc.statSpeed + pc.statEndurance + pc.statMagic >= 80) break;
        }

        pc.pos = worldMap.startSpot;
        pc.name = "The Sacrifice";

        pc.init();
        creatures.add(pc);

        Items.createItemAt(Items.createSword(), pc.pos.add(TilePos.create(1,1)));
        Items.createItemAt(Items.createPlateMail(), pc.pos.add(TilePos.create(2,1)));
        Items.createItemAt(Items.createBow(), pc.pos.add(TilePos.create(1,0)));
        Items.createItemAt(Items.createSpellBook(Spell.Fireball), pc.pos.add(TilePos.create(1,-1)));

        // test monsters
        for (int i=0;i<10;++i){
            Monsters.spawnSlimeAt(worldMap.getOpenSpace());
        }
    }

    public void render(){
        worldMap.render();

        Util.indexIterate(items, Item::render);
        Util.indexIterate(creatures, Creature::render);
    }

    Texture minimapTexture = null;

    public void renderMinimap(){
        Pixmap minimap = new Pixmap(WorldMap.WORLD_WIDTH, WorldMap.WORLD_HEIGHT, Pixmap.Format.RGBA8888);
        for (int x=0;x<WorldMap.WORLD_WIDTH;++x){
            for (int y=0;y<WorldMap.WORLD_HEIGHT;++y){
                if (LD43.s.gameState.worldMap.tileSeen[x][y]){
                    if (LD43.s.gameState.worldMap.isPassable(TilePos.create(x,y))){
                        minimap.drawPixel(x,WorldMap.WORLD_HEIGHT - y, Color.rgba8888(Color.WHITE));
                    } else {
                        minimap.drawPixel(x,WorldMap.WORLD_HEIGHT - y, Color.rgba8888(Color.BLACK));
                    }
                }
            }
        }

        for (Creature c : creatures){
            if (c == pc){
                minimap.drawPixel(c.pos.x,WorldMap.WORLD_HEIGHT - c.pos.y, Color.rgba8888(Color.SKY));
                minimap.drawPixel(c.pos.x-1,WorldMap.WORLD_HEIGHT - c.pos.y, Color.rgba8888(Color.SKY));
                minimap.drawPixel(c.pos.x+1,WorldMap.WORLD_HEIGHT - c.pos.y, Color.rgba8888(Color.SKY));
                minimap.drawPixel(c.pos.x,WorldMap.WORLD_HEIGHT - (c.pos.y-1), Color.rgba8888(Color.SKY));
                minimap.drawPixel(c.pos.x,WorldMap.WORLD_HEIGHT - (c.pos.y+1), Color.rgba8888(Color.SKY));
            } else {
                if (worldMap.canSee(pc.pos, c.pos, 0)){
                    minimap.drawPixel(c.pos.x,WorldMap.WORLD_HEIGHT - c.pos.y, Color.rgba8888(Color.RED));
                }
            }
        }

        if (minimapTexture != null) minimapTexture.dispose();
        Texture tx = new Texture(minimap);
        minimap.dispose();

        LD43.s.batch.draw(tx, Gdx.graphics.getWidth() - 380, 20, WorldMap.WORLD_WIDTH * 2, WorldMap.WORLD_HEIGHT * 2);
    }

    public void tick(){
        Util.indexIterate(creatures, Creature::tick);
        tick++;
    }

    public void tickActions(){
        Util.indexIterate(creatures, Creature::tickActions);
    }

    public void addCombatLogMessage(TilePos loc, String message){
        // todo: Actual combat log
        System.err.println(loc + " " + message);
    }
}

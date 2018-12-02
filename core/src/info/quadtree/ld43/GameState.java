package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameState {
    public WorldMap worldMap;

    public Creature pc;

    public ArrayList<Creature> creatures = new ArrayList<>();
    public ArrayList<Item> items = new ArrayList<>();

    public int tick;

    public Spell selectedSpell = null;

    public List<String> combatLogMessages = new ArrayList<>();

    enum StatMessage {
        Power("Power increases the effect of all your attacks and spells."),
        Speed("Speed increases the speed of all your actions."),
        Endurance("Endurance gives you more health and health regeneration, and helps you resist toxic foods."),
        Magic("Magic increases your spell points and spell point regeneration. Find spellbooks and click cast, then click on a target.");

        String message;

        StatMessage(String message) {
            this.message = message;
        }
    }

    class StatOrder implements Comparable<StatOrder> {
        public StatOrder(StatMessage message, int amount) {
            this.message = message;
            this.amount = amount;
        }

        @Override
        public int compareTo(StatOrder o) {
            return o.amount - amount;
        }

        StatMessage message;
        int amount;
    }

    public void init(){
        this.worldMap = new WorldMap();

        pc = new Creature();
        pc.graphicName = "hero";

        if (Util.randInt(3) == 0) pc.statMagic = Util.randInt(40);

        pc.statEndurance = 10;
        pc.statSpeed = 10;
        pc.statPower = 10;

        while(true){
            switch(Util.randInt(3)){
                case 0: pc.statPower += Util.randInt(10); break;
                case 1: pc.statSpeed += Util.randInt(10); break;
                case 2: pc.statEndurance += Util.randInt(10); break;
            }

            if (pc.statPower + pc.statSpeed + pc.statEndurance + pc.statMagic >= 80) break;
        }

        pc.pos = worldMap.startSpot;
        pc.name = "The Sacrifice";

        pc.init();
        creatures.add(pc);

        StatOrder[] ord = new StatOrder[]{
                new StatOrder(StatMessage.Power, pc.statPower),
                new StatOrder(StatMessage.Speed, pc.statSpeed),
                new StatOrder(StatMessage.Endurance, pc.statEndurance),
                new StatOrder(StatMessage.Magic, pc.statMagic),
        };

        String[] messages = {
            "Your top stat is ",
            "Your second to top stat is ",
            "Your third to top stat is ",
            "Your lowest stat is "
        };

        Arrays.sort(ord);

        for (int i=0;i<4;++i){
            if (ord[i].amount > 0) addCombatLogMessage(pc.pos, messages[i] + ord[i].message.toString() + ". " + ord[i].message.message);
        }

        addCombatLogMessage(pc.pos, "If you would like to roll again for stats, Press Ctrl+R or click Restart Game.");

        /*Items.createItemAt(Items.createSword(), pc.pos.add(TilePos.create(1,1)));
        Items.createItemAt(Items.createPlateMail(), pc.pos.add(TilePos.create(2,1)));
        Items.createItemAt(Items.createBow(), pc.pos.add(TilePos.create(1,0)));
        Items.createItemAt(Items.createSpellBook(Spell.Haste), pc.pos.add(TilePos.create(1,-1)));
        Items.createItemAt(Items.createPotion(Spell.Heal), pc.pos.add(TilePos.create(1,-2)));*/

        for (int i=0;i<30;++i){
            Monsters.spawnMonsertAt(worldMap.getOpenSpace());
        }

        for (int i=0;i<15;++i){
            Items.createItemAt(Items.randomItem(), worldMap.getOpenSpace());
        }

        Monsters.spawnEndBossAt(worldMap.shiftToClear(worldMap.endSpot));
        Monsters.spawnImpAt(worldMap.shiftToClear(worldMap.endSpot));
        Monsters.spawnImpAt(worldMap.shiftToClear(worldMap.endSpot));
        Monsters.spawnImpAt(worldMap.shiftToClear(worldMap.endSpot));
        Monsters.spawnImpAt(worldMap.shiftToClear(worldMap.endSpot));
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

        LD43.s.batch.draw(tx, Gdx.graphics.getWidth() - (LD43.INV_PANE_WIDTH + 200), 20, WorldMap.WORLD_WIDTH * 2, WorldMap.WORLD_HEIGHT * 2);
    }

    public void tick(){
        worldMap.losCache = null;

        Util.indexIterate(creatures, Creature::tick);
        tick++;

        if (creatures.size() < 31 && Util.randInt(300) == 0){
            Monsters.spawnMonsertAt(worldMap.getOpenSpace());
        }
    }

    public void tickActions(){
        Util.indexIterate(creatures, Creature::tickActions);
    }

    public void addCombatLogMessage(TilePos loc, String message){
        message = message.substring(0, 1).toUpperCase() + message.substring(1);

        System.err.println(loc + " " + message);

        if (worldMap.canSee(pc.pos, loc, 0)) combatLogMessages.add(message);

        if (combatLogMessages.size() > 1000){
            combatLogMessages = combatLogMessages.subList(950, combatLogMessages.size());
        }

        // todo: Consider re-adding this!
        //while (combatLogMessages.size() > 50) combatLogMessages.remove(0);
    }
}

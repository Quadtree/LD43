package info.quadtree.ld43;

import java.util.ArrayList;

public class GameState {
    public WorldMap worldMap;

    public Creature pc;

    public ArrayList<Creature> creatures = new ArrayList<>();
    public ArrayList<Item> items = new ArrayList<>();

    public int tick;

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

package info.quadtree.ld43;

import java.util.ArrayList;

public class GameState {
    public WorldMap worldMap;

    public Creature pc;

    public ArrayList<Creature> creatures = new ArrayList<>();

    public int tick;

    public GameState() {
        this.worldMap = new WorldMap();

        pc = new Creature();
        pc.graphicName = "pc1";

        if (Util.randInt(3) == 0) pc.statMagic = Util.randInt(40);

        while(true){
            switch(Util.randInt(3)){
                case 0: pc.statPower += Util.randInt(15); break;
                case 1: pc.statSpeed += Util.randInt(15); break;
                case 2: pc.statEndurance += Util.randInt(15); break;
            }

            if (pc.statPower + pc.statSpeed + pc.statEndurance + pc.statMagic >= 80) break;
        }

        pc.pos = TilePos.create(WorldMap.WORLD_WIDTH / 2, 1);

        creatures.add(pc);
    }

    public void render(){
        worldMap.render();

        Util.indexIterate(creatures, Creature::render);
    }
}

package info.quadtree.ld43;

import java.util.ArrayList;

public class GameState {
    public WorldMap worldMap;

    public Creature pc;

    public ArrayList<Creature> creatures = new ArrayList<>();

    public GameState() {
        this.worldMap = new WorldMap();

        pc = new Creature();
        pc.graphicName = "pc1";
        pc.pos = TilePos.create(WorldMap.WORLD_WIDTH / 2, 1);

        creatures.add(pc);
    }

    public void render(){
        worldMap.render();

        Util.indexIterate(creatures, Creature::render);
    }
}

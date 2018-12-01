package info.quadtree.ld43;

public class Monsters {
    public static void spawnSlimeAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 10;
        ret.statSpeed = 5;
        ret.statEndurance = 20;
        ret.graphicName = "enemy1";

        ret.pos = pos;
        LD43.s.gameState.creatures.add(ret);
    }
}

package info.quadtree.ld43;

public class Monsters {
    public static void spawnSlimeAt(TilePos pos){
        Creature ret = new Creature();
        ret.statPower = 10;
        ret.statSpeed = 5;
        ret.statEndurance = 10;
        ret.graphicName = "enemy1";
        ret.xp = 10;
        ret.name = "Slime";

        ret.corpseFood = 800;
        ret.corpseToxicity = 60;
        ret.corpseWeight = 20;

        ret.pos = pos;
        ret.init();
        LD43.s.gameState.creatures.add(ret);
    }
}

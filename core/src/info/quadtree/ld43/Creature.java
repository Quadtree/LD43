package info.quadtree.ld43;

public class Creature {
    public int statPower;
    public int statSpeed;
    public int statEndurance;
    public int statMagic;

    public int hp;
    public int sp;

    public int xp;
    public int level = 1;

    public float ticksTillNextAction = 0;

    public TilePos pos;

    public String graphicName;

    public void render(){
        LD43.s.cam.drawOnTile(graphicName, pos);
    }

    public void tick(){
        ticksTillNextAction -= 1;
    }

    public void move(int dx, int dy){
        TilePos np = new TilePos()
    }
}

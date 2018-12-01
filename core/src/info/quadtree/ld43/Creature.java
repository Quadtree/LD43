package info.quadtree.ld43;

import info.quadtree.ld43.action.BaseAction;

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

    public BaseAction currentAction;

    public void render(){
        LD43.s.cam.drawOnTile(graphicName, pos);
    }

    public void tick(){
        ticksTillNextAction -= 1;
    }

    public void tickActions(){
        if (currentAction != null) currentAction.tick();
    }

    public void move(int dx, int dy){
        TilePos np = pos.add(dx, dy);
        if (LD43.s.gameState.worldMap.isPassable(np)){
            pos = np;
            ticksTillNextAction = 10 * (1 + ((100 - statSpeed) / 100f));
        }
    }

    public boolean canAct(){
        return ticksTillNextAction <= 0;
    }
}

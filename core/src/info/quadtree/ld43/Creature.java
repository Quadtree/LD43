package info.quadtree.ld43;

import info.quadtree.ld43.action.BaseAction;
import info.quadtree.ld43.action.MoveAction;

import java.util.Optional;

public class Creature {
    public int statPower;
    public int statSpeed;
    public int statEndurance;
    public int statMagic;

    public int hp;
    public int sp;

    public int xp;
    public int level = 1;

    public int naturalArmor = 0;

    public String name = "???";

    public int getMaxDamageOnAttack(){
        return 16;
    }

    public int getArmor(){
        return naturalArmor;
    }

    public float ticksTillNextAction = 0;

    public TilePos pos;

    public String graphicName;

    public BaseAction currentAction;

    public void init(){
        hp = statEndurance;
        sp = statMagic;
    }

    public void render(){
        if (LD43.s.gameState.worldMap.canSee(LD43.s.gameState.pc.pos, pos, 0)) {
            LD43.s.cam.drawOnTile(graphicName, pos);
        }
    }

    public void tick(){
        ticksTillNextAction -= 1;
    }

    public void tickActions(){
        if (currentAction != null){
            if (!currentAction.tick()) currentAction = null;
        }

        if (!isPC()){
            if (LD43.s.gameState.worldMap.canSee(pos, LD43.s.gameState.pc.pos, 0)){
                currentAction = new MoveAction(this, LD43.s.gameState.pc.pos);
            }
        }
    }

    public boolean justMeleeAttackedDueToMove = false;

    public void move(int dx, int dy){
        if (!canAct()) return;

        justMeleeAttackedDueToMove = false;

        TilePos np = pos.add(dx, dy);
        if (LD43.s.gameState.worldMap.isPassable(np)){
            Optional<Creature> onTile = LD43.s.gameState.worldMap.getCreatureOnTile(np);
            if (!onTile.isPresent()){
                pos = np;
                ticksTillNextAction += 10 * getSpeedModifier();
            } else {
                justMeleeAttackedDueToMove = true;
                meleeAttack(onTile.get());
            }
        }
    }

    private float getSpeedModifier() {
        return 0.2f + ((100 - statSpeed) / 100f);
    }

    private float getPowerMultiplier(){
        return statPower / 100f;
    }

    public void meleeAttack(Creature trg){
        if (!canAct()) return;
        if (!hostileTowards(trg)) return;

        int attackRoll = statSpeed + Util.randInt(30) - 15;
        int defense = trg.statSpeed;

        if (attackRoll >= defense){
            int damage = Math.round(Util.randInt(getMaxDamageOnAttack()) * getPowerMultiplier()) - trg.getArmor();

            LD43.s.gameState.addCombatLogMessage(trg.pos, name + " hits " + trg.name + " for " + damage);

            if (trg.takeDamage(damage)){
                gainXP(trg.xp);
            }
        } else {
            LD43.s.gameState.addCombatLogMessage(trg.pos, name + " misses " + trg.name);
        }

        ticksTillNextAction += 20 * getSpeedModifier();
    }

    public boolean takeDamage(int amt){
        hp -= amt;

        if (hp <= 0){
            LD43.s.gameState.creatures.remove(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean canAct(){
        return ticksTillNextAction <= 0;
    }

    public boolean isPC(){
        return this == LD43.s.gameState.pc;
    }

    public boolean hostileTowards(Creature other){
        return this.isPC() != other.isPC();
    }

    void gainXP(int amt){
        this.xp += amt;
    }
}

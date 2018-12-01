package info.quadtree.ld43;

import com.badlogic.gdx.math.MathUtils;
import info.quadtree.ld43.action.BaseAction;
import info.quadtree.ld43.action.MoveAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    ArrayList<Item> inventory = new ArrayList<>();

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

    Map<Item.EquipSlot, Item> equippedItems = new HashMap<>();

    public void unequip(Item item){
        if (equippedItems.containsValue(item)){
            equippedItems.remove(item.slot);
        }
    }

    public void equip(Item item){
        if (item.slot != null) equippedItems.put(item.slot, item);
    }

    public void drop(Item item){
        if (equippedItems.containsValue(item)){
            equippedItems.remove(item.slot);
        }

        TilePos dropPos = pos;
        for (int i=0;i<1000;++i){
            final TilePos fDropPos = dropPos;
            if (LD43.s.gameState.items.stream().noneMatch(it -> it.onGroundLocation.equals(fDropPos))) break;
            TilePos newDropPos = dropPos.add(MathUtils.random(-1, 1), MathUtils.random(-1, 1));
            if (LD43.s.gameState.worldMap.isPassable(newDropPos)) dropPos = newDropPos;
        }

        item.onGroundLocation = dropPos;
        LD43.s.gameState.items.add(item);
        LD43.s.gameState.addCombatLogMessage(pos, name + " drops " + item.name);
    }

    public boolean isEquipped(Item item){
        return equippedItems.containsValue(item);
    }

    public void move(int dx, int dy){
        if (!canAct()) return;

        justMeleeAttackedDueToMove = false;

        TilePos np = pos.add(dx, dy);
        if (LD43.s.gameState.worldMap.isPassable(np)){
            if (!LD43.s.gameState.worldMap.isOpenable(np)){
                Optional<Creature> onTile = LD43.s.gameState.worldMap.getCreatureOnTile(np);
                if (!onTile.isPresent()){
                    pos = np;
                    ticksTillNextAction += 10 * getSpeedModifier();
                } else {
                    justMeleeAttackedDueToMove = true;
                    meleeAttack(onTile.get());
                }
            } else {
                justMeleeAttackedDueToMove = true;
                LD43.s.gameState.worldMap.setTile(np, WorldMap.TerrainType.OpenDoor, null);
                ticksTillNextAction += 10 * getSpeedModifier();
            }
        }
    }

    public void stand(){
        Optional<Item> toPickUp = LD43.s.gameState.items.stream().filter(it -> it.onGroundLocation.equals(pos)).findFirst();

        if (toPickUp.isPresent()){
            inventory.add(toPickUp.get());
            LD43.s.gameState.items.remove(toPickUp.get());
            LD43.s.gameState.addCombatLogMessage(pos, name + " picks up " + toPickUp.get().name);
            ticksTillNextAction += 5 * getSpeedModifier();
        }
    }

    public int getEffectiveSpeed(){
        return statSpeed;
    }

    private float getSpeedModifier() {
        return 1f / (getEffectiveSpeed() / 100f);
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

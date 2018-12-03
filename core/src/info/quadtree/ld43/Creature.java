package info.quadtree.ld43;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import info.quadtree.ld43.action.BaseAction;
import info.quadtree.ld43.action.EatAction;
import info.quadtree.ld43.action.MoveAction;
import info.quadtree.ld43.vfx.P2PVFX;
import info.quadtree.ld43.vfx.TileVisualEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Creature {
    public static final int XP_TO_LEVEL = 50;
    public int statPower;
    public int statSpeed;
    public int statEndurance;
    public int statMagic;

    public int hp;
    public int sp;

    public int xp;
    public int level = 1;

    public int naturalArmor = 0;

    public int corpseFood = 0;
    public int corpseToxicity = 0;
    public int corpseWeight = 0;

    final static int MAX_FOOD = 3000;
    final static int STARTING_FOOD = 2500;

    int food = STARTING_FOOD;

    boolean naturalRangedAttack = false;

    int healthRegenTime;
    int magicRegenTime;

    int sleepTime = 0;
    int invisibleTime = 0;
    int hasteTime = 0;
    int slowTime = 0;

    int oldEncumbrance = 0;
    int newEncumbrance = 0;

    boolean endBoss = false;

    public String name = "???";

    public int getMaxDamageOnAttack(){
        if (equippedItems.containsKey(Item.EquipSlot.Weapon)) return equippedItems.get(Item.EquipSlot.Weapon).attackDamage;

        return 16;
    }

    public int getArmor(){
        int ret = naturalArmor;

        for (Item itm : equippedItems.values()){
            ret += itm.armorMod;
        }

        return ret;
    }

    public float ticksTillNextAction = 0;

    public TilePos pos;

    public String graphicName;
    public Color tint = Color.WHITE;

    public BaseAction currentAction;

    boolean isImmuneToSleep = false;

    ArrayList<Item> inventory = new ArrayList<>();

    public void init(){
        hp = statEndurance;
        sp = statMagic;
    }

    public void render(){
        if (LD43.s.gameState.worldMap.canSee(LD43.s.gameState.pc.pos, pos, 0) && invisibleTime <= 0) {
            LD43.s.cam.drawOnTile(graphicName, pos, tint);

            if (slowTime > 0) LD43.s.cam.drawOnTile("slow", pos, tint);
            if (hasteTime > 0) LD43.s.cam.drawOnTile("haste", pos, tint);
            if (sleepTime > 0) LD43.s.cam.drawOnTile("sleep", pos, tint);
        }
    }

    public void tick(){
        if (sleepTime <= 0) ticksTillNextAction -= 1;

        newEncumbrance = inventory.stream().mapToInt(it -> it.weight).sum() / (getEffectiveEndurance() * 2);
        if (newEncumbrance != oldEncumbrance){
            oldEncumbrance = newEncumbrance;
            switch(newEncumbrance){
                case 0: LD43.s.gameState.addCombatLogMessage(pos, "Your movements are now unencumbered"); break;
                case 1: LD43.s.gameState.addCombatLogMessage(pos, "You are somewhat slowed by your load"); break;
                case 2: LD43.s.gameState.addCombatLogMessage(pos, "You are significantly slowed by your load"); break;
                case 3: LD43.s.gameState.addCombatLogMessage(pos, "You are carrying so much you can barely move"); break;
                case 4: LD43.s.gameState.addCombatLogMessage(pos, "You are carrying too much to move"); break;
            }
        }

        boolean wasInvisible = invisibleTime > 0;
        invisibleTime--;
        if (invisibleTime <= 0 && wasInvisible) LD43.s.gameState.addCombatLogMessage(pos, this.sname() + " appear" + this.gv("", "s"));

        boolean wasSleeping = sleepTime > 0;
        sleepTime--;
        if (sleepTime <= 0 && wasSleeping) LD43.s.gameState.addCombatLogMessage(pos, this.sname() + " wake"+this.gv("", "s")+" up");

        boolean wasHasted = hasteTime > 0;
        hasteTime--;
        if (hasteTime <= 0 && wasHasted) LD43.s.gameState.addCombatLogMessage(pos, this.sname() + " slow"+this.gv("", "s")+" down to normal speed");

        boolean wasSlowed = slowTime > 0;
        slowTime--;
        if (slowTime <= 0 && wasSlowed) LD43.s.gameState.addCombatLogMessage(pos, this.sname() + " speed"+this.gv("", "s")+" up to normal speed");

        if (ticksTillNextAction < 0) ticksTillNextAction = 0;

        if (isPC()){
            if (food <= -200){
                food += 200;
                takeDamage(1);
                LD43.s.gameState.addCombatLogMessage(pos, "You are starving!");
            }

            if (food > 0){
                healthRegenTime -= getEffectiveEndurance();
                magicRegenTime -= getEffectiveMagic();

                if (healthRegenTime <= 0){
                    healthRegenTime = 15000;
                    hp = Math.min(hp + 1, getEffectiveEndurance());
                }

                if (magicRegenTime <= 0){
                    magicRegenTime = 5000;
                    sp = Math.min(sp + 1, getEffectiveMagic());
                }
            }
        }
    }

    public void tickActions(){
        if (currentAction != null){
            boolean couldAct = canAct();

            if (couldAct) {
                if (!currentAction.tick()) currentAction = null;

                if (LD43.s.gameState.creatures.stream().anyMatch(it -> it.hostileTowards(this) && LD43.s.gameState.worldMap.canSee(pos, it.pos, 0))) {
                    currentAction = null;
                }
            }
        }

        if (!isPC() && LD43.s.gameState.pc.invisibleTime <= 0){
            if (LD43.s.gameState.worldMap.canSee(pos, LD43.s.gameState.pc.pos, 0)){
                currentAction = new MoveAction(this, LD43.s.gameState.pc.pos);

                if (endBoss && Util.randInt(100) == 0){
                    switch(Util.randInt(3)){
                        case 0: statPower += 10; LD43.s.gameState.addCombatLogMessage(pos, name + " looks stronger"); break;
                        case 1: statSpeed += 10; LD43.s.gameState.addCombatLogMessage(pos, name + " looks faster"); break;
                        case 2: statEndurance += 10; LD43.s.gameState.addCombatLogMessage(pos, name + " looks tougher"); break;
                    }
                }
            }
        }

        /*if (!isPC() && currentAction == null && getEffectiveSpeed() > 10 && Util.randInt(1000) == 0){
            TilePos trgDest = TilePos.create(-1,-1);

            while(!LD43.s.gameState.worldMap.canSee(pos, trgDest, 0)){
                trgDest = pos.add(MathUtils.random(-20, 20), MathUtils.random(-20, 20));
            }

            currentAction = new MoveAction(this, trgDest);
        }*/
    }

    public boolean justMeleeAttackedDueToMove = false;

    Map<Item.EquipSlot, Item> equippedItems = new HashMap<>();

    public void unequip(Item item){
        if (!canAct()) return;

        if (equippedItems.containsValue(item)){
            equippedItems.remove(item.slot);
            takeTime(5);
            LD43.s.gameState.addCombatLogMessage(pos, sname() + " unequip" + s() + " " + item.name);
        }
    }

    public void equip(Item item){
        if (!canAct()) return;

        if (item.slot != null){
            equippedItems.put(item.slot, item);
            takeTime(5);
            LD43.s.gameState.addCombatLogMessage(pos, sname() + " equip"+s()+" " + item.name);
        }
    }

    public void drop(Item item){
        if (!canAct()) return;

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
        inventory.remove(item);
        LD43.s.gameState.items.add(item);
        LD43.s.gameState.addCombatLogMessage(pos, sname() + " drop"+s()+" " + item.name);
        takeTime(5);
    }

    public boolean isEquipped(Item item){
        return equippedItems.containsValue(item);
    }

    public void move(int dx, int dy){
        if (!canAct()) return;
        if (newEncumbrance >= 4) return;

        justMeleeAttackedDueToMove = false;

        int moveTime = 10;
        switch(newEncumbrance){
            case 1: moveTime = 15; break;
            case 2: moveTime = 25; break;
            case 3: moveTime = 50; break;
        }

        TilePos np = pos.add(dx, dy);
        if (LD43.s.gameState.worldMap.isPassable(np)){
            if (!LD43.s.gameState.worldMap.isOpenable(np)){
                Optional<Creature> onTile = LD43.s.gameState.worldMap.getCreatureOnTile(np);
                if (!onTile.isPresent()){
                    pos = np;
                    takeTime(moveTime);
                    food -= 10;
                } else {
                    justMeleeAttackedDueToMove = true;
                    meleeAttack(onTile.get());
                }
            } else {
                justMeleeAttackedDueToMove = true;
                LD43.s.gameState.worldMap.setTile(np, WorldMap.TerrainType.OpenDoor, null);
                takeTime(moveTime);
                food -= 10;
                LD43.s.gameState.addCombatLogMessage(pos, "The door opens");
            }
        }
    }

    private void takeTime(int amt){
        float time = amt * getSpeedModifier();

        if (slowTime > 0) time *= 2;
        if (hasteTime > 0) time /= 2;

        ticksTillNextAction += time;
    }

    public void stand(){
        if (!canAct()) return;

        food -= 1;
        Optional<Item> toPickUp = LD43.s.gameState.items.stream().filter(it -> it.onGroundLocation.equals(pos)).findFirst();

        if (toPickUp.isPresent()){
            inventory.add(toPickUp.get());
            LD43.s.gameState.items.remove(toPickUp.get());
            LD43.s.gameState.addCombatLogMessage(pos, sname() + " pick"+s()+" up " + toPickUp.get().name);
        }

        takeTime(5);
    }

    public String sname(){
        if (isPC()){
            return "you";
        } else {
            return name;
        }
    }

    public String getPossessive(){
        return isPC() ? "your" : name + "'s";
    }

    public String getAreIs(){
        return isPC() ? "are" : "is";
    }

    public String gv(String ifPlayer, String ifNotPlayer){
        return isPC() ? ifPlayer : ifNotPlayer;
    }

    public int getEffectivePower(){
        int ret = statPower;

        for (Item itm : equippedItems.values()){
            ret += itm.powerMod;
        }

        return ret;
    }

    public int getEffectiveMagic(){
        int ret = statMagic;

        for (Item itm : equippedItems.values()){
            ret += itm.magicMod;
        }

        return ret;
    }

    public int getEffectiveEndurance(){
        int ret = statEndurance;

        for (Item itm : equippedItems.values()){
            ret += itm.enduranceMod;
        }

        return ret;
    }

    public int getEffectiveSpeed(){
        int ret = statSpeed;

        for (Item itm : equippedItems.values()){
            ret += itm.speedMod;
        }

        for (Item itm : equippedItems.values()){
            if (itm.speedSoftCap != null){
                int sc = itm.speedSoftCap;
                sc *= level;
                if (ret > sc){
                    ret -= (ret - sc) / 2;
                }
            }
        }

        return ret;
    }

    private float getSpeedModifier() {
        return 1f / (getEffectiveSpeed() / 100f);
    }

    public float getPowerMultiplier(){
        return getEffectivePower() / 100f;
    }

    public boolean hasRangedAttack(){
        if (naturalRangedAttack || (equippedItems.containsKey(Item.EquipSlot.Weapon) && equippedItems.get(Item.EquipSlot.Weapon).allowsRangedAttack)){
            return true;
        }
        return false;
    }

    public void meleeAttack(Creature trg){
        if (!canAct()) return;
        if (!hostileTowards(trg)) return;

        food -= 20;

        takeTime(20);

        if (hasRangedAttack()){
            LD43.s.activeVisualEffects.add(new P2PVFX(pos, trg.pos, "arrow", () -> finishAttack(trg)));
        } else {
            finishAttack(trg);
        }
    }

    private void finishAttack(Creature trg){
        int attackRoll = getEffectiveSpeed() + MathUtils.random(0, 35);
        int defense = trg.getEffectiveSpeed();

        if ((attackRoll >= defense || Util.randInt(8) == 0) && Util.randInt(8) != 0){
            int damage = Math.round((Util.randInt(getMaxDamageOnAttack() / 2) + getMaxDamageOnAttack() / 2f) * getPowerMultiplier()) - trg.getArmor();

            if (damage == 0 && Util.randInt(5) == 0) damage = 1;

            if (damage > 0){
                LD43.s.gameState.addCombatLogMessage(trg.pos, sname() + " hit"+s()+" " + trg.sname() + " for " + damage);

                if (trg.takeDamage(damage)){
                    gainXP(trg.xp);
                }
            } else {
                LD43.s.playSound("glance", 5);
                LD43.s.activeVisualEffects.add(new TileVisualEffect("", "shield", trg.pos));
                LD43.s.gameState.addCombatLogMessage(trg.pos, getPossessive() + " attack glances off of " + trg.sname());
            }
        } else {
            LD43.s.playSound("miss");
            LD43.s.activeVisualEffects.add(new TileVisualEffect("", "miss", trg.pos));
            LD43.s.gameState.addCombatLogMessage(trg.pos, sname() + " miss"+gv("", "es")+" " + trg.sname());
        }
    }

    public boolean takeDamage(int amt){
        hp -= amt;
        if (sleepTime > 0) sleepTime = 1;

        if (amt > 0){
            LD43.s.activeVisualEffects.add(new TileVisualEffect(Integer.toString(amt), "take_damage", pos));
            LD43.s.playSound("hit", 4);
        }

        if (hp <= 0){
            LD43.s.gameState.creatures.remove(this);

            if (corpseWeight > 0){
                Item corpse = new Item();
                corpse.name = this.name + " Corpse";
                corpse.graphic = "corpse1";
                corpse.weight = corpseWeight;
                corpse.toxcitiy = corpseToxicity;
                corpse.food = corpseFood;

                Items.createItemAt(corpse, pos);
            }

            int lootToDrop = MathUtils.random(0, xp/2);

            while (Util.randInt(3) == 0) lootToDrop += xp / 2;

            for (int i=0;i<20;++i){
                Item po = Items.randomItem();
                if (po.value <= lootToDrop && po.value >= xp / 8){
                    Items.createItemAt(po, LD43.s.gameState.worldMap.shiftToClear(pos));
                    lootToDrop -= po.value;
                }
            }

            if (endBoss){
                LD43.s.showWinScreen();
            }

            if (isPC()){
                LD43.s.showLoseScreen();
            }

            return true;
        } else {
            return false;
        }
    }

    public void castSpell(Spell spell, TilePos target){
        if (!canAct()) return;
        if (sp < spell.spCost){
            if (isPC()) LD43.s.gameState.addCombatLogMessage(pos, "You do not have enough SP to cast that");
            ticksTillNextAction++;
            return;
        }

        if (spell.requiresTargetOnSpace && !LD43.s.gameState.worldMap.getCreatureOnTile(target).isPresent()){
            if (isPC()) LD43.s.gameState.addCombatLogMessage(pos, "That spell requires a specific target");
            ticksTillNextAction++;
            return;
        }

        spell.effect.cast(spell, getPowerMultiplier(), this, target);

        sp -= spell.spCost;
        takeTime(30);
    }

    public void drinkPotion(Item potion){
        if (!canAct()) return;

        LD43.s.gameState.addCombatLogMessage(pos, this.sname() + " drink"+s()+" a " + potion.name);

        potion.potionSpell.effect.cast(potion.potionSpell, 0.25f, this, pos);

        inventory.remove(potion);

        takeTime(5);
    }

    public boolean canAct(){
        return ticksTillNextAction <= 0 && LD43.s.activeVisualEffects.size() == 0;
    }

    public boolean isPC(){
        return this == LD43.s.gameState.pc;
    }

    public boolean hostileTowards(Creature other){
        return this.isPC() != other.isPC();
    }

    void gainXP(int amt){
        if (isPC()) {
            this.xp += amt;

            if (xp >= level* XP_TO_LEVEL){
                xp -= level* XP_TO_LEVEL;
                level++;

                statPower += statPower / 5;
                statSpeed += statSpeed / 5;
                statMagic += statMagic / 5;
                statEndurance += statEndurance / 5;

                LD43.s.gameState.addCombatLogMessage(pos, "Welcome to experience level " + level);
            }
        }
    }

    public void eat(Item itm){
        if (!canAct()) return;

        currentAction = new EatAction(this, itm);
    }

    public void finishEating(Item itm){
        if (inventory.contains(itm)){
            inventory.remove(itm);

            food += itm.food;

            int effTox = itm.toxcitiy - getEffectiveEndurance();

            LD43.s.gameState.addCombatLogMessage(pos, "You finish eating the " + itm.name);

            if (MathUtils.randomBoolean(effTox / 100f)){
                LD43.s.gameState.addCombatLogMessage(pos, "Oog, that was poisonous...");
                takeDamage(MathUtils.random(12,24));
            }

            if (food > MAX_FOOD){
                food = MAX_FOOD;
                LD43.s.gameState.addCombatLogMessage(pos, "You are full");
            }
        }
    }

    public void healedFor(int amt){
        hp += amt;
        if (hp > getEffectiveEndurance()) hp = getEffectiveEndurance();
    }

    public String s(){
        return isPC() ? "" : "s";
    }

    public int getHp() {
        return hp;
    }

    public int getSp() {
        return sp;
    }

    public int getFood() {
        return food;
    }
}

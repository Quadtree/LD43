package info.quadtree.ld43;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Items {
    static ArrayList<Supplier<Item>> lootTable = new ArrayList<>();

    static {
        lootTable.add(Items::createSword);
        lootTable.add(Items::createBow);
        lootTable.add(Items::createPlateMail);

        for (Spell sp : Spell.values()){
            lootTable.add(() -> Items.createSpellBook(sp));
        }

        lootTable.add(() -> Items.createPotion(Spell.Heal));
        lootTable.add(() -> Items.createPotion(Spell.Invisibility));
        lootTable.add(() -> Items.createPotion(Spell.Haste));
    }

    public static void createItemAt(Item itm, TilePos tp){
        itm.onGroundLocation = tp;
        LD43.s.gameState.items.add(itm);
    }

    public static Item randomItem(){
        return lootTable.get(Util.randInt(lootTable.size())).get();
    }

    public static Item createSword(){
        Item ret = new Item();
        ret.name = "Steel Sword";
        ret.attackDamage = 50;
        ret.defenseSpeedMod = 14;
        ret.attackSpeedMod = 10;
        ret.graphic = "sword1";
        ret.weight = 4;
        ret.slot = Item.EquipSlot.Weapon;
        ret.value = 5;

        return ret;
    }

    public static Item createBow(){
        Item ret = new Item();
        ret.name = "Bow";
        ret.attackDamage = 30;
        ret.graphic = "bow1";
        ret.weight = 3;
        ret.slot = Item.EquipSlot.Weapon;
        ret.allowsRangedAttack = true;
        ret.value = 3;

        return ret;
    }

    public static Item createPlateMail(){
        Item ret = new Item();
        ret.name = "Steel Plate Mail";
        ret.graphic = "armor1";
        ret.weight = 30;
        ret.armorMod = 7;
        ret.speedSoftCap = 10;
        ret.value = 20;

        ret.slot = Item.EquipSlot.Armor;

        return ret;
    }

    public static Item createSpellBook(Spell spell){
        Item ret = new Item();
        ret.name = "Spellbook of " + spell.name;
        ret.graphic = "spell_book";
        ret.weight = 3;
        ret.castSpell = spell;
        ret.tint = spell.color;
        ret.value = spell.spCost;

        return ret;
    }

    public static Item createPotion(Spell spell){
        Item ret = new Item();
        ret.name = "Potion of " + spell.name;
        ret.graphic = "potion";
        ret.weight = 2;
        ret.potionSpell = spell;
        ret.tint = spell.color;
        ret.value = spell.spCost / 3;

        return ret;
    }


}

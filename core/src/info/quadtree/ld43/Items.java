package info.quadtree.ld43;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Items {
    static ArrayList<Supplier<Item>> lootTable = new ArrayList<>();

    static {
        lootTable.add(Items::createSword);
        lootTable.add(Items::createWSSword);
        lootTable.add(Items::createAxe);
        lootTable.add(Items::createWSAxe);
        lootTable.add(Items::createClub);

        lootTable.add(Items::createBow);
        lootTable.add(Items::createWeakBow);
        lootTable.add(Items::createCompositeBow);

        lootTable.add(Items::createPlateMail);
        lootTable.add(Items::createLeatherArmor);
        lootTable.add(Items::createGreaterPlateMail);

        lootTable.add(Items::createAmuletOfPower);
        lootTable.add(Items::createAmuletOfSpeed);
        lootTable.add(Items::createAmuletOfEndurance);
        lootTable.add(Items::createAmuletOfMagic);
        lootTable.add(Items::createUltimateAmulet);

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

    public static Item createWSSword(){
        Item ret = new Item();
        ret.name = "Wizardsteel Sword";
        ret.attackDamage = 100;
        ret.defenseSpeedMod = 18;
        ret.attackSpeedMod = 14;
        ret.graphic = "sword1";
        ret.weight = 2;
        ret.slot = Item.EquipSlot.Weapon;
        ret.value = 40;
        ret.tint = Color.CYAN;

        return ret;
    }

    public static Item createAxe(){
        Item ret = new Item();
        ret.name = "Steel Axe";
        ret.attackDamage = 80;
        ret.attackSpeedMod = 10;
        ret.graphic = "axe";
        ret.weight = 4;
        ret.slot = Item.EquipSlot.Weapon;
        ret.value = 4;

        return ret;
    }

    public static Item createWSAxe(){
        Item ret = new Item();
        ret.name = "Wizardsteel Axe";
        ret.attackDamage = 160;
        ret.attackSpeedMod = 14;
        ret.graphic = "axe";
        ret.weight = 2;
        ret.slot = Item.EquipSlot.Weapon;
        ret.value = 35;
        ret.tint = Color.CYAN;

        return ret;
    }

    public static Item createClub(){
        Item ret = new Item();
        ret.name = "Club";
        ret.attackDamage = 30;
        ret.graphic = "club";
        ret.weight = 2;
        ret.slot = Item.EquipSlot.Weapon;
        ret.value = 1;

        return ret;
    }

    public static Item createBow(){
        Item ret = new Item();
        ret.name = "Bow";
        ret.attackDamage = 50;
        ret.graphic = "bow1";
        ret.weight = 3;
        ret.slot = Item.EquipSlot.Weapon;
        ret.allowsRangedAttack = true;
        ret.value = 12;

        return ret;
    }

    public static Item createWeakBow(){
        Item ret = new Item();
        ret.name = "Bow";
        ret.attackDamage = 30;
        ret.graphic = "bow1";
        ret.weight = 3;
        ret.slot = Item.EquipSlot.Weapon;
        ret.allowsRangedAttack = true;
        ret.value = 3;
        ret.tint = Color.GREEN;

        return ret;
    }

    public static Item createCompositeBow(){
        Item ret = new Item();
        ret.name = "Bow";
        ret.attackDamage = 120;
        ret.graphic = "bow1";
        ret.weight = 3;
        ret.slot = Item.EquipSlot.Weapon;
        ret.allowsRangedAttack = true;
        ret.value = 40;
        ret.tint = Color.GRAY;

        return ret;
    }

    public static Item createLeatherArmor(){
        Item ret = new Item();
        ret.name = "Leather Armor";
        ret.graphic = "armor1";
        ret.weight = 15;
        ret.armorMod = 3;
        ret.speedSoftCap = 25;
        ret.value = 8;
        ret.tint = Color.BROWN;

        ret.slot = Item.EquipSlot.Armor;

        return ret;
    }

    public static Item createPlateMail(){
        Item ret = new Item();
        ret.name = "Steel Armor";
        ret.graphic = "armor1";
        ret.weight = 30;
        ret.armorMod = 7;
        ret.speedSoftCap = 10;
        ret.value = 20;

        ret.slot = Item.EquipSlot.Armor;

        return ret;
    }

    public static Item createGreaterPlateMail(){
        Item ret = new Item();
        ret.name = "Wizardsteel Armor";
        ret.graphic = "armor1";
        ret.weight = 30;
        ret.armorMod = 12;
        ret.speedSoftCap = 15;
        ret.value = 60;
        ret.tint = Color.CYAN;

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

    public static Item createAmuletOfPower(){
        Item ret = new Item();
        ret.name = "Amulet of Power";
        ret.powerMod = 20;
        ret.graphic = "amulet";
        ret.weight = 1;
        ret.slot = Item.EquipSlot.Amulet;
        ret.value = 20;
        ret.tint = Color.RED;

        return ret;
    }

    public static Item createAmuletOfSpeed(){
        Item ret = new Item();
        ret.name = "Amulet of Speed";
        ret.speedMod = 20;
        ret.graphic = "amulet";
        ret.weight = 1;
        ret.slot = Item.EquipSlot.Amulet;
        ret.value = 20;
        ret.tint = Color.YELLOW;

        return ret;
    }

    public static Item createAmuletOfEndurance(){
        Item ret = new Item();
        ret.name = "Amulet of Endurance";
        ret.enduranceMod = 20;
        ret.graphic = "amulet";
        ret.weight = 1;
        ret.slot = Item.EquipSlot.Amulet;
        ret.value = 20;
        ret.tint = Color.GREEN;

        return ret;
    }

    public static Item createAmuletOfMagic(){
        Item ret = new Item();
        ret.name = "Amulet of Magic";
        ret.magicMod = 20;
        ret.graphic = "amulet";
        ret.weight = 1;
        ret.slot = Item.EquipSlot.Amulet;
        ret.value = 20;
        ret.tint = Color.BLUE;

        return ret;
    }

    public static Item createUltimateAmulet(){
        Item ret = new Item();
        ret.name = "Ultimate Amulet";
        ret.powerMod = 20;
        ret.speedMod = 20;
        ret.enduranceMod = 20;
        ret.magicMod = 20;
        ret.graphic = "amulet";
        ret.weight = 1;
        ret.slot = Item.EquipSlot.Amulet;
        ret.value = 100;

        return ret;
    }
}

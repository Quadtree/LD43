package info.quadtree.ld43;

public class Items {
    public static void createItemAt(Item itm, TilePos tp){
        itm.onGroundLocation = tp;
        LD43.s.gameState.items.add(itm);
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

        return ret;
    }

    public static Item createPlateMail(){
        Item ret = new Item();
        ret.name = "Steel Plate Mail";
        ret.graphic = "armor1";
        ret.weight = 30;
        ret.armorMod = 7;
        ret.speedSoftCap = 10;

        ret.slot = Item.EquipSlot.Armor;

        return ret;
    }

    public static Item createSpellBook(Spell spell){
        Item ret = new Item();
        ret.name = "Spellbook of " + spell.name;
        ret.graphic = "spell_book";
        ret.weight = 3;
        ret.castSpell = spell;

        return ret;
    }

    public static Item createPotion(Spell spell){
        Item ret = new Item();
        ret.name = "Potion of " + spell.name;
        ret.graphic = "potion";
        ret.weight = 2;
        ret.potionSpell = spell;

        return ret;
    }
}

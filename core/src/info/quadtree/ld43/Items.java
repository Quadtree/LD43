package info.quadtree.ld43;

public class Items {
    public static void createItemAt(Item itm, TilePos tp){
        itm.onGroundLocation = tp;
        LD43.s.gameState.items.add(itm);
    }

    public static Item createSword(){
        Item ret = new Item();
        ret.name = "Steel Sword";
        ret.attackDamage = 20;
        ret.defenseSpeedMod = 14;
        ret.attackSpeedMod = 10;
        ret.graphic = "sword1";
        ret.weight = 4;
        ret.slot = Item.EquipSlot.Weapon;

        return ret;
    }
}

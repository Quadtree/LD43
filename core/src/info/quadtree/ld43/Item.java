package info.quadtree.ld43;

public class Item {
    public enum EquipSlot {
        Weapon,
        Armor,
        Amulet
    }

    int weight;
    String graphic;
    String name;
    EquipSlot slot;
    boolean isEquipped;
    TilePos onGroundLocation;

    int powerMod;
    int speedMod;
    int enduranceMod;
    int magicMod;

    int speedSoftCap;

    int attackDamage;
    int attackSpeedMod;
    int defenseSpeedMod;

    public void render(){
        if (LD43.s.gameState.worldMap.canSee(LD43.s.gameState.pc.pos, onGroundLocation, 0)) {
            LD43.s.cam.drawOnTile(graphic, onGroundLocation);
        }
    }
}

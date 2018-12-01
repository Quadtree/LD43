package info.quadtree.ld43;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class InventoryDisplay extends Table {
    boolean needsRefresh = true;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (needsRefresh) doRefresh();

        super.draw(batch, parentAlpha);
    }

    public void refresh(){
        needsRefresh = true;
    }

    private void doRefresh(){
        needsRefresh = false;

        clear();

        for (Item itm : LD43.s.gameState.pc.inventory){
            add(Util.lbl(itm.name));

            if (itm.slot != null){
                if (LD43.s.gameState.pc.isEquipped(itm)){
                    add(Util.btn("Unequip", () -> LD43.s.gameState.pc.unequip(itm)));
                } else {
                    add(Util.btn("Equip", () -> LD43.s.gameState.pc.equip(itm)));
                }
            }

            add(Util.btn("Drop", () -> LD43.s.gameState.pc.drop(itm)));

            row();
        }
    }
}

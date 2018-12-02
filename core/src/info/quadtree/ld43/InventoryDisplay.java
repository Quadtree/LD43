package info.quadtree.ld43;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class InventoryDisplay extends Table {
    boolean needsRefresh = true;

    public InventoryDisplay() {
        setBackground(new SpriteDrawable(LD43.s.getGraphic("floor1")));
    }

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
            add(Util.lbl(itm.name)).padLeft(100);

            if (itm.slot != null){
                if (LD43.s.gameState.pc.isEquipped(itm)){
                    add(Util.btn("Unequip", () -> LD43.s.gameState.pc.unequip(itm)));
                } else {
                    add(Util.btn("Equip", () -> LD43.s.gameState.pc.equip(itm)));
                }
            }

            if (itm.food > 0){
                add(Util.btn("Eat", () -> LD43.s.gameState.pc.eat(itm)));
            }

            if (itm.castSpell != null){
                if (!itm.castSpell.selfCastOnly){
                    add(Util.btn("Cast", () -> LD43.s.gameState.selectedSpell = itm.castSpell));
                } else {
                    add(Util.btn("Cast", () -> LD43.s.gameState.pc.castSpell(itm.castSpell, LD43.s.gameState.pc.pos)));
                }
            }

            add(Util.btn("Drop", () -> LD43.s.gameState.pc.drop(itm)));

            row();
        }
    }
}

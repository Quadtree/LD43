package info.quadtree.ld43;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class InventoryDisplay extends Table {
    boolean needsRefresh = true;

    public InventoryDisplay() {
        setBackground(new NinePatchDrawable(LD43.s.buttonDark));
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
            add(new Image(new SpriteDrawable(LD43.s.getGraphic(itm.graphic)))).padLeft(120).padBottom(6);

            add(Util.lbl(itm.name)).padLeft(12).padBottom(6);

            if (itm.slot != null){
                if (LD43.s.gameState.pc.isEquipped(itm)){
                    add(Util.btn("Unequip", () -> LD43.s.gameState.pc.unequip(itm))).padLeft(12).padBottom(6);
                } else {
                    add(Util.btn("Equip", () -> LD43.s.gameState.pc.equip(itm))).padLeft(12).padBottom(6);
                }
            }

            if (itm.food > 0){
                add(Util.btn("Eat", () -> LD43.s.gameState.pc.eat(itm))).padLeft(12).padBottom(6);
            }

            if (itm.castSpell != null){
                if (!itm.castSpell.selfCastOnly){
                    add(Util.btn("Cast", () -> LD43.s.gameState.selectedSpell = itm.castSpell)).padLeft(12).padBottom(6);
                } else {
                    add(Util.btn("Cast", () -> LD43.s.gameState.pc.castSpell(itm.castSpell, LD43.s.gameState.pc.pos))).padLeft(12).padBottom(6);
                }
            }

            if (itm.potionSpell != null){
                add(Util.btn("Drink", () -> LD43.s.gameState.pc.drinkPotion(itm))).padLeft(12).padBottom(6);
            }

            add(Util.btn("Drop", () -> LD43.s.gameState.pc.drop(itm))).padLeft(12).padBottom(6);

            row();
        }
    }
}

package info.quadtree.ld43;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class InventoryDisplay extends Table {
    public static final int BUTTON_WIDTH = 80;
    boolean needsRefresh = true;

    public InventoryDisplay() {

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
            Sprite sp = new Sprite(LD43.s.getGraphic(itm.graphic));
            sp.setColor(itm.tint);
            add(new Image(new SpriteDrawable(sp))).padBottom(6);

            add(Util.lbl(itm.name)).fill().expandX().padLeft(12).padBottom(6);

            if (itm.slot != null){
                if (LD43.s.gameState.pc.isEquipped(itm)){
                    add(Util.btn("Unequip", () -> LD43.s.gameState.pc.unequip(itm))).padLeft(6).padBottom(6).width(BUTTON_WIDTH);
                } else {
                    add(Util.btn("Equip", () -> LD43.s.gameState.pc.equip(itm))).padLeft(6).padBottom(6).width(BUTTON_WIDTH);
                }
            }

            if (itm.food > 0){
                add(Util.btn("Eat", () -> LD43.s.gameState.pc.eat(itm))).padLeft(6).padBottom(6).width(BUTTON_WIDTH);
            }

            if (itm.castSpell != null){
                if (!itm.castSpell.selfCastOnly){
                    add(Util.btn("Cast", () -> LD43.s.gameState.selectedSpell = itm.castSpell)).padLeft(6).padBottom(6).width(BUTTON_WIDTH);
                } else {
                    add(Util.btn("Cast", () -> LD43.s.gameState.pc.castSpell(itm.castSpell, LD43.s.gameState.pc.pos))).padLeft(6).padBottom(6).width(BUTTON_WIDTH);
                }
            }

            if (itm.potionSpell != null){
                add(Util.btn("Drink", () -> LD43.s.gameState.pc.drinkPotion(itm))).padLeft(6).padBottom(6).width(BUTTON_WIDTH);
            }

            add(Util.btn("Drop", () -> LD43.s.gameState.pc.drop(itm))).padLeft(6).padBottom(6).width(BUTTON_WIDTH);

            row();
        }
    }
}

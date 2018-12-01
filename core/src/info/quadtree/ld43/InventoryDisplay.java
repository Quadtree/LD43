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
            row();
        }
    }
}

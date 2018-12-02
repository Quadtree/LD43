package info.quadtree.ld43.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import info.quadtree.ld43.LD43;
import info.quadtree.ld43.TilePos;

public class TileVisualEffect extends BaseVisualEffect {
    String text;
    String graphic;
    TilePos tp;

    float alpha=1f;

    public TileVisualEffect(String text, String graphic, TilePos tp) {
        this.text = text;
        this.graphic = graphic;
        this.tp = tp;
    }

    @Override
    public void render() {
        LD43.s.cam.drawOnTile(graphic, tp, new Color(1,1,1,alpha));
        LD43.s.cam.drawTextOnTile(text, tp);
        alpha -= Gdx.graphics.getDeltaTime();
    }

    @Override
    public boolean keep() {
        return alpha > 0;
    }
}

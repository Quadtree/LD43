package info.quadtree.ld43.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import info.quadtree.ld43.LD43;
import info.quadtree.ld43.TilePos;

public class TileVisualEffect extends BaseVisualEffect {
    String text;
    String graphic;
    TilePos tp;

    float alpha=2f;

    public TileVisualEffect(String text, String graphic, TilePos tp, Runnable onComplete) {
        super(onComplete);
        this.text = text;
        this.graphic = graphic;
        this.tp = tp;
    }

    @Override
    public void render() {
        Color cc = new Color(1,1,1, MathUtils.clamp(alpha, 0, 1f));
        LD43.s.cam.drawOnTile(graphic, tp, cc);
        LD43.s.cam.drawTextOnTile(text, tp, cc);
        alpha -= Gdx.graphics.getDeltaTime() * 3;
    }

    @Override
    public boolean keep() {
        return alpha > 0;
    }
}

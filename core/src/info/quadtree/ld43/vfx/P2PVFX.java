package info.quadtree.ld43.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import info.quadtree.ld43.Camera;
import info.quadtree.ld43.LD43;
import info.quadtree.ld43.TilePos;
import info.quadtree.ld43.action.BaseAction;

public class P2PVFX extends BaseVisualEffect {
    TilePos start;
    TilePos end;
    String graphic;

    Vector2 curPos;
    Vector2 delta;

    float angle;

    float rangeToGo;

    public P2PVFX(TilePos start, TilePos end, String graphic) {
        this.start = start;
        this.end = end;
        this.graphic = graphic;

        rangeToGo = (float)Math.sqrt(Math.pow(start.x - end.x, 2)+Math.pow(start.y - end.y, 2));
        delta = new Vector2(end.x - start.x, end.y - start.y);
        delta.nor();

        angle = delta.angleRad();

        curPos = new Vector2(start.x, start.y);
    }

    @Override
    public void render() {
        float move = Gdx.graphics.getDeltaTime() * 20;
        curPos.add(delta.x * move, delta.y * move);
        rangeToGo -= move;

        Vector2 tv = LD43.s.cam.realToScreen(curPos);
        Sprite gr = LD43.s.getGraphic(graphic);
        gr.setBounds(tv.x, tv.y, Camera.TILE_SIZE, Camera.TILE_SIZE);
        gr.setColor(Color.WHITE);
        gr.setRotation(angle * MathUtils.radiansToDegrees);
        gr.draw(LD43.s.batch);

        gr.setColor(Color.WHITE);
        gr.setRotation(0);
    }

    @Override
    public boolean keep() {
        return rangeToGo > 0;
    }
}

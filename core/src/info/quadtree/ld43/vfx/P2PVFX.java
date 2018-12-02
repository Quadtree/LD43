package info.quadtree.ld43.vfx;

import com.badlogic.gdx.math.Vector2;
import info.quadtree.ld43.TilePos;
import info.quadtree.ld43.action.BaseAction;

public class P2PVFX extends BaseVisualEffect {
    TilePos start;
    TilePos end;
    String graphic;

    Vector2 curPos;

    float rangeToGo;

    public P2PVFX(TilePos start, TilePos end, String graphic) {
        this.start = start;
        this.end = end;
        this.graphic = graphic;

        rangeToGo = (float)Math.sqrt(Math.pow(start.x - end.x, 2)+Math.pow(start.y - end.y, 2));

        curPos = new Vector2(start.x, start.y);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public boolean keep() {
        return rangeToGo > 0;
    }
}

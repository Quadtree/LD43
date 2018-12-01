package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Camera {
    public static final int TILE_SIZE = 32;
    public TilePos pos = TilePos.create(0,0);

    public void drawOnTile(String graphic, TilePos tp){
        drawOnTile(graphic, tp, Color.WHITE);
    }

    public void drawOnTile(String graphic, TilePos tp, Color color){
        Vector2 tv = realToScreen(tp);
        Sprite gr = LD43.s.getGraphic(graphic);
        gr.setBounds(tv.x, tv.y, TILE_SIZE, TILE_SIZE);
        gr.setColor(color);
        gr.draw(LD43.s.batch);
    }

    public Vector2 realToScreen(TilePos real){
        return new Vector2((real.x - pos.x) * TILE_SIZE + Gdx.graphics.getWidth() / 2, (real.y - pos.y) * TILE_SIZE + Gdx.graphics.getHeight() / 2);
    }

    public TilePos screenToReal(Vector2 screenPos){
        return null; // @todo
    }
}

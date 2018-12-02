package info.quadtree.ld43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
        gr.setColor(Color.WHITE);
    }

    public void drawOnTile(String graphic, int x, int y, Color color){
        Vector2 tv = realToScreen(x, y);
        Sprite gr = LD43.s.getGraphic(graphic);
        gr.setBounds(tv.x, tv.y, TILE_SIZE, TILE_SIZE);
        gr.setColor(color);
        gr.draw(LD43.s.batch);
        gr.setColor(Color.WHITE);
    }

    public void drawTextOnTile(String text, TilePos tp, Color color){
        Vector2 tv = realToScreen(tp);

        GlyphLayout gl = new GlyphLayout();
        gl.setText(LD43.s.bitmapFont, text);

        LD43.s.bitmapFont.setColor(color);
        LD43.s.bitmapFont.draw(LD43.s.batch, gl, tv.x - gl.width / 2 + 16, tv.y + gl.height - 2 + 16);
    }

    public Vector2 realToScreen(int x, int y){
        return new Vector2((x - pos.x) * TILE_SIZE + getEffectiveScreenWidth() / 2, (y - pos.y) * TILE_SIZE + Gdx.graphics.getHeight() / 2);
    }

    public Vector2 realToScreen(TilePos real){
        return new Vector2((real.x - pos.x) * TILE_SIZE + getEffectiveScreenWidth() / 2, (real.y - pos.y) * TILE_SIZE + Gdx.graphics.getHeight() / 2);
    }

    public Vector2 realToScreen(Vector2 real){
        return new Vector2((real.x - pos.x) * TILE_SIZE + getEffectiveScreenWidth() / 2, (real.y - pos.y) * TILE_SIZE + Gdx.graphics.getHeight() / 2);
    }

    public int getEffectiveScreenWidth() {
        return Gdx.graphics.getWidth() - 300;
    }

    public TilePos screenToReal(Vector2 screenPos){
        return TilePos.create(
                (int)(pos.x - ((getEffectiveScreenWidth() - 2 * screenPos.x) / (2 * TILE_SIZE))),
                (int)(pos.y - ((Gdx.graphics.getHeight() - 2 * (Gdx.graphics.getHeight() - screenPos.y)) / (2 * TILE_SIZE)))
        );
    }
}

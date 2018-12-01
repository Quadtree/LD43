package info.quadtree.ld43;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;
import java.util.Map;

public class LD43 extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

	TextureAtlas atlas;

	public static LD43 s;

	Camera cam = new Camera();

	GameState gameState;

	Map<String, Sprite> graphics = new HashMap<String, Sprite>();

	BitmapFont bitmapFont;

	public Sprite getGraphic(String name){
		if (!graphics.containsKey(name)) graphics.put(name, atlas.createSprite(name));

		return graphics.get(name);
	}
	
	@Override
	public void create () {
		s = this;

		bitmapFont = new BitmapFont();

		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		gameState = new GameState();
		cam.pos = TilePos.create(WorldMap.WORLD_WIDTH / 2, 1);

		Gdx.input.setInputProcessor(new GameInputProcessor());
	}

	@Override
	public void render () {
		cam.pos = gameState.pc.pos;

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(img, 0, 0);
		gameState.render();


		bitmapFont.draw(batch,
				"PWR: " + gameState.pc.statPower +
					" SPD: " + gameState.pc.statSpeed +
					" END: " + gameState.pc.statEndurance +
					" MGC: " + gameState.pc.statMagic,
				20, 20);

		batch.end();


	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

package info.quadtree.ld43;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LD43 extends ApplicationAdapter {
	static boolean CHEATS = true;

	SpriteBatch batch;
	Texture img;

	TextureAtlas atlas;

	public static LD43 s;

	public Camera cam = new Camera();

	public GameState gameState;

	Map<String, Sprite> graphics = new HashMap<String, Sprite>();

	BitmapFont bitmapFont;

	Stage mainStage;

	Label.LabelStyle defaultLabelStyle;
	TextButton.TextButtonStyle defaultTextButtonStyle;

	InventoryDisplay inventoryDisplay;

	Table combatLog;
	ScrollPane combatLogPane;

	NinePatchDrawable buttonDark;
	NinePatchDrawable buttonLight;

	public Sprite getGraphic(String name){
		if (!graphics.containsKey(name)) graphics.put(name, atlas.createSprite(name));

		return graphics.get(name);
	}
	
	@Override
	public void create () {
		s = this;

		bitmapFont = new BitmapFont();
		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		buttonDark = new NinePatchDrawable(atlas.createPatch("toolbar"));

		defaultLabelStyle = new Label.LabelStyle(LD43.s.bitmapFont, Color.WHITE);
		defaultTextButtonStyle = new TextButton.TextButtonStyle(
				new NinePatchDrawable(atlas.createPatch("button")),
				new NinePatchDrawable(atlas.createPatch("button_light")),
				new NinePatchDrawable(atlas.createPatch("button_light")),
				LD43.s.bitmapFont
		);

		mainStage = new Stage();
		Label lowerStatusLabel = Util.createDynamicLabel(() -> "PWR: " + gameState.pc.statPower +
						" SPD: " + gameState.pc.statSpeed +
						" END: " + gameState.pc.statEndurance +
						" MGC: " + gameState.pc.statMagic
		);
		mainStage.addActor(lowerStatusLabel);
		lowerStatusLabel.setPosition(20, 20);

		Label upperStatusLabel = Util.createDynamicLabel(() -> "Tick: " + gameState.tick +
				"    HP: " + gameState.pc.hp + "/" + gameState.pc.statEndurance +
				"    SP: " + gameState.pc.sp + "/" + gameState.pc.statMagic +
				"    Level: " + gameState.pc.level +
				"    " + ((gameState.pc.food < 1000) ? "Hungry" : "")
		);
		mainStage.addActor(upperStatusLabel);
		upperStatusLabel.setPosition(20, 40);

		Label mouseOverLabel = Util.createDynamicLabel(() -> {
			TilePos tp = cam.screenToReal(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

			Optional<Creature> trgCrt = gameState.worldMap.getCreatureOnTile(tp);
			if (trgCrt.isPresent()){
				return trgCrt.get().name;
			}

			Optional<Item> trgItm = gameState.worldMap.getItemOnTile(tp);
			if (trgItm.isPresent()){
				return trgItm.get().name;
			}

			return "";
		});
		mainStage.addActor(mouseOverLabel);
		mouseOverLabel.setPosition(20, 60);

		inventoryDisplay = new InventoryDisplay();

		ScrollPane invDisplayPane = new ScrollPane(inventoryDisplay);
		mainStage.addActor(invDisplayPane);
		invDisplayPane.setBounds(Gdx.graphics.getWidth() - 400, 0, 400, Gdx.graphics.getHeight());
		inventoryDisplay.setFillParent(true);
		inventoryDisplay.align(Align.topLeft);
		inventoryDisplay.pad(20);

		combatLog = new Table();

		combatLogPane = new ScrollPane(combatLog);
		mainStage.addActor(combatLogPane);
		combatLogPane.setBounds(0, Gdx.graphics.getHeight() - 120, Gdx.graphics.getWidth() - 400, 120);

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		resetGameState();
		cam.pos = TilePos.create(WorldMap.WORLD_WIDTH / 2, 1);

		InputMultiplexer mp = new InputMultiplexer();
		mp.addProcessor(mainStage);
		mp.addProcessor(new GameInputProcessor());

		Gdx.input.setInputProcessor(mp);
	}

	public void resetGameState() {
		gameState = new GameState();
		gameState.init();
	}

	@Override
	public void render () {
		while (!gameState.pc.canAct()){
			gameState.tick();
			gameState.tickActions();

			if (!gameState.creatures.contains(gameState.pc)){
				System.err.println("Player has died!!!");
				resetGameState();
			}

			inventoryDisplay.refresh();

			combatLog.clear();
			for (String s : gameState.combatLogMessages){
				combatLog.add(Util.lbl(s));
				combatLog.row();
			}
			combatLogPane.layout();
			combatLogPane.setScrollY(100000);
		}

		gameState.pc.tickActions();

		cam.pos = gameState.pc.pos;

		mainStage.act();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//batch.draw(img, 0, 0);
		gameState.render();
		batch.end();

		mainStage.draw();

		batch.begin();
		gameState.renderMinimap();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

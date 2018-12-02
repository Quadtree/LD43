package info.quadtree.ld43;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import info.quadtree.ld43.vfx.BaseVisualEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LD43 extends ApplicationAdapter {
	static boolean CHEATS = true;

	public static final String EVIL_GOD_NAME = "EvilGod";
	public static final String TOWN_NAME = "SomeTown";

	public SpriteBatch batch;
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

	ArrayList<BaseVisualEffect> activeVisualEffects = new ArrayList<>();

	Stage modalScreen;

	InputMultiplexer mp;

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
		Label lowerStatusLabel = Util.createDynamicLabel(() ->
						"PWR: " + gameState.pc.statPower +
						" SPD: " + gameState.pc.statSpeed +
						" END: " + gameState.pc.statEndurance +
						" MGC: " + gameState.pc.statMagic
		);
		mainStage.addActor(lowerStatusLabel);
		lowerStatusLabel.setPosition(20, 20);

		Label upperStatusLabel = Util.createDynamicLabel(() -> "Tick: " + gameState.tick +
				"    HP: " + gameState.pc.hp + "/" + gameState.pc.getEffectiveEndurance() +
				"    SP: " + gameState.pc.sp + "/" + gameState.pc.getEffectiveMagic() +
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

		modalScreen = new Stage();
		Label titleScreenLabel = Util.lbl("To placate the fel demigod " + EVIL_GOD_NAME + " the city of " + TOWN_NAME + " sends a sacrifice to the ever-shifting tunnels and caves that make up his home each year. This year, you were chosen...");
		modalScreen.addActor(titleScreenLabel);
		titleScreenLabel.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);

		Label copyrightLabel = Util.lbl("Made by Quadtree for Ludum Dare 43");
		modalScreen.addActor(copyrightLabel);
		copyrightLabel.setPosition(Gdx.graphics.getWidth() - 20, 20, Align.bottomRight);

		Gdx.input.setInputProcessor(new ModalScreenCloser());

		mp = new InputMultiplexer();
		mp.addProcessor(mainStage);
		mp.addProcessor(new GameInputProcessor());
	}

	public void showWinScreen(){
		showModalText("You slew the vile " + EVIL_GOD_NAME + " and the city is safe for this year. But legend holds that " + EVIL_GOD_NAME + " will return and demand more sacrifices when a year and a day have passed...");
		resetGameState();
	}

	public void showLoseScreen(){
		showModalText("You have died and made a fitting sacrifice to " + EVIL_GOD_NAME + ". You take some solace in the fact that " + TOWN_NAME + " is safe for another year.");
		resetGameState();
	}

	public void showModalText(String text){
		modalScreen = new Stage();
		Label titleScreenLabel = Util.lbl(text);
		modalScreen.addActor(titleScreenLabel);
		titleScreenLabel.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
		Gdx.input.setInputProcessor(new ModalScreenCloser());
	}

	public void resetGameState() {
		gameState = new GameState();
		gameState.init();
		updateDisplays();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (modalScreen != null){
			modalScreen.act();
			modalScreen.draw();
			return;
		}

		while (activeVisualEffects.size() == 0 && !gameState.pc.canAct()) {
			gameState.tick();
			gameState.tickActions();

			if (!gameState.creatures.contains(gameState.pc)) {
				System.err.println("Player has died!!!");
				resetGameState();
			}

			updateDisplays();
		}

		gameState.pc.tickActions();

		cam.pos = gameState.pc.pos;

		mainStage.act();

		batch.begin();
		//batch.draw(img, 0, 0);
		gameState.render();

		if (activeVisualEffects.size() > 0){
			if (activeVisualEffects.get(0).keep()){
				activeVisualEffects.get(0).render();
			} else {
				if (activeVisualEffects.get(0).onComplete != null) activeVisualEffects.get(0).onComplete.run();
				activeVisualEffects.remove(0);
			}
		}

		batch.end();

		mainStage.draw();

		batch.begin();
		gameState.renderMinimap();
		batch.end();
	}

	public void updateDisplays() {
		inventoryDisplay.refresh();

		combatLog.clear();
		for (String s : gameState.combatLogMessages) {
			combatLog.add(Util.lbl(s));
			combatLog.row();
		}
		combatLogPane.layout();
		combatLogPane.setScrollY(100000);
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

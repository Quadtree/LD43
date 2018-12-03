package info.quadtree.ld43;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import info.quadtree.ld43.action.RestAction;
import info.quadtree.ld43.vfx.BaseVisualEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LD43 extends ApplicationAdapter {
	public static int INV_PANE_WIDTH = 800;
	public static boolean CHEATS = false;

	public static final String EVIL_GOD_NAME = "Zavghul";
	public static final String TOWN_NAME = "Tharburg";

	public static final float MUSIC_VOLUME = 0.5f;

	public SpriteBatch batch;
	Texture img;

	TextureAtlas atlas;

	public static LD43 s;

	public Camera cam = new Camera();

	public GameState gameState;

	Map<String, Sprite> graphics = new HashMap<String, Sprite>();

	BitmapFont bitmapFont;
	BitmapFont bigFont;
	BitmapFont combatLogFont;

	Stage mainStage;

	Label.LabelStyle defaultLabelStyle;
	Label.LabelStyle combatLogLabelStyle;
	TextButton.TextButtonStyle defaultTextButtonStyle;

	InventoryDisplay inventoryDisplay;

	Table combatLog;

	NinePatchDrawable buttonDark;
	NinePatchDrawable buttonLight;

	ArrayList<BaseVisualEffect> activeVisualEffects = new ArrayList<>();

	Stage modalScreen;

	Texture backgroundCloud;

	InputMultiplexer mp;

	Map<String, Sound> soundMap = new HashMap<>();

	Music bgm;

	public boolean loopMusic = false;

	public void playSound(String name){
		if (!soundMap.containsKey(name)) soundMap.put(name, Gdx.audio.newSound(Gdx.files.internal(name + ".wav")));

		soundMap.get(name).play();
	}

	public void playSound(String name, int variants){
		playSound(name + Util.randInt(variants));
	}

	public Sprite getGraphic(String name){
		if (!graphics.containsKey(name)) graphics.put(name, atlas.createSprite(name));

		return graphics.get(name);
	}
	
	@Override
	public void create () {
		s = this;

		if (Gdx.graphics.getWidth() < 1200) INV_PANE_WIDTH = 270;

		Runnable someLambda = () -> {
			Gdx.app.log("SOME_LAMBDA", "HAHAHA");
		};
		someLambda.run();

		bgm = Gdx.audio.newMusic(Gdx.files.internal("bgm.ogg"));

		backgroundCloud = new Texture(Gdx.files.internal("background_cloud.png"));

		bitmapFont = new BitmapFont(Gdx.files.internal("font_45.fnt"));
		bigFont = new BitmapFont(Gdx.files.internal("font_180.fnt"));
		combatLogFont = new BitmapFont(Gdx.files.internal("font_25.fnt"));
		atlas = new TextureAtlas(Gdx.files.internal("main.atlas"));

		buttonDark = new NinePatchDrawable(atlas.createPatch("toolbar"));

		BitmapFont uiFont = Gdx.graphics.getWidth() > 1200 ? LD43.s.bitmapFont : new BitmapFont(Gdx.files.internal("font_10.fnt"));

		defaultLabelStyle = new Label.LabelStyle(uiFont, Color.WHITE);
		combatLogLabelStyle = new Label.LabelStyle(combatLogFont, Color.WHITE);
		defaultTextButtonStyle = new TextButton.TextButtonStyle(
				new NinePatchDrawable(atlas.createPatch("button")),
				new NinePatchDrawable(atlas.createPatch("button_light")),
				new NinePatchDrawable(atlas.createPatch("button_light")),
				uiFont
		);

		final String[] encumbMessages = {
			"",
			"Overburdened",
			"Very Overburdened",
			"Extremely Overburdened",
			"Immobile Due to Load"
		};

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
				"    " + ((gameState.pc.food < 1000) ? "Hungry" : "") +
				"    " + encumbMessages[gameState.pc.newEncumbrance]
		);
		mainStage.addActor(upperStatusLabel);
		upperStatusLabel.setPosition(20, 20+50);

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
		mouseOverLabel.setPosition(20, 20+50*2);

		inventoryDisplay = new InventoryDisplay();

		ScrollPane invDisplayPane = new ScrollPane(inventoryDisplay);
		inventoryDisplay.setFillParent(true);
		inventoryDisplay.align(Align.topLeft);
		inventoryDisplay.pad(10);

		Table rightToolbar = new Table();
		rightToolbar.setBackground(new NinePatchDrawable(LD43.s.buttonDark));
		rightToolbar.add(invDisplayPane).expand().fill().colspan(2);
		rightToolbar.setBounds(Gdx.graphics.getWidth() - INV_PANE_WIDTH, 0, INV_PANE_WIDTH, Gdx.graphics.getHeight());

		rightToolbar.row();
		rightToolbar.add(Util.btn("Restart Game", this::resetGameState)).pad(6);
		rightToolbar.add(Util.btn("Rest Until Healed", () -> {
			gameState.pc.currentAction = new RestAction(gameState.pc);
		})).pad(6);

		mainStage.addActor(rightToolbar);

		combatLog = new Table();
		mainStage.addActor(combatLog);

		combatLog.setBounds(0, Gdx.graphics.getHeight() - 24*5, Gdx.graphics.getWidth() - INV_PANE_WIDTH, 24*5);
		combatLog.setWidth(Gdx.graphics.getWidth() - INV_PANE_WIDTH);
		//combatLog.debug();

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		resetGameState();
		cam.pos = TilePos.create(WorldMap.WORLD_WIDTH / 2, 1);

		modalScreen = new Stage();
		addBackgroundCloud();

		Label titleLabel = new Label("Pit of " + LD43.EVIL_GOD_NAME, new Label.LabelStyle(LD43.s.bigFont, Color.WHITE));
		modalScreen.addActor(titleLabel);
		titleLabel.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.75f, Align.center);

		Label titleScreenLabel = Util.lbl("To placate the fel demigod " + EVIL_GOD_NAME + " the city of " + TOWN_NAME + " sends a sacrifice to the ever-shifting tunnels and caves that make up his home each year. This year, you were chosen...");
		titleScreenLabel.setWidth(700);
		titleScreenLabel.setWrap(true);
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

	private void addBackgroundCloud(){
		if (modalScreen != null){
			Image bkg = new Image(new TiledDrawable(new TextureRegion(backgroundCloud)));

			modalScreen.addActor(bkg);
			bkg.setFillParent(true);
		}
	}

	public void showModalText(String text){
		modalScreen = new Stage();
		addBackgroundCloud();
		Label titleScreenLabel = Util.lbl(text);
		titleScreenLabel.setWidth(700);
		titleScreenLabel.setWrap(true);
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
		if (loopMusic && !bgm.isPlaying()){
			bgm.stop();
			bgm.dispose();
			bgm = Gdx.audio.newMusic(Gdx.files.internal("bgm.ogg"));
			LD43.s.bgm.setVolume(LD43.MUSIC_VOLUME);
			bgm.play();
		}

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
			combatLog.add(new Label(s, combatLogLabelStyle));
			combatLog.row().padTop(-6);
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}

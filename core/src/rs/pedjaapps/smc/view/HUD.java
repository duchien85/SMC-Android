package rs.pedjaapps.smc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashSet;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.object.Box;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.shader.Shader;
import rs.pedjaapps.smc.utility.Constants;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.HUDTimeText;
import rs.pedjaapps.smc.utility.MyMathUtils;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.NATypeConverter;
import rs.pedjaapps.smc.utility.PrefsManager;
import rs.pedjaapps.smc.utility.Utility;

import static com.badlogic.gdx.Gdx.gl;

public class HUD
{
	private World world;
	
	private TextureRegion pause, play, fire, jump, up, down, right,
		left, soundOn, soundOff, musicOn, musicOff, fireP, jumpP, upP,
		downP, leftP, rightP;
    public Rectangle pauseR, playR, fireR, jumpR, upR, downR, rightR,
		leftR, soundR, musicR, fireRT, jumpRT;
	private Texture itemBox, maryoL, goldM;
	private Rectangle itemBoxR, maryoLR;
    public Array<Vector2> leftPolygon = new Array<Vector2>(5);
    public Array<Vector2> rightPolygon = new Array<Vector2>(5);
    public Array<Vector2> upPolygon = new Array<Vector2>(5);
    public Array<Vector2> downPolygon = new Array<Vector2>(5);

	public OrthographicCamera cam;
	private SpriteBatch batch;
	private Stage stage;

	private ShapeRenderer shapeRenderer = new ShapeRenderer();

	private BitmapFont font, tts, pauseFont;
	private GlyphLayout ttsGlyphLayout, fontGlyphLayout, pauseGlyph;

	private static float C_W = Gdx.graphics.getWidth();
	private static float C_H = Gdx.graphics.getHeight();

	public enum Key
	{
		none, pause, fire, jump, left, right, up, down, play, sound, music
	}

	private HashSet<Key> pressedKeys = new HashSet<>(Key.values().length);
	
	private float stateTime;
	public boolean updateTimer = true;
	
	private static final String ttsText = "TOUCH ANYWHERE TO START";
	private static final String pauseText = "PAUSE";
	private boolean ttsFadeIn;
	private float ttsAlpha = 1;
	private int points;
	private String pointsText;
	private final NATypeConverter<Integer> coins = new NATypeConverter<>();
	private final NAHudText<Integer> lives = new NAHudText<>(null, "x");
	private final HUDTimeText time = new HUDTimeText();
	public BoxTextPopup boxTextPopup;

	public HUD(World world)
	{
		this.world = world;
		cam = new OrthographicCamera(C_W, C_H);
		cam.position.set(new Vector2(C_W / 2, C_H / 2), 0);
		cam.update();
		batch = new SpriteBatch();
		stage = new Stage(new ScreenViewport());
        setBounds();
	}

    private void setBounds()
    {
        float width = C_H / 10f;
        float height = width;
        float x = C_W - width * 1.5f;
        float y = C_H - height * 1.5f;
        pauseR = new Rectangle(x, y, width, height);

        width = C_H / 7f;
        height = width;
        x = C_W - width * 1.5f;
        y = C_H - height * 5f;
        fireR = new Rectangle(x, y, width, height);
		float bX = x - width * .25f;
		float bY = y - height * .25f;
		float bW = width + width *.5f;
		float bH = height + height * 0.5f;
		fireRT = new Rectangle(bX, bY, bW, bH);
		
        x = bX - width * 1.25f;
        y = bY - height;
		bY = bY + bH * .25f;
        jumpR = new Rectangle(x, y, width, height);
		jumpRT = new Rectangle(bX - bW, bY - bH, bW, bH);
		
        x = width / 2f;
        y = height * 1.5f;
        width = width * 1.24f;
        leftR = new Rectangle(x, y, width, height);
        leftPolygon.clear();
        leftPolygon.add(new Vector2(x, y + height));
        leftPolygon.add(new Vector2(x + width - x / 100 * 23.25f, y + height));
        leftPolygon.add(new Vector2(x + width, y + height / 2));
        leftPolygon.add(new Vector2(x + width - x / 100 * 23.25f, y));
        leftPolygon.add(new Vector2(x, y));

        x = x + width + width / 4f;
        rightR = new Rectangle(x, y, width, height);
        rightPolygon.clear();
        rightPolygon.add(new Vector2(x, y + height / 2));
        rightPolygon.add(new Vector2(x + x / 100 * 23.25f, y + height));//x / 100 * 23.25%
        rightPolygon.add(new Vector2(x + width, y + height));
        rightPolygon.add(new Vector2(x + width, y));
        rightPolygon.add(new Vector2(x + x / 100 * 23.25f, y));

        width = C_H / 7f;
        height = width * 1.24f;
        x = x - width / 2f - width / 8f;
        y = y + height / 2f;
        upR = new Rectangle(x, y, width, height);
        upPolygon.clear();
        upPolygon.add(new Vector2(x, y + height));
        upPolygon.add(new Vector2(x + width, y + height));
        upPolygon.add(new Vector2(x + width, y + y / 100 * 23.25f));
        upPolygon.add(new Vector2(x + width / 2, y));
        upPolygon.add(new Vector2(x, y + y / 100 * 23.25f));

        y = y - height - height / 4f;
        downR = new Rectangle(x, y, width, height);
        downPolygon.clear();
        downPolygon.add(new Vector2(x + width / 2, y + width));
        downPolygon.add(new Vector2(x + width, y + height - y / 100 * 23.25f));
        downPolygon.add(new Vector2(x + width, y));
        downPolygon.add(new Vector2(x, y));
        downPolygon.add(new Vector2(x, y + height - y / 100 * 23.25f));

		width = C_H / 10f;
		height = width;
		x = C_W / 2 - width / 2;
		y = height * 2.1f + height;
		soundR = new Rectangle(x, y, width, height);
		
		x = x * 1.20f;
		musicR = new Rectangle(x, y, width, height);
		
		x = soundR.x * 0.80f;
		playR = new Rectangle(x, y, width, height);
		
		float ibSize = C_W / 14;
		itemBoxR = new Rectangle(C_W / 2 - ibSize, C_H - ibSize - ibSize / 5, ibSize, ibSize);
		
		float maryoLSize = itemBoxR.height / 2.5f;
		maryoLR = new Rectangle(pauseR.x - maryoLSize * 3, itemBoxR.y + itemBoxR.height - maryoLSize - maryoLSize / 2, maryoLSize * 2, maryoLSize);
    }

    public void resize(int width, int height)
    {
        C_W = Gdx.graphics.getWidth();
        C_H = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(C_W, C_H);
        cam.position.set(new Vector2(C_W / 2, C_H / 2), 0);
        cam.update();
		stage.getViewport().update(width, height, true);
        setBounds();
    }
	
	public void loadAssets()
	{
		world.screen.game.assets.manager.load("data/sounds/item/live_up_2.mp3", Sound.class);

		world.screen.game.assets.manager.load("data/hud/controls.pack", TextureAtlas.class);
		world.screen.game.assets.manager.load("data/hud/SMCLook512.pack", TextureAtlas.class);
		world.screen.game.assets.manager.load("data/hud/hud.pack", TextureAtlas.class);
		world.screen.game.assets.manager.load("data/game/itembox.png", Texture.class, world.screen.game.assets.textureParameter);
        world.screen.game.assets.manager.load("data/game/maryo_l.png", Texture.class, world.screen.game.assets.textureParameter);
        world.screen.game.assets.manager.load("data/game/gold_m.png", Texture.class, world.screen.game.assets.textureParameter);
		world.screen.game.assets.manager.load("data/game/game_over.png", Texture.class, world.screen.game.assets.textureParameter);
		
		FreetypeFontLoader.FreeTypeFontLoaderParameter ttsTextParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        ttsTextParams.fontFileName = Constants.DEFAULT_FONT_FILE_NAME;
        ttsTextParams.fontParameters.size = (int) C_H / 15;
        ttsTextParams.fontParameters.characters = "TOUCHANYWERS";
		ttsTextParams.fontParameters.borderWidth = 2f;
		world.screen.game.assets.manager.load("touch_to_start.ttf", BitmapFont.class, ttsTextParams);

		FreetypeFontLoader.FreeTypeFontLoaderParameter boxPD = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		boxPD.fontFileName = "data/fonts/MyriadPro-Regular.otf";
		boxPD.fontParameters.size = (int) HUD.C_H / 30;
		boxPD.fontParameters.borderWidth = 2f;
		world.screen.game.assets.manager.load("btf.ttf", BitmapFont.class, boxPD);

		FreetypeFontLoader.FreeTypeFontLoaderParameter pauseParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		pauseParams.fontFileName = Constants.DEFAULT_FONT_FILE_NAME;
		pauseParams.fontParameters.size = (int) C_H / 4;
		pauseParams.fontParameters.characters = "PAUSE";
		pauseParams.fontParameters.borderWidth = 2f;
		world.screen.game.assets.manager.load("pause.ttf", BitmapFont.class, pauseParams);
	}

    public void initAssets()
	{
		TextureAtlas atlas = world.screen.game.assets.manager.get("data/hud/controls.pack", TextureAtlas.class);
		TextureAtlas hud = world.screen.game.assets.manager.get("data/hud/hud.pack", TextureAtlas.class);
		pause = hud.findRegion("pause");
		play = hud.findRegion("play");
		musicOn = hud.findRegion("music");
        musicOff = hud.findRegion("music_off");
        
		soundOn = hud.findRegion("sound");
        soundOff = hud.findRegion("sound_off");

		if (MaryoGame.showOnScreenControls())
		{
			fire = atlas.findRegion("fire");
			fireP = atlas.findRegion("fire-pressed");
			jump = atlas.findRegion("jump");
			jumpP = atlas.findRegion("jump-pressed");
			left = atlas.findRegion("dpad-left");
			leftP = atlas.findRegion("dpad-left-pressed");
			right = new TextureRegion(left);
			right.flip(true, false);
			rightP = new TextureRegion(leftP);
			rightP.flip(true, false);
			up = atlas.findRegion("dpad-up");
			upP = atlas.findRegion("dpad-up-pressed");
			down = new TextureRegion(up);
			down.flip(false, true);
			downP = new TextureRegion(upP);
			downP.flip(false, true);
		}

		itemBox = world.screen.game.assets.manager.get("data/game/itembox.png");
		maryoL = world.screen.game.assets.manager.get("data/game/maryo_l.png");
		goldM = world.screen.game.assets.manager.get("data/game/gold_m.png");
		
		Texture.TextureFilter filter = Texture.TextureFilter.Linear;
		itemBox.setFilter(filter, filter);
		maryoL.setFilter(filter, filter);
		goldM.setFilter(filter, filter);
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(Constants.DEFAULT_FONT_FILE_NAME));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) C_H / 25;
        parameter.characters = "0123456789TimePontsx:";
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.borderWidth = 2f;
        font = generator.generateFont(parameter);
		font.setColor(1, 1, 1, 1);//white
		fontGlyphLayout = new GlyphLayout();
		
		generator.dispose();

		tts = world.screen.game.assets.manager.get("touch_to_start.ttf");
		tts.setColor(1, 1, 1, 1);
		ttsGlyphLayout = new GlyphLayout(font, ttsText);
		BitmapFont btf = world.screen.game.assets.manager.get("btf.ttf");
		btf.setColor(1, 1, 1, 1);
		boxTextPopup = new BoxTextPopup(btf, world.screen.game.assets);

		pauseFont = world.screen.game.assets.manager.get("pause.ttf");
		pauseFont.setColor(1, 1, 1, 1);
		pauseGlyph = new GlyphLayout(pauseFont, pauseText);
	}

	public void render(GameScreen.GAME_STATE gameState, float deltaTime)
	{
		if (gameState == GameScreen.GAME_STATE.GAME_READY)
		{
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			if(ttsAlpha >= 1)
			{
				ttsAlpha = 1;
				ttsFadeIn = false;
			}
			else if(ttsAlpha <= 0.3f)
			{
				ttsFadeIn = true;
			}
			tts.setColor(1, 1, 1, ttsFadeIn ? (ttsAlpha += deltaTime) : (ttsAlpha -= deltaTime));
			ttsGlyphLayout.setText(tts, ttsText);
			tts.draw(batch, ttsText, C_W / 2 - ttsGlyphLayout.width / 2, C_H / 2 + ttsGlyphLayout.height / 2);
			batch.end();
		}
		else if (gameState == GameScreen.GAME_STATE.GAME_PAUSED)
		{
			drawPauseOverlay();
		}
		else
		{
			stage.act(deltaTime);
			stage.draw();

			if(updateTimer)stateTime += deltaTime;
			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			if(pressedKeys.contains(Key.pause))batch.setShader(Shader.GLOW_SHADER);
			batch.draw(pause, pauseR.x, pauseR.y, pauseR.width, pauseR.height);
			batch.setShader(null);
			if (MaryoGame.showOnScreenControls())
			{
				batch.draw(pressedKeys.contains(Key.fire) ? fireP : fire, fireR.x, fireR.y , fireR.width, fireR.height);
				batch.draw(pressedKeys.contains(Key.jump) ? jumpP : jump, jumpR.x, jumpR.y , jumpR.width, jumpR.height);
				batch.draw(pressedKeys.contains(Key.left) ? leftP : left, leftR.x, leftR.y , leftR.width, leftR.height);
				batch.draw(pressedKeys.contains(Key.right) ? rightP : right, rightR.x, rightR.y , rightR.width, rightR.height);
				batch.draw(pressedKeys.contains(Key.up) ? upP : up, upR.x, upR.y, upR.width, upR.height);
				batch.draw(pressedKeys.contains(Key.down) ? downP : down, downR.x, downR.y, downR.width, downR.height);
			}
            if(GameSave.getItem() != null)
                batch.setColor(Color.RED);
			batch.draw(itemBox, itemBoxR.x, itemBoxR.y, itemBoxR.width, itemBoxR.height);
            batch.setColor(Color.WHITE);

			batch.draw(maryoL, maryoLR.x, maryoLR.y, maryoLR.width, maryoLR.height);
			
			// points
			pointsText = formatPointsString(GameSave.save.points);
			fontGlyphLayout.setText(font, pointsText);
			float pointsX = C_W * 0.03f;
			float pointsY = fontGlyphLayout.height / 2 + maryoLR.y + maryoLR.height / 2;
			font.setColor(1, 1, 1, 1);
			font.draw(batch, pointsText, pointsX, pointsY);
			
			//coins
			float goldHeight = fontGlyphLayout.height * 1.1f;
			float goldX = pointsX + fontGlyphLayout.width + goldHeight;
			batch.draw(goldM, goldX, pointsY - fontGlyphLayout.height, goldHeight * 2, goldHeight);
			
			String coins =  this.coins.toString(GameSave.getCoins());
			font.setColor(1, 1, 1, 1);
			font.draw(batch, coins, goldX + goldHeight * 2, pointsY);
			
			//time
			time.update(stateTime);
			fontGlyphLayout.setText(font, time);
			float timeX = (itemBoxR.x + itemBoxR.width) + (maryoLR.x - (itemBoxR.x + itemBoxR.width)) / 2 - fontGlyphLayout.width / 2;
			font.setColor(1, 1, 1, 1);
			font.draw(batch, time, timeX, pointsY);
			
			//lives
			String lives = this.lives.toString(MyMathUtils.max(GameSave.save.lifes, 0));
			fontGlyphLayout.setText(font, lives);
			float lifesX = maryoLR.x - fontGlyphLayout.width;
			font.setColor(0, 1, 0, 1);
			font.draw(batch, lives, lifesX, pointsY);

            //draw item if any
            if(GameSave.getItem() != null)
            {
                float w = itemBoxR.width * 0.5f;
                float h = itemBoxR.height * 0.5f;
                float x = itemBoxR.x + itemBoxR.width * 0.5f - w * 0.5f;
                float y = itemBoxR.y + itemBoxR.height * 0.5f - h * 0.5f;
                batch.draw(GameSave.getItem().texture, x, y, w, h);
            }

			batch.end();
			boxTextPopup.render(batch, cam);
		}
		if(PrefsManager.isDebug())drawDebug();
	}
	
	private void drawDebug()
	{
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1, 0, 0, 1f);
		shapeRenderer.rect(fireRT.x, fireRT.y, fireRT.width, fireRT.height);
		shapeRenderer.rect(jumpRT.x, jumpRT.y, jumpRT.width, jumpRT.height);
		
		shapeRenderer.end();
	}

    private String formatPointsString(int points)
    {
        if(pointsText != null && this.points == points)
		{
			return pointsText;
		}
		else
		{
			this.points = points;
			String pointsPrefix = "Points ";
			String pointsString = points + "";
			int zeroCount = 8 - pointsString.length();
			for (int i = 0; i < zeroCount; i++)
			{
				pointsPrefix += "0";
			}
			return (pointsText = pointsPrefix + pointsString);
		}
    }

    private void drawPauseOverlay()
	{
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL20.GL_BLEND);

		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 0.5f);
		shapeRenderer.rect(0, 0, C_W, C_H);
		shapeRenderer.end();

		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		//pause
		pauseFont.draw(batch, pauseGlyph, C_W * .5f - pauseGlyph.width * .5f, C_H * .8f);

		if(pressedKeys.contains(Key.play))batch.setShader(Shader.GLOW_SHADER);
		batch.draw(play, playR.x, playR.y, playR.width, playR.height);
		batch.setShader(null);

		if(pressedKeys.contains(Key.sound))batch.setShader(Shader.GLOW_SHADER);
		batch.draw((PrefsManager.isPlaySounds() ? soundOn : soundOff), soundR.x, soundR.y, soundR.width, soundR.height);
		batch.setShader(null);

		if(pressedKeys.contains(Key.music))batch.setShader(Shader.GLOW_SHADER);
        batch.draw((PrefsManager.isPlayMusic() ? musicOn : musicOff), musicR.x, musicR.y, musicR.width, musicR.height);
		batch.setShader(null);
		

		batch.end();
	}

    public void leftPressed()
    {
        pressedKeys.add(Key.left);
    }

    public void leftReleased()
    {
        pressedKeys.remove(Key.left);
    }

    public void rightPressed()
    {
        pressedKeys.add(Key.right);
    }

    public void rightReleased()
    {
        pressedKeys.remove(Key.right);
    }

    public void upPressed()
    {
        pressedKeys.add(Key.up);
    }

    public void upReleased()
    {
        pressedKeys.remove(Key.up);
    }

    public void downPressed()
    {
        pressedKeys.add(Key.down);
    }

    public void downReleased()
    {
        pressedKeys.remove(Key.down);
    }

    public void firePressed()
    {
        pressedKeys.add(Key.fire);
    }

    public void fireReleased()
    {
        pressedKeys.remove(Key.fire);
    }

    public void jumpPressed()
    {
        pressedKeys.add(Key.jump);
    }

    public void jumpReleased()
    {
        pressedKeys.remove(Key.jump);
    }

    public void pausePressed()
    {
        pressedKeys.add(Key.pause);
    }

    public void pauseReleased()
    {
        pressedKeys.remove(Key.pause);
    }
	
	public void playPressed()
    {
        pressedKeys.add(Key.play);
    }

    public void playReleased()
    {
        pressedKeys.remove(Key.play);
    }
	
	public void soundPressed()
    {
        pressedKeys.add(Key.sound);
    }

    public void soundReleased()
    {
        pressedKeys.remove(Key.sound);
    }
	
	public void musicPressed()
    {
        pressedKeys.add(Key.music);
    }

    public void musicReleased()
    {
        pressedKeys.remove(Key.music);
    }

	public void dispose() {
		stage.dispose();
	}

	public static class BoxTextPopup
	{
		BitmapFont font;
		GlyphLayout glyphLayout;
		float x, y, w, h;
		public float scrollY, touchDownY;
		public boolean showing;
		TextureRegion back;
		String text;

		Rectangle scissors;
		Rectangle clipBounds;

		public BoxTextPopup(BitmapFont font, Assets assets)
		{
			this.font = font;
			glyphLayout = new GlyphLayout();
			TextureAtlas atlas = assets.manager.get("data/hud/SMCLook512.pack");
			back = atlas.findRegion("ClientBrush");
			scissors = new Rectangle();
			clipBounds = new Rectangle();
		}

		public void render(SpriteBatch batch, OrthographicCamera camera)
		{
			if(!showing)return;

			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
			ScissorStack.pushScissors(scissors);

			batch.draw(back, x, y, w, h);
			glyphLayout.setText(font, text, Color.WHITE, w - 20, Align.left, true);
			font.draw(batch, glyphLayout, x + 10, y + h - 10 - scrollY);

			batch.end();
			ScissorStack.popScissors();
		}

		public void show(Box box, GameScreen gameScreen)
		{
			scrollY = 0;
			showing = true;

			Vector2 point = World.VECTOR2_POOL.obtain();
			Utility.gamePositionToGuiPosition(box.mDrawRect.x + box.mDrawRect.width / 2,
					box.mDrawRect.y + box.mDrawRect.height * 1.1f, gameScreen, point);

			//set initial x, y, w, h
			w = gameScreen.hud.cam.viewportWidth * 0.33f;
			h = w / 1.6f;//16x10
			x = point.x - w / 2;
			y = point.y;

			if(x < 0)
			{
				x = 10;
			}
			if(y + h > gameScreen.hud.cam.viewportHeight)
			{
				//since by default popup is above box, and we don't want to cover box, we must first move it on x
				float boxWidth = Utility.gameWidthToGuiWidth(gameScreen, box.mDrawRect.width);
				x = point.x + boxWidth;
				y = gameScreen.hud.cam.viewportHeight - 10 - h;
			}
			if(x + w > gameScreen.hud.cam.viewportWidth)
			{
				float boxWidth = Utility.gameWidthToGuiWidth(gameScreen, box.mDrawRect.width);
				x = point.x - w - boxWidth;
			}
			clipBounds.set(x, y, w, h);

			World.VECTOR2_POOL.free(point);
			text = box.text;
		}

		public void hide()
		{
			showing = false;
		}

		public void dispose()
		{
			font.dispose();
			font = null;
			back = null;
		}

		public boolean onTouchDown(int x, float y, int pointer, int button)
		{
			if(clipBounds.contains(x, y))
			{
				touchDownY = scrollY + y;
				return true;
			}
			return false;
		}

		public boolean onTouchUp(int x, float y, int pointer, int button)
		{
			touchDownY = 0;
			return false;
		}

		public boolean onTouchDragged(int x, float y, int pointer)
		{
			if(touchDownY > 0 || clipBounds.contains(x, y))
			{
				float newScrollY = touchDownY - y;
				if(newScrollY > 0)
				{
					newScrollY = 0;
					touchDownY = y;
				}

				if(h - glyphLayout.height > newScrollY)
				{
					newScrollY = h - glyphLayout.height;
				}

				scrollY = newScrollY;
				return true;
			}
			return false;
		}
	}
}

package vnp.com.activity;

import java.util.Iterator;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Display;
import android.view.KeyEvent;

import com.example.game.object.BGGame;
import com.example.game.object.Fail;
import com.example.game.object.FontObject;
import com.example.game.object.FontTimeObject;
import com.example.game.object.Pause;
import com.example.game.object.Player;
import com.example.game.object.RunningCat;
import com.example.game.object.Win;
import com.example.game.object.music.BackgroundMusic;
import com.example.game.utils.GAMEMODESTATUS;
import com.example.game.utils.TargetAndProjectileManager;
import com.example.game.utils.TargetAndProjectileManager.CharactorOfTargetAndProjectile;

public class GameActivity extends BaseGameActivity implements IOnSceneTouchListener {
	private GAMEMODESTATUS gamemodestatus = GAMEMODESTATUS.RUN;
	private BackgroundMusic backgroundMusic = new BackgroundMusic();
	private long time = 10;
	private TargetAndProjectileManager targetAndProjectileManager = new TargetAndProjectileManager() {

		@Override
		public void removeSprite(Sprite _sprite, Iterator<CharactorOfTargetAndProjectile> it) {
			GameActivity.this.removeSprite(_sprite, it);
		}

		@Override
		public void updateHitCount(int hitCount) {
			fontObject.getScore().setText(String.valueOf(hitCount));
		}

		@Override
		public void checkWin(int hitCount) {
			if (hitCount >= 10) {
				// gamemodestatus = GAMEMODESTATUS.WIN;
				// updateMode();
			}

		}

	};
	private FontObject fontObject = new FontObject();
	private FontTimeObject fontTimeObject = new FontTimeObject();
	private Scene mainScene = new Scene();
	private Player player = new Player();
	private BGGame bgGame = new BGGame();
	private Pause pause = new Pause();
	private Win win = new Win();
	private Fail fail = new Fail();
	private RunningCat runningCat = new RunningCat();
	private Camera mCamera = new Camera(0, 0, 960, 640);

	@Override
	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		int cameraWidth = display.getWidth();
		int cameraHeight = display.getHeight();
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera).setNeedsSound(true).setNeedsMusic(true));
	}

	@Override
	public void onLoadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		bgGame.onLoadResources(this, getBitmapTextureAtlas());
		player.onLoadResources(this, getBitmapTextureAtlas(), "player_01.png", 0, 64);
		pause.onLoadResources(this, getBitmapTextureAtlas(), "paused.png", 0, 128);
		win.onLoadResources(this, getBitmapTextureAtlas(), "win.png", 0, 256);
		fail.onLoadResources(this, getBitmapTextureAtlas(), "fail.png", 0, 512);

		BitmapTextureAtlas atlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		runningCat.onCreateResources(this, atlas);
		targetAndProjectileManager.onLoadResources(mEngine, this, getBitmapTextureAtlas());

		fontObject.onLoadResources(mEngine);
		fontTimeObject.onLoadResources(mEngine);
		mEngine.getTextureManager().loadTexture(getBitmapTextureAtlas());
		mEngine.getTextureManager().loadTexture(atlas);
		backgroundMusic.onLoadResources(mEngine, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Scene onLoadScene() {
		/**
		 * create main scene
		 */
		mainScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		mainScene.setOnSceneTouchListener(this);
		mEngine.registerUpdateHandler(new FPSLogger());
		bgGame.onLoadScene(mCamera, null);
		player.onLoadScene(mCamera, null);
		targetAndProjectileManager.onLoadScene(mCamera);
		// target.onLoadScene(mCamera, null);
		// projectile.onLoadScene(mCamera, null);
		pause.onLoadScene(mCamera, null);
		win.onLoadScene(mCamera, null);
		fail.onLoadScene(mCamera, null);
		/**
		 * add font
		 */
		fontObject.onLoadScene(mCamera, 100);
		fontObject.getScore().setText(String.valueOf(0));

		fontTimeObject.onLoadScene(mCamera, 1000);
		runningCat.onCreateScene(mainScene);
		createSpriteSpawnTimeHandler();
		mainScene.registerUpdateHandler(detect);

		return mainScene;
	}

	@Override
	public void onLoadComplete() {
		bgGame.attachChild(mainScene, 0);
		player.attachChild(mainScene, 0);
		fontObject.attachChild(mainScene);
		fontTimeObject.attachChild(mainScene);
		updateTime();
		pause.attachChild(mainScene, 2);
		pause.setVisible(false);
		win.attachChild(mainScene, 2);
		win.setVisible(false);
		fail.attachChild(mainScene, 2);
		fail.setVisible(false);

		runningCat.onloadSucess(mainScene);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}

	public void removeSprite(final Sprite _sprite, Iterator<CharactorOfTargetAndProjectile> it) {
		runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				mainScene.detachChild(_sprite);
			}
		});
		it.remove();
	}

	private IUpdateHandler detect = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			targetAndProjectileManager.onUpdate(mCamera);
		}
	};

	private void createSpriteSpawnTimeHandler() {
		float mEffectSpawnDelay = 1f;

		TimerHandler spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (gamemodestatus == GAMEMODESTATUS.RUN) {
					targetAndProjectileManager.addTarget(mCamera, mainScene);
					time--;
					updateTime();

					if (time == 0) {
						gamemodestatus = GAMEMODESTATUS.FAIL;
						updateMode();
					}
				}
			}
		});

		getEngine().registerUpdateHandler(spriteTimerHandler);
	}

	private void updateTime() {
		fontTimeObject.getScore().setText(String.valueOf("Time : " + time));
	}

	@Override
	public boolean onSceneTouchEvent(Scene arg0, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN && gamemodestatus == GAMEMODESTATUS.RUN) {
			targetAndProjectileManager.shootProjectile(player.getSprite(), mainScene, mCamera, pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			return true;
		} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN && gamemodestatus == GAMEMODESTATUS.PAUSE) {
			gamemodestatus = GAMEMODESTATUS.RUN;
			updateMode();
		}

		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && gamemodestatus == GAMEMODESTATUS.RUN) {
			gamemodestatus = GAMEMODESTATUS.PAUSE;
			updateMode();
			return false;
		} else if (gamemodestatus == GAMEMODESTATUS.PAUSE) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void updateMode() {
		pause.setVisible(false);
		win.setVisible(false);
		fail.setVisible(false);
		if (gamemodestatus == GAMEMODESTATUS.PAUSE) {
			pause.setVisible(true);
			targetAndProjectileManager.registerOrUnRegisterMove(false);
		} else if (gamemodestatus == GAMEMODESTATUS.WIN) {
			win.setVisible(true);
		} else if (gamemodestatus == GAMEMODESTATUS.FAIL) {
			fail.setVisible(true);
		} else if (gamemodestatus == GAMEMODESTATUS.RUN) {
			targetAndProjectileManager.registerOrUnRegisterMove(true);
		}
	}

	@Override
	public void onPauseGame() {
		if (gamemodestatus == GAMEMODESTATUS.RUN) {
			gamemodestatus = GAMEMODESTATUS.PAUSE;
			updateMode();
			// backgroundMusic.pause();
		}

		super.onPauseGame();
	}

	private BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

	public BitmapTextureAtlas getBitmapTextureAtlas() {
		return bitmapTextureAtlas;
	}
}
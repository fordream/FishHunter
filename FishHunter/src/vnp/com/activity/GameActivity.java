package vnp.com.activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Display;
import android.view.KeyEvent;

import com.example.game.object.Fail;
import com.example.game.object.FontObject;
import com.example.game.object.FontTimeObject;
import com.example.game.object.Pause;
import com.example.game.object.Player;
import com.example.game.object.Projectile;
import com.example.game.object.Target;
import com.example.game.object.Win;
import com.example.game.utils.GAMEMODESTATUS;

public class GameActivity extends BaseGameActivity implements IOnSceneTouchListener {
	private GAMEMODESTATUS gamemodestatus = GAMEMODESTATUS.RUN;
	private int hitCount = 0;
	private long time = 60;
	private LinkedList<Sprite> projectilesToBeAdded = new LinkedList<Sprite>();
	private LinkedList<Sprite> TargetsToBeAdded = new LinkedList<Sprite>();

	private LinkedList<Sprite> projectileLL = new LinkedList<Sprite>();
	private LinkedList<Sprite> targetLL = new LinkedList<Sprite>();

	private FontObject fontObject = new FontObject();
	private FontTimeObject fontTimeObject = new FontTimeObject();
	private Scene mainScene = new Scene();
	private Projectile projectile = new Projectile();
	private Player player = new Player();
	private Target target = new Target();
	private Pause pause = new Pause();
	private Win win = new Win();
	private Fail fail = new Fail();
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
		player.onLoadResources(this, getBitmapTextureAtlas());
		target.onLoadResources(this, getBitmapTextureAtlas());
		projectile.onLoadResources(this, getBitmapTextureAtlas());

		pause.onLoadResources(this, getBitmapTextureAtlas());
		win.onLoadResources(this, getBitmapTextureAtlas());
		fail.onLoadResources(this, getBitmapTextureAtlas());

		fontObject.onLoadResources(mEngine);
		fontTimeObject.onLoadResources(mEngine);
		mEngine.getTextureManager().loadTexture(getBitmapTextureAtlas());
	}

	@Override
	public Scene onLoadScene() {
		/**
		 * create main scene
		 */
		// if (mainScene == null) {
		mainScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		mainScene.setOnSceneTouchListener(this);
		mEngine.registerUpdateHandler(new FPSLogger());
		// }

		player.onLoadScene(mCamera, null);
		target.onLoadScene(mCamera, null);
		projectile.onLoadScene(mCamera, null);
		pause.onLoadScene(mCamera, null);
		win.onLoadScene(mCamera, null);
		fail.onLoadScene(mCamera, null);
		/**
		 * add font
		 */
		fontObject.onLoadScene(mCamera, 100);
		fontObject.getScore().setText(String.valueOf(0));

		fontTimeObject.onLoadScene(mCamera, 1000);

		// startGame();
		createSpriteSpawnTimeHandler();
		mainScene.registerUpdateHandler(detect);
		return mainScene;
	}

	public void removeSprite(final Sprite _sprite, Iterator<Sprite> it) {
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

			Iterator<Sprite> targets = targetLL.iterator();
			Sprite _target = null;
			boolean hit = false;

			while (targets.hasNext()) {
				_target = targets.next();

				if (_target.getX() <= -_target.getWidth()) {
					removeSprite(_target, targets);
					// gameMode(GAMEMODESTATUS.FAIL);
					break;
				}
				Iterator<Sprite> projectiles = projectileLL.iterator();
				Sprite _projectile;
				while (projectiles.hasNext()) {
					_projectile = projectiles.next();

					if (_projectile.getX() >= mCamera.getWidth() || _projectile.getY() >= mCamera.getHeight() + _projectile.getHeight() || _projectile.getY() <= -_projectile.getHeight()) {
						removeSprite(_projectile, projectiles);
						continue;
					}

					if (_target.collidesWith(_projectile)) {
						removeSprite(_projectile, projectiles);
						hit = true;
						break;
					}
				}

				if (hit) {
					removeSprite(_target, targets);
					hit = false;
					hitCount++;
					fontObject.getScore().setText(String.valueOf(hitCount));
				}
			}

			if (hitCount >= 10) {
				gamemodestatus = GAMEMODESTATUS.WIN;
				updateMode();
				// gameMode(GAMEMODESTATUS.WIN);
			}

			projectileLL.addAll(projectilesToBeAdded);
			projectilesToBeAdded.clear();

			targetLL.addAll(TargetsToBeAdded);
			TargetsToBeAdded.clear();
		}
	};

	private void createSpriteSpawnTimeHandler() {
		float mEffectSpawnDelay = 1f;

		TimerHandler spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if (gamemodestatus == GAMEMODESTATUS.RUN) {
					addTarget();
					// FIXME
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

	public void addTarget() {
		Random rand = new Random();
		TextureRegion xtarget = target.getTextureRegion();
		int x = (int) mCamera.getWidth() + xtarget.getWidth();
		int minY = xtarget.getHeight();
		int maxY = (int) (mCamera.getHeight() - xtarget.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		Sprite target = new Sprite(x, y, this.target.getTextureRegion().deepCopy());
		mainScene.attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(), -target.getWidth());
		target.registerEntityModifier(mod.deepCopy());

		TargetsToBeAdded.add(target);
	}

	private void shootProjectile(final float pX, final float pY) {
		Sprite player = this.player.getSprite();
		int offX = (int) (pX - player.getX());
		int offY = (int) (pY - player.getY());
		if (offX <= 0) {
		}

		Sprite projectile = new Sprite(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight(), this.projectile.getTextureRegion().deepCopy());
		mainScene.attachChild(projectile, 1);

		int realX = (int) (mCamera.getWidth() + projectile.getWidth() / 2.0f);
		float ratio = (float) offY / (float) offX;
		int realY = (int) ((realX * ratio) + projectile.getY());

		int offRealX = (int) (realX - projectile.getX());
		int offRealY = (int) (realY - projectile.getY());
		float length = (float) Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;

		MoveModifier mod = new MoveModifier(realMoveDuration, projectile.getX(), realX, projectile.getY(), realY);
		projectile.registerEntityModifier(mod.deepCopy());
		projectilesToBeAdded.add(projectile);
		// getMusic(EnumMusic.SHOOTINGSHOUND).play();
	}

	@Override
	public void onLoadComplete() {
		player.attachChild(mainScene, 0);
		fontObject.attachChild(mainScene);
		fontTimeObject.attachChild(mainScene);
		updateTime();
		pause.attachChild(mainScene, 1);
		pause.setVisible(false);
		win.attachChild(mainScene, 1);
		win.setVisible(false);
		fail.attachChild(mainScene, 1);
		fail.setVisible(false);
	}

	@Override
	public boolean onSceneTouchEvent(Scene arg0, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN && gamemodestatus == GAMEMODESTATUS.RUN) {
			shootProjectile(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
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
		} else if (gamemodestatus == GAMEMODESTATUS.WIN) {
			win.setVisible(true);
		} else if (gamemodestatus == GAMEMODESTATUS.FAIL) {
			fail.setVisible(true);
		}
	}

	private BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);;

	public BitmapTextureAtlas getBitmapTextureAtlas() {
		return bitmapTextureAtlas;
	}
}
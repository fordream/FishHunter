package com.example.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.game.object.music.BackgroundMusic;
import com.example.game.utils.GAMEMODESTATUS;

public class AndEngineSimpleGame extends BaseMGameActivty implements IOnSceneTouchListener {
	/**
 * 
 */
	private BackgroundMusic backgroundMusic = new BackgroundMusic();
	// private Camera mCamera;

	// This one is for the font
	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private ChangeableText score;

	// this one is for all other textures
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TextureRegion mPlayerTextureRegion;
	private TextureRegion mProjectileTextureRegion;
	private TextureRegion mTargetTextureRegion;
	private TextureRegion mPausedTextureRegion;
	private TextureRegion mWinTextureRegion;
	private TextureRegion mFailTextureRegion;

	// the main scene for the game
	private Scene mMainScene;
	private Sprite player;

	// win/fail sprites
	private Sprite winSprite;
	private Sprite failSprite;

	private LinkedList<Sprite> projectileLL;
	private LinkedList<Sprite> targetLL;
	private LinkedList<Sprite> projectilesToBeAdded;
	private LinkedList<Sprite> TargetsToBeAdded;
	private Sound shootingSound;
	// private Music backgroundMusic;
	private boolean runningFlag = false;
	private boolean pauseFlag = false;
	private CameraScene mPauseScene;
	private CameraScene mResultScene;
	private int hitCount;
	private final int maxScore = 10;

	@Override
	public void onLoadResources() {
		// prepare a container for the image
		mBitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// prepare a container for the font
		mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		// setting assets path for easy access
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// loading the image inside the container
		mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "player_01.png", 0, 0);

		mProjectileTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Projectile_01.png", 64, 0);

		mTargetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "target_01.png", 128, 0);

		mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "paused.png", 0, 64);

		mWinTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "win.png", 0, 128);

		mFailTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "fail.png", 0, 256);

		// preparing the font
		mFont = new Font(mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK);

		// loading textures in the engine
		mEngine.getTextureManager().loadTexture(mBitmapTextureAtlas);
		mEngine.getTextureManager().loadTexture(mFontTexture);
		mEngine.getFontManager().loadFont(mFont);

		SoundFactory.setAssetBasePath("mfx/");
		try {
			shootingSound = SoundFactory.createSoundFromAsset(mEngine.getSoundManager(), this, "pew_pew_lei.wav");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		backgroundMusic.onLoadResources(mEngine, this);

	}

	@Override
	public Scene onLoadScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		// creating a new scene for the pause menu
		mPauseScene = new CameraScene(getmCamera());
		/* Make the label centered on the camera. */
		final int x = (int) (getmCamera().getWidth() / 2 - mPausedTextureRegion.getWidth() / 2);
		final int y = (int) (getmCamera().getHeight() / 2 - mPausedTextureRegion.getHeight() / 2);
		final Sprite pausedSprite = new Sprite(x, y, mPausedTextureRegion);
		mPauseScene.attachChild(pausedSprite);
		// makes the scene transparent
		mPauseScene.setBackgroundEnabled(false);

		// the results scene, for win/fail
		mResultScene = new CameraScene(getmCamera());
		winSprite = new Sprite(x, y, mWinTextureRegion);
		failSprite = new Sprite(x, y, mFailTextureRegion);
		mResultScene.attachChild(winSprite);
		mResultScene.attachChild(failSprite);
		// makes the scene transparent
		mResultScene.setBackgroundEnabled(false);

		winSprite.setVisible(false);
		failSprite.setVisible(false);

		// set background color
		mMainScene = new Scene();
		mMainScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		mMainScene.setOnSceneTouchListener(this);

		// set coordinates for the player
		final int PlayerX = (int) (getmCamera().getWidth() - mPlayerTextureRegion.getWidth()) / 2;
		final int PlayerY = (int) ((getmCamera().getHeight() - mPlayerTextureRegion.getHeight()));
		// set the player on the scene
		player = new Sprite(PlayerX, PlayerY, mPlayerTextureRegion);
		// player.setScale(2);

		// initializing variables
		projectileLL = new LinkedList<Sprite>();
		targetLL = new LinkedList<Sprite>();
		projectilesToBeAdded = new LinkedList<Sprite>();
		TargetsToBeAdded = new LinkedList<Sprite>();

		// settings score to the value of the max score to make sure it appears
		// correctly on the screen
		score = new ChangeableText(0, 0, mFont, String.valueOf(maxScore));
		// repositioning the score later so we can use the score.getWidth()
		score.setPosition(getmCamera().getWidth() - score.getWidth() - 5, 5);

		createSpriteSpawnTimeHandler();
		mMainScene.registerUpdateHandler(detect);

		// starting background music
		backgroundMusic.play();
		// runningFlag = true;

		restart();
		return mMainScene;
	}

	@Override
	public void onLoadComplete() {
	}

	// TimerHandler for collision detection and cleaning up
	IUpdateHandler detect = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {

			Iterator<Sprite> targets = targetLL.iterator();
			Sprite _target;
			boolean hit = false;

			// iterating over the targets
			while (targets.hasNext()) {
				_target = targets.next();

				// if target passed the left edge of the screen, then remove it
				// and call a fail
				if (_target.getX() <= -_target.getWidth()) {
					removeSprite(_target, targets);
					// fail();
					gameMode(GAMEMODESTATUS.FAIL);
					break;
				}
				Iterator<Sprite> projectiles = projectileLL.iterator();
				Sprite _projectile;
				// iterating over all the projectiles (bullets)
				while (projectiles.hasNext()) {
					_projectile = projectiles.next();

					// in case the projectile left the screen
					if (_projectile.getX() >= getmCamera().getWidth() || _projectile.getY() >= getmCamera().getHeight() + _projectile.getHeight() || _projectile.getY() <= -_projectile.getHeight()) {
						removeSprite(_projectile, projectiles);
						continue;
					}

					// if the targets collides with a projectile, remove the
					// projectile and set the hit flag to true
					if (_target.collidesWith(_projectile)) {
						removeSprite(_projectile, projectiles);
						hit = true;
						break;
					}
				}

				// if a projectile hit the target, remove the target, increment
				// the hit count, and update the score
				if (hit) {
					removeSprite(_target, targets);
					hit = false;
					hitCount++;
					score.setText(String.valueOf(hitCount));
				}
			}

			// if max score , then we are done
			if (hitCount >= maxScore) {
				gameMode(GAMEMODESTATUS.WIN);
				// win();
			}

			// a work around to avoid ConcurrentAccessException
			projectileLL.addAll(projectilesToBeAdded);
			projectilesToBeAdded.clear();

			targetLL.addAll(TargetsToBeAdded);
			TargetsToBeAdded.clear();
		}
	};

	/* safely detach the sprite from the scene and remove it from the iterator */
	public void removeSprite(final Sprite _sprite, Iterator<Sprite> it) {
		runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				mMainScene.detachChild(_sprite);
			}
		});
		it.remove();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		// if the user tapped the screen
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			final float touchX = pSceneTouchEvent.getX();
			final float touchY = pSceneTouchEvent.getY();
			shootProjectile(touchX, touchY);
			return true;
		}
		return false;
	}

	/* shoots a projectile from the player's position along the touched area */
	private void shootProjectile(final float pX, final float pY) {

		int offX = (int) (pX - player.getX());
		int offY = (int) (pY - player.getY());
		if (offX <= 0) {
			// FIXME
			// return;
		}

		// position the projectile on the player
		Sprite projectile = new Sprite(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight(), mProjectileTextureRegion.deepCopy());
		mMainScene.attachChild(projectile, 1);

		int realX = (int) (getmCamera().getWidth() + projectile.getWidth() / 2.0f);
		float ratio = (float) offY / (float) offX;
		int realY = (int) ((realX * ratio) + projectile.getY());

		int offRealX = (int) (realX - projectile.getX());
		int offRealY = (int) (realY - projectile.getY());
		float length = (float) Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;

		// defining a move modifier from the projectile's position to the
		// calculated one
		MoveModifier mod = new MoveModifier(realMoveDuration, projectile.getX(), realX, projectile.getY(), realY);
		projectile.registerEntityModifier(mod.deepCopy());

		projectilesToBeAdded.add(projectile);
		// plays a shooting sound
		shootingSound.play();
	}

	// adds a target at a random location and let it move along the x-axis
	public void addTarget() {
		Random rand = new Random();

		int x = (int) getmCamera().getWidth() + mTargetTextureRegion.getWidth();
		int minY = mTargetTextureRegion.getHeight();
		int maxY = (int) (getmCamera().getHeight() - mTargetTextureRegion.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		Sprite target = new Sprite(x, y, mTargetTextureRegion.deepCopy());
		mMainScene.attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(), -target.getWidth());
		target.registerEntityModifier(mod.deepCopy());

		TargetsToBeAdded.add(target);

	}

	// a Time Handler for spawning targets, triggers every 1 second
	private void createSpriteSpawnTimeHandler() {
		TimerHandler spriteTimerHandler;
		float mEffectSpawnDelay = 1f;

		spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {

				addTarget();
			}
		});

		getEngine().registerUpdateHandler(spriteTimerHandler);
	}

	/* to restart the game and clear the whole screen */
	public void restart() {

		runOnUpdateThread(new Runnable() {

			@Override
			// to safely detach and re-attach the sprites
			public void run() {
				mMainScene.detachChildren();
				mMainScene.attachChild(player, 0);
				mMainScene.attachChild(score);
			}
		});

		// resetting everything
		hitCount = 0;
		score.setText(String.valueOf(hitCount));
		projectileLL.clear();
		projectilesToBeAdded.clear();
		TargetsToBeAdded.clear();
		targetLL.clear();
	}

	@Override
	// pauses the music and the game when the game goes to the background
	protected void onPause() {
		if (runningFlag) {
			pauseMusic();
			if (mEngine.isRunning()) {
				gameMode(GAMEMODESTATUS.PAUSE);
				pauseFlag = true;
			}
		}
		super.onPause();
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		// shows this Toast when coming back to the game
		if (runningFlag) {
			if (pauseFlag) {
				pauseFlag = false;
				Toast.makeText(this, "Menu button to resume", Toast.LENGTH_SHORT).show();
			} else {
				// in case the user clicks the home button while the game on the
				// resultScene
				resumeMusic();
				mEngine.stop();
			}
		} else {
			runningFlag = true;
		}
	}

	public void gameMode(GAMEMODESTATUS gamemode) {
		if (gamemode == GAMEMODESTATUS.FAIL && mEngine.isRunning()) {
			// FIXME

			// winSprite.setVisible(false);
			// failSprite.setVisible(true);
			// mMainScene.setChildScene(mResultScene, false, true, true);
			// mEngine.stop();
		} else if (gamemode == GAMEMODESTATUS.WIN && mEngine.isRunning()) {
			failSprite.setVisible(false);
			winSprite.setVisible(true);
			mMainScene.setChildScene(mResultScene, false, true, true);
			mEngine.stop();
		} else if (gamemode == GAMEMODESTATUS.PAUSE && runningFlag) {
			mMainScene.setChildScene(mPauseScene, false, true, true);
			mEngine.stop();
		} else if (gamemode == GAMEMODESTATUS.UNPAUSE) {
			mMainScene.clearChildScene();
		}
	}

	public void pauseMusic() {
		if (runningFlag)
			backgroundMusic.pause();
	}

	public void resumeMusic() {
		if (runningFlag)
			backgroundMusic.resume();
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		// if menu button is pressed
		if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (mEngine.isRunning() && backgroundMusic.isPlaying()) {
				pauseMusic();
				pauseFlag = true;
				gameMode(GAMEMODESTATUS.PAUSE);
				Toast.makeText(this, "Menu button to resume", Toast.LENGTH_SHORT).show();
			} else {
				if (!backgroundMusic.isPlaying()) {
					gameMode(GAMEMODESTATUS.UNPAUSE);
					pauseFlag = false;
					resumeMusic();
					mEngine.start();
				}
				return true;
			}
			// if back key was pressed
		} else if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {

			if (!mEngine.isRunning() && backgroundMusic.isPlaying()) {
				mMainScene.clearChildScene();
				mEngine.start();
				restart();
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
}

// for time handler
// http://www.andengine.org/forums/tutorials/using-timer-s-sprite-spawn-example-t463.html
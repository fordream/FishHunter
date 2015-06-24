package com.example.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.example.game.object.Fail;
import com.example.game.object.Pause;
import com.example.game.object.Player;
import com.example.game.object.Projectile;
import com.example.game.object.Target;
import com.example.game.object.Win;
import com.example.game.object.music.BackgroundMusic;
import com.example.game.object.music.EnumMusic;
import com.example.game.object.music.ShootingSound;
import com.example.game.utils.GAMEMODESTATUS;

public class AndEngineSimpleGame extends BaseMGameActivty {

	private BitmapTextureAtlas mFontTexture;
	private Font mFont;
	private ChangeableText score;

	private LinkedList<Sprite> projectileLL;
	private LinkedList<Sprite> targetLL;
	private LinkedList<Sprite> projectilesToBeAdded = new LinkedList<Sprite>();
	private LinkedList<Sprite> TargetsToBeAdded = new LinkedList<Sprite>();
	private boolean runningFlag = false;
	private boolean pauseFlag = false;
	private CameraScene mPauseScene;
	private CameraScene mResultScene;
	private int hitCount;
	private final int maxScore = 10;

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		/**
		 * add sound and music
		 */
		addSounds(EnumMusic.BACKGROUNDMISIC, new BackgroundMusic());
		addSounds(EnumMusic.SHOOTINGSHOUND, new ShootingSound());

		/**
		 * charactor
		 */
		addCharactors("player", new Player());
		addCharactors("projectile", new Projectile());
		addCharactors("target", new Target());

		addCharactors("win", new Win());
		addCharactors("fail", new Fail());
		addCharactors("pause", new Pause());
	}

	@Override
	public void onLoadResources() {
		super.onLoadResources();

		mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mFont = new Font(mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, true, Color.BLACK);
		mEngine.getTextureManager().loadTexture(mFontTexture);
		mEngine.getFontManager().loadFont(mFont);

	}

	@Override
	public Scene onLoadScene() {
		Scene mMainScene = super.onLoadScene();
		getCharactor("player").onLoadScene(getmCamera(), null);

		mPauseScene = new CameraScene(getmCamera());
		mPauseScene.setBackgroundEnabled(false);
		getCharactor("pause").onLoadScene(getmCamera(), mPauseScene);

		mResultScene = new CameraScene(getmCamera());
		mResultScene.setBackgroundEnabled(false);
		getCharactor("win").onLoadScene(getmCamera(), mResultScene);
		getCharactor("fail").onLoadScene(getmCamera(), mResultScene);

		projectileLL = new LinkedList<Sprite>();
		targetLL = new LinkedList<Sprite>();
		projectilesToBeAdded = new LinkedList<Sprite>();
		TargetsToBeAdded = new LinkedList<Sprite>();

		score = new ChangeableText(0, 0, mFont, String.valueOf(maxScore));
		score.setPosition(getmCamera().getWidth() - score.getWidth() - 5, 5);

		createSpriteSpawnTimeHandler();
		mMainScene.registerUpdateHandler(detect);
		getMusic(EnumMusic.BACKGROUNDMISIC).play();
		restart();
		return mMainScene;
	}

	@Override
	public void onLoadComplete() {
	}

	IUpdateHandler detect = new IUpdateHandler() {
		@Override
		public void reset() {
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {

			Iterator<Sprite> targets = targetLL.iterator();
			Sprite _target;
			boolean hit = false;

			while (targets.hasNext()) {
				_target = targets.next();

				if (_target.getX() <= -_target.getWidth()) {
					removeSprite(_target, targets);
					gameMode(GAMEMODESTATUS.FAIL);
					break;
				}
				Iterator<Sprite> projectiles = projectileLL.iterator();
				Sprite _projectile;
				while (projectiles.hasNext()) {
					_projectile = projectiles.next();

					if (_projectile.getX() >= getmCamera().getWidth() || _projectile.getY() >= getmCamera().getHeight() + _projectile.getHeight() || _projectile.getY() <= -_projectile.getHeight()) {
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
					score.setText(String.valueOf(hitCount));
				}
			}

			if (hitCount >= maxScore) {
				gameMode(GAMEMODESTATUS.WIN);
			}

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
				getmMainScene().detachChild(_sprite);
			}
		});
		it.remove();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			final float touchX = pSceneTouchEvent.getX();
			final float touchY = pSceneTouchEvent.getY();
			shootProjectile(touchX, touchY);
			return true;
		}
		return false;
	}

	private void shootProjectile(final float pX, final float pY) {
		Sprite player = getCharactor("player").getSprite();
		int offX = (int) (pX - player.getX());
		int offY = (int) (pY - player.getY());
		if (offX <= 0) {
		}

		Sprite projectile = new Sprite(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight(), getCharactor("projectile").getTextureRegion().deepCopy());
		getmMainScene().attachChild(projectile, 1);

		int realX = (int) (getmCamera().getWidth() + projectile.getWidth() / 2.0f);
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
		getMusic(EnumMusic.SHOOTINGSHOUND).play();
	}

	public void addTarget() {
		Random rand = new Random();
		TextureRegion xtarget = getCharactor("target").getTextureRegion();
		int x = (int) getmCamera().getWidth() + xtarget.getWidth();
		int minY = xtarget.getHeight();
		int maxY = (int) (getmCamera().getHeight() - xtarget.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		Sprite target = new Sprite(x, y, getCharactor("target").getTextureRegion().deepCopy());
		getmMainScene().attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(), -target.getWidth());
		target.registerEntityModifier(mod.deepCopy());

		TargetsToBeAdded.add(target);

	}

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

	public void restart() {

		runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				getmMainScene().detachChildren();
				getCharactor("player").restart(getmMainScene());
				// mMainScene.attachChild(player, 1);
				getmMainScene().attachChild(score);
			}
		});

		hitCount = 0;
		score.setText(String.valueOf(hitCount));
		projectileLL.clear();
		projectilesToBeAdded.clear();
		TargetsToBeAdded.clear();
		targetLL.clear();
	}

	@Override
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
		if (runningFlag) {
			if (pauseFlag) {
				pauseFlag = false;
				Toast.makeText(this, "Menu button to resume", Toast.LENGTH_SHORT).show();
			} else {
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
			getCharactor("fail").setVisible(false);
			getCharactor("win").setVisible(true);
			getmMainScene().setChildScene(mResultScene, false, true, true);
			mEngine.stop();
		} else if (gamemode == GAMEMODESTATUS.PAUSE && runningFlag) {
			getmMainScene().setChildScene(mPauseScene, false, true, true);
			mEngine.stop();
		} else if (gamemode == GAMEMODESTATUS.UNPAUSE) {
			getmMainScene().clearChildScene();
		}
	}

	public void pauseMusic() {
		if (runningFlag)
			getMusic(EnumMusic.BACKGROUNDMISIC).pause();
	}

	public void resumeMusic() {
		if (runningFlag)
			getMusic(EnumMusic.BACKGROUNDMISIC).resume();
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (mEngine.isRunning() && getMusic(EnumMusic.BACKGROUNDMISIC).isPlaying()) {
				pauseMusic();
				pauseFlag = true;
				gameMode(GAMEMODESTATUS.PAUSE);
				Toast.makeText(this, "Menu button to resume", Toast.LENGTH_SHORT).show();
			} else {
				if (!getMusic(EnumMusic.BACKGROUNDMISIC).isPlaying()) {
					gameMode(GAMEMODESTATUS.UNPAUSE);
					pauseFlag = false;
					resumeMusic();
					mEngine.start();
				}
				return true;
			}
		} else if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {

			if (!mEngine.isRunning() && getMusic(EnumMusic.BACKGROUNDMISIC).isPlaying()) {
				getmMainScene().clearChildScene();
				mEngine.start();
				restart();
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
}

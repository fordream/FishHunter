package com.example.game.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.IModifier.IModifierListener;

import android.content.Context;

import com.example.game.object.Projectile;
import com.example.game.object.RunningCat;
import com.example.game.object.Target;
import com.example.game.object.music.ShootingSound;

public abstract class TargetAndProjectileManager {
	public class CharactorOfTargetAndProjectile {

		private int hp = 0;

		public int getHp() {
			return hp;
		}

		public void setHp(int hp) {
			this.hp = hp;
		}

		public int dame = 0;

		public int getDame() {
			return dame;
		}

		public void setDame(int dame) {
			this.dame = dame;
		}

		private AnimatedSprite sprite;
		private MoveXModifier modifier;
		private MoveModifier moveModifier;

		public MoveModifier getMoveModifier() {
			return moveModifier;
		}

		public AnimatedSprite getSprite() {
			return sprite;
		}

		public MoveXModifier getModifier() {
			return modifier;
		}

		public CharactorOfTargetAndProjectile(AnimatedSprite sprite,
				MoveXModifier modifier) {
			this.sprite = sprite;
			this.modifier = modifier;

			randomHpAndDame();
		}

		private void randomHpAndDame() {
			Random random = new Random();
			setHp(random.nextInt(10) + 10);
			setDame(random.nextInt(5) + 1);
		}

		public CharactorOfTargetAndProjectile(AnimatedSprite sprite,
				MoveModifier mod) {
			this.sprite = sprite;
			moveModifier = mod;
			randomHpAndDame();
		}
	}

	private ShootingSound shootingSound = new ShootingSound();
	private LinkedList<CharactorOfTargetAndProjectile> projectilesToBeAdded = new LinkedList<CharactorOfTargetAndProjectile>();
	private LinkedList<CharactorOfTargetAndProjectile> TargetsToBeAdded = new LinkedList<CharactorOfTargetAndProjectile>();
	private LinkedList<CharactorOfTargetAndProjectile> projectileLL = new LinkedList<CharactorOfTargetAndProjectile>();
	private LinkedList<CharactorOfTargetAndProjectile> targetLL = new LinkedList<CharactorOfTargetAndProjectile>();

	public void onLoadResources(Engine mEngine, Context context,
			BitmapTextureAtlas bitmapTextureAtlas) {
		target.onLoadResources(context, bitmapTextureAtlas, "target_01.png",
				128, 0);
		projectile.onLoadResources(context, bitmapTextureAtlas,
				"Projectile_01.png", 256, 0);
		shootingSound.onLoadResources(mEngine, context);
	}

	public void onLoadScene(Camera mCamera) {
		target.onLoadScene(mCamera, null);
		projectile.onLoadScene(mCamera, null);
	}

	private Projectile projectile = new Projectile();
	private Target target = new Target();
	private int hitCount = 0;

	public abstract void removeSprite(final AnimatedSprite _sprite,
			Iterator<CharactorOfTargetAndProjectile> it);

	public abstract void updateHitCount(int hitCount);

	public abstract void checkWin(int hitCount);

	public void onUpdate(Camera mCamera) {
		Iterator<CharactorOfTargetAndProjectile> targets = targetLL.iterator();
		AnimatedSprite _target = null;
		boolean hit = false;

		while (targets.hasNext()) {
			CharactorOfTargetAndProjectile charactorOftargert = targets.next();
			int dame = charactorOftargert.getDame();
			_target = charactorOftargert.getSprite();

			if (_target.getX() <= -_target.getWidth()) {
				removeSprite(_target, targets);
				break;
			}

			Iterator<CharactorOfTargetAndProjectile> projectiles = projectileLL
					.iterator();
			AnimatedSprite _projectile;
			while (projectiles.hasNext()) {
				CharactorOfTargetAndProjectile charactorOfprojectile = projectiles
						.next();
				_projectile = charactorOfprojectile.getSprite();

				if (FishUtils.isOutScreen(mCamera, _projectile)) {
					removeSprite(_projectile, projectiles);
					continue;
				}

				if (_target.collidesWith(_projectile)) {
					charactorOfprojectile.setHp(charactorOfprojectile.getHp()
							- dame);
					if (charactorOfprojectile.getHp() <= 0) {
						removeSprite(_projectile, projectiles);
						hit = true;
					}

					break;
				}
			}

			if (hit) {
				removeSprite(_target, targets);
				hit = false;
				hitCount = hitCount + new Random().nextInt(5) * 3;

				updateHitCount(hitCount);
			}
		}
		checkWin(hitCount);

		projectileLL.addAll(projectilesToBeAdded);
		projectilesToBeAdded.clear();

		targetLL.addAll(TargetsToBeAdded);
		TargetsToBeAdded.clear();
	}

	public void registerOrUnRegisterMove(boolean needRegister) {
		for (CharactorOfTargetAndProjectile s : projectileLL) {
			if (!needRegister) {
				s.getSprite().unregisterEntityModifier(s.getMoveModifier());
			} else {
				s.getSprite().registerEntityModifier(s.getMoveModifier());
			}
		}

		for (CharactorOfTargetAndProjectile s : targetLL) {
			if (!needRegister) {
				s.getSprite().unregisterEntityModifier(s.getModifier());
			} else {
				s.getSprite().registerEntityModifier(s.getModifier());
			}
		}
	}

	public void addTarget(Camera mCamera, Scene mainScene, RunningCat runningCat) {
		Random rand = new Random();
		TextureRegion xtarget = target.getTextureRegion();
		int x = (int) mCamera.getWidth() + xtarget.getWidth();
		int minY = xtarget.getHeight();
		int maxY = (int) (mCamera.getHeight() - xtarget.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		// Sprite target = new Sprite(x, y, this.target.getTextureRegion()
		// .deepCopy());
		AnimatedSprite target = new AnimatedSprite(x, y, runningCat.getRegCat());
		target.animate(300);
		mainScene.attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(),
				-target.getWidth()).deepCopy();
		target.registerEntityModifier(mod);

		CharactorOfTargetAndProjectile charactorOfTargetAndProjectile = new CharactorOfTargetAndProjectile(
				target, mod);
		TargetsToBeAdded.add(charactorOfTargetAndProjectile);
	}

	public void shootProjectile(Sprite player, final Scene mainScene,
			Camera mCamera, final float pX, final float pY,
			RunningCat runningCat) {
		// TextureRegion region = this.projectile.getTextureRegion().deepCopy();

		TiledTextureRegion region = runningCat.getRegCat();
		int playerCenterX = (int) (player.getX() + player.getWidth() / 2);
		int playerCenterY = (int) (player.getY() + player.getHeight() / 2);

		if (playerCenterX == (int) pX && playerCenterY == (int) pY) {
			return;
		}

		int realX = 0;
		int realY = 0;

		if (playerCenterX == (int) pX) {
			realX = playerCenterX;
			realY = (int) ((playerCenterY - (int) pY) < 0 ? -region.getHeight()
					: (mCamera.getHeight() + region.getHeight()));
		} else if (playerCenterY == (int) pY) {
			realY = playerCenterY;
			realX = (int) ((playerCenterX - (int) pX) < 0 ? (mCamera.getWidth() + region
					.getWidth()) : -region.getWidth());
		} else {
			float a = ((float) playerCenterY - pY)
					/ ((float) playerCenterX - pX);
			float b = (float) playerCenterY - a * ((float) playerCenterX);

			if ((pX - (float) playerCenterX) > 0) {
				realX = (int) (mCamera.getWidth() + region.getWidth());
			} else {
				realX = -region.getWidth();
			}

			realY = (int) (a * realX + b);
		}

		// final Sprite projectile = new Sprite(playerCenterX -
		// region.getWidth()
		// / 2, playerCenterY - region.getHeight() / 2, region);

		AnimatedSprite projectile = new AnimatedSprite(playerCenterX
				- region.getWidth() / 2,
				playerCenterY - region.getHeight() / 2, region);
		projectile.animate(300);
		mainScene.attachChild(projectile, 1);

		int offRealX = (int) (realX - projectile.getX());
		int offRealY = (int) (realY - projectile.getY());

		float length = (float) Math.sqrt((offRealX * offRealX)
				+ (offRealY * offRealY));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;

		if (realMoveDuration < 1f) {
			realMoveDuration = 1f;
		}

		MoveModifier mod = new MoveModifier(realMoveDuration,
				projectile.getX(), realX, projectile.getY(), realY).deepCopy();
		final CharactorOfTargetAndProjectile charactorOfTargetAndProjectile = new CharactorOfTargetAndProjectile(
				projectile, mod);
		projectile.registerEntityModifier(mod);

		projectilesToBeAdded.add(charactorOfTargetAndProjectile);
		shootingSound.play();
	}
}
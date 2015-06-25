package com.example.game.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Context;

import com.example.game.object.Projectile;
import com.example.game.object.Target;

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

		private Sprite sprite;
		private MoveXModifier modifier;
		private MoveModifier moveModifier;

		public MoveModifier getMoveModifier() {
			return moveModifier;
		}

		public Sprite getSprite() {
			return sprite;
		}

		public MoveXModifier getModifier() {
			return modifier;
		}

		public CharactorOfTargetAndProjectile(Sprite sprite, MoveXModifier modifier) {
			this.sprite = sprite;
			this.modifier = modifier;

			randomHpAndDame();
		}

		private void randomHpAndDame() {
			Random random = new Random();
			setHp(random.nextInt(10) + 10);
			setDame(random.nextInt(5) + 1);
		}

		public CharactorOfTargetAndProjectile(Sprite sprite, MoveModifier mod) {
			this.sprite = sprite;
			moveModifier = mod;
			randomHpAndDame();
		}
	}

	private LinkedList<CharactorOfTargetAndProjectile> projectilesToBeAdded = new LinkedList<CharactorOfTargetAndProjectile>();
	private LinkedList<CharactorOfTargetAndProjectile> TargetsToBeAdded = new LinkedList<CharactorOfTargetAndProjectile>();

	private LinkedList<CharactorOfTargetAndProjectile> projectileLL = new LinkedList<CharactorOfTargetAndProjectile>();
	private LinkedList<CharactorOfTargetAndProjectile> targetLL = new LinkedList<CharactorOfTargetAndProjectile>();

	public void onLoadResources(Context context, BitmapTextureAtlas bitmapTextureAtlas) {
		target.onLoadResources(context, bitmapTextureAtlas);
		projectile.onLoadResources(context, bitmapTextureAtlas);
	}

	public void onLoadScene(Camera mCamera) {
		target.onLoadScene(mCamera, null);
		projectile.onLoadScene(mCamera, null);
	}

	private Projectile projectile = new Projectile();
	private Target target = new Target();
	private int hitCount = 0;

	public abstract void removeSprite(final Sprite _sprite, Iterator<CharactorOfTargetAndProjectile> it);

	public abstract void updateHitCount(int hitCount);

	public abstract void checkWin(int hitCount);

	public void onUpdate(Camera mCamera) {
		Iterator<CharactorOfTargetAndProjectile> targets = targetLL.iterator();
		Sprite _target = null;
		boolean hit = false;

		while (targets.hasNext()) {
			CharactorOfTargetAndProjectile charactorOftargert = targets.next();
			int dame = charactorOftargert.getDame();
			_target = charactorOftargert.getSprite();

			if (_target.getX() <= -_target.getWidth()) {
				removeSprite(_target, targets);
				break;
			}
			
			Iterator<CharactorOfTargetAndProjectile> projectiles = projectileLL.iterator();
			Sprite _projectile;
			while (projectiles.hasNext()) {
				CharactorOfTargetAndProjectile charactorOfprojectile = projectiles.next();
				_projectile = charactorOfprojectile.getSprite();

				if (_projectile.getX() >= mCamera.getWidth() || _projectile.getY() >= mCamera.getHeight() + _projectile.getHeight() || _projectile.getY() <= -_projectile.getHeight()) {
					removeSprite(_projectile, projectiles);
					continue;
				}

				if (_target.collidesWith(_projectile)) {
					charactorOfprojectile.setHp(charactorOfprojectile.getHp() - dame);
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
				hitCount++;
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

	public void addTarget(Camera mCamera, Scene mainScene) {
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

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(), -target.getWidth()).deepCopy();
		target.registerEntityModifier(mod);

		CharactorOfTargetAndProjectile charactorOfTargetAndProjectile = new CharactorOfTargetAndProjectile(target, mod);
		TargetsToBeAdded.add(charactorOfTargetAndProjectile);
	}

	public void shootProjectile(Sprite player, Scene mainScene, Camera mCamera, final float pX, final float pY) {
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

		MoveModifier mod = new MoveModifier(realMoveDuration, projectile.getX(), realX, projectile.getY(), realY).deepCopy();
		projectile.registerEntityModifier(mod);

		CharactorOfTargetAndProjectile charactorOfTargetAndProjectile = new CharactorOfTargetAndProjectile(projectile, mod);
		projectilesToBeAdded.add(charactorOfTargetAndProjectile);
		// getMusic(EnumMusic.SHOOTINGSHOUND).play();
	}
}
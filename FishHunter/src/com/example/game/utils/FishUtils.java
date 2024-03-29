package com.example.game.utils;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.sprite.AnimatedSprite;

public class FishUtils {

	public static boolean isOutScreen(Camera mCamera, AnimatedSprite _projectile) {
		return _projectile.getX() <= -_projectile.getX() || _projectile.getX() >= mCamera.getWidth() || _projectile.getY() >= mCamera.getHeight() + _projectile.getHeight()
				|| _projectile.getY() <= -_projectile.getHeight();
	}

}

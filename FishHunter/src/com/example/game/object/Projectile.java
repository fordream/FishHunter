package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.content.Context;

public class Projectile extends BaseCharactor {

	@Override
	public void onLoadScene(Camera camera, CameraScene cameraScene) {
	}

	@Override
	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
		onLoadResources(context, mBitmapTextureAtlas, "Projectile_01.png", 128, 0);
	}

	@Override
	public void restart(Scene scene) {
	}
}
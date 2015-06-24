package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.content.Context;

public class Pause extends BaseCharactor {

	public Pause() {
	}

	@Override
	public void onLoadScene(Camera camera, CameraScene cameraScene) {
		final int x = (int) (camera.getWidth() / 2 - textureRegion.getWidth() / 2);
		final int y = (int) (camera.getHeight() / 2 - textureRegion.getHeight() / 2);
		onLoadScene(x, y, 1);
		cameraScene.attachChild(sprite);
		// setVisible(false);
	}

	@Override
	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
		onLoadResources(context, mBitmapTextureAtlas, "paused.png", 0, 64);
	}

}

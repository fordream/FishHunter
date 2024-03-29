package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.content.Context;

public class Win extends BaseCharactor {

	public Win() {
	}

	@Override
	public void onLoadScene(Camera camera, CameraScene cameraScene) {
		final int x = (int) (camera.getWidth() / 2 - textureRegion.getWidth() / 2);
		final int y = (int) (camera.getHeight() / 2 - textureRegion.getHeight() / 2);
		onLoadScene(x, y, 1);
		if (cameraScene != null)
			cameraScene.attachChild(sprite);
	}

	@Override
	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
		onLoadResources(context, mBitmapTextureAtlas, "win.png", 0, 128);
	}

}

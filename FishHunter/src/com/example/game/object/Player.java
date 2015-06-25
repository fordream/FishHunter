package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.content.Context;

public class Player extends BaseCharactor {

	@Override
	public void onLoadScene(Camera camera, CameraScene cameraScene) {
		final int PlayerX = (int) (camera.getWidth() - textureRegion.getWidth()) / 2;
		final int PlayerY = (int) ((camera.getHeight() - textureRegion.getHeight()));
		onLoadScene(PlayerX, PlayerY, 1);
	}

	@Override
	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
		onLoadResources(context, mBitmapTextureAtlas, "player_01.png", 0, 0);
	}

	@Override
	public void restart(Scene scene) {
		attachChild(scene, 0);
	}
}

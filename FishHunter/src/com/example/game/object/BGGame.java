package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.content.Context;

public class BGGame extends BaseCharactor {

	@Override
	public void onLoadScene(Camera camera, CameraScene cameraScene) {
//		onLoadScene(0, 0, 3);
	}

	@Override
	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
//		onLoadResources(context, mBitmapTextureAtlas, "bg.png", 0, 0);
	}

	@Override
	public void restart(Scene scene) {
//		attachChild(scene, 0);
	}
}

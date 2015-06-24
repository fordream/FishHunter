package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;

import android.content.Context;

public class BGGame extends BaseCharactor {

	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
		onLoadResources(context, mBitmapTextureAtlas, "bg.png", 0, 100);
	}

	public void onLoadScene(Camera camera) {
		onLoadScene(0, 0, 3.0f);
	}

}

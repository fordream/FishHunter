package com.example.game.object;

import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;

import android.content.Context;

public class Bomber extends RunningCat {

	public void onCreateResources(Context context, BitmapTextureAtlas textureManager) {
		regCat = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureManager, context, "bomber.png", 0, 0, 5, 1);
	}

}

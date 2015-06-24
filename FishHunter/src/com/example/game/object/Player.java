package com.example.game.object;

import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;

public class Player {
	private TextureRegion mPlayerTextureRegion;

	public Player() {
	}

	public void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {
		mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, "Player.png", 0, 0);
	}

	public void onLoadScene(Context context, BitmapTextureAtlas mBitmapTextureAtlas) {

	}

}

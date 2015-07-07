package com.example.game.object;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

public class TargetAnimationCat extends RunningCat {
	public TiledTextureRegion regCat;
	public AnimatedSprite sprCat;

	public void onCreateResources(Context context, BitmapTextureAtlas textureManager) {
		regCat = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureManager, context, "diabay.png", 0, 0, 1, 3);
	}

	public void onCreateScene(Scene scene) {
		sprCat = new AnimatedSprite(0, 0, regCat);
	}

	public TiledTextureRegion getRegCat() {
		return regCat.deepCopy();
	}

	public void onloadSucess(Scene scene) {
	}

	public AnimatedSprite getSprCat() {
		return sprCat;
	}
}
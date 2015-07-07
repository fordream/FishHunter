package com.example.game.object;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.vnp.core.common.LogUtils;

import android.content.Context;

public class RunningCat {
	public TiledTextureRegion regCat;
	public AnimatedSprite sprCat;

	public void onCreateResources(Context context, BitmapTextureAtlas textureManager) {
		regCat = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureManager, context, "bong.png", 0, 0, 3, 1);
	}

	public TiledTextureRegion getRegCat() {
		return regCat.deepCopy();
	}

	public void onCreateScene(Scene scene) {
		sprCat = new AnimatedSprite(0, 0, regCat);
		LogUtils.e("abcs", sprCat.getWidth() + " : " + sprCat.getHeight());
	}

	public void onloadSucess(Scene scene) {
		// scene.attachChild(sprCat, 1);
		// sprCat.animate(100);
	}

	public AnimatedSprite getSprCat() {
		return sprCat;
	}
}
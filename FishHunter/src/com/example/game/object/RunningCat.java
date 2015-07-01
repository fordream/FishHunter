package com.example.game.object;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;

public class RunningCat {
	private BitmapTextureAtlas texCat;
	private TiledTextureRegion regCat;
	private AnimatedSprite sprCat;

	public void onCreateResources(Context context, BitmapTextureAtlas textureManager) {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		texCat = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		regCat = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(texCat, context, "gfx/runningcat.png", 0, 0, 2, 4);
	}

	public void onCreateScene(Scene scene) {
		sprCat = new AnimatedSprite(0, 0, regCat);
	}

	public void onloadSucess(Scene scene) {
		scene.attachChild(sprCat, 2000);
		sprCat.animate(100);
	}
}
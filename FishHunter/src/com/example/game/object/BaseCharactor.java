package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Context;

public abstract class BaseCharactor {
	protected TextureRegion textureRegion;
	protected Sprite sprite;

	public Sprite getSprite() {
		return sprite;
	}

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public abstract void onLoadScene(Camera camera, CameraScene cameraScene);

	public abstract void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas);

	public final void onLoadResources(Context context, BitmapTextureAtlas mBitmapTextureAtlas, String file, int pTexturePositionX, int pTexturePositionY) {

		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, context, file, pTexturePositionX, pTexturePositionY);
	}

	public final void onLoadScene(float pX, float pY, float scale) {
		sprite = new Sprite(pX, pY, textureRegion);
		sprite.setScale(scale);
	}

	public void restart(Scene scene) {
		attachChild(scene, 0);
	}

	public final void attachChild(Scene scene, int index) {
		scene.attachChild(sprite, index);
	}

	public void setVisible(boolean isVisible) {
		if (sprite != null) {
			sprite.setVisible(isVisible);
		}
	}
}

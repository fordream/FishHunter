package com.example.game.object;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class Player {
	private Sprite player;
	private TextureRegion mPlayerTextureRegion;

	public Player() {
	}

	public void onLoadScene(Camera mCamera) {
		mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Player.png", 0, 0);
		final int PlayerX = (int) (mCamera.getWidth() - mPlayerTextureRegion.getWidth()) / 2;
		final int PlayerY = (int) ((mCamera.getHeight() - mPlayerTextureRegion.getHeight()));
		// set the player on the scene
		player = new Sprite(PlayerX, PlayerY, mPlayerTextureRegion);
		player.setScale(2);

	}

}

package com.example.game;

import java.util.HashMap;
import java.util.Set;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.view.Display;

import com.example.game.object.BaseCharactor;
import com.example.game.object.music.BaseMusic;
import com.example.game.object.music.EnumMusic;

public abstract class BaseMGameActivty extends BaseGameActivity {

	/**
	 * Sound and music
	 */
	private HashMap<EnumMusic, BaseMusic> sounds = new HashMap<EnumMusic, BaseMusic>();

	public void addSounds(EnumMusic type, BaseMusic music) {
		sounds.put(type, music);
	}

	public BaseMusic getMusic(EnumMusic type) {
		return sounds.get(type);
	}

	public void onLoadSoundsResources(Engine mEngine) {
		Set<EnumMusic> keys = sounds.keySet();

		for (EnumMusic key : keys) {
			sounds.get(key).onLoadResources(mEngine, this);
		}
	}

	/**
	 * Charactors
	 */
	private HashMap<String, BaseCharactor> charactors = new HashMap<String, BaseCharactor>();

	public void addCharactors(String type, BaseCharactor music) {
		charactors.put(type, music);
	}

	public BaseCharactor getCharactor(String type) {
		return charactors.get(type);
	}

	public void onLoadCharactorsResources(BitmapTextureAtlas mBitmapTextureAtlas) {
		Set<String> keys = charactors.keySet();

		for (String key : keys) {
			charactors.get(key).onLoadResources(this, mBitmapTextureAtlas);
		}
	}

	/**
	 * Camera
	 */
	private Camera mCamera;

	public Camera getmCamera() {
		return mCamera;
	}

	private BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);;

	public BitmapTextureAtlas getBitmapTextureAtlas() {
		return bitmapTextureAtlas;
	}

	@Override
	public Engine onLoadEngine() {
		final Display display = getWindowManager().getDefaultDisplay();
		int cameraWidth = display.getWidth();
		int cameraHeight = display.getHeight();
		mCamera = new Camera(0, 0, 960, 640);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth, cameraHeight), mCamera).setNeedsSound(true).setNeedsMusic(true));
	}
}

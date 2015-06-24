package com.example.game.object.music;

import org.anddev.andengine.engine.Engine;

import android.content.Context;

public class BackgroundMusic extends BaseMusic {
	@Override
	public void onLoadResources(Engine mEngine, Context context) {
		onLoadResources(true, mEngine, context, "background_music.wav", true);
	}
}

package com.example.game.object.music;

import org.anddev.andengine.engine.Engine;

import android.content.Context;

public class ShootingSound extends BaseMusic {

	public ShootingSound() {
	}

	@Override
	public void onLoadResources(Engine mEngine, Context context) {
		onLoadResources(false, mEngine, context, "pew_pew_lei.wav", false);
	}

}

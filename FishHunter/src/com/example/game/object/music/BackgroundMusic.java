package com.example.game.object.music;

import java.io.IOException;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;

import android.content.Context;

public class BackgroundMusic {
	private Music backgroundMusic;

	public void onLoadResources(org.anddev.andengine.engine.Engine mEngine, Context context) {
		MusicFactory.setAssetBasePath("mfx/");

		try {
			backgroundMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), context, "background_music.wav");
			backgroundMusic.setLooping(true);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		backgroundMusic.play();
	}

	public void pause() {
		if (backgroundMusic.isPlaying())
			backgroundMusic.pause();

	}

	public void resume() {
		if (!backgroundMusic.isPlaying())
			backgroundMusic.resume();
	}

	public boolean isPlaying() {
		return backgroundMusic.isPlaying();
	}
}

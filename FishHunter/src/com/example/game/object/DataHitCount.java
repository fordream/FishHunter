package com.example.game.object;

import android.content.Context;
import android.content.SharedPreferences;

public class DataHitCount {
	private Context context;

	public DataHitCount(Context context) {
		this.context = context;
	}

	public void saveHitCount(int hitCount) {
		if (hitCount > getHitCount()) {
			SharedPreferences preferences = context.getSharedPreferences(getClass().getName(), 0);
			preferences.edit().putInt("hitcount", hitCount).commit();
		}

	}

	public int getHitCount() {
		SharedPreferences preferences = context.getSharedPreferences(getClass().getName(), 0);
		return preferences.getInt("hitcount", 0);
	}
}

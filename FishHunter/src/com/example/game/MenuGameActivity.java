package com.example.game;

import vnp.com.activity.GameActivity;

import com.vnp.core.common.CommonAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MenuGameActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menugame);
		findViewById(R.id.play).setOnClickListener(this);
		findViewById(R.id.score).setOnClickListener(this);
		findViewById(R.id.moregame).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (R.id.play == v.getId()) {
			startActivity(new Intent(this, GameActivity.class));
		} else if (R.id.moregame == v.getId()) {
			CommonAndroid.showMarketPublish(this, "Vnp Game");
		}
	}
}

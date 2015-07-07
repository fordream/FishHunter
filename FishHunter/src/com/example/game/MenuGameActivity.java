package com.example.game;

import org.vnp.urohunter.R;
import vnp.com.activity.GameActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.example.game.object.DataHitCount;
import com.vnp.core.common.CommonAndroid;

public class MenuGameActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menugame);
		findViewById(R.id.play).setOnClickListener(this);
		findViewById(R.id.score).setOnClickListener(this);
		findViewById(R.id.moregame).setOnClickListener(this);
		findViewById(R.id.score).setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TextView maxhitcount = CommonAndroid.getView(this, R.id.maxhitcount);
		maxhitcount.setText(String.format(getString(R.string.hitccont_format), new DataHitCount(this).getHitCount()));
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

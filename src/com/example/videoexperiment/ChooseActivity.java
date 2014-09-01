package com.example.videoexperiment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ChooseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		
		Button mRecorderButton = (Button)findViewById(R.id.button_recorder);
		
		mRecorderButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ChooseActivity.this, LetsGoActivity.class);
				startActivity(intent);
			}
		});
	}
}


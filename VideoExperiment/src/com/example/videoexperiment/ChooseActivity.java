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
		
		LinkButtonToActivity(R.id.button_recorder, LetsGoActivity.class);
		LinkButtonToActivity(R.id.button_recorder2, RecordJamActivity.class);
		LinkButtonToActivity(R.id.button_player, CustomPlayerActivity.class);	
		LinkButtonToActivity(R.id.button_try_drag_experiment, DragExperimentActivity.class);
	}
	
	private void LinkButtonToActivity(int buttonId, final Class<?> intentClass) {
	
		Button button = (Button)findViewById(buttonId);
		
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(ChooseActivity.this, intentClass));
			}
		});
	}
}


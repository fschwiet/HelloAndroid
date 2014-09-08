package com.example.videoexperiment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MoarLearningActivity extends Activity {
	
	CameraPreview recordingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moar_learning);
		
		LinearLayout mLayout = (LinearLayout)findViewById(R.id.moar_layout);
		
		recordingView = new CameraPreview(this, CameraChooser.GetFrontFacingCameraGingerbread());
		
		mLayout.addView(recordingView);
		
		Button clipButton = (Button)this.findViewById(R.id.moar_clip);
		
		clipButton.setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View arg0) {
	            recordingView.ClipRecording();
            }
		});
	}
}


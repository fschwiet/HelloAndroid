package com.example.videoexperiment;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class MoarLearningActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moar_learning);
		
		LinearLayout mLayout = (LinearLayout)findViewById(R.id.moar_layout);
		
		//mLayout.addView(new CameraPreview(this, CameraChooser.GetFrontFacingCameraGingerbread()));
	}
}

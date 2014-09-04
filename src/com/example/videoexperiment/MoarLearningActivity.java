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
		
		Camera camera = openFrontFacingCameraGingerbread();
		
		LinearLayout mLayout = (LinearLayout)findViewById(R.id.moar_layout);
		
		mLayout.addView(new CameraPreview(this, camera));
	}
	
	// http://stackoverflow.com/questions/2779002/how-to-open-front-camera-on-android-platform
	private Camera openFrontFacingCameraGingerbread() {
	    int cameraCount = 0;
	    Camera cam = null;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    Log.d("camera info", "have " + cameraCount + " cameras.");
	    for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
	        Camera.getCameraInfo(camIdx, cameraInfo);
	        Log.d("camera info", cameraInfo.toString());
	        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	                return Camera.open(camIdx);
	        }
	    }

	    Log.e("opencamera", "failed");
	    throw new RuntimeException("Could not find camera");
	}
}

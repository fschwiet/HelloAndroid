package com.example.videoexperiment;

import android.hardware.Camera;
import android.util.Log;

public class CameraChooser {

	// http://stackoverflow.com/questions/2779002/how-to-open-front-camera-on-android-platform
	public static int GetFrontFacingCameraGingerbread() {
	    int cameraCount = 0;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    Log.d("camera info", "have " + cameraCount + " cameras.");
	    for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
	        Camera.getCameraInfo(camIdx, cameraInfo);
	        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	                return camIdx;
	        }
	    }

	    throw new RuntimeException("Could not find camera");
	}
}

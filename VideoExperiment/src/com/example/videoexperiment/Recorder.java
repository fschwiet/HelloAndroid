package com.example.videoexperiment;

import java.io.File;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;

public class Recorder {

    private String outputPath;
    private MediaRecorder mRecorder;
    private Camera mCamera;

    public Recorder(Camera camera) {
    	this.mCamera = camera;
    }
    
    public void Start(CamcorderProfile profile, String outputPath, Surface surface) {
    	
    	this.outputPath = outputPath;
    	
    	mRecorder = new MediaRecorder();
    	
    	/*
    	mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
			
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				Log.d("Recorder error", String.format("what: %d, extra: %d", what, extra));
			}
		});
        */
    	
        mCamera.unlock();

        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setProfile(profile);
        mRecorder.setOutputFile(outputPath);
        mRecorder.setPreviewDisplay(surface);
        
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            mCamera.lock();
            throw new RuntimeException(e);
        }
    	
        // 800ms
        mRecorder.start(); 
    }
    
    public void Stop() {
    	
    	if (mRecorder == null) {
    		return;
    	}
    	/*  
    	 * From the MediaRecorder documentation:
    	 * 
    	 * Stops recording. Call this after start(). Once recording is stopped, you will have to configure it again 
    	 * as if it has just been constructed. Note that a RuntimeException is intentionally thrown to the application, 
    	 * if no valid audio/video data has been received when stop() is called. This happens if stop() is called 
    	 * immediately after start(). The failure lets the application take action accordingly to clean up the output 
    	 * file (delete the output file, for instance), since the output file is not properly constructed when this 
    	 * happens.
    	 * 
    	 */
    	
    	try {
    		// 200-300 ms
            mRecorder.stop();
    	}
    	catch(RuntimeException e) {
    		File outputFile = new File(outputPath);
    		if (outputFile.exists())
    			outputFile.delete();
    	}
    	
        mRecorder.reset();
        mRecorder.release();
    	
        // 30 ms
        mCamera.lock();
    	
        mRecorder = null;
    }    
}

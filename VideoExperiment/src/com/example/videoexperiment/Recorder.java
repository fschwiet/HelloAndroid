package com.example.videoexperiment;

import java.io.File;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
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

        mRecorder.start();    	
    }
    
    public void Stop() {
    	
    	if (mRecorder == null) {
    		return;
    	}
    	
    	// .stop() fails rather unpredictably...  When it does,
    	// remove the recording because it is corrupt.
    	boolean needDelete = true;
    	
    	try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            needDelete = false;
    	}
    	finally {
    		if (needDelete) {
    			new File(outputPath).delete();
    		}
    		
            mCamera.lock();
            mRecorder = null;
    	}
    }
    
}

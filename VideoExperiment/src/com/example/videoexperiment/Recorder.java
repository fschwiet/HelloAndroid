package com.example.videoexperiment;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.Surface;

public class Recorder {
    private MediaRecorder mRecorder;
    private Camera mCamera;

    public Recorder(Camera camera) {
    	this.mCamera = camera;
    }
    
    public void Start(CamcorderProfile profile, String outputPath, Surface surface) {
    	
    	mRecorder = new MediaRecorder();
        
        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
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
    	
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mCamera.lock();
        mRecorder = null;
    }
    
}

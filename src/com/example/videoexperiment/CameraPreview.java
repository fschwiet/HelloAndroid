package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private int iCameraId;
    private Camera mCamera;
    private MediaRecorder mRecorder;

    public CameraPreview(Context context, int cameraId) {
        super(context);
        
        iCameraId = cameraId;
        mCamera = Camera.open(iCameraId);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            StartRecorder(mCamera, holder);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	Log.d("CameraPreview", "Releasing camera");
    	
    	mRecorder.stop();
    	mRecorder.reset();
    	mRecorder.release();
    	mCamera.lock();
    	mCamera.stopPreview();
    	mCamera.release();
    }
    
    private void StartRecorder(Camera camera, SurfaceHolder previewDisplay){
    	
    	mRecorder = new MediaRecorder();
    	
    	camera.unlock();
    	mRecorder.setCamera(camera);
    	mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    	mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    	
    	/*
    	mRecorder.setProfile(CamcorderProfile.get(iCameraId, CamcorderProfile.QUALITY_LOW));
    	*/
    	
    	///*
    	mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    	mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
    	//*/

    	mRecorder.setOutputFile(FileUtil.getOutputVideoFileUri(this.getContext()).getPath());
    	mRecorder.setPreviewDisplay(previewDisplay.getSurface());
    	
    	try {
			mRecorder.prepare();
		} catch (Exception e) {
			camera.lock();
			throw new RuntimeException(e);
		}

    	mRecorder.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	
    	if (true) {
    		//throw new RuntimeException("hmm");
    	}
    	
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
        	//mRecorder.stop();
            mCamera.stopPreview();
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
        
        //mRecorder.start();
    }
}
package com.example.videoexperiment;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
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
    
    private CamcorderProfile GetProfile() {
    	return CamcorderProfile.get(iCameraId, CamcorderProfile.QUALITY_LOW);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	
        	CamcorderProfile profile = GetProfile();
        	
        	Camera.Parameters parameters = mCamera.getParameters();
        	parameters.setPreviewSize(profile.videoFrameWidth,profile.videoFrameHeight);
        	mCamera.setParameters(parameters);
        	
        	mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
            //StartRecorder();
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	Log.d("CameraPreview", "Releasing camera");
    	
    	StopRecorder();
    	mCamera.stopPreview();
    	mCamera.release();
    }
    
    private void StartRecorder(){
    	
    	if (mRecorder != null) {
    		throw new RuntimeException("StartRecorder called while already recording.");
    	}
    	
    	mRecorder = new MediaRecorder();
    	
    	mCamera.unlock();
    	mRecorder.setCamera(mCamera);
    	mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    	mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    	
    	///*
    	mRecorder.setProfile(GetProfile());
    	//*/
    	
    	/*
    	mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    	mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
    	*/

    	mRecorder.setOutputFile(FileUtil.getOutputVideoFileUri(this.getContext()).getPath());
    	mRecorder.setPreviewDisplay(getHolder().getSurface());
    	
    	try {
			mRecorder.prepare();
		} catch (Exception e) {
			mCamera.lock();
			throw new RuntimeException(e);
		}

    	mRecorder.start();
    }
    
    private void StopRecorder() {
    	
    	if (mRecorder == null) {
    		return;
    	}
    	
    	mRecorder.stop();
    	mRecorder.reset();
    	mRecorder.release();
    	mCamera.lock();
    	mRecorder = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }
        
    	if (true) {
    		//throw new RuntimeException("hmm");
    	}

        // stop preview before making changes
        try {
        	StopRecorder();
            mCamera.stopPreview();
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(getHolder());
            mCamera.startPreview();
            StartRecorder();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
        
        //mRecorder.start();
    }
}
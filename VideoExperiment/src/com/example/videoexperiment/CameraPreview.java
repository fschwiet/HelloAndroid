package com.example.videoexperiment;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private int iCameraId;
    private Camera mCamera;
    private Recorder mRecorder;
    private Timer clipRecordingTimer;
    private int secondsPerRecording = 3;
    
    @SuppressWarnings("deprecation")
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
    
    private void StartCamera() {
        
        SurfaceHolder holder = getHolder();
        
        Log.d("StartCamera", holder.getSurfaceFrame().width() + ", " + holder.getSurfaceFrame().height());
        
        try {
            mCamera.setPreviewDisplay(getHolder());    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        mCamera.startPreview();    
    }

    public void surfaceCreated(SurfaceHolder holder) {
        CamcorderProfile profile = GetProfile();
        
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(profile.videoFrameWidth,profile.videoFrameHeight);
        mCamera.setParameters(parameters);
        
        StartCamera();
             
        //StartRecorder();  // This recording never works, need to wait until surfaceChanged
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
        
        String outputPath = FileUtil.getOutputVideoFileUri(this.getContext()).getPath();
        
        mRecorder = new Recorder(mCamera);
        mRecorder.Start(GetProfile(), outputPath, getHolder().getSurface());
        
        clipRecordingTimer = new Timer();
        clipRecordingTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				StopRecorder();
				StartRecorder();
			}
        	
        }, secondsPerRecording * 1000, secondsPerRecording * 1000);
    }
    
    public void StopRecorder() {
        
        if (mRecorder == null) {
            return;
        }
        
        mRecorder.Stop();
        
        mRecorder = null;
        
        clipRecordingTimer.cancel();
        clipRecordingTimer = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }
        
        StopRecorder();
        mCamera.stopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here

        StartCamera();
        StartRecorder();
    }

	public void ClipRecording() {
		StopRecorder();
		StartRecorder();
    }
}
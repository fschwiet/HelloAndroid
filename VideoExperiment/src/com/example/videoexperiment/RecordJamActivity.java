package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class RecordJamActivity extends Activity {
	
	CameraPreview recordingView;
	
	Handler enableButtonHandler = new Handler();
	Button reviewButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState == null) {
			String[] allClips = FileUtil.getOutputVideoFiles();
			for(String clip : allClips) {
				new File(clip).delete();				
			}
			
			File mergeFile = FileUtil.getMergedOutputFile();
			if (mergeFile.exists())
				mergeFile.delete();
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_jam);
		
		ViewGroup mLayout = (ViewGroup)findViewById(R.id.layout_record_jam);
		
		recordingView = new CameraPreview(this, CameraChooser.GetFrontFacingCameraGingerbread());
		
		mLayout.addView(recordingView);
		
		
		reviewButton = (Button)this.findViewById(R.id.button_review_jam);
		
		reviewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DisableButtons();
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						recordingView.StopRecorder();
						
						//  HACK TODO:  sleep a little to ensure output file is closed
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							throw new RuntimeException(e1);
						}
						
						String[] files = FileUtil.getOutputVideoFiles();
						File outputFile = FileUtil.getMergedOutputFile();
						try {
			                MP4Util.AppendMP4Files(outputFile.toString(), files);
		                } catch (IOException e) {
			                throw new RuntimeException(e);
		                }
						finally
						{
							TemporarilyDisableButtons();
						}
						
						Intent reviewIntent = new Intent(RecordJamActivity.this, CustomPlayerActivity.class);
						startActivity(reviewIntent);
					}
					
				}).start();
			}
		});
		
		TemporarilyDisableButtons();
	}

	Runnable enableButtons = new Runnable() {

		@Override
		public void run() {
			reviewButton.setEnabled(true);
		}
	};
	
	private void DisableButtons() {
		reviewButton.setEnabled(false);
	}
	
	//
	//  We need to temporarily disable buttons because MediaRecorder will crash
	//  if asked to stop() too quickly.
	//
	
	private void TemporarilyDisableButtons() {
		reviewButton.setEnabled(false);
		
		enableButtonHandler.removeCallbacks(enableButtons);
		enableButtonHandler.postDelayed(enableButtons, 1000);
	}
}


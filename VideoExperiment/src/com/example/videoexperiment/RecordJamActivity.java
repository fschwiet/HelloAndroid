package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class RecordJamActivity extends Activity {
	
	CameraPreview recordingView;
	
	Handler enableButtonHandler = new Handler();
	Button clipButton;
	Button reviewButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_jam);
		
		LinearLayout mLayout = (LinearLayout)findViewById(R.id.layout_record_jam);
		
		recordingView = new CameraPreview(this, CameraChooser.GetFrontFacingCameraGingerbread());
		
		mLayout.addView(recordingView);
		
		clipButton = (Button)this.findViewById(R.id.moar_clip);
		
		clipButton.setOnClickListener(new View.OnClickListener() {

			@Override
            public void onClick(View arg0) {
				DisableButtons();
				
				recordingView.ClipRecording();
				
				TemporarilyDisableButtons();
            }
		});
		
		reviewButton = (Button)this.findViewById(R.id.button_review_jam);
		
		reviewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DisableButtons();
				
				recordingView.StopRecorder();
				
				String[] files = FileUtil.getOutputVideoFiles();
				File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "merged.mp4");
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
		});
		
		TemporarilyDisableButtons();
	}

	Runnable enableButtons = new Runnable() {

		@Override
		public void run() {
			clipButton.setEnabled(true);
			reviewButton.setEnabled(true);
		}
	};
	
	private void DisableButtons() {
		clipButton.setEnabled(false);
		reviewButton.setEnabled(false);
	}
	
	//
	//  We need to temporarily disable buttons because MediaRecorder will crash
	//  if asked to stop() too quickly.
	//
	
	private void TemporarilyDisableButtons() {
		clipButton.setEnabled(false);
		reviewButton.setEnabled(false);
		
		enableButtonHandler.removeCallbacks(enableButtons);
		enableButtonHandler.postDelayed(enableButtons, 1000);
	}
}


package com.example.videoexperiment;

import java.io.File;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MoveFileActivity extends Activity {
	
	private File originalLocation;
	private File targetDirectory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_move_file);
		
		originalLocation = (File)getIntent().getExtras().get("originalLocation");
		targetDirectory = (File)getIntent().getExtras().get("targetDirectory");
		
		if (!originalLocation.exists())
			throw new RuntimeException("MoveFileActivity could not find file originalLocation at " + originalLocation);
		
		if (!targetDirectory.exists())
			throw new RuntimeException("MoveFileActivity could not find directory targetDirectory at " + targetDirectory);
		
		final EditText filenameText = (EditText)findViewById(R.id.moveFile_filename);
		final Button saveButton = (Button)findViewById(R.id.moveFile_saveButton);
		
		filenameText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence value, int arg1, int arg2, int arg3) {
				saveButton.setEnabled(isValidFilename(value.toString()));
			}
		});
		
		saveButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveButton.setEnabled(false);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						
						originalLocation.renameTo(new File(targetDirectory, filenameText.getText().toString() + ".mp4"));
						
						MoveFileActivity.this.finish();
					}
				}).start();
			}
		});
		
		TextView targetDirectoryName = (TextView)findViewById(R.id.moveFile_targetDirectoryName);
		targetDirectoryName.setText(targetDirectory.getName());
		
		VideoView video = (VideoView)findViewById(R.id.moveFile_videoView);
		MediaController videoController = new MediaController(this);
		videoController.setAnchorView(video);
		videoController.setMediaPlayer(video);
		video.setMediaController(videoController);
		video.setVideoPath(originalLocation.toString());
		video.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				arg0.setLooping(true);
			}
		});
		video.start();
	}
	
	private static final String[] reservedChars = 
			new String[] {"|", "\\", "?", "*", "<", "\"", ":", ">", "+", "[", "]", "/", "'"};
			
	private boolean isValidFilename(String value) {
		
		if (value.length() == 0)
			return false;
		
		for(String reservedChar : reservedChars) {
			if (value.contains(reservedChar))
				return false;
		}
		
		return true;
	}
}

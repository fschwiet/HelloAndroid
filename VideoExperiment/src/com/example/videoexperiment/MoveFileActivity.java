package com.example.videoexperiment;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.SharedPreferences;
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
						
						String filename = filenameText.getText().toString();
						
						originalLocation.renameTo(new File(targetDirectory, filename + ".mp4"));
						
						SharedPreferences preferences = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString("lastFilename", filename);
						editor.commit();
						
						MoveFileActivity.this.finish();
					}
				}).start();
			}
		});
		
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		String lastFilename = preferences.getString("lastFilename", "");
		if (lastFilename.length() > 0) {
			int lastIndex = 1;
			
			Pattern regex = Pattern.compile("\\s#(\\d+)$");
			Matcher matcher = regex.matcher(lastFilename);
			
			if (matcher.find()) {
				lastIndex = Integer.parseInt(matcher.group(1));
				lastFilename = lastFilename.substring(0, matcher.start());
			}
			
			String possibleFilename;
			File possibleFile;
			do{
				possibleFilename = lastFilename + " #" + ++lastIndex;
				possibleFile = new File(targetDirectory, possibleFilename + ".mp4");	
			} while(possibleFile.exists());
			
			filenameText.setText(possibleFilename);
		}
		
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

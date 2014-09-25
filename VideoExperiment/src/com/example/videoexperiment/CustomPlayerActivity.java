package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CustomPlayerActivity extends Activity implements SurfaceHolder.Callback {

	MediaPlayer player;
	File fileToPlay;
	int fps = 5;
	int currentSpeed = 0;
	Timer playbackTimer;
	boolean userMovingSlider = false;
	HashMap<Integer, Button> speedButtons = new HashMap<Integer,Button>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_custom_player);
		
		fileToPlay = FileUtil.getMergedOutputFile();
		
		if (!fileToPlay.exists()) {
			throw new RuntimeException("could not find file " + fileToPlay.toString());
		}
		
		SurfaceView surface = (SurfaceView)findViewById(R.id.player_surface);
		surface.getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub	
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		
		player = new MediaPlayer();
		
		player.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				Log.e("CustomPlayerActivity", String.format("MediaPlayer onError: arg1: %d,  arg2: %d", arg1, arg2));
				return false;
			}
		});

		player.setDisplay(arg0);
		
		try {
			player.setDataSource(fileToPlay.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {
			player.prepare();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		player.seekTo(player.getDuration());
		CustomPlayerActivity.this.setSpeed(-4);
		
		final SeekBar scroller = (SeekBar)findViewById(R.id.player_scroller);
		scroller.setMax(player.getDuration());
		
		scroller.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int position, boolean fromUser) {
				
				if (fromUser) {
					player.seekTo(position);					
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				userMovingSlider = true;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				userMovingSlider = false;
			}
		});
		
		AttachPlaybackSpeedButton(R.id.button_playFastReverse, -4);
		AttachPlaybackSpeedButton(R.id.button_playReverse, -1);
		AttachPlaybackSpeedButton(R.id.button_pause, 0);
		AttachPlaybackSpeedButton(R.id.button_play, 1);
		AttachPlaybackSpeedButton(R.id.button_playFast, 4);
		
		final RangeSlider slider = (RangeSlider)findViewById(R.id.player_rangeSlider);
		slider.minimum = 0;
		slider.maximum = player.getDuration();
		slider.setOnChangeListener(new RangeSlider.ChangeListener() {
			
			@Override
			public void onStartChanged(float start) {
				CustomPlayerActivity.this.setSpeed(0);
				player.seekTo((int)start);
			}
			
			@Override
			public void onEndChanged(float stop) {
				CustomPlayerActivity.this.setSpeed(0);
				player.seekTo((int)stop);
			}
		});
		
		final Button saveButton = (Button)findViewById(R.id.button_saveVideo);
		saveButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final float start = slider.getStart() / 1000;
				final float end = slider.getEnd() / 1000;
				saveButton.setEnabled(false);
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						final String outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "saved.mp4").toString();
						try {
							MP4Util.TrimMP4(FileUtil.getMergedOutputFile().toString(), (double)start, (double)end, outputFile);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						finally {
							CustomPlayerActivity.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									saveButton.setEnabled(true);
									
									Intent moveFile = new Intent(CustomPlayerActivity.this, MoveFileActivity.class);
									moveFile.putExtra("originalLocation", new File(outputFile));
									moveFile.putExtra("targetDirectory", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
									startActivity(moveFile);
								}
							});
						}
					}
				}).start();
			}
		});
				
		playbackTimer = new Timer();
		playbackTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				
				CustomPlayerActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						if (userMovingSlider || playbackTimer == null) {
							return;
						}
						
						scroller.setProgress(player.getCurrentPosition());
						
						if (currentSpeed == 0) {
							return;
						}

						int delta = currentSpeed * 1000 / fps;
						
						int nextTime = player.getCurrentPosition() + delta;
						
						if (nextTime < 0) {
							nextTime = 0;
						} else if (nextTime > player.getDuration()) {
							nextTime = player.getDuration();
						}
						
						if (nextTime != player.getCurrentPosition()) {
							player.seekTo(nextTime);
							scroller.setProgress(nextTime);
						}
					}
					
				});
			}}, 1000 /fps, 1000 / fps);
	}

	private void AttachPlaybackSpeedButton(int resourceId, final int speed) {
		
		Button button = (Button)findViewById(resourceId);
		
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				CustomPlayerActivity.this.setSpeed(speed);
			}
		});
		
		speedButtons.put(speed, button);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
		if (playbackTimer != null) {
			playbackTimer.cancel();
			playbackTimer = null;
		}
		
		if (player != null) {
			player.release();
			player = null;
		}	
	}
	
	public void setSpeed(int value) {
		
		for(Button button : speedButtons.values()) {
			button.setEnabled(true);
		}
		
		if (value == 1) {
			currentSpeed = 0;
			player.start();
		} else {
			if (player.isPlaying()) {
				player.pause();
			}
			
			currentSpeed = value;			
		}
		
		Button selected = speedButtons.get(value);
		if (selected != null) {
			selected.setEnabled(false);
		}
	}
}

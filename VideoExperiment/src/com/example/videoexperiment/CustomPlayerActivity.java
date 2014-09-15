package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	int fps = 30;
	int currentSpeed = 0;
	Timer playbackTimer;
	boolean userMovingSlider = false;
	
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
		currentSpeed = -1;
		
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
		AttachPlaybackSpeedButton(R.id.button_playFast, 4);
		
		Button playButton = (Button)findViewById(R.id.button_play);
		playButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentSpeed = 0;
				player.start();
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
		//player.start();
	}

	private void AttachPlaybackSpeedButton(int resourceId, final int speed) {
		
		Button button = (Button)findViewById(resourceId);
		
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (player.isPlaying()) {
					player.pause();
				}
				currentSpeed = speed;
			}
		});
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
}

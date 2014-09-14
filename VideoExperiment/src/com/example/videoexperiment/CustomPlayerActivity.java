package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CustomPlayerActivity extends Activity implements SurfaceHolder.Callback {

	MediaPlayer player;
	File fileToPlay;
	int fps = 30;
	int currentSpeed = 0;
	Timer playbackTimer;
	boolean pauseTimer = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_player);
		
		fileToPlay = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "merged.mp4");
		
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
				pauseTimer = true;
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				pauseTimer = false;
			}
		});
		
		AttachPlaybackSpeedButton(R.id.button_playFastReverse, -4);
		AttachPlaybackSpeedButton(R.id.button_playReverse, -1);
		AttachPlaybackSpeedButton(R.id.button_pause, 0);
		AttachPlaybackSpeedButton(R.id.button_play, 1);
		AttachPlaybackSpeedButton(R.id.button_playFast, 4);
		
		playbackTimer = new Timer();
		playbackTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				int delta = currentSpeed * 1000 / fps;
				
				int nextTime = player.getCurrentPosition() + delta;
				
				if (nextTime < 0) {
					nextTime = 0;
				} else if (nextTime > player.getDuration()) {
					nextTime = player.getDuration();
				}
				
				Log.d("playback timer", "current: " + player.getCurrentPosition());
				
				if (nextTime != player.getCurrentPosition()) {
					player.seekTo(nextTime);
					scroller.setProgress(nextTime);
				}
			}}, 1000 /fps, 1000 / fps);
		//player.start();
	}

	private void AttachPlaybackSpeedButton(int resourceId, final int speed) {
		
		Button button = (Button)findViewById(resourceId);
		
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentSpeed = speed;
			}
		});
	}
	
	private Button findViewById(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
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

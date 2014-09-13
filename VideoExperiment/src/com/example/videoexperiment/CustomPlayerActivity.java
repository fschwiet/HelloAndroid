package com.example.videoexperiment;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CustomPlayerActivity extends Activity implements SurfaceHolder.Callback {

	MediaPlayer player;
	File fileToPlay;
	
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
		
		player.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}
}

package com.example.videoexperiment;

import junit.framework.Assert;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.TextView;

public class DragExperimentActivity extends Activity {
	
	TextView textStart;
	TextView textEnd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_experiment);
		
		final RangeSlider slider = (RangeSlider)findViewById(R.id.slider_experiment);
		
		slider.minimum = -100;
		slider.maximum = 100;
		
		textStart = (TextView)findViewById(R.id.text_start);
		textEnd = (TextView)findViewById(R.id.text_end);
		
		slider.setOnChangeListener(new RangeSlider.ChangeListener() {

			@Override
			public void onStartChanged(float start) {
				Assert.assertEquals(start, slider.getStart());
				textStart.setText(String.format("%f", start));
				textStart.setAlpha(1);
				textEnd.setAlpha((float) 0.5);
			}

			@Override
			public void onEndChanged(float end) {
				Assert.assertEquals(end, slider.getEnd());
				textEnd.setText(String.format("%f", end));
				textStart.setAlpha((float)0.5);
				textEnd.setAlpha(1);
			}
		});
		
		textStart.setText(String.format("%f", slider.getStart()));
		textEnd.setText(String.format("%f", slider.getEnd()));
	}
}

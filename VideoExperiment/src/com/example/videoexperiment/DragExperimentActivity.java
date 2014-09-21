package com.example.videoexperiment;

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
		
		CustomSlider slider = (CustomSlider)findViewById(R.id.slider_experiment);
		
		slider.minimum = -100;
		slider.maximum = 100;
		
		textStart = (TextView)findViewById(R.id.text_start);
		textEnd = (TextView)findViewById(R.id.text_end);
		
		slider.setOnChangeListener(new CustomSlider.ChangeListener() {

			@Override
			public void onUpdate(float start, float end) {
				DragExperimentActivity.this.setText(start, end);
			}
		});
		
		setText(slider.getStart(), slider.getEnd());
	}
	
	private void setText(float start, float end) {
		textStart.setText(String.format("%f", start));
		textEnd.setText(String.format("%f", end));
	}
}

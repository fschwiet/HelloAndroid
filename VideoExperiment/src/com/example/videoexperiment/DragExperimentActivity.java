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

public class DragExperimentActivity extends Activity {
	
	public class DragExperimentTouchListener implements View.OnTouchListener {

		public DragExperimentTouchListener(float initalX, float initialY) {
			lastX = initalX;
			lastY = initialY;
		}
		
		boolean isDragging = false;
		float lastX;
		float lastY;
		float deltaX;
		
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			int action = arg1.getAction();
			
			if (action == MotionEvent.ACTION_DOWN && !isDragging) {
				isDragging = true; 
				deltaX = arg1.getX();
				return true;
			} else if (isDragging) {
				if (action == MotionEvent.ACTION_MOVE) {
					
					arg0.setX(arg0.getX() + arg1.getX() - deltaX);
					arg0.setY(arg0.getY());
					return true;
				} else if (action == MotionEvent.ACTION_UP) {
					isDragging = false;
					lastX = arg1.getX();
					lastY = arg1.getY();
					return true;
				} else if (action == MotionEvent.ACTION_CANCEL) {
					arg0.setX(lastX);
					arg0.setY(lastY);
					isDragging = false;
					return true;
				}
			}
			
			return false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_experiment);
		
		final View dragButton = (View)findViewById(R.id.experiment_draggable);
		
		dragButton.setOnTouchListener(new DragExperimentTouchListener(dragButton.getX(), dragButton.getY()));
		
		/*
		dragButton.setLongClickable(true);
		dragButton.setOnLongClickListener(new View.OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				
				ClipData dragData = ClipData.newPlainText(INPUT_SERVICE, "hi");
				View.DragShadowBuilder myShadow = new DragShadowBuilder(dragButton);

				v.startDrag(dragData,  // the data to be dragged
					myShadow,  // the drag shadow builder
					null,      // no need to use local data
					0          // flags (not currently used, set to 0)
				);
    
				return true;
			}
		})
		*/;
	}

}

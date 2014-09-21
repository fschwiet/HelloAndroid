package com.example.videoexperiment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CustomSlider extends RelativeLayout {
	
	View draggableStart;
	View draggableEnd;
	
	public CustomSlider(Context context) {
        super(context);
        init();
    }
	
	public CustomSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}	
	
	public float getStart() {
		return draggableStart.getX();
	}
	
	public float getEnd() {
		return draggableEnd.getX();
	}
	
	public float getMax() {
		return this.getWidth() - draggableEnd.getWidth();
	}
	
	private void init() {
		inflate(getContext(), R.layout.control_custom_slider, this);
		
		draggableStart = (View)findViewById(R.id.customSlider_draggable_start);
		draggableStart.setOnTouchListener(new DragExperimentTouchListener(draggableStart.getX(), draggableStart.getY(), new DynamicRange() {

			@Override
			public float getStart() {
				return 0;
			}

			@Override
			public float getEnd() {
				return draggableEnd.getX();
			}
		}));
		
		draggableEnd = (View)findViewById(R.id.customSlider_draggable_end);
		draggableEnd.setOnTouchListener(new DragExperimentTouchListener(draggableEnd.getX(), draggableEnd.getY(), new DynamicRange() {

			@Override
			public float getStart() {
				return draggableStart.getX();
			}

			@Override
			public float getEnd() {
				return CustomSlider.this.getMax();
			}
		}));
	}
	
	public interface DynamicRange {
		public float getStart();
		public float getEnd();
	}
	
	public class DragExperimentTouchListener implements View.OnTouchListener {

		public DragExperimentTouchListener(float initalX, float initialY, DynamicRange range) {
			this.range = range;
			lastX = initalX;
			lastY = initialY;
		}
		
		DynamicRange range;
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
					
					float resultingX = arg0.getX() + arg1.getX() - deltaX;
					
					if (resultingX < range.getStart())
						resultingX = range.getStart();
					
					if (resultingX > range.getEnd())
						resultingX = range.getEnd();
					
					arg0.setX(resultingX);
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
}

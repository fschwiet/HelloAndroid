package com.example.videoexperiment;

import java.util.ArrayList;

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
	
	public float minimum;
	public float maximum;
	
	public float getStart() {
		return getValue(draggableStart);
	}
	
	public float getEnd() {
		return getValue(draggableEnd);
	}
	
	public interface ChangeListener {
		void onUpdate(float start, float end);
	}
	
	public void setOnChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
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
	
	private float getValue(View draggable) {
		float position = draggable.getX();
		
		Log.d("position", String.format("position: %f, width: %d, width2: %d", position, this.getWidth(), draggable.getWidth()));
		
		return (position / (this.getWidth() - draggable.getWidth())) * (maximum - minimum) + minimum;	
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
				return CustomSlider.this.getWidth() - draggableEnd.getWidth();
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
					
					applyPosition(arg0, arg1);
					
					return true;
				} else if (action == MotionEvent.ACTION_UP) {
					isDragging = false;
					
					applyPosition(arg0, arg1);
					
					lastX = arg0.getX();
					lastY = arg0.getY();
					
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
		
		private void applyPosition(View arg0, MotionEvent arg1) {
			float originalX = arg0.getX();
			
			float resultingX = arg0.getX() + arg1.getX() - deltaX;
			
			if (resultingX < range.getStart())
				resultingX = range.getStart();
			
			if (resultingX > range.getEnd())
				resultingX = range.getEnd();
			
			arg0.setX(resultingX);
			arg0.setY(arg0.getY());	
			
			if (resultingX != originalX) {
				for(ChangeListener listener : changeListeners) {
					listener.onUpdate(getStart(), getEnd());
				}
			}
		}
	}
}

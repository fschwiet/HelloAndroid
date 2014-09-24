package com.example.videoexperiment;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class RangeSlider extends RelativeLayout {
	
	public float minimum;
	public float maximum;
	
	public float getStart() {
		return getValue(draggableStart, minimum);
	}
	
	public float getEnd() {
		return getValue(draggableEnd, maximum);
	}
	
	public interface ChangeListener {
		void onStartChanged(float start);
		void onEndChanged(float end);
	}
	
	public void setOnChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	
	ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	RangeSliderWidget draggableStart;
	RangeSliderWidget draggableEnd;
	
	
	public RangeSlider(Context context) {
        super(context);
        init();
    }
	
	public RangeSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RangeSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}	
	
	private float getValue(View draggable, float initialValue) {
		
		float draggableWidth = draggable.getWidth();
		
		// before the UI is rendered, we use the initial value.
		if (this.getWidth() == 0) {
			return initialValue;
		}
		
		return (draggable.getX() / (this.getWidth() - draggableWidth)) * (maximum - minimum) + minimum;	
	}
	
	private void init() {
		inflate(getContext(), R.layout.control_range_slider, this);
		
		draggableStart = (RangeSliderWidget)findViewById(R.id.rangeSlider_draggable_start);
		draggableStart.setOnTouchListener(new DragExperimentTouchListener(draggableStart.getX(), draggableStart.getY(), new DynamicRange() {

			@Override
			public float getStart() {
				return 0;
			}

			@Override
			public float getEnd() {
				return draggableEnd.getX();
			}

			@Override
			public void onChange() {
				for(ChangeListener listener : changeListeners) {
					listener.onStartChanged(RangeSlider.this.getStart());
				}
			}
		}));
		
		draggableEnd = (RangeSliderWidget)findViewById(R.id.rangeSlider_draggable_end);
		draggableEnd.setPointingLeft();
		draggableEnd.setOnTouchListener(new DragExperimentTouchListener(draggableEnd.getX(), draggableEnd.getY(), new DynamicRange() {

			@Override
			public float getStart() {
				return draggableStart.getX();
			}

			@Override
			public float getEnd() {
				return RangeSlider.this.getWidth() - getDraggableWidth();
			}

			@Override
			public void onChange() {
				for(ChangeListener listener : changeListeners) {
					listener.onEndChanged(RangeSlider.this.getEnd());
				}
			}
		}));
	}
	
	private float getDraggableWidth() {
		return draggableEnd.getWidth();
	}
	
	public interface DynamicRange {
		public float getStart();
		public float getEnd();
		public void onChange();
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
				
				if (((RangeSliderWidget)arg0).isClickable(arg1.getX(), arg1.getY())) {
					isDragging = true; 
					deltaX = arg1.getX();
					return true;
				}
			} else if (isDragging) {
				if (action == MotionEvent.ACTION_MOVE) {
					
					applyPosition(arg0, arg1);
					
					return true;
				} else if (action == MotionEvent.ACTION_UP) {
					isDragging = false;
				
					lastX = arg0.getX();
					lastY = arg0.getY();
					
					applyPosition(arg0, arg1);
					
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
			
			if (originalX != resultingX) {
				range.onChange();
			}
		}	
	}
}

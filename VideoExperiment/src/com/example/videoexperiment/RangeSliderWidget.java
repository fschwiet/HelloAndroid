package com.example.videoexperiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RangeSliderWidget extends View {
	
	Paint filler;
	Path triangle;
	boolean pointLeft = false;

	public RangeSliderWidget(Context context) {
		super(context);
		init();
	}
	
	public RangeSliderWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public RangeSliderWidget(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public void setPointingLeft(){
		pointLeft = true;
	}
	
	private void init() {
		filler = new Paint(Paint.ANTI_ALIAS_FLAG);
		filler.setStyle(Paint.Style.FILL_AND_STROKE);
		filler.setColor(0xe500e5);
		filler.setAlpha(255);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("onMeasure", "");
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		this.setMeasuredDimension(40, 80);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		Log.d("onSizeChanged", String.format("%d x %d", w, h));
		
		super.onSizeChanged(w, h, oldw, oldh);
		
		if (pointLeft) {
			triangle = new Path();
			triangle.moveTo(w, 0);
			triangle.lineTo(0, h / 2);
			triangle.lineTo(w, h);
			triangle.lineTo(w, 0);
			triangle.close();	
		} else {
			triangle = new Path();
			triangle.moveTo(0, 0);
			triangle.lineTo(w, h / 2);
			triangle.lineTo(0, h);
			triangle.lineTo(0, 0);
			triangle.close();		
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawPath(triangle, filler);
	}
}
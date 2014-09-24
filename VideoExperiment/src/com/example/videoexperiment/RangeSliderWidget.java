package com.example.videoexperiment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RangeSliderWidget extends View {
	
	Paint clickableFiller;
	Paint outerFiller;
	Path triangle;
	Path outerBoundary;
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
	
	public boolean isClickable(float f, float g) {
		if (pointLeft) {
			return f + g >= this.getWidth();
		} else {
			return f + g <= this.getWidth();
		}
	}
	
	private void init() {
		clickableFiller = new Paint(Paint.ANTI_ALIAS_FLAG);
		clickableFiller.setStyle(Paint.Style.FILL_AND_STROKE);
		clickableFiller.setColor(0xe500e5);
		clickableFiller.setAlpha(255);
		
		outerFiller = new Paint(Paint.ANTI_ALIAS_FLAG);
		outerFiller.setStyle(Paint.Style.FILL_AND_STROKE);
		outerFiller.setColor(0x008080);
		outerFiller.setAlpha(40);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		this.setMeasuredDimension(80, 80);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		super.onSizeChanged(w, h, oldw, oldh);
		
		if (pointLeft) {
			triangle = new Path();
			triangle.moveTo(w, 0);
			triangle.lineTo(w/2, h / 2);
			triangle.lineTo(w, h);
			triangle.lineTo(w, 0);
			triangle.close();	
			
			outerBoundary = new Path();
			outerBoundary.moveTo(0,h);
			outerBoundary.lineTo(w/2, h/2);
			outerBoundary.lineTo(w,h);
			outerBoundary.lineTo(0,h);
			outerBoundary.close();
		} else {
			triangle = new Path();
			triangle.moveTo(0, 0);
			triangle.lineTo(w/2, h / 2);
			triangle.lineTo(0, h);
			triangle.lineTo(0, 0);
			triangle.close();

			outerBoundary = new Path();
			outerBoundary.moveTo(0,0);
			outerBoundary.lineTo(w/2, h/2);
			outerBoundary.lineTo(w,0);
			outerBoundary.lineTo(0,0);
			outerBoundary.close();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawPath(triangle, clickableFiller);
		canvas.drawPath(outerBoundary, outerFiller);
	}
}
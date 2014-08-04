package com.kooco.socialmatic.camera;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView {

	// private static final double ASPECT_RATIO = 1.0 / 1.0;

	public CameraSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));

		/*
		 * int height = MeasureSpec.getSize(heightMeasureSpec); int width =
		 * MeasureSpec.getSize(widthMeasureSpec);
		 * 
		 * 
		 * if (width > height * ASPECT_RATIO) { width = (int) (height *
		 * ASPECT_RATIO + .5); } else { height = (int) (width / ASPECT_RATIO +
		 * .5); }
		 * 
		 * 
		 * setMeasuredDimension(width, height);
		 */

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			float x = event.getX();
			float y = event.getY();
			float touchMajor = event.getTouchMajor();
			float touchMinor = event.getTouchMinor();

			Rect touchRect = new Rect((int) (x - touchMajor / 2),
					(int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
					(int) (y + touchMinor / 2));

			try {
				MainView.MainFragment fragment = new MainView.MainFragment();

				fragment.touchFocus(touchRect);
			} catch (Exception ex) {
				Log.d("onTouchEvent", "onTouchEvent ==> ex:" + ex);
			}
		}

		return true;
	}

}

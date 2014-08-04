/**
 * Copyright 2013 Tony Atkins <duhrer@gmail.com>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Tony Atkins ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Tony Atkins OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 */
package com.kooco.socialmatic.camera.crop;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class MoveAndCropListener implements OnTouchListener {

	CropOverlayView overlayView = null;

	short mResizeState = 0;

	int mStartX = 0;
	int mStartY = 0;
	int mEndX = 0;
	int mEndY = 0;

	int mOrgStartX = 0;
	int mOrgStartY = 0;
	int mOrgEndX = 0;
	int mOrgEndY = 0;

	boolean mIsBoundFlag = false;

	int mTmpX = 0;
	int mTmpY = 0;
	
    private ScaleGestureDetector mScaleDetector;

    private float mScaleFactor = 0.f;
    private float mPreScaleFactor = -99.f;
    
	public MoveAndCropListener(boolean allowResizing, Context context) {
		super();
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	/*@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    // Let the ScaleGestureDetector inspect all events.
	    mScaleDetector.onTouchEvent(ev);
	    return true;
	}*/

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		mScaleDetector.onTouchEvent(event);
		
		if (!(view instanceof CropOverlayView)) {
			Log.e(Constants.TAG,
					"Move and crop listener only works with CropOverlayView objects.");
			return false;
		}

		if (overlayView == null) {
			overlayView = (CropOverlayView) view;
		}

		int current_width = overlayView.getEndx() - overlayView.getStartx();
		int current_height = overlayView.getEndy() - overlayView.getStarty();

		int eventaction = event.getAction();

		if (event.getPointerCount() <= 1) {

			PointerCoords finger0Coords = new PointerCoords();
			event.getPointerCoords(0, finger0Coords);

			switch (eventaction) {

			case MotionEvent.ACTION_DOWN:
				mPreScaleFactor = -99.f;
				mOrgStartX = overlayView.getStartx();
				mOrgStartY = overlayView.getStarty();
				mOrgEndX = overlayView.getEndx();
				mOrgEndY = overlayView.getEndy();

				mStartX = mOrgStartX;
				mStartY = mOrgStartY;
				mEndX = mOrgEndX;
				mEndY = mOrgEndY;

				if ((finger0Coords.x >= mOrgStartX - 20 && finger0Coords.x <= mOrgStartX + 20)
						&& finger0Coords.x >= overlayView.getMinX()) {
					mResizeState = 1;
				} else if ((finger0Coords.y >= mOrgStartY - 20 && finger0Coords.y <= mOrgStartY + 20)
						&& finger0Coords.y >= overlayView.getMinY()) {
					mResizeState = 2;
				} else if ((finger0Coords.x >= mOrgEndX - 20 && finger0Coords.x <= mOrgEndX + 20)
						&& finger0Coords.x <= overlayView.getMaxX()) {
					mResizeState = 3;
				}

				else if ((finger0Coords.y >= mOrgEndY - 20 && finger0Coords.y <= mOrgEndY + 20)
						&& finger0Coords.y <= overlayView.getMaxY()) {
					mResizeState = 4;
				} else if (finger0Coords.x >= overlayView.getStartx()
						&& finger0Coords.x <= overlayView.getEndx()
						&& finger0Coords.y >= overlayView.getStarty()
						&& finger0Coords.y <= overlayView.getEndy()) {
					mTmpX = (int) finger0Coords.x;
					mTmpY = (int) finger0Coords.y;
				}

				break;
			case MotionEvent.ACTION_MOVE:

				if (mResizeState == 1) {
					setFrameWithLeftSide(finger0Coords);
				} else if (mResizeState == 2) {
					setFrameWithTopSide(finger0Coords);
				} else if (mResizeState == 3) {
					setFrameWithRightSide(finger0Coords);
				} else if (mResizeState == 4) {
					setFrameWithBottomSide(finger0Coords);
				} else {

					mIsBoundFlag = false;

					int distX = (int) finger0Coords.x - mTmpX;
					int distY = (int) finger0Coords.y - mTmpY;

					mStartX = mStartX + distX;
					mStartY = mStartY + distY;
					mEndX = mEndX + distX;
					mEndY = mEndY + distY;

					if (mStartX <= overlayView.getMinX()) {
						mStartX = overlayView.getMinX();
						mEndX = mStartX + current_width;
					}
					if (mStartY <= overlayView.getMinY()) {
						mStartY = overlayView.getMinY();
						mEndY = mStartY + current_height;
					}
					if (mEndX >= overlayView.getMaxX()) {
						mEndX = overlayView.getMaxX();
						mStartX = mEndX - current_width;
					}
					if (mEndY >= overlayView.getMaxY()) {
						mEndY = overlayView.getMaxY();
						mStartY = mEndY - current_height;
					}

					mTmpX = (int) finger0Coords.x;
					mTmpY = (int) finger0Coords.y;
				}
				break;

			case MotionEvent.ACTION_UP:
				mResizeState = 0;
				break;
			}

			overlayView.setRect(mStartX, mStartY, mEndX, mEndY);

		}

		return true;
	}

	private void setFrameWithLeftSide(PointerCoords finger0Coords) {
		
		if (finger0Coords.x >= overlayView.getMinX()
				&& finger0Coords.x <= mEndX - 20) {

			int centerX = (mOrgEndX + mOrgStartX) / 2;
			int centerY = (mOrgStartY + mOrgEndY) / 2;

			float frameRatio = overlayView.getRatio();

			if (mOrgEndX - finger0Coords.x <= mOrgEndX - mOrgStartX) {

				mStartX = (int) finger0Coords.x;
				mEndX = centerX + (centerX - mStartX);

				mStartY = centerY - (int) ((centerX - mStartX) * frameRatio);
				mEndY = centerY + (int) ((centerX - mStartX) * frameRatio);

				mIsBoundFlag = false;
			} else {
				if (mIsBoundFlag)
					return;

				if (mOrgEndX - finger0Coords.x <= overlayView.getMaxWidth()) {

					int dist = centerX - (int) finger0Coords.x;

					int minY = centerY - (int) (dist * frameRatio);
					int maxY = centerY + (int) (dist * frameRatio);

					int maxX = centerX + dist;

					if (maxX >= overlayView.getMaxX()) {
						mIsBoundFlag = true;

						dist = overlayView.getMaxX() - centerX;

						if (minY <= overlayView.getMinY()) {
							int dist1 = centerY - overlayView.getMinY();

							if (dist1 < (int) (dist * frameRatio)) {
								dist = (int) (dist1 / frameRatio);
							}

						} else if (maxY >= overlayView.getMaxY()) {
							int dist1 = overlayView.getMaxY() - centerY;

							if (dist1 < (int) (dist * frameRatio))
								dist = (int) (dist1 / frameRatio);
						}
					} else if (minY <= overlayView.getMinY()) {
						mIsBoundFlag = true;
						dist = centerY - overlayView.getMinY();

						int dist1 = overlayView.getMaxY() - centerY;

						if (dist1 < (int) (dist * frameRatio))
							dist = (int) (dist1 / frameRatio);
					}

					else if (maxY >= overlayView.getMaxY()) {
						mIsBoundFlag = true;

						dist = (int) ((overlayView.getMaxY() - centerY) / frameRatio);
					}

					mEndX = centerX + dist;
					mStartX = centerX - dist;
					mStartY = centerY - (int) (dist * frameRatio);
					mEndY = centerY + (int) (dist * frameRatio);
				}
			}
		}
	}

	private void setFrameWithTopSide(PointerCoords finger0Coords) {
		if (finger0Coords.y >= overlayView.getMinY()
				&& finger0Coords.y <= mEndY - 20) {

			int centerX = (mOrgEndX + mOrgStartX) / 2;
			int centerY = (mOrgStartY + mOrgEndY) / 2;

			float frameRatio = overlayView.getRatio();

			if (mOrgEndY - finger0Coords.y <= mOrgEndY - mOrgStartY) {

				mStartY = (int) finger0Coords.y;
				mEndY = centerY + (centerY - mStartY);

				mStartX = centerX - (int) ((centerY - mStartY) / frameRatio);
				mEndX = centerX + (int) ((centerY - mStartY) / frameRatio);

				mIsBoundFlag = false;
			} else {
				if (mIsBoundFlag)
					return;

				if (mOrgEndY - finger0Coords.y <= overlayView.getMaxHeight()) {

					int dist = centerY - (int) finger0Coords.y;

					int minX = centerX - (int) (dist / frameRatio);
					int maxX = centerX + (int) (dist / frameRatio);

					int maxY = centerY + dist;

					if (maxY >= overlayView.getMaxY()) {
						mIsBoundFlag = true;

						dist = overlayView.getMaxY() - centerY;

						if (minX <= overlayView.getMinX()) {
							int dist1 = centerX - overlayView.getMinX();

							if ((int) (dist1 * frameRatio) < dist)
								dist = (int) (dist1 * frameRatio);
						} else if (maxX >= overlayView.getMaxX()) {
							int dist1 = overlayView.getMaxX() - centerX;

							if ((int) (dist1 * frameRatio) < dist)
								dist = (int) (dist1 * frameRatio);
						}
					} else if (minX <= overlayView.getMinX()) {
						mIsBoundFlag = true;
						dist = centerX - overlayView.getMinX();

						int dist1 = overlayView.getMaxX() - centerX;

						if ((int) (dist1 * frameRatio) < dist)
							dist = (int) (dist1 * frameRatio);
					}

					else if (maxX >= overlayView.getMaxX()) {
						mIsBoundFlag = true;

						dist = (int) ((overlayView.getMaxX() - centerX) * frameRatio);
					}

					mEndY = centerY + dist;
					mStartY = centerY - dist;
					mStartX = centerX - (int) (dist / frameRatio);
					mEndX = centerX + (int) (dist / frameRatio);
				}
			}
		}
	}

	private void setFrameWithRightSide(PointerCoords finger0Coords) {
		if (finger0Coords.x <= overlayView.getMaxX()
				&& finger0Coords.x >= mStartX + 20) {

			int centerX = (mOrgEndX + mOrgStartX) / 2;
			int centerY = (mOrgStartY + mOrgEndY) / 2;

			float frameRatio = overlayView.getRatio();

			if (finger0Coords.x - mOrgStartX <= mOrgEndX - mOrgStartX) {

				mEndX = (int) finger0Coords.x;
				mStartX = centerX - (mEndX - centerX);

				mStartY = centerY - (int) ((mEndX - centerX) * frameRatio);
				mEndY = centerY + (int) ((mEndX - centerX) * frameRatio);

				mIsBoundFlag = false;
			} else {
				if (mIsBoundFlag)
					return;

				if (finger0Coords.x - mOrgStartX <= overlayView.getMaxWidth()) {

					int dist = (int) finger0Coords.x - centerX;

					int minY = centerY - (int) (dist * frameRatio);
					int maxY = centerY + (int) (dist * frameRatio);

					int minX = centerX - dist;

					if (minX <= overlayView.getMinX()) {
						mIsBoundFlag = true;

						dist = centerX - overlayView.getMinX();

						if (minY <= overlayView.getMinY()) {
							int dist1 = centerY - overlayView.getMinY();

							if (dist1 < (int) (dist * frameRatio)) {
								dist = (int) (dist1 / frameRatio);
							}

						} else if (maxY >= overlayView.getMaxY()) {
							int dist1 = overlayView.getMaxY() - centerY;

							if (dist1 < (int) (dist * frameRatio)) {
								dist = (int) (dist1 / frameRatio);
							}
						}
					} else if (minY <= overlayView.getMinY()) {
						mIsBoundFlag = true;
						dist = centerY - overlayView.getMinY();

						int dist1 = overlayView.getMaxY() - centerY;

						if (dist1 < (int) (dist * frameRatio)) {
							dist = (int) (dist1 / frameRatio);
						}
					}

					else if (maxY >= overlayView.getMaxY()) {
						mIsBoundFlag = true;

						dist = (int) ((overlayView.getMaxY() - centerY) / frameRatio);
					}

					mEndX = centerX + dist;
					mStartX = centerX - dist;
					mStartY = centerY - (int) (dist * frameRatio);
					mEndY = centerY + (int) (dist * frameRatio);
				}
			}
		}
	}

	private void setFrameWithBottomSide(PointerCoords finger0Coords) {
		if (finger0Coords.y <= overlayView.getMaxY()
				&& finger0Coords.y >= mStartY + 20) {

			int centerX = (mOrgEndX + mOrgStartX) / 2;
			int centerY = (mOrgStartY + mOrgEndY) / 2;

			float frameRatio = overlayView.getRatio();

			if (finger0Coords.y - mOrgStartY <= mOrgEndY - mOrgStartY) {

				mEndY = (int) finger0Coords.y;
				mStartY = centerY - (mEndY - centerY);

				mStartX = centerX - (int) ((mEndY - centerY) / frameRatio);
				mEndX = centerX + (int) ((mEndY - centerY) / frameRatio);

				mIsBoundFlag = false;
			} else {
				if (mIsBoundFlag)
					return;

				if (finger0Coords.y - mOrgStartY <= overlayView.getMaxHeight()) {

					int dist = (int) finger0Coords.y - centerY;

					int minX = centerX - (int) (dist / frameRatio);
					int maxX = centerX + (int) (dist / frameRatio);

					int minY = centerY - dist;

					if (minY <= overlayView.getMinY()) {
						mIsBoundFlag = true;

						dist = centerY - overlayView.getMinY();

						if (minX <= overlayView.getMinX()) {
							int dist1 = centerX - overlayView.getMinX();

							if ((dist1 * frameRatio) < dist) {
								dist = (int) (dist1 * frameRatio);
							}

						} else if (maxX >= overlayView.getMaxX()) {
							int dist1 = overlayView.getMaxX() - centerX;

							if ((dist1 * frameRatio) < dist) {
								dist = (int) (dist1 * frameRatio);
							}
						}
					} else if (minX <= overlayView.getMinX()) {
						mIsBoundFlag = true;
						dist = centerX - overlayView.getMinX();

						int dist1 = overlayView.getMaxX() - centerX;

						if ((dist1 * frameRatio) < dist) {
							dist = (int) (dist1 * frameRatio);
						}
					}

					else if (maxX >= overlayView.getMaxX()) {
						mIsBoundFlag = true;

						dist = (int) ((overlayView.getMaxX() - centerX) * frameRatio);
					}

					mEndY = centerY + dist;
					mStartY = centerY - dist;
					mStartX = centerX - (int) (dist / frameRatio);
					mEndX = centerX + (int) (dist / frameRatio);
				}
			}
		}
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

        	mScaleFactor = detector.getCurrentSpan()/100.f;
        	mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
        	
        	mResizeState = 0;

            if (mPreScaleFactor == -99.f)
            	mPreScaleFactor = mScaleFactor;
            else
            {
                if (mPreScaleFactor != mScaleFactor)
                {
                	float scaleRate = (mScaleFactor - mPreScaleFactor);
              
                	PointerCoords pointerCoords = new PointerCoords();
                	pointerCoords.x = mStartX + scaleRate * (-0.4f) * (mEndX - mStartX);
                	pointerCoords.y = mEndY;
                	setFrameWithLeftSide(pointerCoords);
                	
                	overlayView.setRect(mStartX, mStartY, mEndX, mEndY);
                	 
                	mPreScaleFactor = mScaleFactor;
                }
            }
            	
            return true;
        }
    }
}

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
package com.kooco.socialmatic.camera;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.camera.crop.CropOverlayView;
import com.kooco.socialmatic.camera.crop.MoveAndCropListener;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class CropActivity extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {
	public static final String KEY_OUTPUT_X = "outputX";
	public static final String KEY_OUTPUT_Y = "outputY";
	public static final String KEY_DATA = "data";

	private int outputX = 0;
	private int outputY = 0;
	private Bitmap bitmap;
	private Bitmap bitmap1;
	private CropOverlayView cropOverlayView;
	private FrameLayout frameLayout;
	private ImageView cropImageView;

	private Uri mUri = null;

	public int mScreenWidth = 0;
	public int mScreenHeight = 0;

	public int mBitMapWidth = 0;
	public int mBitMapHeight = 0;

	Button mButton1X1;
	Button mButton4X3;
	Button mButton16X9;

	private int mDistX = 0;
	private int mDistY = 0;

	public static String mAuthDeviceID = "";
	public static String mAuthToken = "DD659070-87405B00-17514DB4-FB1D39AE";
	public static int mUserID = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		// ----------------------------------------------
		String deviceId = UserDataAccess.getDeviceId(this);
		String newDeviceId = deviceId.replace("-", "").toUpperCase();

		int X1 = 0, X2 = 0, X3 = 0, X4 = 0;

		int count = 0;
		for (int i = 0; i < 32; i++) {
			int num = (int) newDeviceId.charAt(i);

			if (num < 65)
				num = Character.getNumericValue(newDeviceId.charAt(i));

			count += num;

			if (i == 7) {
				X1 = count % 9;
				count = 0;
			} else if (i == 15) {
				X2 = count % 9;
				count = 0;
			} else if (i == 19) {
				X3 = count % 9;
				count = 0;
			} else if (i == 31) {
				X4 = count % 9;
				count = 0;
			}
		}

		mAuthDeviceID = deviceId.toUpperCase() + "-" + Integer.toString(X1)
				+ Integer.toString(X2) + Integer.toString(X3)
				+ Integer.toString(X4);

		// http query
		// ---------------------------------------
		/*
		 * String queryUrl = getString(R.string.socialmatic_base_url) +
		 * getString(R.string.enrolldevice_url);
		 * 
		 * 
		 * ArrayList<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>();
		 * 
		 * nameValuePairs.add(new
		 * BasicNameValuePair("deviceID","6EC7D0E8-73D4-45CC-A3B7-E0FCA0383F02-6066"
		 * )); nameValuePairs.add(new
		 * BasicNameValuePair("username","cwcheng@kooco.com.tw"));
		 * nameValuePairs.add(new BasicNameValuePair("password",
		 * "chingwen888")); nameValuePairs.add(new
		 * BasicNameValuePair("deviceType", "2"));
		 * 
		 * Log.d("queryUrl-----------", "queryUrl--------->" + queryUrl);
		 * WebManager webManager = new WebManager(CropActivity.this);
		 * 
		 * 
		 * try { webManager.setQueryEntity(new
		 * UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8)); } catch
		 * (UnsupportedEncodingException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); }
		 * //webManager.setQueryParamList(nameValuePairs);
		 * 
		 * if (webManager.isNetworkAvailable()) {
		 * 
		 * Log.d("webManager........................",
		 * "webManager........................"); // do web connect...
		 * webManager.execute(queryUrl, "POST"); }
		 */
		// ----------------------------------------------

		setContentView(R.layout.crop);

		mScreenWidth = Config.getScreenSize(this).x;
		mScreenHeight = Config.getScreenSize(this).y;

		frameLayout = (FrameLayout) findViewById(R.id.cropImageFrame);
		cropOverlayView = (CropOverlayView) findViewById(R.id.cropOverlayView);
		cropImageView = (ImageView) findViewById(R.id.cropImageView);

		Typeface tf1 = Typeface.createFromAsset(getAssets(),
				"fonts/SFIntellivisedItalic.ttf");

		Button cancelBtn = (Button) findViewById(R.id.cropCancelButton);

		Button nextBtn = (Button) findViewById(R.id.cropSaveButton);

		cancelBtn.setTypeface(tf1);

		mButton1X1 = (Button) findViewById(R.id.Button1X1);
		mButton4X3 = (Button) findViewById(R.id.Button4X3);
		mButton16X9 = (Button) findViewById(R.id.Button16X9);

		mButton1X1.setOnClickListener(new SetFrameTypeListener());
		mButton4X3.setOnClickListener(new SetFrameTypeListener());
		mButton16X9.setOnClickListener(new SetFrameTypeListener());

		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			Intent i = getIntent();
			String strImageUri = i.getStringExtra("ImageURI");

			mUri = Uri.parse(strImageUri);

			if (mUri == null) {
				Toast.makeText(this,
						"Can't continue without image data to crop.",
						Toast.LENGTH_LONG).show();
				finish();
			} else {
				// If we exit the activity without saving, we should return the
				// original image.
				Intent intent = new Intent();
				intent.putExtra(KEY_DATA, bitmap);
				setResult(RESULT_CANCELED, intent);

				try {
					resizeBitmapAndWireToView();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				initFrameWithType(1);

				// Wire up the "save" button
				cancelBtn.setOnClickListener(new CancelClickListener());
				nextBtn.setOnClickListener(new SaveClickListener());
			}

		} else {
			Toast.makeText(this, "Can't continue without image data to crop.",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	public void initFrameWithType(int type) {
		mBitMapHeight = Config.getScreenSize(this).y;
		mBitMapWidth = ((bitmap.getWidth() * Config.getScreenSize(this).y) / bitmap
				.getHeight());

		int frameWidth = mBitMapWidth;
		int frameHeight = 0;

		int startX;
		int startY;

		int minX = mScreenWidth / 2 - mBitMapWidth / 2;
		int maxX = mScreenWidth / 2 + mBitMapWidth / 2;

		mDistX = (int) (mScreenWidth / 2 - mBitMapWidth / 2);
		// mDistY = (int)(mScreenWidth/2 - mBitMapHeig/2);

		float ratio = 1.0f;

		switch (type) {
		case 2:
			ratio = 0.75f;

			break;
		case 3:
			ratio = 0.5625f;
			break;
		default:

			break;
		}

		frameHeight = (int) (frameWidth * ratio);

		if (frameHeight > mBitMapHeight) {
			frameHeight = mBitMapHeight;
			frameWidth = (int) (frameHeight / ratio);
		}

		startX = mScreenWidth / 2 - frameWidth / 2;
		startY = mScreenHeight / 2 - frameHeight / 2;

		// mDistX = startX;
		// mDistY = startY;

		cropOverlayView.setRect(startX, startY, startX + frameWidth, startY
				+ frameHeight);

		cropOverlayView.setMinX(minX);
		cropOverlayView.setMaxX(maxX);
		cropOverlayView.setMinY(0);
		cropOverlayView.setMaxY(mBitMapHeight);

		cropOverlayView.setType(type);

		cropOverlayView.setOnTouchListener(new MoveAndCropListener(true,
				getApplicationContext()));
	}

	private void resizeBitmapAndWireToView() throws FileNotFoundException {
		System.gc();

		ContentResolver cr = getContentResolver();
		InputStream in = cr.openInputStream(mUri);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 2;
		bitmap = BitmapFactory.decodeStream(in, null, options);

		cropImageView.setImageBitmap(bitmap);
	}

	private class CancelClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class SaveClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Bitmap croppedBitmap = getCroppedBitmap();

			EditPictureView.mImg = croppedBitmap;

			int height = Config.getScreenSize(CropActivity.this).y;
			float scale = height / 480.0f;

			// EditPictureView.mSmallImg = getResizedBitmap(croppedBitmap,
			// (int)(120.0f * scale), (int)((int)(120.0f * scale) *
			// cropOverlayView.getRatio()));

			Intent i = new Intent(CropActivity.this, EditPictureView.class);
			startActivity(i);
		}

	}

	private class SetFrameTypeListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (v == mButton1X1) {
				EditPictureView.mCropType = 1;
				initFrameWithType(1);
			}

			else if (v == mButton4X3) {
				EditPictureView.mCropType = 2;
				initFrameWithType(2);
			} else {
				EditPictureView.mCropType = 3;
				initFrameWithType(3);
			}
		}
	}

	private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
		Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), config);
		Canvas canvas = new Canvas(convertedBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return convertedBitmap;
	}

	public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
			int bitmapHeight) {
		return Bitmap
				.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
	}

	private Bitmap getCroppedBitmap() {

		BitmapDrawable drawable = (BitmapDrawable) cropImageView.getDrawable();
		Bitmap cropBitmap = drawable.getBitmap();

		float xRatio = (float) cropBitmap.getWidth() / (float) mBitMapWidth;
		float yRatio = (float) cropBitmap.getHeight() / (float) mBitMapHeight;

		int startx = (int) (cropOverlayView.getStartx() * xRatio)
				- (int) (mDistX * xRatio);
		int starty = (int) (cropOverlayView.getStarty() * yRatio);
		int width = (int) (cropOverlayView.getEndx() * xRatio - (int) (mDistX * xRatio))
				- startx;
		int height = (int) (cropOverlayView.getEndy() * yRatio) - starty;

		if (startx <= 0)
			startx = 0;

		Bitmap newBitmap = Bitmap.createBitmap(cropBitmap, startx, starty,
				width, height);

		// 20140127 resize...
		int newHeight = Config.getScreenSize(this).y;
		int newWidth = (int) ((float) newHeight / cropOverlayView.getRatio());

		if (newWidth > (int) ((float) Config.getScreenSize(this).y / 0.75)) {
			newWidth = (int) ((float) Config.getScreenSize(this).y / 0.75);
			newHeight = (int) ((float) newWidth * cropOverlayView.getRatio());
		}

		newBitmap = getResizedBitmap(newBitmap, newWidth, newHeight);

		newBitmap = convert(newBitmap, Bitmap.Config.RGB_565);
		// newBitmap.
		return newBitmap;
	}

	@Override
	public void onRequestComplete(Object[] results) {
		// TODO Auto-generated method stub
		Log.d("result------>", "result---->" + results[0].toString());
	}

}

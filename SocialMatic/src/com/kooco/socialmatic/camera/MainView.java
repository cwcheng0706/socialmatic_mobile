package com.kooco.socialmatic.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;

public class MainView extends SherlockFragmentActivity {

	private static View mMainView;

	static Face[] detectedFaces;

	private static int RESULT_LOAD_IMAGE = 1;

	public static String mDeviceID = "";

	private static byte[] mFrontCameraData = null;
	private static byte[] mFaceCameraData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().hide();

		// hide status bar
		// ---------------------------------------------
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			MainFragment fragment = new MainFragment();
			fm.beginTransaction().add(android.R.id.content, fragment).commit();
		}

	}

	public static class MainFragment extends SherlockFragment implements
			Callback {

		private View mView;

		private Context mContext;

		// TextView prompt;
		private Button btnTakePicture;
		private Button btnPictureFile;
		private Button btnFlash;
		private Button btnCameraSelectType;

		static DrawingView drawingView = null;

		static DrawingView mFaceDrawingView = null;

		SurfaceHolder surfaceHolder = null;
		SurfaceHolder mFaceSurfaceHolder = null;

		static Camera camera;
		static Camera mBackCamera = null;

		boolean mOpenCamerasFlag = false;

		CameraSurfaceView cameraSurfaceView;
		CameraSurfaceView mFaceCameraSurfaceView;

		LayoutInflater controlInflater = null;

		private int lighType = 0;

		private ScheduledExecutorService myScheduledExecutorService;

		boolean previewing = false;

		boolean mFaceCameraPreviewing = false;

		String mFrontFilePath = "";

		View mViewControl = null;

		static MainFragment newInstance() {
			MainFragment f = new MainFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			f.setArguments(args);

			return f;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			float px = convertDIPtoPixel(1.0f);
		}

		@Override
		public void onPause() {

			super.onPause();
		}

		@Override
		public void onResume() {

			super.onResume();
		}

		private int getPhotoAmountFromDisk() {
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

			File file = new File(path, "SocialMatic/");

			int amount = 0;

			if (file.isDirectory()) {
				File[] listFile = file.listFiles();

				amount = listFile.length;
			}

			return amount;
		}

		public float convertDIPtoPixel(float dp) {
			final float scale = getResources().getDisplayMetrics().density;
			float pixels = (dp * scale);
			return pixels;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			mView = inflater
					.inflate(R.layout.camera_mainview, container, false);
			mContext = mView.getContext();

			((Activity) mContext).getWindow().setFormat(PixelFormat.UNKNOWN);
			cameraSurfaceView = (CameraSurfaceView) mView
					.findViewById(R.id.camerapreview);

			mFaceCameraSurfaceView = (CameraSurfaceView) mView
					.findViewById(R.id.face_camerapreview);

			// open camera
			// -------------------------------------------
			Log.d("No of cameras", Camera.getNumberOfCameras() + "");

			for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {

				CameraInfo camInfo = new CameraInfo();

				Camera.getCameraInfo(camNo, camInfo);

				if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_BACK)) {

					camera = Camera.open(camNo);
				}
			}

			if (camera == null) {

				for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {

					CameraInfo camInfo = new CameraInfo();

					Camera.getCameraInfo(camNo, camInfo);

					if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {

						camera = Camera.open(camNo);
					}

				}

			}
			// -------------------------------------------

			// open face camera
			// -------------------------------------------
			for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {

				CameraInfo camInfo = new CameraInfo();

				Camera.getCameraInfo(camNo, camInfo);

				if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {

					try {
						mBackCamera = Camera.open(camNo);
						mOpenCamerasFlag = true;
					} catch (Exception e) {
						mOpenCamerasFlag = false;
					}
				}
			}

			camera.release();
			camera = null;
			try {
				mBackCamera.release();
				mBackCamera = null;
			} catch (Exception e) {

			}
			// -------------------------------------------

			surfaceHolder = cameraSurfaceView.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			mFaceSurfaceHolder = mFaceCameraSurfaceView.getHolder();
			mFaceSurfaceHolder.addCallback(this);
			mFaceSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			// --------------------------------------------------------------
			Display display = ((Activity) mContext).getWindowManager()
					.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);
			float scale = ((Activity) mContext).getResources()
					.getDisplayMetrics().density;

			// DisplayMetrics displaymetrics = new DisplayMetrics();
			DisplayMetrics displaymetrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(displaymetrics);
			float sHeight = displaymetrics.ydpi;
			float sWidth = displaymetrics.xdpi;
			// --------------------------------------------------------------

			return mView;
		}

		ShutterCallback myShutterCallback = new ShutterCallback() {

			@Override
			public void onShutter() {
				// TODO Auto-generated method stub

			}
		};

		PictureCallback myPictureCallback_RAW = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] arg0, Camera arg1) {
				// TODO Auto-generated method stub

			}
		};

		PictureCallback myPictureCallback_JPG = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera c) {
				// TODO Auto-generated method stub

				if (c == camera) {
					mFrontCameraData = data;
				}

				else {
					mFaceCameraData = data;
				}

				if (mFrontCameraData != null) {
					Drawable bothDrawable = getResources().getDrawable(
							R.drawable.camera_both);
					if (btnCameraSelectType.getBackground().getConstantState()
							.equals(bothDrawable.getConstantState())) {
						byte[] mergeImage = mergeImage();
						savefile(mergeImage);
					} else
						savefile(mFrontCameraData);
				}

			}
		};

		public void savefile(byte[] data) {
			Date d = new Date();

			CharSequence s = DateFormat.format("yyyyMMddss", d.getTime());
			File path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File file = new File(path, "SocialMatic/" + s + ".jpg");

			file.getParentFile().mkdirs();

			Uri uriTarget = Uri.fromFile(file);

			OutputStream imageFileOS;
			try {

				imageFileOS = ((Activity) mContext).getContentResolver()
						.openOutputStream(uriTarget);

				imageFileOS.write(data);
				imageFileOS.flush();
				imageFileOS.close();

				mFrontFilePath = uriTarget.toString();

				goEditView();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private byte[] Bitmap2Bytes(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			return baos.toByteArray();
		}

		public byte[] mergeImage() {

			Bitmap base = BitmapFactory.decodeByteArray(mFrontCameraData, 0,
					mFrontCameraData.length);
			Bitmap faceBitmap = BitmapFactory.decodeByteArray(mFaceCameraData,
					0, mFaceCameraData.length);

			// Create a matrix to be used to transform the bitmap
			Matrix mirrorMatrix = new Matrix();
			// Set the matrix to mirror the image in the x direction
			mirrorMatrix.preScale(-1.0f, 1.0f);
			// Create a flipped sprite using the transform matrix and the
			// original sprite
			Bitmap flipFaceBitmap = Bitmap.createBitmap(faceBitmap, 0, 0,
					faceBitmap.getWidth(), faceBitmap.getHeight(),
					mirrorMatrix, false);

			Bitmap bmOverlay = Bitmap.createBitmap(base.getWidth(),
					base.getHeight(), base.getConfig());
			Canvas canvas = new Canvas(bmOverlay);
			canvas.drawBitmap(base, new Matrix(), null);
			canvas.drawBitmap(flipFaceBitmap, 600, 400, null);

			return Bitmap2Bytes(bmOverlay);
		}

		private void goEditView() {
			Intent i = new Intent(mContext, EditPictureView.class);

			i.putExtra("ImageURI", mFrontFilePath);

			startActivity(i);
		}

		public void touchFocus(final Rect tfocusRect) {

			// Convert from View's width and height to +/- 1000
			final Rect targetFocusRect = new Rect(tfocusRect.left * 2000
					/ drawingView.getWidth() - 1000, tfocusRect.top * 2000
					/ drawingView.getHeight() - 1000, tfocusRect.right * 2000
					/ drawingView.getWidth() - 1000, tfocusRect.bottom * 2000
					/ drawingView.getHeight() - 1000);

			final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
			Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
			focusList.add(focusArea);

			// -----------------------------------------------------
			Parameters para = camera.getParameters();
			para.setFocusAreas(focusList);
			para.setMeteringAreas(focusList);
			camera.setParameters(para);

			camera.autoFocus(myAutoFocusCallback);
			// -----------------------------------------------------

			drawingView.setHaveTouch(true, tfocusRect);
			drawingView.invalidate();
		}

		FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {

			@Override
			public void onFaceDetection(Face[] faces, Camera tcamera) {

				if (faces.length == 0) {
					// prompt.setText(" No Face Detected! ");
					drawingView.setHaveFace(false);
				} else {
					// prompt.setText(String.valueOf(faces.length) +
					// " Face Detected :) ");
					drawingView.setHaveFace(true);
					detectedFaces = faces;

					// Set the FocusAreas using the first detected face
					List<Camera.Area> focusList = new ArrayList<Camera.Area>();
					Camera.Area firstFace = new Camera.Area(faces[0].rect, 1000);
					focusList.add(firstFace);

					Parameters para = camera.getParameters();

					if (para.getMaxNumFocusAreas() > 0) {
						para.setFocusAreas(focusList);
					}

					if (para.getMaxNumMeteringAreas() > 0) {
						para.setMeteringAreas(focusList);
					}

					camera.setParameters(para);

					// btnTakePicture.setEnabled(false);

					// Stop further Face Detection
					camera.stopFaceDetection();

					// btnTakePicture.setEnabled(false);

					// Delay call autoFocus(myAutoFocusCallback)
					myScheduledExecutorService = Executors
							.newScheduledThreadPool(1);
					myScheduledExecutorService.schedule(new Runnable() {
						public void run() {
							camera.autoFocus(myAutoFocusCallback);
						}
					}, 500, TimeUnit.MILLISECONDS);

				}

				drawingView.invalidate();

			}
		};

		AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) {

				// Log.d("myAutoFocusCallback....................",
				// "myAutoFocusCallback...........");
				// TODO Auto-generated method stub
				/*
				 * if (arg0) { btnTakePicture.setEnabled(true);
				 * camera.cancelAutoFocus(); }
				 * 
				 * float focusDistances[] = new float[3];
				 * arg1.getParameters().getFocusDistances(focusDistances);
				 */
				/*
				 * prompt.setText("Optimal Focus Distance(meters): " +
				 * focusDistances
				 * [Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX]);
				 */
			}
		};

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
					&& null != data) {
				Uri selectedImage = data.getData();

				Log.d("setOnItemClickListener",
						"mPhoto_adapter.data[position]--->"
								+ selectedImage.toString());

				CropImageView.mCropFlag = true;

				Intent i = new Intent(mContext, EditPictureView.class);

				i.putExtra("ImageURI", selectedImage.toString());

				startActivity(i);

			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			if (holder == mFaceSurfaceHolder) {
				if (mFaceCameraPreviewing) {
					mBackCamera.stopFaceDetection();
					mBackCamera.stopPreview();
					mFaceCameraPreviewing = false;
				}
				if (mBackCamera != null) {
					try {

						// -----------------------------------------------------------
						final DisplayMetrics dm = this.getResources()
								.getDisplayMetrics();
						Camera.Parameters parameters = mBackCamera
								.getParameters();

						float d = getResources().getDisplayMetrics().density;
						Camera.Size cSize = getBestPreviewSize(mBackCamera,
								(int) (400 * d), (int) (300 * d));

						parameters.setPreviewSize(cSize.width, cSize.height);

						mBackCamera.setParameters(parameters);

						mBackCamera.setPreviewDisplay(mFaceSurfaceHolder);
						mBackCamera.startPreview();

						mFaceCameraPreviewing = true;

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				return;
			}

			// TODO Auto-generated method stub
			if (previewing) {
				camera.stopFaceDetection();
				camera.stopPreview();
				previewing = false;
			}

			if (camera != null) {
				try {

					// -----------------------------------------------------------
					final DisplayMetrics dm = this.getResources()
							.getDisplayMetrics();
					Camera.Parameters parameters = camera.getParameters();

					float d = getResources().getDisplayMetrics().density;

					Camera.Size cSize = getBestPreviewSize(camera,
							(int) (800 * d), (int) (600 * d));

					parameters.setPreviewSize(cSize.width, cSize.height);

					camera.setParameters(parameters);

					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();

					previewing = true;

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private Camera.Size getBestPreviewSize(Camera c, int width, int height) {
			Camera.Size result = null;
			Camera.Parameters p = c.getParameters();
			for (Camera.Size size : p.getSupportedPreviewSizes()) {

				if (size.width <= width && size.height <= height) {
					if (result == null) {
						result = size;
					} else {
						int resultArea = result.width * result.height;
						int newArea = size.width * size.height;

						if (newArea > resultArea) {
							result = size;
						}
					}
				}
			}
			return result;

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

			System.gc();

			// TODO Auto-generated method stub

			if (holder == mFaceSurfaceHolder) {

				for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {

					CameraInfo camInfo = new CameraInfo();

					Camera.getCameraInfo(camNo, camInfo);

					if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {

						try {
							mBackCamera = Camera.open(camNo);
							mOpenCamerasFlag = true;
						} catch (Exception e) {
							mOpenCamerasFlag = false;
						}
					}
				}

				mFaceDrawingView = new DrawingView(mContext);

				LayoutParams layoutParamsDrawing = new LayoutParams(120, 90);

				((Activity) mContext).addContentView(mFaceDrawingView,
						layoutParamsDrawing);

				return;
			}

			for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {

				CameraInfo camInfo = new CameraInfo();

				Camera.getCameraInfo(camNo, camInfo);

				if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_BACK)) {

					camera = Camera.open(camNo);
				}
			}

			if (camera == null) {

				for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {

					CameraInfo camInfo = new CameraInfo();

					Camera.getCameraInfo(camNo, camInfo);

					if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {

						camera = Camera.open(camNo);
					}

				}

			}

			camera.setFaceDetectionListener(faceDetectionListener);

			// create view components while view will back to load
			// -------------------------------------------

			drawingView = new DrawingView(mContext);

			LayoutParams layoutParamsDrawing = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

			((Activity) mContext).addContentView(drawingView,
					layoutParamsDrawing);

			controlInflater = LayoutInflater.from(mContext
					.getApplicationContext());

			// remove view and readd
			// -------------------------------------------
			if (mViewControl != null) {
				ViewGroup vg = (ViewGroup) (mViewControl.getParent());
				vg.removeView(mViewControl);
			}
			// -------------------------------------------

			mViewControl = controlInflater.inflate(R.layout.control, null);

			LayoutParams layoutParamsControl = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			((Activity) mContext).addContentView(mViewControl,
					layoutParamsControl);

			// ---------------------------------------------

			Typeface tf = Typeface.createFromAsset(mContext.getAssets(),
					"fonts/FontAwesome.ttf");

			btnCameraSelectType = (Button) mViewControl
					.findViewById(R.id.takepicture_type);

			if (mOpenCamerasFlag)
				btnCameraSelectType.setVisibility(View.VISIBLE);
			else
				btnCameraSelectType.setVisibility(View.GONE);

			mFaceCameraSurfaceView.setVisibility(View.GONE);

			btnCameraSelectType
					.setOnClickListener(new Button.OnClickListener() {

						@Override
						public void onClick(View view) {

							Drawable frontDrawable = getResources()
									.getDrawable(R.drawable.camera_front);
							Drawable bothDrawable = getResources().getDrawable(
									R.drawable.camera_both);
							if (btnCameraSelectType.getBackground()
									.getConstantState()
									.equals(frontDrawable.getConstantState())) {
								mFaceCameraSurfaceView
										.setVisibility(View.VISIBLE);
								btnCameraSelectType.setBackground(bothDrawable);
							} else {
								mFaceCameraSurfaceView.setVisibility(View.GONE);
								btnCameraSelectType
										.setBackground(frontDrawable);
							}
						}
					});

			btnPictureFile = (Button) mViewControl
					.findViewById(R.id.picturefile);

			btnPictureFile.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					/*
					 * String ImagePath = "file://" + 路徑; Uri uri =
					 * ((Activity)mContext).getContentResolver().insert(
					 * Media.EXTERNAL_CONTENT_URI;
					 */
					/*
					 * File path = Environment
					 * .getExternalStoragePublicDirectory
					 * (Environment.DIRECTORY_PICTURES); File file = new
					 * File(path, "SocialMatic/");
					 * 
					 * Uri uriTarget = Uri.fromFile(file);
					 */

					/*
					 * Uri baseUri = Uri
					 * .parse("file://storage/sdcard0/Pictures/SocialMatic");
					 * 
					 * Intent i = new Intent(Intent.ACTION_PICK,
					 * Media.EXTERNAL_CONTENT_URI);
					 * 
					 * startActivityForResult(i, RESULT_LOAD_IMAGE);
					 */
					SelectPhotoView.mCallView = "CameraMainView";

					Intent i = new Intent(mContext, SelectPhotoView.class);
					startActivity(i);

				}
			});

			if (getPhotoAmountFromDisk() <= 0) {
				btnPictureFile.setEnabled(false);
			} else {
				btnPictureFile.setEnabled(true);
			}

			btnTakePicture = (Button) mViewControl
					.findViewById(R.id.takepicture);

			btnTakePicture.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					camera.takePicture(myShutterCallback,
							myPictureCallback_RAW, myPictureCallback_JPG);

					// ----------------------------------------
					Drawable bothDrawable = getResources().getDrawable(
							R.drawable.camera_both);

					if (btnCameraSelectType.getBackground().getConstantState()
							.equals(bothDrawable.getConstantState())) {
						if (mBackCamera != null) {
							mBackCamera.takePicture(myShutterCallback,
									myPictureCallback_RAW,
									myPictureCallback_JPG);
						}
					}
					// ----------------------------------------

				}
			});

			final Parameters p = camera.getParameters();

			float d = getResources().getDisplayMetrics().density;

			p.set("jpeg-quality", 100);
			p.setPictureFormat(PixelFormat.JPEG);
			p.setPictureSize(800, 600);
			camera.setParameters(p);

			btnFlash = (Button) mViewControl.findViewById(R.id.flashlight);

			btnFlash.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (lighType == 0) {

						// Log.i("info", "torch is turn off!");
						Drawable replacer = getResources().getDrawable(
								R.drawable.camera_option_flash);
						btnFlash.setBackground(replacer);

						p.setFlashMode(Parameters.FLASH_MODE_ON);
						camera.setParameters(p);
						// camera.stopPreview();
						lighType = 1;

					} else if (lighType == 1) {

						Drawable replacer = getResources().getDrawable(
								R.drawable.camera_option_flash_auto);
						btnFlash.setBackground(replacer);

						p.setFlashMode(Parameters.FLASH_MODE_AUTO);

						camera.setParameters(p);
						// camera.startPreview();
						lighType = 2;
					} else if (lighType == 2) {
						Drawable replacer = getResources().getDrawable(
								R.drawable.camera_option_noflash);
						btnFlash.setBackground(replacer);

						p.setFlashMode(Parameters.FLASH_MODE_OFF);
						camera.setParameters(p);
						// camera.stopPreview();
						lighType = 0;
					}

				}
			});
			// -------------------------------------------
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

			if (holder == mFaceSurfaceHolder) {
				if (mBackCamera != null) {
					mBackCamera.stopPreview();
					mBackCamera.release();

					mBackCamera = null;
					mFaceCameraPreviewing = false;
				}

				Handler localHandler = new Handler();
				localHandler.post(new Runnable() {

					@Override
					public void run() {
						((ViewGroup) mFaceDrawingView.getParent())
								.removeView(mFaceDrawingView);
						mFaceDrawingView = null;
					}
				});

				return;
			}

			// TODO Auto-generated method stub
			// camera.stopFaceDetection();
			camera.stopPreview();
			camera.release();

			// avoid remoe view exception
			// ------------------------------------------
			Handler localHandler = new Handler();
			localHandler.post(new Runnable() {

				@Override
				public void run() {
					((ViewGroup) drawingView.getParent())
							.removeView(drawingView);
					drawingView = null;

					lighType = 0;

					((ViewGroup) btnTakePicture.getParent())
							.removeView(btnTakePicture);

					((ViewGroup) btnPictureFile.getParent())
							.removeView(btnPictureFile);
					((ViewGroup) btnFlash.getParent()).removeView(btnFlash);
				}
			});
			// ------------------------------------------

			camera = null;
			previewing = false;
		}
	}

	private static class DrawingView extends View {

		boolean haveFace;
		Paint drawingPaint;

		boolean haveTouch;
		Rect touchArea;

		public DrawingView(Context context) {
			super(context);
			haveFace = false;
			drawingPaint = new Paint();
			drawingPaint.setColor(Color.GREEN);
			drawingPaint.setStyle(Paint.Style.STROKE);
			drawingPaint.setStrokeWidth(2);

			haveTouch = false;
		}

		public void setHaveFace(boolean h) {
			haveFace = h;
		}

		public void setHaveTouch(boolean t, Rect tArea) {
			haveTouch = t;
			touchArea = tArea;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			/*
			 * if (haveFace) {
			 * 
			 * // Camera driver coordinates range from (-1000, -1000) to (1000,
			 * // 1000). // UI coordinates range from (0, 0) to (width, height).
			 * 
			 * int vWidth = getWidth(); int vHeight = getHeight();
			 * 
			 * for (int i = 0; i < detectedFaces.length; i++) {
			 * 
			 * if (i == 0) { drawingPaint.setColor(Color.GREEN); } else {
			 * drawingPaint.setColor(Color.RED); }
			 * 
			 * int l = detectedFaces[i].rect.left; int t =
			 * detectedFaces[i].rect.top; int r = detectedFaces[i].rect.right;
			 * int b = detectedFaces[i].rect.bottom; int left = (l + 1000) *
			 * vWidth / 2000; int top = (t + 1000) * vHeight / 2000; int right =
			 * (r + 1000) * vWidth / 2000; int bottom = (b + 1000) * vHeight /
			 * 2000; canvas.drawRect(left, top, right, bottom, drawingPaint); }
			 * } else { canvas.drawColor(Color.TRANSPARENT); }
			 * 
			 * if (haveTouch) { drawingPaint.setColor(Color.BLUE);
			 * canvas.drawRect(touchArea.left, touchArea.top, touchArea.right,
			 * touchArea.bottom, drawingPaint); }
			 */
		}

	}
}

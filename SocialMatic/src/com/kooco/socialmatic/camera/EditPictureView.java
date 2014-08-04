package com.kooco.socialmatic.camera;

import gesturedetectors.RotateGestureDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage.OnPictureSavedListener;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.devsmart.android.ui.HorizontalListView;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.lazylist.LazyAdapter;
import com.kooco.tool.ConvolutionMatrix;
import com.kooco.tool.GPUImageFilterTools;
import com.kooco.tool.GPUImageFilterTools.FilterType;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class EditPictureView extends SherlockFragmentActivity implements
		OnPictureSavedListener, OnGestureListener, WebManagerCallbackInterface {

	private GPUImageView mGPUImageView;
	private GPUImageFilterTools mGPUImageFilterTools;

	public static Bitmap mImg;
	public static int mCropType = 2;

	@SuppressWarnings("unused")
	public class ViewInfo {
		public View mView;
		public String type = "image";
		public float mScaleFactor = .4f;
		public float mRotationDegrees = 0.f;
		public float mFocusX = 0.f;
		public float mFocusY = 0.f;
		public int mImageHeight, mImageWidth;

		public int mSelectViewCenterX = 0;
		public int mSelectViewCenterY = 0;

		public Matrix mMatrix = new Matrix();

		public ViewInfo(View view) {
			mView = view;
		}
	};

	public List<ViewInfo> addViewList = new ArrayList<ViewInfo>();

	private List<Integer> allIconsList = new ArrayList<Integer>();
	HorizontalListView mListview;

	private static String[] dataObjects;
	private final String[] mEffectObjects = { "Sharpness", "Exposure",
			"Contrast", "Brightness", "Saturation", "Color temp" };

	private ImageView mTmpImageView = null;
	private ImageView mFilterImageView = null;

	private Button mCancelBtn;
	private Button mShareBtn;
	private Button mSaveBtn;
	private Button mPrintBtn;
	private Button mIconBtn;
	private Button mTextBtn;
	private Button mFilterBtn;

	private Button mEffectBtn;

	private Button mTmpClickEffectBtn;

	ImageView mBackgroundView;

	GPUImageFilter mSelectFilter = null;

	public RelativeLayout mRootLayout;

	private BaseAdapter mAdapter;
	final int[] mLstViewLocation = new int[2];

	private int mType = 1;

	// Gesture dector use for image scale ， rotate or move action judge
	// --------------------------------
	private GestureDetector mDetector;

	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector mRotateDetector;

	private GestureDetector mDoubleTapGestureDetector;
	// --------------------------------

	private ViewInfo mNowSelectViewInfo = null;

	private int mBeginMotionX;
	private int mBeginMotionY;

	private boolean mSelectIconFlag = false;

	private LinearLayout mPresetsBar;
	private LinearLayout mPropertiesBar;
	private RelativeLayout mSettingsLayout;

	private boolean mIsSettingView = false;

	static int mTextColor = -33554432;

	private Bitmap mCurrentBitmap;

	private boolean mChangeFalg = false;

	private Uri mUri = null;

	private String[] mFileStrings;
	private File[] mListFile;

	private ListView mPhoto_list;
	private LazyAdapter mPhoto_adapter;

	//
	// --------------------------------
	private int mSelectFilterIndex = -1;
	private int mSelectEffectIndex = -1;
	// --------------------------------

	// effect progess value set default is -1
	// --------------------------------
	public static int mSharpnessEffectProgress = -1;
	public static int mExposureEffectProgress = -1;
	public static int mContrastEffectProgress = -1;
	public static int mBrightnessEffectProgress = -1;
	public static int mSaturationEffectProgress = -1;
	public static int mColorTempEffectProgress = -1;
	// --------------------------------

	private static boolean mChangeSelectFlag = false;

	GPUImageView mOrgGPUImageView;

	private Bitmap mTmpBitmap;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			mListview.getLocationOnScreen(mLstViewLocation);
		}
	}

	private class DoubleTapListener implements
			GestureDetector.OnDoubleTapListener {
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub

			if (mNowSelectViewInfo != null)
				ShowMsgDialog("Remove this object from screen?");

			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDetector = new GestureDetector(this, this);
		// Setup Gesture Detectors
		mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
		mRotateDetector = new RotateGestureDetector(this, new RotateListener());

		mDoubleTapGestureDetector = new GestureDetector(this);
		mDoubleTapGestureDetector
				.setOnDoubleTapListener(new DoubleTapListener());

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.edit_picture_view);
		mRootLayout = (RelativeLayout) findViewById(R.id.rootlayout);

		LayoutInflater mInflater = LayoutInflater.from(this);
		View contentView = mInflater.inflate(R.layout.edit_text_view, null);

		mSettingsLayout = (RelativeLayout) contentView
				.findViewById(R.id.settings_layout);
		mPresetsBar = (LinearLayout) contentView.findViewById(R.id.presets_bar);
		mPresetsBar.setVisibility(View.INVISIBLE);

		mPropertiesBar = (LinearLayout) contentView
				.findViewById(R.id.properties_bar);
		mPropertiesBar.setVisibility(View.INVISIBLE);

		int counter = 1;
		while (true) {

			int result = getResources().getIdentifier(
					"char_a_" + String.format("%02d", counter), "drawable",
					getPackageName());

			if (result == 0)
				break;
			else {
				allIconsList.add(result);
				counter++;
			}
		}

		counter = 1;
		while (true) {

			int result = getResources().getIdentifier(
					"obj_" + String.format("%02d", counter), "drawable",
					getPackageName());

			if (result == 0)
				break;
			else {
				allIconsList.add(result);
				counter++;
			}
		}

		// ---------------------------------------
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

				try {
					resizeBitmapAndWireToView();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			Toast.makeText(this, "Can't continue without image data to crop.",
					Toast.LENGTH_LONG).show();
			finish();
		}
		// ---------------------------------------

		mListview.setY(30);

		mCancelBtn = (Button) findViewById(R.id.cancelButton);

		mShareBtn = (Button) findViewById(R.id.shareButton);
		mSaveBtn = (Button) findViewById(R.id.saveButton);
		mPrintBtn = (Button) findViewById(R.id.printButton);
		mIconBtn = (Button) findViewById(R.id.iconButton);
		mTextBtn = (Button) findViewById(R.id.textButton);
		mFilterBtn = (Button) findViewById(R.id.filterButton);

		mEffectBtn = (Button) findViewById(R.id.effectButton);

		mFilterBtn.setOnClickListener(new CreateListViewClickListener());
		mIconBtn.setOnClickListener(new CreateListViewClickListener());
		mEffectBtn.setOnClickListener(new CreateListViewClickListener());

		mCancelBtn.setOnClickListener(new CancelClickListener());
		mSaveBtn.setOnClickListener(new SaveClickListener());
		mTextBtn.setOnClickListener(new EditTextListener());
		mShareBtn.setOnClickListener(new UploadListener());

		mTmpClickEffectBtn = mFilterBtn;

		// --------------------------------------

		getPhotoFromDisk();

		mPhoto_list = (ListView) findViewById(R.id.photo_list);
		mPhoto_adapter = new LazyAdapter(this, mFileStrings, "Edit_Photo");
		mPhoto_list.setAdapter(mPhoto_adapter);

		mPhoto_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				resetEffectParmater();

				System.gc();

				ContentResolver cr = getContentResolver();
				InputStream in = null;
				try {
					in = cr.openInputStream(Uri.parse("file://"
							+ mPhoto_adapter.data[position]));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				options.inInputShareable = true;

				if (Config.getScreenSize(EditPictureView.this).x < 1200)
					options.inSampleSize = 4;
				else
					options.inSampleSize = 1;

				mCurrentBitmap = BitmapFactory.decodeStream(in, null, options);

				mGPUImageView.setImage(mCurrentBitmap);

			}
		});
		// --------------------------------------
	}

	private void getPhotoFromDisk() {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		File file = new File(path, "SocialMatic/");

		if (file.isDirectory()) {
			mListFile = file.listFiles();

			mFileStrings = new String[mListFile.length];

			for (int i = 0; i < mListFile.length; i++) {
				mFileStrings[(mListFile.length - 1) - i] = mListFile[i]
						.getAbsolutePath();
			}
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	private void resizeBitmapAndWireToView() throws FileNotFoundException {
		System.gc();

		ContentResolver cr = getContentResolver();
		InputStream in = cr.openInputStream(mUri);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;

		if (Config.getScreenSize(EditPictureView.this).x < 1200)
			options.inSampleSize = 4;
		else
			options.inSampleSize = 1;

		mImg = BitmapFactory.decodeStream(in, null, options);

		mGPUImageFilterTools = new GPUImageFilterTools();

		dataObjects = mGPUImageFilterTools.filters.names
				.toArray(new String[mGPUImageFilterTools.filters.names.size()]);

		CropImageView.mCropFlag = true;

		mGPUImageView = (GPUImageView) findViewById(R.id.imageview);

		int frameHeight = (int) (Config.getScreenSize(EditPictureView.this).y * 0.75f);
		int frameWidth = frameHeight * 4 / 3;

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				frameWidth, frameHeight, Gravity.CENTER_HORIZONTAL);

		layoutParams.topMargin = 40;

		mGPUImageView.setLayoutParams(layoutParams);

		mGPUImageView.setImage(mImg);

		mOrgGPUImageView = mGPUImageView;

		float d = getResources().getDisplayMetrics().density;

		mBackgroundView = (ImageView) findViewById(R.id.background_view);
		FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(
				frameWidth + (int) (20 * d / 1.5), frameHeight
						+ (int) (20 * d / 1.5), Gravity.CENTER_HORIZONTAL);

		if (d == 3.0f)
			layoutParams1.topMargin = 19;
		else
			layoutParams1.topMargin = 30;

		mBackgroundView.setLayoutParams(layoutParams1);

		mCurrentBitmap = mGPUImageView.getDrawingCache();

		int height = Config.getScreenSize(EditPictureView.this).y;
		float scale = height / 480.0f;

		mAdapter = new ImageListAdapter();

		mListview = (HorizontalListView) findViewById(R.id.listview);
		mListview.setAdapter(mAdapter);
	}

	public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
			int bitmapHeight) {
		return Bitmap
				.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onRestart() {
		super.onRestart();

		getPhotoFromDisk();
		mPhoto_adapter.setData(mFileStrings);
		mPhoto_adapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mPhoto_list.setAdapter(null);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mIsSettingView) {
				mIsSettingView = false;
				exitBrushSetup();
				return true;
			}
		}
		return false;
	}

	public String encode(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		bitmap.recycle();
		byte[] data = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		baos = null;
		String test = Base64.encodeToString(data, Base64.DEFAULT);
		return test;
	}

	private class UploadListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String fileName = System.currentTimeMillis() + ".jpg";

			Bitmap baseBmp = mGPUImageView.getGPUImage()
					.getBitmapWithFilterApplied();

			Bitmap newBmp = mergeImage(baseBmp);
			saveBitmap("SocialMatic", fileName, newBmp, true);

		}
	}

	private class EditTextListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			mIsSettingView = true;

			EditTextView.mEditPictureView = EditPictureView.this;
			Intent i = new Intent(EditPictureView.this, EditTextView.class);
			startActivity(i);
			// enterBrushSetup();
		}
	}

	private void enterBrushSetup() {

		setContentView(mSettingsLayout);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				setPanelVerticalSlide(mPresetsBar, -1.0f, 0.0f, 300);
				setPanelVerticalSlide(mPropertiesBar, 1.0f, 0.0f, 300, true);
			}
		}, 10);

	}

	private void exitBrushSetup() {

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				setPanelVerticalSlide(mPresetsBar, 0.0f, -1.0f, 300);
				setPanelVerticalSlide(mPropertiesBar, 0.0f, 1.0f, 300, true);
			}
		}, 10);
	}

	private void setPanelVerticalSlide(LinearLayout layout, float from,
			float to, int duration) {
		setPanelVerticalSlide(layout, from, to, duration, false);
	}

	private void setPanelVerticalSlide(LinearLayout layout, float from,
			float to, int duration, boolean last) {
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, from,
				Animation.RELATIVE_TO_SELF, to);

		animation.setDuration(duration);
		animation.setFillAfter(true);
		animation.setInterpolator(this, android.R.anim.decelerate_interpolator);

		final float listenerFrom = Math.abs(from);
		final float listenerTo = Math.abs(to);
		final boolean listenerLast = last;
		final View listenerLayout = layout;
		listenerLayout.clearAnimation();

		if (listenerFrom > listenerTo) {
			listenerLayout.setVisibility(View.VISIBLE);
		}
		animation.setAnimationListener(new Animation.AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				if (listenerFrom < listenerTo) {
					listenerLayout.setVisibility(View.INVISIBLE);
					if (listenerLast) {
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								setContentView(mRootLayout);
							}
						}, 10);
					}
				} else {
					if (listenerLast) {
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							public void run() {
								mSettingsLayout
										.setBackgroundColor(Color.TRANSPARENT);
							}
						}, 10);
					}
				}
			}
		});

		listenerLayout.setAnimation(animation);
	}

	private class CreateListViewClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			Button btn = (Button) v;

			float d = getResources().getDisplayMetrics().density;

			if (mListview.getY() > Config.getScreenSize(EditPictureView.this).y
					|| !btn.equals(mTmpClickEffectBtn)) {
				if (mListview.getY() > Config
						.getScreenSize(EditPictureView.this).y)
					mListview.animate().setDuration(300)
							.translationYBy(-220 * d);

				if (btn.equals(mIconBtn)) {
					if (mType != 2) {
						mType = 2;

						mAdapter = new ImageListAdapter();
						mListview.setAdapter(mAdapter);
					}

					mListview.setAlpha(0);
					mListview.animate().setDuration(0).translationY(30);
					mListview.animate().setDuration(100).alpha(1);

					mListview.setY(30);

				} else if (btn.equals(mFilterBtn)) {
					if (mType != 1) {
						mType = 1;

						mAdapter = new ImageListAdapter();
						mListview.setAdapter(mAdapter);
					}

					mListview.setAlpha(0);

					mListview.animate().setDuration(0).translationY(30);
					mListview.setY(30);

					mListview.animate().setDuration(100).alpha(1);
				} else if (btn.equals(mEffectBtn)) {
					if (mType != 3) {
						mType = 3;

						mAdapter = new ImageListAdapter();
						mListview.setAdapter(mAdapter);
					}

					mListview.setAlpha(0);

					mListview.animate().setDuration(0).translationY(30);
					mListview.setY(30);

					mListview.animate().setDuration(100).alpha(1);
				}
			} else {
				mListview.animate().setDuration(300).translationYBy(220 * d);
			}

			mTmpClickEffectBtn = btn;
		}
	}

	private class CancelClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			resetEffectParmater();
			finish();
		}
	}

	private class SaveClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String fileName = System.currentTimeMillis() + ".jpg";

			Bitmap baseBmp = mGPUImageView.getGPUImage()
					.getBitmapWithFilterApplied();
			Bitmap newBmp = mergeImage(baseBmp);
			saveBitmap("SocialMatic", fileName, newBmp, false);
		}
	}

	public Bitmap takeScreenshot() {
		View rootView = findViewById(android.R.id.content).getRootView();
		rootView.setDrawingCacheEnabled(true);
		return rootView.getDrawingCache();
	}

	public void switchFilterTo(final GPUImageFilter filter,
			GPUImageView gpuImageView) {

		gpuImageView.setFilter(filter);
	}

	public Bitmap sharpenImage(Bitmap src, double weight) {
		// set sharpness configuration
		double[][] SharpConfig = new double[][] { { 0, -2, 0 },
				{ -2, weight, -2 }, { 0, -2, 0 } };
		// create convolution matrix instance
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		// apply configuration
		convMatrix.applyConfig(SharpConfig);
		// set weight according to factor
		convMatrix.Factor = weight - 8;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}

	private void resetEffectParmater() {
		mSelectFilter = null;
		mSharpnessEffectProgress = -1;
		mExposureEffectProgress = -1;
		mContrastEffectProgress = -1;
		mBrightnessEffectProgress = -1;
		mSaturationEffectProgress = -1;
		mColorTempEffectProgress = -1;
	}

	public class ImageListAdapter extends BaseAdapter {

		AlternateListViewAdapter mFilterSelectAdapte;
		ListView mFilter_list;

		AlternateListViewAdapter mEffectSelectAdapte;
		ListView mEffect_list;
		SeekBar mSeekBar;
		TextView mSeekbarValueLab;
		LinearLayout mSeekbarLinearLayout;

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mType == 1) {

					Drawable conerImage = getResources().getDrawable(
							R.drawable.photo_edit_addfilter_on);
					((ImageView) v).setImageDrawable(conerImage);

					if (mFilterImageView != null) {
						Drawable replacer = getResources().getDrawable(
								R.drawable.photo_edit_addfilter_shadow);
						mFilterImageView.setImageDrawable(replacer);
					}

					mFilterImageView = (ImageView) v;

					int index = Integer.parseInt(v.getTag().toString());
					FilterType filterType = mGPUImageFilterTools.filters.filters
							.get(index);
					GPUImageFilter filter = GPUImageFilterTools
							.createFilterForType(EditPictureView.this,
									filterType);

					switchFilterTo(filter, mGPUImageView);
					mGPUImageView.requestRender();
				} else if (mType == 2) {

					int resId = Integer.parseInt(v.getTag().toString());

					ImageView imgView = new ImageView(EditPictureView.this);

					Drawable d = EditPictureView.this.getResources()
							.getDrawable(resId);

					float orgImgHeight = d.getIntrinsicHeight();
					float orgImgWidth = d.getIntrinsicWidth();

					Log.d("Config.getScreenSize(EditPictureView.this).y",
							"Config.getScreenSize(EditPictureView.this).y===>"
									+ Config.getScreenSize(EditPictureView.this).y);

					float height = 100.0f
							* Config.getScreenSize(EditPictureView.this).y
							/ orgImgWidth;
					float width = orgImgWidth * height / orgImgHeight;

					RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(
							(int) width, (int) height);
					paramsImage.addRule(RelativeLayout.CENTER_IN_PARENT);
					imgView.setLayoutParams(paramsImage);

					imgView.setImageDrawable(getResources().getDrawable(resId));
					imgView.setScaleType(ImageView.ScaleType.MATRIX.FIT_CENTER);

					ViewInfo viewInfo = new ViewInfo(imgView);
					viewInfo.mMatrix.postScale(100.0f / orgImgHeight,
							100.0f / orgImgHeight);
					viewInfo.mImageHeight = (int) orgImgHeight;
					viewInfo.mImageWidth = (int) orgImgWidth;
					viewInfo.mScaleFactor = 100.0f / orgImgHeight;

					viewInfo.mMatrix.postTranslate(0, 0);

					imgView.setImageMatrix(viewInfo.mMatrix);

					addViewList.add(viewInfo);

					mRootLayout.addView(viewInfo.mView);

				} else if (mType == 3) {

				}
			}
		};

		@Override
		public int getCount() {
			if (mType == 2)
				return allIconsList.size();

			// return dataObjects.length;
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = null;

			if (mType == 3) {
				retval = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.photo_effect_item, null);

				mEffectSelectAdapte = new AlternateListViewAdapter(
						parent.getContext(), R.layout.filter_select_item,
						mEffectObjects);

				mEffect_list = (ListView) retval
						.findViewById(R.id.effect_text_list);
				mEffect_list.setAdapter(mEffectSelectAdapte);

				mSelectEffectIndex = 0;

				if (mSelectEffectIndex > -1) {
					mEffectSelectAdapte.mSelectIndex = mSelectEffectIndex;
					mEffect_list.setSelection(mSelectEffectIndex);
				}

				mSeekbarLinearLayout = (LinearLayout) retval
						.findViewById(R.id.seekbarLinearLayout);

				mSeekbarValueLab = (TextView) retval
						.findViewById(R.id.seekbarValueLab);

				mSeekBar = (SeekBar) retval.findViewById(R.id.seekBar);

				setSeekBarPosition();

				mEffect_list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {

						if (position < mEffectObjects.length) {

							mSelectEffectIndex = position;
							mEffectSelectAdapte.mSelectIndex = mSelectEffectIndex;

							for (int i = 0; i < mEffectObjects.length; i++) {
								View subView = mEffect_list.getChildAt(i);

								if (subView != null) {
									TextView tv = (TextView) subView
											.findViewById(R.id.name_textview);
									tv.setTextColor(Color.parseColor("#696969"));
								}
							}

							TextView tv = (TextView) v
									.findViewById(R.id.name_textview);
							tv.setTextColor(Color.parseColor("#00C5CD"));

							setSeekBarPosition();

						}
					}

				});

				mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub

						switch (mSelectEffectIndex) {
						case 1: // ExposureEffect
							mExposureEffectProgress = progress;
							mSeekbarValueLab.setText(String
									.valueOf(mExposureEffectProgress));
							break;
						case 2: // ContrastEffect

							mContrastEffectProgress = progress;
							mSeekbarValueLab.setText(String
									.valueOf(mContrastEffectProgress));
							break;
						case 3: // BrightnessEffect

							mBrightnessEffectProgress = progress;
							mSeekbarValueLab.setText(String
									.valueOf(mBrightnessEffectProgress));
							break;
						case 4: // SaturationEffect

							mSaturationEffectProgress = progress;
							mSeekbarValueLab.setText(String
									.valueOf(mSaturationEffectProgress));
							break;
						case 5: // ColorTempEffect

							mColorTempEffectProgress = progress;
							mSeekbarValueLab.setText(String
									.valueOf(mColorTempEffectProgress));
							break;
						default: // SharpnessEffect

							mSharpnessEffectProgress = progress;
							mSeekbarValueLab.setText(String
									.valueOf(mSharpnessEffectProgress));
							break;
						}
						updateBitmapWithEffect();

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
					}
				});

			}

			else if (mType == 1) {

				retval = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.viewitem, null);

				mFilterSelectAdapte = new AlternateListViewAdapter(
						parent.getContext(), R.layout.filter_select_item,
						dataObjects);

				if (mSelectFilterIndex > -1) {
					mFilterSelectAdapte.mSelectIndex = mSelectFilterIndex;
				}

				mFilter_list = (ListView) retval
						.findViewById(R.id.filter_text_list);

				mFilter_list.setAdapter(mFilterSelectAdapte);

				if (mSelectFilterIndex > -1) {
					mFilter_list.setSelection(mSelectFilterIndex);
				}

				mFilter_list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {

						if (position < dataObjects.length) {
							mSelectFilterIndex = position;
							mFilterSelectAdapte.mSelectIndex = mSelectFilterIndex;

							for (int i = 0; i < dataObjects.length; i++) {
								View subView = mFilter_list.getChildAt(i);

								if (subView != null) {
									TextView tv = (TextView) subView
											.findViewById(R.id.name_textview);
									tv.setTextColor(Color.parseColor("#696969"));
								}
							}

							TextView tv = (TextView) v
									.findViewById(R.id.name_textview);
							tv.setTextColor(Color.parseColor("#00C5CD"));

							FilterType filterType = mGPUImageFilterTools.filters.filters
									.get(position);
							GPUImageFilter filter = GPUImageFilterTools
									.createFilterForType(EditPictureView.this,
											filterType);

							mSelectFilter = filter;
							updateBitmapWithEffect();
						}
					}
				});

				TextView title = (TextView) retval.findViewById(R.id.title);
				Typeface tf = Typeface.createFromAsset(getAssets(),
						"fonts/31513_RCKWL.ttf");
				title.setTypeface(tf);

				Button cancel = (Button) retval.findViewById(R.id.cancelButton);

				cancel.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {

						mSelectFilterIndex = -1;
						mFilterSelectAdapte.mSelectIndex = mSelectFilterIndex;

						for (int i = 0; i < dataObjects.length; i++) {
							View subView = mFilter_list.getChildAt(i);

							if (subView != null) {
								TextView tv = (TextView) subView
										.findViewById(R.id.name_textview);
								tv.setTextColor(Color.parseColor("#696969"));
							}
						}

						GPUImageFilter filter = GPUImageFilterTools
								.createFilterForType(EditPictureView.this,
										FilterType.SATURATION);
						mSelectFilter = filter;
						updateBitmapWithEffect();
					}
				});

			} else if (mType == 2) {

				retval = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.icon_image_item, null);

				ImageView imgView = (ImageView) retval
						.findViewById(R.id.imageview);

				float size = 100.0f * Config
						.getScreenSize(EditPictureView.this).y / 480.0f;

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						(int) size, (int) size, Gravity.CENTER);
				imgView.setLayoutParams(layoutParams);
				imgView.setOnClickListener(mOnButtonClicked);
				// imgView.setOnTouchListener(mOnButtonClicked);

				imgView.setTag(allIconsList.get(position));
				new LoadImage(imgView, allIconsList.get(position), 2).execute();
			}
			return retval;
		}

		private void setSeekBarPosition() {
			switch (mSelectEffectIndex) {
			case 1: // ExposureEffect
				if (mExposureEffectProgress == -1)
					mExposureEffectProgress = 0;

				mSeekBar.setProgress(mExposureEffectProgress);
				mSeekbarValueLab.setText(String
						.valueOf(mExposureEffectProgress));
				break;
			case 2: // ContrastEffect
				if (mContrastEffectProgress == -1)
					mContrastEffectProgress = 0;

				mSeekBar.setProgress(mContrastEffectProgress);
				mSeekbarValueLab.setText(String
						.valueOf(mContrastEffectProgress));
				break;
			case 3: // BrightnessEffect
				if (mBrightnessEffectProgress == -1)
					mBrightnessEffectProgress = 0;

				mSeekBar.setProgress(mBrightnessEffectProgress);
				mSeekbarValueLab.setText(String
						.valueOf(mBrightnessEffectProgress));
				break;
			case 4: // SaturationEffect
				if (mSaturationEffectProgress == -1)
					mSaturationEffectProgress = 0;

				mSeekBar.setProgress(mSaturationEffectProgress);
				mSeekbarValueLab.setText(String
						.valueOf(mSaturationEffectProgress));
				break;
			case 5: // ColorTempEffect
				if (mColorTempEffectProgress == -1)
					mColorTempEffectProgress = 50;

				mSeekBar.setProgress(mColorTempEffectProgress);
				mSeekbarValueLab.setText(String
						.valueOf(mColorTempEffectProgress));
				break;
			default: // SharpnessEffect
				if (mSharpnessEffectProgress == -1)
					mSharpnessEffectProgress = 0;

				mSeekBar.setProgress(mSharpnessEffectProgress);
				mSeekbarValueLab.setText(String
						.valueOf(mSharpnessEffectProgress));
				break;
			}
		}

		@SuppressWarnings("unused")
		private void updateBitmapWithEffect() {
			List<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();

			if (mSelectFilter != null)
				filters.add(mSelectFilter);

			if (mExposureEffectProgress >= 0) {
				GPUImageExposureFilter exposure = new GPUImageExposureFilter();
				exposure.setExposure(mExposureEffectProgress * 0.004f);

				filters.add(exposure);
			}

			if (mContrastEffectProgress >= 0) {
				GPUImageContrastFilter contrastFilter = new GPUImageContrastFilter();

				float val = mContrastEffectProgress * 0.1f;

				if (val < 1.0f)
					val = 1.0f;

				contrastFilter.setContrast(val);
				filters.add(contrastFilter);
			}

			if (mBrightnessEffectProgress >= 0) {
				GPUImageBrightnessFilter brightness = new GPUImageBrightnessFilter();
				brightness.setBrightness(mBrightnessEffectProgress * 0.0009f);

				filters.add(brightness);
			}

			if (mSaturationEffectProgress >= 0) {

				GPUImageSaturationFilter saturation = new GPUImageSaturationFilter();
				float saturationVal = mSaturationEffectProgress * 0.1f;

				if (saturationVal < 1.0f)
					saturationVal = 1.0f;
				saturation.setSaturation(saturationVal);

				filters.add(saturation);
			}

			if (mColorTempEffectProgress >= 0) {
				GPUImageHueFilter colorTemp = new GPUImageHueFilter();
				colorTemp.setHue((mColorTempEffectProgress - 50) * (-0.6f));

				filters.add(colorTemp);
			}

			if (mSharpnessEffectProgress >= 0) {
				GPUImageSharpenFilter sharpness = new GPUImageSharpenFilter();
				sharpness.setSharpness(mSharpnessEffectProgress * 0.05f);

				filters.add(sharpness);
			}

			if (filters.size() > 0) {
				GPUImageFilterGroup groupFilter = new GPUImageFilterGroup(
						filters);
				switchFilterTo(groupFilter, mGPUImageView);
			}
		}

		public Bitmap decodeSampledBitmapFromResource(int resId, int reqWidth,
				int reqHeight) {

			Bitmap bitmap = null;
			InputStream is = null;

			int orgWidth = 0;
			int ordHeight = 0;

			final BitmapFactory.Options options = new BitmapFactory.Options();

			try {
				is = getResources().openRawResource(resId);
				is.mark(is.available());

				options.inJustDecodeBounds = true;

				BitmapFactory.decodeStream(is, null, options);

				orgWidth = options.outWidth;
				ordHeight = options.outHeight;
				is = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				is = getResources().openRawResource(resId);

				float scale = 100.0f / (float) ordHeight;
				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options,
						(int) (orgWidth * scale), 100);

				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				bitmap = BitmapFactory.decodeStream(is, null, options);
				is.close();
				return bitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		public int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) { // Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;
			if (height > reqHeight || width > reqWidth) {

				final int heightRatio = Math.round((float) height
						/ (float) reqHeight);
				final int widthRatio = Math.round((float) width
						/ (float) reqWidth);

				inSampleSize = heightRatio < widthRatio ? heightRatio
						: widthRatio;
			}
			return inSampleSize;
		}

		class LoadImage extends AsyncTask<Object, Void, Bitmap> {

			private ImageView mImv;
			private int mResId;
			private int mType;

			private LruCache<String, Bitmap> mMemoryCache;

			public LoadImage(ImageView imv, int resId, int type) {
				this.mImv = imv;
				this.mResId = resId;
				this.mType = type;
			}

			@Override
			protected Bitmap doInBackground(Object... params) {

				int w = 100;
				int h = 100;

				if (mResId > 10000) {
					return decodeSampledBitmapFromResource(mResId, w, h);
				} else {

					GPUImage gpuimage = new GPUImage(EditPictureView.this);

					FilterType filterType = mGPUImageFilterTools.filters.filters
							.get(mResId);
					GPUImageFilter filter = GPUImageFilterTools
							.createFilterForType(EditPictureView.this,
									filterType);

					gpuimage.setFilter(filter);

					return gpuimage.getBitmapWithFilterApplied();
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {

				if (result != null && mImv != null) {

					if (mImv.getTag().equals(mResId)) {

						mImv.setVisibility(View.VISIBLE);

						if (mType == 1) {
							Drawable d = new BitmapDrawable(getResources(),
									result);
							mImv.setBackground(d);
						} else
							mImv.setImageBitmap(result);
					}
				} else {
					mImv.setVisibility(View.GONE);
				}
			}

		}

	};

	@Override
	public void onPictureSaved(Uri uri) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		if (mListview.getY() > Config.getScreenSize(EditPictureView.this).y)
			mListview.animate().setDuration(300).translationYBy(-220);
		else
			mListview.animate().setDuration(300).translationYBy(220);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		mScaleDetector.onTouchEvent(event);
		mRotateDetector.onTouchEvent(event);
		mDoubleTapGestureDetector.onTouchEvent(event);
		mDetector.onTouchEvent(event);

		int touchX = (int) event.getX();
		int touchY = (int) event.getY();

		if (addViewList.size() > 0) {
			int action = event.getAction();
			if (event.getPointerCount() <= 1) {

				switch (action) {
				// MotionEvent class constant signifying a finger-down event
				case MotionEvent.ACTION_DOWN: {

					mBeginMotionX = (int) event.getX();
					mBeginMotionY = (int) event.getY();
					mSelectIconFlag = false;

					for (int i = addViewList.size() - 1; i >= 0; i--) {
						ImageView imgView = (ImageView) addViewList.get(i).mView;
						// Drawable drawable = imgView.getDrawable();
						Rect imageBounds = new Rect();
						imgView.getDrawingRect(imageBounds);

						final int[] location = new int[2];
						imgView.getLocationOnScreen(location);

						Rect imgRect = new Rect(imgView.getLeft(),
								imgView.getTop(), imgView.getLeft()
										+ imgView.getWidth(), imgView.getTop()
										+ imgView.getHeight());

						if (imgRect.contains(touchX, touchY)
								&& !mSelectIconFlag) {
							mSelectIconFlag = true;
							imgView.setBackgroundResource(R.drawable.image_border);

							mNowSelectViewInfo = addViewList.get(i);

							mNowSelectViewInfo.mSelectViewCenterX = (int) (location[0] + imageBounds
									.width() / 2.0f);
							mNowSelectViewInfo.mSelectViewCenterY = (int) (location[1] + imageBounds
									.height() / 2.0f);

							break;
						}
					}

					for (int i = 0; i < addViewList.size(); i++) {
						ImageView imgView = (ImageView) addViewList.get(i).mView;

						if (mNowSelectViewInfo != null) {
							if (imgView != mNowSelectViewInfo.mView)
								imgView.setBackgroundColor(Color
										.parseColor("#00000000"));
						} else
							imgView.setBackgroundColor(Color
									.parseColor("#00000000"));
					}

				}
					break;
				case MotionEvent.ACTION_MOVE:

					if (mNowSelectViewInfo != null && mSelectIconFlag) {

						int imgHeight = mNowSelectViewInfo.mView.getHeight();
						int imgWidth = mNowSelectViewInfo.mView.getWidth();

						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								imgWidth, imgHeight);
						params.setMargins(mNowSelectViewInfo.mView.getLeft()
								+ (touchX - mBeginMotionX),
								mNowSelectViewInfo.mView.getTop()
										+ (touchY - mBeginMotionY), 0, 0);
						mNowSelectViewInfo.mView.setLayoutParams(params);

						mNowSelectViewInfo.mSelectViewCenterX = mNowSelectViewInfo.mView
								.getLeft()
								+ (mNowSelectViewInfo.mView.getWidth() / 2);
						mNowSelectViewInfo.mSelectViewCenterY = mNowSelectViewInfo.mView
								.getTop()
								+ (mNowSelectViewInfo.mView.getHeight() / 2);

						mBeginMotionX = (int) event.getX();
						mBeginMotionY = (int) event.getY();
					}

					break;
				case MotionEvent.ACTION_UP:

					break;
				}
			}
		}

		if (mNowSelectViewInfo != null && event.getPointerCount() > 1) {
			final int[] location = new int[2];
			mNowSelectViewInfo.mView.getLocationOnScreen(location);

			float scaledImageCenterX = (mNowSelectViewInfo.mImageWidth * mNowSelectViewInfo.mScaleFactor) / 2.0f;
			float scaledImageCenterY = (mNowSelectViewInfo.mImageHeight * mNowSelectViewInfo.mScaleFactor) / 2.0f;
			mNowSelectViewInfo.mMatrix.reset();
			mNowSelectViewInfo.mMatrix.postScale(
					mNowSelectViewInfo.mScaleFactor,
					mNowSelectViewInfo.mScaleFactor);

			ImageView view = (ImageView) mNowSelectViewInfo.mView;

			// reset imageview rect with scale
			// ------------------------------------------------------
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) (mNowSelectViewInfo.mImageWidth * mNowSelectViewInfo.mScaleFactor),
					(int) (mNowSelectViewInfo.mImageHeight * mNowSelectViewInfo.mScaleFactor));

			params.setMargins(
					(int) (mNowSelectViewInfo.mSelectViewCenterX - scaledImageCenterX),
					(int) (mNowSelectViewInfo.mSelectViewCenterY - scaledImageCenterY),
					0, 0);

			view.setLayoutParams(params);
			// ------------------------------------------------------

			// rotate imageview
			// ------------------------------------------------------
			view.setImageMatrix(mNowSelectViewInfo.mMatrix);
			view.setPivotX(view.getWidth() / 2);
			view.setPivotY(view.getHeight() / 2);
			view.setRotation(mNowSelectViewInfo.mRotationDegrees);
			// ------------------------------------------------------
		}

		return super.onTouchEvent(event);
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			if (mNowSelectViewInfo != null) {
				mNowSelectViewInfo.mScaleFactor *= detector.getScaleFactor(); // scale
																				// change
																				// since
																				// previous
																				// event

				mNowSelectViewInfo.mScaleFactor = Math.max(0.1f,
						Math.min(mNowSelectViewInfo.mScaleFactor, 10.0f));
			}
			return true;
		}
	}

	private class RotateListener extends
			RotateGestureDetector.SimpleOnRotateGestureListener {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			if (mNowSelectViewInfo != null) {
				mNowSelectViewInfo.mRotationDegrees -= detector
						.getRotationDegreesDelta();
			}
			return true;
		}
	}

	public Bitmap mergeImage(Bitmap base) {
		Bitmap mBitmap = Bitmap.createBitmap(base.getWidth(), base.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		canvas.drawBitmap(base, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));

		Matrix matrix = new Matrix();

		mNowSelectViewInfo = null;

		for (int i = 0; i < addViewList.size(); i++) {
			ViewInfo viewInfo = addViewList.get(i);

			// 加入圖片在base view 的位移
			// --------------------------------------
			int adWDelta = (int) ((viewInfo.mSelectViewCenterX - ((Config
					.getScreenSize(this).x - mGPUImageView.getWidth()) / 2))
					* base.getWidth() / mGPUImageView.getWidth());
			int adHDelta = (int) ((viewInfo.mSelectViewCenterY - 40)
					* base.getHeight() / mGPUImageView.getHeight());
			// --------------------------------------

			viewInfo.mView.setBackgroundColor(Color.parseColor("#00000000"));

			viewInfo.mView.setDrawingCacheEnabled(true);

			viewInfo.mView.buildDrawingCache();

			Bitmap overlayBmp = viewInfo.mView.getDrawingCache();

			matrix.reset();

			float viewWidth = viewInfo.mView.getWidth();
			float viewHeight = viewInfo.mView.getHeight();
			float viewTop = viewInfo.mView.getTop();
			float viewLeft = viewInfo.mView.getLeft();

			float centerX = viewLeft + (viewWidth / 2.0f);
			float centerY = viewTop + (viewHeight / 2.0f);

			// 需先作旋轉在作放大縮小，避免合成時位置錯誤
			// ------------------------------------------
			matrix.postTranslate(-overlayBmp.getWidth() / 2,
					-overlayBmp.getHeight() / 2);

			matrix.postRotate(viewInfo.mRotationDegrees);

			float scaleRate = ((float) base.getWidth())
					/ ((float) mGPUImageView.getWidth());

			matrix.postScale(scaleRate, scaleRate);
			// ------------------------------------------

			// 圖片在合成圖中的位置
			// -------------------------------------------
			matrix.postTranslate(adWDelta, adHDelta);
			// -------------------------------------------

			canvas.drawBitmap(overlayBmp, matrix, new Paint(
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			viewInfo.mView.setDrawingCacheEnabled(false);
		}

		mChangeFalg = true;
		mCurrentBitmap = mBitmap;
		return mBitmap;
	}

	private void saveBitmap(final String folderName, final String fileName,
			Bitmap bmp, boolean intentFlag) {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File file = new File(path, folderName + "_Share/" + fileName);

		try {
			file.getParentFile().mkdirs();
			bmp.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
			MediaScannerConnection.scanFile(this,
					new String[] { file.toString() }, null, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (intentFlag) {

			SharePhotoView.mImg = bmp;
			Intent i = new Intent(EditPictureView.this, SharePhotoView.class);
			startActivity(i);
		}
	}

	private void ShowMsgDialog(String Msg) {

		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setTitle("SocialMatic");

		MyAlertDialog.setMessage(Msg);

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				if (which == -1 && mNowSelectViewInfo != null) {
					mRootLayout.removeView(mNowSelectViewInfo.mView);
					addViewList.remove(mNowSelectViewInfo);
					mNowSelectViewInfo = null;
				}

			}

		};

		MyAlertDialog.setPositiveButton("Remove", OkClick);
		MyAlertDialog.setNeutralButton("Cancel", OkClick);
		MyAlertDialog.show();

	}

	public void resetPresets() {
		// TODO Auto-generated method stub

	}

	public Bitmap getLastPicture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRequestComplete(Object[] results) {
		// TODO Auto-generated method stub
	}
}

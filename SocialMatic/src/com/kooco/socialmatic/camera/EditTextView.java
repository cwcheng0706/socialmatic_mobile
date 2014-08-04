package com.kooco.socialmatic.camera;

import paint.ColorPickerDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.camera.EditPictureView.ViewInfo;

public class EditTextView extends SherlockFragmentActivity {

	private EditText editText;
	private TextView textview;
	private Spinner fontSizeSpinner;
	private Spinner fontTypeSpinner;
	private Typeface mTf;

	private Button mConfirmBtn;
	private Button mCancelBtn;

	public static EditPictureView mEditPictureView;

	private String[] aryFontType = { "Rockwel", "Helvetica Neue", "Myriad",
			"Arial", "Times New Roman", "Trebuchet", "Mistral", "Verdana",
			"Garamond", "Goudy B", "Rotis Sans Serif", "Rotis Serif" };

	private int mFontSize = 18;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.edit_text_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mConfirmBtn = (Button) findViewById(R.id.confirm);
		mConfirmBtn.setTypeface(tf);
		mConfirmBtn.setOnClickListener(new ConfirmClickListener());

		mCancelBtn = (Button) findViewById(R.id.cancel);
		mCancelBtn.setTypeface(tf);
		mCancelBtn.setOnClickListener(new CancelClickListener());

		editText = (EditText) findViewById(R.id.edit_text);
		textview = (TextView) findViewById(R.id.text_view);
		textview.setGravity(Gravity.CENTER_HORIZONTAL);
		textview.setTextSize(mFontSize);

		mTf = Typeface.createFromAsset(getAssets(), "fonts/Rockwel.ttf");
		textview.setTypeface(mTf);

		fontSizeSpinner = (Spinner) findViewById(R.id.fontsize_spinnner);
		fontTypeSpinner = (Spinner) findViewById(R.id.fonttype_spinnner);

		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item, new Integer[] { 18, 24,
						30, 36, 42 });

		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, aryFontType);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		fontSizeSpinner.setAdapter(adapter);
		fontTypeSpinner.setAdapter(typeAdapter);

		fontSizeSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {

						mFontSize = 18 + (position * 6);
						textview.setTextSize(mFontSize);
					}

					public void onNothingSelected(AdapterView arg0) {

					}

				});

		fontTypeSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {

						String strType = aryFontType[position];
						String strPath = "fonts/" + strType + ".ttf";

						mTf = Typeface.createFromAsset(getAssets(), strPath);
						textview.setTypeface(mTf);
					}

					public void onNothingSelected(AdapterView arg0) {

					}

				});

		editText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				textview.setText(editText.getText());
				textview.setTextColor(EditPictureView.mTextColor);

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	private class CancelClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class ConfirmClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (textview.getText().toString().trim().length() > 0) {
				// mEditPictureView
				TextView tv = new TextView(mEditPictureView);
				tv.setDrawingCacheEnabled(true);

				tv.setTypeface(mTf);
				tv.setTextSize(mFontSize);
				tv.setTextColor(EditPictureView.mTextColor);
				tv.setText(textview.getText().toString().trim());

				RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				paramsImage.addRule(RelativeLayout.CENTER_IN_PARENT);

				tv.setLayoutParams(paramsImage);
				tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
				int widht = tv.getMeasuredWidth();
				int height = tv.getMeasuredHeight();

				// convert TextView to Bitmap
				// ----------------------------------------
				Bitmap bitmap;
				bitmap = Bitmap.createBitmap(widht, height,
						Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(bitmap);
				tv.layout(0, 0, widht, height);
				tv.draw(c);
				// ----------------------------------------

				// convert Bitmap to ImageView
				// ----------------------------------------

				RelativeLayout.LayoutParams paramsImage1 = new RelativeLayout.LayoutParams(
						widht, height);
				paramsImage1.addRule(RelativeLayout.CENTER_IN_PARENT);

				ImageView iv = new ImageView(mEditPictureView);
				iv.setLayoutParams(paramsImage);
				iv.setImageBitmap(bitmap);
				// ----------------------------------------

				ViewInfo viewInfo = mEditPictureView.new ViewInfo(iv);

				viewInfo.mMatrix.postScale(1, 1);
				viewInfo.mImageHeight = (int) tv.getHeight();
				viewInfo.mImageWidth = (int) tv.getWidth();
				viewInfo.mScaleFactor = 1;

				viewInfo.mMatrix.postTranslate(0, 0);
				viewInfo.type = "text";

				iv.setImageMatrix(viewInfo.mMatrix);

				mEditPictureView.addViewList.add(viewInfo);

				mEditPictureView.mRootLayout.addView(viewInfo.mView);

				finish();
			}
		}
	}

	public void changeBrushColor(View v) {
		new ColorPickerDialog(this,
				new ColorPickerDialog.OnColorChangedListener() {
					public void colorChanged(int color) {
						EditPictureView.mTextColor = color;
						textview.setTextColor(color);
					}
				}, EditPictureView.mTextColor).show();
	}
}

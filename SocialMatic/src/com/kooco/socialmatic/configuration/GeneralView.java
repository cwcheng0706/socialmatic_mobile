package com.kooco.socialmatic.configuration;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;

public class GeneralView extends SherlockFragmentActivity {

	private Button mBackBtn;
	private Button mConfirmBtn;

	private TextView mGeneralLab;

	private TextView mAccountLab;
	private TextView mNicknameLab;
	private TextView mTimezoneLab;
	private TextView mLocaleLab;

	private EditText mAccountEditText;
	private EditText mNicknameEditText;
	
	private Spinner mTimezoneSpinner;
	private Spinner mLocaleSpinner;
	
	private ImageView mUserPhotoImageView;
	
	private ImageLoader imageLoader;
	
	private TextView mUserNameLab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		imageLoader = new ImageLoader(getApplicationContext());
		
		setContentView(R.layout.general_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		String account = UserDataAccess.getDataWithKeyAndEncrypt(
				GeneralView.this, Config.accountnKey);
		String nickname = UserDataAccess.getDataWithKeyAndEncrypt(
				GeneralView.this, Config.nameKey);
		
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mGeneralLab = (TextView) findViewById(R.id.general_lab);
		mAccountLab = (TextView) findViewById(R.id.account_lab);
		mAccountEditText = (EditText) findViewById(R.id.account_text);
		mAccountEditText.setText(account);
		
		mNicknameLab = (TextView) findViewById(R.id.nickname_lab);
		mNicknameEditText = (EditText) findViewById(R.id.nickname_text);
		mNicknameEditText.setText(nickname);
		
		mTimezoneLab = (TextView) findViewById(R.id.timezone_lab);
		mLocaleLab = (TextView) findViewById(R.id.locale_lab);

		mTimezoneSpinner = (Spinner) findViewById(R.id.timezone_spinnner);
		mLocaleSpinner = (Spinner) findViewById(R.id.locale_spinnner);

		mGeneralLab.setTypeface(tf);
		mAccountLab.setTypeface(tf);
		mAccountEditText.setTypeface(tf);
		mNicknameLab.setTypeface(tf);
		mNicknameEditText.setTypeface(tf);
		mTimezoneLab.setTypeface(tf);
		mLocaleLab.setTypeface(tf);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		// --------------------------------------------------
		ArrayAdapter<String> timeZoneAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {
						"UTC+08:00(Taiwan)", "UTC−10:00 (HAT)",
						"UTC+01:00 (CET)" });

		timeZoneAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mTimezoneSpinner.setAdapter(timeZoneAdapter);

		mTimezoneSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {

						/*
						 * Toast.makeText(MainActivity.this, "您選擇" +
						 * adapterView.getSelectedItem().toString(),
						 * Toast.LENGTH_LONG).show();
						 */
					}

					public void onNothingSelected(AdapterView arg0) {

						/*
						 * Toast.makeText(MainActivity.this, "您沒有選擇任何項目",
						 * Toast.LENGTH_LONG).show();
						 */
					}

				});
		// --------------------------------------------------

		// --------------------------------------------------
		ArrayAdapter<String> localeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {
						"English", "中文",
						"Italy" });

		localeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mLocaleSpinner.setAdapter(localeAdapter);

		mLocaleSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {

						/*
						 * Toast.makeText(MainActivity.this, "您選擇" +
						 * adapterView.getSelectedItem().toString(),
						 * Toast.LENGTH_LONG).show();
						 */
					}

					public void onNothingSelected(AdapterView arg0) {

						/*
						 * Toast.makeText(MainActivity.this, "您沒有選擇任何項目",
						 * Toast.LENGTH_LONG).show();
						 */
					}

				});
		// --------------------------------------------------
		
		//createUserData();
	}
	
	private void createUserData() {
		String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(
				this, Config.photoKey);
		String gender = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.genderKey);
		String userName = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.nameKey);

		if (photoUrl.equals("")) {
			Drawable res;

			if (gender.equals("M"))
				res = getResources()
						.getDrawable(R.drawable.account_photo_3);
			else if (gender.equals("W"))
				res = getResources()
						.getDrawable(R.drawable.account_photo_1);
			else
				res = getResources()
						.getDrawable(R.drawable.account_photo_2);
			mUserPhotoImageView.setImageDrawable(res);

		} else {
			mUserPhotoImageView.setTag(photoUrl);

			imageLoader.DisplayImage(photoUrl, mUserPhotoImageView, 40);
		}

		mUserNameLab.setText("Hi! " + userName);
	}



	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class ConfirmBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}

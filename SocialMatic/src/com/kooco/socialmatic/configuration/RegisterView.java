package com.kooco.socialmatic.configuration;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.countrypicker.CountryPicker;
import com.countrypicker.CountryPickerListener;
import com.devsmart.android.ui.HorizontalListView;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.camera.CropImageView;
import com.kooco.socialmatic.camera.EditPictureView;
import com.kooco.socialmatic.camera.SelectPhotoView;
import com.kooco.socialmatic.camera.SharePhotoView;
import com.kooco.socialmatic.camera.EditPictureView.ImageListAdapter;
import com.kooco.socialmatic.flow.FlowView.MainFragment;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.GPUImageFilterTools;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class RegisterView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mSpet1BackBtn;
	private Button mSpet1NextBtn;

	/*
	 * private RadioButton mDeviceTypeHave; private RadioButton
	 * mDeviceTypeNotHave; private RadioGroup mDeviceTypeRadioGroup;
	 */

	private RadioButton mAccountTypePersonal;
	private RadioButton mAccountTypeBusiness;
	private RadioGroup mAccountTypeRadioGroup;

	// private ImageView mProfilePhotoImageView;

	private TextView mToSTextView;

	private EditText mCountryText;

	private EditText mAccountText;
	private EditText mNicknameText;
	private EditText mPasswordText;
	private EditText mConfirmPasswordText;

	// for personal account type
	// ------------------------------------
	private EditText mPersonalNameText;
	private EditText mPersonalSurnameText;
	private EditText mPersonalLocationText;
	private EditText mBirthdayText;
	private RadioButton mGenderMan;
	private RadioButton mGenderWoman;
	private RadioGroup mGenderRadioGroup;
	// ------------------------------------

	// for business account type
	// ------------------------------------
	private EditText mBusinessCompanyNameText;
	private EditText mBusinessCountryText;
	private EditText mBusinessNameText;
	private EditText mBusinessSurnameText;
	private EditText mBusinessVATText;
	private Spinner mBusinessPWFSpinner;
	private EditText mBusinessLocationText;
	// ------------------------------------

	private EditText mCameraSNText;

	private LinearLayout mCameraSNLinearLayout;

	private LinearLayout mPersonalLayout;
	private LinearLayout mBusinessLayout;

	// public Bitmap mUserPhotoBitmap = null;

	private CheckBox mSubscribeMeCheckBox;
	private CheckBox mTOSCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.register_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		/*
		 * mProfilePhotoImageView = (ImageView)
		 * findViewById(R.id.profile_photo_imageview); mProfilePhotoImageView
		 * .setOnClickListener(new SetProfilePhotoOnClickListener());
		 */
		mBirthdayText = (EditText) findViewById(R.id.birthday_text);
		mBirthdayText.setInputType(InputType.TYPE_NULL);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			mBirthdayText.setRawInputType(InputType.TYPE_CLASS_TEXT);
			mBirthdayText.setTextIsSelectable(true);
		}
		mBirthdayText.setOnClickListener(DatePickerButtonOnClickListener);

		mCountryText = (EditText) findViewById(R.id.country_text);
		mCountryText.setInputType(InputType.TYPE_NULL);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			mCountryText.setRawInputType(InputType.TYPE_CLASS_TEXT);
			mCountryText.setTextIsSelectable(true);
		}
		mCountryText
				.setOnClickListener(new CountryPickerButtonOnClickListener());

		mSpet1BackBtn = (Button) findViewById(R.id.step1_back_btn);
		mSpet1BackBtn.setOnClickListener(new BackBtnListener());

		mSpet1NextBtn = (Button) findViewById(R.id.step1_next_btn);
		mSpet1NextBtn.setOnClickListener(new ConfirmBtnListener());

		mAccountText = (EditText) findViewById(R.id.account_text);
		mNicknameText = (EditText) findViewById(R.id.nickname_text);
		mPasswordText = (EditText) findViewById(R.id.password_text);
		mConfirmPasswordText = (EditText) findViewById(R.id.confirm_text);

		mPersonalNameText = (EditText) findViewById(R.id.name_text);
		mPersonalSurnameText = (EditText) findViewById(R.id.surname_text);
		mPersonalLocationText = (EditText) findViewById(R.id.location_text);

		mBusinessCompanyNameText = (EditText) findViewById(R.id.company_name_text);
		mBusinessCountryText = (EditText) findViewById(R.id.country_text);
		mBusinessNameText = (EditText) findViewById(R.id.business_name_text);
		mBusinessSurnameText = (EditText) findViewById(R.id.business_surname_text);
		mBusinessVATText = (EditText) findViewById(R.id.voe_text);
		mBusinessPWFSpinner = (Spinner) findViewById(R.id.work_item_spinner);
		mBusinessLocationText = (EditText) findViewById(R.id.business_location_text);

		// -------------------------------------------------
		mGenderRadioGroup = (RadioGroup) findViewById(R.id.gender_radiogroup);
		mGenderMan = (RadioButton) findViewById(R.id.man_radio);
		mGenderWoman = (RadioButton) findViewById(R.id.woman_radio);

		mGenderRadioGroup.setOnCheckedChangeListener(GenderRadioGroupListener);
		/*
		 * Drawable res =
		 * getResources().getDrawable(R.drawable.account_photo_3);
		 * mProfilePhotoImageView.setImageDrawable(res); //
		 * -------------------------------------------------
		 * 
		 * // -------------------------------------------------
		 * mDeviceTypeRadioGroup = (RadioGroup)
		 * findViewById(R.id.divice_type_radiogroup); mDeviceTypeHave =
		 * (RadioButton) findViewById(R.id.have_camera_radio);
		 * mDeviceTypeNotHave = (RadioButton)
		 * findViewById(R.id.not_have_camera_radio);
		 * 
		 * mDeviceTypeRadioGroup
		 * .setOnCheckedChangeListener(DeviceTypeRadioGroupListener);
		 * 
		 * mCameraSNLinearLayout = (LinearLayout)
		 * findViewById(R.id.camera_sn_linerlayout);
		 * mCameraSNLinearLayout.setVisibility(View.VISIBLE); //
		 * -------------------------------------------------
		 */

		// -------------------------------------------------
		mAccountTypeRadioGroup = (RadioGroup) findViewById(R.id.account_type_radiogroup);
		mAccountTypePersonal = (RadioButton) findViewById(R.id.personal_radio);
		mAccountTypeBusiness = (RadioButton) findViewById(R.id.business_radio);

		mAccountTypeRadioGroup
				.setOnCheckedChangeListener(AccountTypeRadioGroupListener);

		mPersonalLayout = (LinearLayout) findViewById(R.id.personal_relativelayout);
		mPersonalLayout.setVisibility(View.VISIBLE);

		mBusinessLayout = (LinearLayout) findViewById(R.id.business_relativelayout);
		mBusinessLayout.setVisibility(View.GONE);
		// -------------------------------------------------
		/*
		 * mProfilePhotoImageView = (ImageView)
		 * findViewById(R.id.profile_photo_imageview);
		 */
		mToSTextView = (TextView) findViewById(R.id.tos_textview);
		mToSTextView.setOnClickListener(new ToSButtonOnClickListener());

		mTOSCheckBox = (CheckBox) findViewById(R.id.chk_tos);
	}

	/*
	 * public void setUserPhotoImage(String strImageUri) {
	 * 
	 * Uri uri = Uri.parse(strImageUri);
	 * 
	 * if (uri == null) {
	 * 
	 * // finish(); } else {
	 * 
	 * try { resizeBitmapAndWireToView(uri); } catch (FileNotFoundException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } }
	 * 
	 * }
	 * 
	 * private void resizeBitmapAndWireToView(Uri uri) throws
	 * FileNotFoundException { System.gc();
	 * 
	 * ContentResolver cr = getContentResolver(); InputStream in =
	 * cr.openInputStream(uri); BitmapFactory.Options options = new
	 * BitmapFactory.Options(); options.inPurgeable = true;
	 * options.inInputShareable = true;
	 * 
	 * options.inSampleSize = 4;
	 * 
	 * mUserPhotoBitmap = BitmapFactory.decodeStream(in, null, options);
	 * 
	 * mProfilePhotoImageView.setImageBitmap(mUserPhotoBitmap); }
	 */
	DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
		}

	};

	private void doSelectPhoto() {
		SelectPhotoView.mCallView = "RegisterView";
		SelectPhotoView.mRegisterView = RegisterView.this;
		Intent i = new Intent(RegisterView.this, SelectPhotoView.class);
		startActivity(i);
	}

	/*
	 * private void setDefaultUserPhoto(int checkedId) {
	 * 
	 * switch (checkedId) { case R.id.man_radio:
	 * 
	 * if (mUserPhotoBitmap == null) { Drawable res =
	 * getResources().getDrawable( R.drawable.account_photo_3);
	 * mProfilePhotoImageView.setImageDrawable(res); } break; case
	 * R.id.woman_radio:
	 * 
	 * if (mUserPhotoBitmap == null) { Drawable res =
	 * getResources().getDrawable( R.drawable.account_photo_1);
	 * mProfilePhotoImageView.setImageDrawable(res); } break;
	 * 
	 * } }
	 * 
	 * private void ShowDealPhotoMsgDialog(String Msg) {
	 * 
	 * Builder MyAlertDialog = new AlertDialog.Builder(this);
	 * 
	 * MyAlertDialog.setTitle("SocialMatic");
	 * 
	 * MyAlertDialog.setMessage(Msg);
	 * 
	 * DialogInterface.OnClickListener OkClick = new
	 * DialogInterface.OnClickListener() {
	 * 
	 * public void onClick(DialogInterface dialog, int which) { switch (which) {
	 * case -2: doSelectPhoto(); break; case -3: mUserPhotoBitmap = null;
	 * setDefaultUserPhoto(mGenderRadioGroup .getCheckedRadioButtonId()); break;
	 * } }
	 * 
	 * };
	 * 
	 * MyAlertDialog.setNegativeButton("Select Other", OkClick);
	 * MyAlertDialog.setNeutralButton("Remove", OkClick);
	 * MyAlertDialog.setPositiveButton("Cancel", OkClick); MyAlertDialog.show();
	 * 
	 * }
	 */
	private class CountryPickerButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(final View v) {

			final CountryPicker picker = CountryPicker
					.newInstance("Select Country");
			picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

			picker.setListener(new CountryPickerListener() {

				@Override
				public void onSelectCountry(String name, String code) {
					// Invoke your function here
					((EditText) v).setText(name);
					picker.dismiss();
				}
			});

		}
	}

	private class ToSButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(final View v) {

			Intent i = new Intent(RegisterView.this, TermsOfServiceView.class);
			startActivity(i);
		}
	}

	/*
	 * private class SetProfilePhotoOnClickListener implements OnClickListener {
	 * 
	 * @Override public void onClick(final View v) {
	 * 
	 * if (mUserPhotoBitmap == null) { doSelectPhoto(); } else {
	 * ShowDealPhotoMsgDialog("Please select action."); } } }
	 */
	private Button.OnClickListener DatePickerButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			new DatePickerDialog(RegisterView.this,
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
							((EditText) v).setText(year + "-"
									+ String.format("%02d", monthOfYear + 1)
									+ "-" + String.format("%02d", dayOfMonth));
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH)).show();

		}

	};

	private RadioGroup.OnCheckedChangeListener GenderRadioGroupListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub

			// setDefaultUserPhoto(checkedId);

		}

	};

	private RadioGroup.OnCheckedChangeListener AccountTypeRadioGroupListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub

			switch (checkedId) {
			case R.id.personal_radio:
				mPersonalLayout.setVisibility(View.VISIBLE);
				mBusinessLayout.setVisibility(View.GONE);
				break;
			case R.id.business_radio:
				mPersonalLayout.setVisibility(View.GONE);
				mBusinessLayout.setVisibility(View.VISIBLE);
				break;

			}

		}

	};

	/*
	 * private RadioGroup.OnCheckedChangeListener DeviceTypeRadioGroupListener =
	 * new RadioGroup.OnCheckedChangeListener() {
	 * 
	 * @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
	 * // TODO Auto-generated method stub
	 * 
	 * switch (checkedId) { case R.id.have_camera_radio:
	 * mCameraSNLinearLayout.setVisibility(View.VISIBLE); break; case
	 * R.id.not_have_camera_radio:
	 * mCameraSNLinearLayout.setVisibility(View.GONE); break;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * };
	 */
	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private void ShowMsgDialog(String Msg) {

		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setTitle("SocialMatic");

		MyAlertDialog.setMessage(Msg);

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}

		};

		MyAlertDialog.setNeutralButton("OK", OkClick);
		MyAlertDialog.show();

	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	private class ConfirmBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			// if (mAccountTypeRadioGroup.getCheckedRadioButtonId() ==
			// R.id.personal_radio)

			if (mAccountText.getText().toString().equals("")
					|| mNicknameText.getText().toString().equals("")
					|| mPasswordText.getText().toString().equals("")
					|| mConfirmPasswordText.getText().toString().equals("")) {
				ShowMsgDialog("Account\nNickname\nPassword\nConfirm Password\nfields is required.");
				return;
			}

			if (!isEmailValid(mAccountText.getText().toString())) {
				ShowMsgDialog("Account must email format.");
				return;
			}

			if (!mTOSCheckBox.isChecked()) {
				ShowMsgDialog("Should read and agree to the terms of service.");
				return;
			}

			if (!mPasswordText.getText().toString()
					.equals(mConfirmPasswordText.getText().toString())) {
				ShowMsgDialog("Password and Confirm Password doesn't match.");
				return;
			}

			int accountType = 0;
			if (mAccountTypeRadioGroup.getCheckedRadioButtonId() == R.id.business_radio)
				accountType = 1;

			String personalName = "";
			String personalSurname = "";
			String personalGender = "M";
			String personalBirthday = "";
			String personalLocation = "";

			String businessCompanyName = "";
			String businessCountry = "";
			String businessName = "";
			String businessSurname = "";
			String businessVoe = "";
			String businessPWF = "";
			String businessLocation = "";

			if (accountType == 0) {
				if (mPersonalNameText.getText().toString().equals("")
						|| mPersonalSurnameText.getText().toString().equals("")
						|| mBirthdayText.getText().toString().equals("")
						|| mPersonalLocationText.getText().toString()
								.equals("")) {
					ShowMsgDialog("Name\nSurnamename\nBirthday\nLocation\nfields is required.");
					return;
				}

				if (mGenderRadioGroup.getCheckedRadioButtonId() == R.id.woman_radio)
					personalGender = "W";

				personalName = mPersonalNameText.getText().toString();
				personalSurname = mPersonalSurnameText.getText().toString();
				personalBirthday = mBirthdayText.getText().toString();
				personalLocation = mPersonalLocationText.getText().toString();

			} else if (accountType == 1) {
				if (mBusinessCompanyNameText.getText().toString().equals("")
						|| mBusinessCountryText.getText().toString().equals("")
						|| mBusinessNameText.getText().toString().equals("")
						|| mBusinessSurnameText.getText().toString().equals("")
						|| mBusinessVATText.getText().toString().equals("")
						|| mBusinessPWFSpinner.getSelectedItem().toString()
								.equals("")
						|| mBusinessLocationText.getText().toString()
								.equals("")) {
					ShowMsgDialog("Company Name\nCountry\nName\nSurname\nVAT or EIN\nProfessional Work Field\nLocation\nfields is required.");
					return;
				}

				businessCompanyName = mBusinessCompanyNameText.getText()
						.toString();
				businessCountry = mBusinessCountryText.getText().toString();
				businessName = mBusinessNameText.getText().toString();
				businessSurname = mBusinessSurnameText.getText().toString();
				businessVoe = mBusinessVATText.getText().toString();
				businessPWF = mBusinessPWFSpinner.getSelectedItem().toString();
				businessLocation = mBusinessLocationText.getText().toString();
			}

			String account = mAccountText.getText().toString();
			String password = mPasswordText.getText().toString();
			String nickname = mNicknameText.getText().toString();
			/*
			 * // image convert to base64 string //
			 * -------------------------------------------- String img_full =
			 * ""; if (mUserPhotoBitmap != null) { ByteArrayOutputStream
			 * full_stream = new ByteArrayOutputStream();
			 * mUserPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
			 * full_stream); byte[] full_bytes = full_stream.toByteArray();
			 * img_full = Base64.encodeToString(full_bytes, Base64.DEFAULT);
			 * 
			 * } // --------------------------------------------
			 */
			// post data to server
			// --------------------------------------------
			String deviceId = UserDataAccess
					.getSystemUseDeviceId(RegisterView.this);
			String deviceType = "2";
			String locale = "en";

			String jsonString = "";
			jsonString = "[{" + "\"email\":" + "\"" + account + "\","
					+ "\"nickname\":" + "\"" + nickname + "\","
					+ "\"password\":" + "\"" + password + "\",";

			if (accountType == 0) {
				jsonString += "\"name\":" + "\"" + personalName + "\",";
				jsonString += "\"surname\":" + "\"" + personalSurname + "\",";
				jsonString += "\"gender\":" + "\"" + personalGender + "\",";
				jsonString += "\"birthday\":" + "\"" + personalBirthday + "\",";
				jsonString += "\"location\":" + "\"" + personalLocation + "\",";
			} else {
				jsonString += "\"company_name\":" + "\"" + businessCompanyName
						+ "\",";
				jsonString += "\"name\":" + "\"" + businessName + "\",";
				jsonString += "\"surname\":" + "\"" + businessSurname + "\",";
				jsonString += "\"country\":" + "\"" + businessCountry + "\",";
				jsonString += "\"vat\":" + "\"" + businessVoe + "\",";
				jsonString += "\"professional_work_fld\":" + "\"" + businessPWF
						+ "\",";
				jsonString += "\"location\":" + "\"" + businessLocation + "\",";
			}
			/*
			 * if (mUserPhotoBitmap != null) { jsonString +=
			 * "\"profile_photo\":" + "\"" + img_full + "\","; }
			 */
			jsonString += "\"account_type\":" + "\"" + accountType + "\"";
			jsonString += "}]";

			String queryUrl = RegisterView.this
					.getString(R.string.socialmatic_base_url)
					+ RegisterView.this
							.getString(R.string.user_registration_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs
					.add(new BasicNameValuePair("deviceType", deviceType));
			nameValuePairs.add(new BasicNameValuePair("registration_fields",
					jsonString));
			nameValuePairs.add(new BasicNameValuePair("locale", locale));

			WebManager webManager = new WebManager(RegisterView.this);

			webManager.setDoType("user_registration");

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(
						nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
			}

			if (webManager.isNetworkAvailable()) { // do web connect...
				webManager.execute(queryUrl, "POST");
			}
			// --------------------------------------------
		}
	}

	@SuppressWarnings("unused")
	public void showAlertView(String title, String Content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(Content);
		builder.setPositiveButton("Exit", null);

		AlertDialog dialog = builder.show();
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects.length == 2) {

			if (objects[1].toString().equals("user_registration")) {

				JSONParser parser = new JSONParser();
				JSONObject jsonObject = new JSONObject();

				boolean successFlag = false;
				String errorMsg = "";
				try {
					jsonObject = (JSONObject) parser.parse(objects[0]
							.toString());
					String response = jsonObject.get("Response").toString();

					if (response.equals("Request OK")) {
						successFlag = true;
						showAlertView("Socialmatic",
								"Thanks for your support, please confirmation the email to open account!");
						finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (!successFlag) {
					try {
						jsonObject = (JSONObject) parser.parse(objects[0]
								.toString());
						errorMsg = jsonObject.get("errors").toString();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (errorMsg.equals("")) {
						errorMsg = "Please make sure all required fields are properly filled with data!";
					}

					showAlertView("Socialmatic", errorMsg);
				}
			}
		}
	}
}

package com.kooco.socialmatic.configuration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ChangePassword extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {
	private Button mBackBtn;
	private Button mConfirmBtn;

	private TextView mTitleLab;
	private TextView mOldPasswordLab;
	private TextView mNewPasswordLab;
	private TextView mNewPasswordLab1;

	private EditText mOldPasswordEditText;
	private EditText mNewPasswordEditText;

	private TextView mConfirmPasswordLab;
	private TextView mConfirmPasswordLab1;

	private EditText mConfirmPassworEditText;

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

		setContentView(R.layout.change_password_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mTitleLab = (TextView) findViewById(R.id.title_lab);
		mOldPasswordLab = (TextView) findViewById(R.id.old_password_lab);
		mNewPasswordLab = (TextView) findViewById(R.id.new_password_lab);
		mNewPasswordLab1 = (TextView) findViewById(R.id.new_password_lab1);
		mConfirmPasswordLab = (TextView) findViewById(R.id.confirm_password_lab);
		mConfirmPasswordLab1 = (TextView) findViewById(R.id.confirm_password_lab1);
		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);

		mOldPasswordEditText = (EditText) findViewById(R.id.old_password_text);
		mNewPasswordEditText = (EditText) findViewById(R.id.new_password_text);
		mConfirmPassworEditText = (EditText) findViewById(R.id.confirm_password_text);

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		
		mTitleLab.setTypeface(tf);
		mOldPasswordLab.setTypeface(tf);
		mNewPasswordLab.setTypeface(tf);
		mNewPasswordLab1.setTypeface(tf);
		mConfirmPasswordLab.setTypeface(tf);
		mConfirmPasswordLab1.setTypeface(tf);

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

	
	private void doSignOut() {


		UserDataAccess.setDataWithKeyAndEncrypt(this,
				Config.isLoginKey, "n");

		UserDataAccess.setDataWithKeyAndEncrypt(this, Config.nameKey,
				"");
		UserDataAccess.setDataWithKeyAndEncrypt(this, Config.genderKey,
				"");
		UserDataAccess.setDataWithKeyAndEncrypt(this, Config.photoKey,
				"");
		UserDataAccess.setDataWithKeyAndEncrypt(this, Config.passwordKey,
				"");
	}

	public void showAlertView(String title, String Content, int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ChangePassword.this);
		builder.setTitle(title);
		builder.setMessage(Content);

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				doSignOut();
				finish();
			}

		};

		if (type == 1) 
			builder.setPositiveButton("Exit", OkClick);
		else
			builder.setPositiveButton("Exit", null);
		
		@SuppressWarnings("unused")
		AlertDialog dialog = builder.show();
	}

	private void doChangePasswod() {

		String oldPassword = mOldPasswordEditText.getText().toString();
		String newPassword = mNewPasswordEditText.getText().toString();
		String confirmPassword = mConfirmPassworEditText.getText().toString();
		String nowPassword = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.passwordKey);

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		if (oldPassword.equals("") || newPassword.equals("")
				|| confirmPassword.equals("")) {
			showAlertView("Socialmatic", "Please input all data fields", 0);
			return;
		}

		if (!oldPassword.equals(nowPassword)) {
			showAlertView("Socialmatic", "Old password is wrong!", 0);

			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			showAlertView("Socialmatic",
					"Password and Confirm Password doesn't match.", 0);

			return;
		}

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.set_new_psw_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("oldPsw", nowPassword));
		nameValuePairs.add(new BasicNameValuePair("newPsw", newPassword));
		

		WebManager webManager = new WebManager(ChangePassword.this);

		webManager.setDoType("set_new_psw");

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
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

			doChangePasswod();
			// finish();
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects[1].toString().equals("set_new_psw")) {

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = new JSONObject();

			String errorMsg = "";
			try {
				jsonObject = (JSONObject) parser.parse(objects[0].toString());
				String response = jsonObject.get("Response").toString();
				if (response.equals("Request OK")) {
					showAlertView("Socialmatic",
							"Please log in using the new password again!", 1);

					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				jsonObject = (JSONObject) parser.parse(objects[0].toString());
				errorMsg = jsonObject.get("message").toString();
				showAlertView("Socialmatic", errorMsg, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}

package com.kooco.socialmatic.configuration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.actionbarsherlock.app.SherlockFragment;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.configuration.LoginView.MainFragment;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnrollDeviceDialog extends Dialog implements
		WebManagerCallbackInterface {

	public static String mAccount = "";
	public static String mPassword = "";
	private EditText mAccountEditText;
	private EditText mPasswordEditText;
	private Button mOkButton;
	private Button mCancelButton;
	private Context mContext;

	public static boolean mIsEnrollDevice = true;
	
	public SherlockFragment mParentFragment;

	public EnrollDeviceDialog(SherlockFragment fragment, Context context) {
		super(context);

		mParentFragment = fragment;
		mContext = context;
		setContentView(R.layout.enroll_device_dialog);

		mAccountEditText = (EditText) findViewById(R.id.account_edittext);

		/*
		 * if (!mIsEnrollDevice) {
		 * mAccountEditText.setVisibility(View.INVISIBLE); }
		 */

		mPasswordEditText = (EditText) findViewById(R.id.password_edittext);

		if (!UserDataAccess.getDataWithKeyAndEncrypt(mContext,
				Config.accountnKey).equals("")
				&& !UserDataAccess.getDataWithKeyAndEncrypt(mContext,
						Config.passwordKey).equals("")) {
			mAccountEditText.setText(UserDataAccess.getDataWithKeyAndEncrypt(
					mContext, Config.accountnKey));
			mPasswordEditText.setText(UserDataAccess.getDataWithKeyAndEncrypt(
					mContext, Config.passwordKey));
		}

		mOkButton = (Button) findViewById(R.id.ok_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);

		mOkButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doEnrollDevice();
			}
		});

		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EnrollDeviceDialog.this.dismiss();
			}
		});
	}

	public void showAlertView(String title, String Content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setMessage(Content);
		builder.setPositiveButton("Exit", null);
		@SuppressWarnings("unused")
		AlertDialog dialog = builder.show();
	}

	private void doEnrollDevice() {

		if (mAccountEditText.getText().toString().trim().equals("")
				|| mPasswordEditText.getText().toString().trim().equals("")) {
			showAlertView("Socailmatic", "Account & password can not be empty.");

			return;
		}

		mAccount = mAccountEditText.getText().toString().trim();
		mPassword = mPasswordEditText.getText().toString().trim();

		UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.accountnKey,
				mAccount);
		UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.passwordKey,
				mPassword);

		String deviceId = UserDataAccess.getSystemUseDeviceId(mContext);

		if (mIsEnrollDevice) {

			String recipeQueryUrl = mContext
					.getString(R.string.socialmatic_base_url)
					+ mContext.getString(R.string.enrolldevice_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("username", mAccount));
			nameValuePairs.add(new BasicNameValuePair("password", mPassword));
			nameValuePairs.add(new BasicNameValuePair("deviceType", "2"));

			WebManager webManager = new WebManager(EnrollDeviceDialog.this,
					mContext);

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(
						nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(recipeQueryUrl, "POST");
			}
		} else {
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mContext, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mContext,
					Config.userIdKey);

			String unlinkUrl = mContext
					.getString(R.string.socialmatic_base_url)
					//+ mContext.getString(R.string.unlinkdevice_url);
					+ mContext.getString(R.string.logout_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("userID", userId));
			//nameValuePairs.add(new BasicNameValuePair("password", mPassword));
			nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));

			WebManager webManager = new WebManager(EnrollDeviceDialog.this,
					mContext);

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(
						nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(unlinkUrl, "POST");
			}
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		// Log.d("result----------------", "objects[0]--->" + objects[0] +
		// "    objects[1]--->" + objects[1]);
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		
		try {
			jsonObject = (JSONObject) parser.parse(objects[0].toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mIsEnrollDevice) {
			try {
				JSONObject data = (JSONObject) jsonObject.get("data");
				String authToken = data.get("authtoken").toString();
				String userID = data.get("userID").toString();

				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.authTokenKey, authToken);
				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.userIdKey, userID);
				
				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.isLoginKey, "y");

				showAlertView("Socialmatic","Enroll device success!");
				mParentFragment.onResume();
				EnrollDeviceDialog.this.dismiss();

			} catch (Exception ex) {

				try {
					String message = jsonObject.get("message").toString();
					showAlertView("Socialmatic",message);
				} catch (Exception exception) {

				}
			}
		}
		else
		{
			try
			{
				/*String response = jsonObject.get("Response").toString();
				showAlertView("Socialmatic",response);
				*/
				
				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.authTokenKey, "");
				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.userIdKey, "");
				
				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.isLoginKey, "n");
				

				showAlertView("Socialmatic", "Unenroll device ok!");

				mParentFragment.onResume();
				EnrollDeviceDialog.this.dismiss();
			}catch(Exception ex)
			{
				try {
					String message = jsonObject.get("message").toString();
					showAlertView("Socialmatic",message);
				} catch (Exception exception) {

				}
			}
		}
	}
}

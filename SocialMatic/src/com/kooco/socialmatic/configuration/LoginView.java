package com.kooco.socialmatic.configuration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.follow.FollowerAdapter;
import com.kooco.socialmatic.follow.FollowingAdapter;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.profile.ProfileView.MainFragment;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class LoginView extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			MainFragment fragment = new MainFragment();
			fm.beginTransaction().add(android.R.id.content, fragment).commit();
		}
	}

	public static class MainFragment extends SherlockFragment implements
			WebManagerCallbackInterface {
		static Context mContext;
		public Activity mActivity;

		FollowingAdapter mFollowingAdapter;
		FollowerAdapter mFollowerAdapter;

		private View mView;
		Bundle mSavedInstanceState;
		LayoutInflater mInflater;

		private EditText mAccountText;
		private EditText mPasswordText;
		private Button mConfirmBtn;
		private Button mRegisterBtn;
		private TextView mForgetPasswordBtn;
		// private Button mEnrollDeviceBtn;

		private TextView mAccountLab;
		private TextView mPasswordLab;
		private TextView mMysettingsLab;
		private TextView mMysettingslab;

		private Button mGeneralBtn;
		private Button mPrivacyBtn;
		private Button mNotificationBtn;
		private Button mChangePasswordBtn;
		private Button mDeleteAccountBtn;
		private Button mSignOutBtn;

		private RelativeLayout mLoginLayout;
		private RelativeLayout mMySettingsLayout;

		private Spinner mTimezoneSpinner;

		ViewGroup mContainer;

		ForgotPasswordDialog mFPDialog;
		EnrollDeviceDialog mEDDialog;

		private ImageView mUserPhotoImageView;

		private ImageLoader imageLoader;

		private TextView mUserNameLab;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(false);
		}

		@Override
		public void onResume() {
			super.onResume();
			
			if (UserDataAccess.mGCMRegId.equals(""))
				UserDataAccess.requestGCMRegId(mContext);
			
			Log.d("UserDataAccess.mGCMRegId...........", "UserDataAccess.mGCMRegId.........." + UserDataAccess.mGCMRegId);

			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mContext, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mContext,
					Config.userIdKey);

			String isLogin = UserDataAccess.getDataWithKeyAndEncrypt(mContext,
					Config.isLoginKey);

			if (isLogin.equals("y")) {
				mLoginLayout.setVisibility(View.GONE);
				mMySettingsLayout.setVisibility(View.VISIBLE);
			} else {
				mLoginLayout.setVisibility(View.VISIBLE);
				mMySettingsLayout.setVisibility(View.GONE);
			}

			//createUserData();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			mActivity = MainActivity.mActivity;

			mInflater = inflater;
			mContainer = container;
			mSavedInstanceState = savedInstanceState;

			imageLoader = new ImageLoader(mActivity.getApplicationContext());

			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			mView = inflater.inflate(R.layout.login_view, container, false);

			mLoginLayout = (RelativeLayout) mView
					.findViewById(R.id.login_linearlayout);
			mMySettingsLayout = (RelativeLayout) mView
					.findViewById(R.id.mysetting_linearlayout);

			String isLogin = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.isLoginKey);

			if (isLogin.equals("y")) {
				mLoginLayout.setVisibility(View.GONE);
				mMySettingsLayout.setVisibility(View.VISIBLE);
			} else {
				mLoginLayout.setVisibility(View.VISIBLE);
				mMySettingsLayout.setVisibility(View.GONE);
			}

			/*mUserPhotoImageView = (ImageView) mView
					.findViewById(R.id.userphoto_imageview);
			  mUserNameLab = (TextView) mView
					.findViewById(R.id.username_textview);
			  mUserNameLab.setTypeface(tf);
					*/

			mAccountText = (EditText) mView.findViewById(R.id.account_text);
			mPasswordText = (EditText) mView.findViewById(R.id.password_text);

			mAccountLab = (TextView) mView.findViewById(R.id.account_lab);
			mPasswordLab = (TextView) mView.findViewById(R.id.password_lab);

			if (!UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.accountnKey).equals("")
					&& !UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
							Config.passwordKey).equals("")) {
				mAccountText.setText(UserDataAccess.getDataWithKeyAndEncrypt(
						mActivity, Config.accountnKey));
				mPasswordText.setText(UserDataAccess.getDataWithKeyAndEncrypt(
						mActivity, Config.passwordKey));
			}

			mConfirmBtn = (Button) mView.findViewById(R.id.confirm);
			mRegisterBtn = (Button) mView.findViewById(R.id.register);
			mForgetPasswordBtn = (TextView) mView
					.findViewById(R.id.forget_password);

			mConfirmBtn.setOnClickListener(new LoginClickListener());
			mRegisterBtn.setOnClickListener(new registerClickListener());
			mForgetPasswordBtn
					.setOnClickListener(new forgetPasswordClickListener());

			mMysettingslab = (TextView) mView.findViewById(R.id.mysettings_lab);
			mGeneralBtn = (Button) mView.findViewById(R.id.general_btn);
			mPrivacyBtn = (Button) mView.findViewById(R.id.privaacy_btn);
			mNotificationBtn = (Button) mView
					.findViewById(R.id.notification_btn);
			mChangePasswordBtn = (Button) mView
					.findViewById(R.id.change_password_btn);
			mDeleteAccountBtn = (Button) mView
					.findViewById(R.id.delete_account_btn);
			mSignOutBtn = (Button) mView.findViewById(R.id.signout_btn);

			
			mAccountLab.setTypeface(tf);
			mPasswordLab.setTypeface(tf);
			mForgetPasswordBtn.setTypeface(tf);
			mConfirmBtn.setTypeface(tf);
			mRegisterBtn.setTypeface(tf);
			// mEnrollDeviceBtn.setTypeface(tf);

			mMysettingslab.setTypeface(tf);
			mGeneralBtn.setTypeface(tf);
			mPrivacyBtn.setTypeface(tf);
			mNotificationBtn.setTypeface(tf);
			mChangePasswordBtn.setTypeface(tf);
			mDeleteAccountBtn.setTypeface(tf);
			mSignOutBtn.setTypeface(tf);

			mNotificationBtn.setOnClickListener(new NotificationBtnListener());
			mGeneralBtn.setOnClickListener(new GeneralBtnListener());
			mPrivacyBtn.setOnClickListener(new PrivacyBtnListener());
			mChangePasswordBtn
					.setOnClickListener(new ChangePasswordBtnListener());
			mDeleteAccountBtn
					.setOnClickListener(new DeleteAccountBtnListener());
			mSignOutBtn.setOnClickListener(new signOutListener());

			mContext = mView.getContext();

			return mView;
		}

		private void signOutAndClearData() {
			UserDataAccess.setDataWithKeyAndEncrypt(mContext,
					Config.isLoginKey, "n");

			UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.nameKey,
					"");
			UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.genderKey,
					"");
			UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.photoKey,
					"");
			UserDataAccess.setDataWithKeyAndEncrypt(mContext, Config.GCMRegIdKey,
					"");
			UserDataAccess.mGCMRegId = "";
            
			onResume();
			
			((MainActivity) mActivity).createProfileBtn();
			
		}

		private void doSignOut() {

			String userID = UserDataAccess.getDataWithKeyAndEncrypt(mContext,
					Config.userIdKey);
			String password = UserDataAccess.getDataWithKeyAndEncrypt(mContext,
					Config.passwordKey);

			String deviceId = UserDataAccess.getSystemUseDeviceId(mContext);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mContext, Config.authTokenKey);

			String queryUrl = mContext.getString(R.string.socialmatic_base_url)
					+ mContext.getString(R.string.unlinkdevice_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			WebManager webManager = new WebManager(MainFragment.this, mContext);
			webManager.setDoType("unlinkdevice_url");

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(
						nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "POST");
			}
			
			signOutAndClearData();
		}

		private void doEnrollDevice() {
			String account = mAccountText.getText().toString().trim();
			String password = mPasswordText.getText().toString().trim();

			UserDataAccess.setDataWithKeyAndEncrypt(mContext,
					Config.accountnKey, account);
			UserDataAccess.setDataWithKeyAndEncrypt(mContext,
					Config.passwordKey, password);

			String deviceId = UserDataAccess.getSystemUseDeviceId(mContext);

			String recipeQueryUrl = mContext
					.getString(R.string.socialmatic_base_url)
					+ mContext.getString(R.string.enrolldevice_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("username", account));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("deviceType", "2"));
			nameValuePairs.add(new BasicNameValuePair("regDevID", UserDataAccess.mGCMRegId));
			
			WebManager webManager = new WebManager(MainFragment.this, mContext);
			webManager.setDoType("enrolldevice");

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
		}

		private void getUserData() {
			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mActivity
					.getString(R.string.socialmatic_base_url)
					+ mActivity.getString(R.string.get_user_info_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&subjectKey=" + userId;

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_user_info");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}
		}

		private void createUserData() {
			String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.photoKey);
			String gender = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.genderKey);
			String userName = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.nameKey);

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

		private class DeleteAccountBtnListener implements OnClickListener {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(mActivity, DeleteAccountView.class);
				startActivity(i);
			}
		}

		private class ChangePasswordBtnListener implements OnClickListener {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(mActivity, ChangePassword.class);
				startActivity(i);
			}
		}

		private class NotificationBtnListener implements OnClickListener {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(mActivity, NotificationView.class);
				startActivity(i);
			}
		}

		private class PrivacyBtnListener implements OnClickListener {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(mActivity, PrivacyView.class);
				startActivity(i);
			}
		}

		private class GeneralBtnListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mActivity, GeneralView.class);
				startActivity(i);
			}
		}

		private class signOutListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doSignOut();

			}
		}

		private class LoginClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {

				if (mAccountText.getText().toString().trim().equals("")
						|| mPasswordText.getText().toString().trim().equals("")) {
					showAlertView("Socailmatic",
							"Account & password can not be empty.");

					return;
				}

				String account = mAccountText.getText().toString().trim();
				String password = mPasswordText.getText().toString().trim();

				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.accountnKey, account);
				UserDataAccess.setDataWithKeyAndEncrypt(mContext,
						Config.passwordKey, password);

				String deviceId = UserDataAccess.getSystemUseDeviceId(mContext);

				String recipeQueryUrl = mContext
						.getString(R.string.socialmatic_base_url)
						+ mContext.getString(R.string.authtoken_get_url);

				recipeQueryUrl += "?deviceID=" + deviceId + "&username="
						+ mAccountText.getText().toString() + "&password="
						+ mPasswordText.getText().toString() + "&deviceType="
						+ Config.DeviceType;

				WebManager webManager = new WebManager(MainFragment.this,
						mContext);

				webManager.setDoType("Login_Request");

				if (webManager.isNetworkAvailable()) {
					// do web connect...
					webManager.execute(recipeQueryUrl, "GET");
				}
			}
		}

		public void showAlertView(String title, String Content) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(title);
			builder.setMessage(Content);
			builder.setPositiveButton("Exit", null);
			@SuppressWarnings("unused")
			AlertDialog dialog = builder.show();
		}

		private class registerClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mActivity, RegisterView.class);
				startActivity(i);
			}
		}

		private class enrollDeviceClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				mEDDialog = new EnrollDeviceDialog(MainFragment.this, mActivity);

				if (EnrollDeviceDialog.mIsEnrollDevice)
					mEDDialog.setTitle("Enroll device");
				else
					mEDDialog.setTitle("Unenroll device");
				mEDDialog.show();
			}
		}

		private class forgetPasswordClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {

				String account = UserDataAccess.getDataWithKeyAndEncrypt(
						mActivity, Config.accountnKey);

				ForgotPasswordDialog.mAccount = account;
				mFPDialog = new ForgotPasswordDialog(mActivity);
				mFPDialog.setTitle("Forget Password");
				mFPDialog.show();
			}
		}

		@Override
		public void onRequestComplete(Object... objects) {
			// TODO Auto-generated method stub

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = new JSONObject();

			try {
				jsonObject = (JSONObject) parser.parse(objects[0].toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String requestType = "";

			try {
				requestType = objects[1].toString();
			} catch (Exception ex) {
				requestType = "";
			}

			if (requestType.equals("Login_Request")) {
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

					onResume();

					getUserData();

					//showAlertView("Socialmatic", "Login success!");

				} catch (Exception ex) {
					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.authTokenKey, "");
					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.userIdKey, "");
					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.isLoginKey, "n");

					doEnrollDevice();
				}
			} else if (requestType.equals("Logout_Request")) {
				try {

				} catch (Exception exception) {

				}
			} else if (requestType.equals("get_user_info")) {

				String gender = "";
				try {
					gender = jsonObject.get("gender").toString();
				} catch (Exception exception) {
					gender = "B";
				}
				try {

					String photoUrl = jsonObject.get("profile_photo_url")
							.toString();
					String userNickname = jsonObject.get("nickname").toString();

					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.nameKey, userNickname);
					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.genderKey, gender);
					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.photoKey, photoUrl);

					//createUserData();

					((MainActivity) mActivity).createProfileBtn();

				} catch (Exception exception) {

					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.nameKey, "");
					UserDataAccess.setDataWithKeyAndEncrypt(mContext,
							Config.photoKey, "");
				}
			} else if (requestType.equals("enrolldevice")) {
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

					getUserData();
					onResume();
					//showAlertView("Socialmatic", "Login success!");

				} catch (Exception ex) {

					try {
						String message = jsonObject.get("message").toString();
						showAlertView("Socialmatic",message);
						/*showAlertView("Socialmatic",
								"It seems that you are not registered: you want to proceed with registration?");*/
					} catch (Exception exception) {

					}
				}
			} else if (requestType.equals("unlinkdevice_url")) {
				try {
					// JSONObject data = (JSONObject) jsonObject.get("data");
					String response = jsonObject.get("Response").toString();
					// if (response.equals("Unlink OK"))
					/*signOutAndClearData();
					((MainActivity) mActivity).createProfileBtn();
                    */
				} catch (Exception ex) {
				}
			}
		}
	}
}

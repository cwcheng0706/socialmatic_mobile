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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.configuration.LoginView.MainFragment;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class DeleteAccountView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private Button mConfirmBtn;

	private TextView mTitleLab;
	private TextView mQuestionLab;
	private TextView mContentLab;

	private Spinner mSpinner;

	private ImageView mUserPhotoImageView;

	private ImageLoader imageLoader;

	private TextView mUserNameLab;

	int mSelectPosition = 1;

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

		setContentView(R.layout.delete_account_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mTitleLab = (TextView) findViewById(R.id.title_lab);
		mQuestionLab = (TextView) findViewById(R.id.question_lab);
		mContentLab = (TextView) findViewById(R.id.content_lab);
		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
		mSpinner = (Spinner) findViewById(R.id.spinnner);

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		mTitleLab.setTypeface(tf);
		mQuestionLab.setTypeface(tf);
		mContentLab.setTypeface(tf);

		// --------------------------------------------------
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {
						"I don't like this social network",
						"Absence of privacy", "Excessive advertising",
						"Personal" });

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mSpinner.setAdapter(adapter);

		mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView adapterView, View view,
					int position, long id) {

				mSelectPosition = position + 1;

			}

			public void onNothingSelected(AdapterView arg0) {

			}

		});
		// --------------------------------------------------

		// createUserData();
	}

	private void createUserData() {
		String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.photoKey);
		String gender = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.genderKey);
		String userName = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.nameKey);

		if (photoUrl.equals("")) {
			Drawable res;

			if (gender.equals("M"))
				res = getResources().getDrawable(R.drawable.account_photo_3);
			else if (gender.equals("W"))
				res = getResources().getDrawable(R.drawable.account_photo_1);
			else
				res = getResources().getDrawable(R.drawable.account_photo_2);
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

			String deviceID = UserDataAccess
					.getSystemUseDeviceId(DeleteAccountView.this);
			String authtoken = UserDataAccess.getDataWithKeyAndEncrypt(
					DeleteAccountView.this, Config.authTokenKey);
			String userID = UserDataAccess.getDataWithKeyAndEncrypt(
					DeleteAccountView.this, Config.userIdKey);

			String recipeQueryUrl = getString(R.string.socialmatic_base_url)
					+ getString(R.string.delete_user_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceID));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("authtoken", authtoken));
			nameValuePairs.add(new BasicNameValuePair("motivationID", String
					.valueOf(mSelectPosition)));

			WebManager webManager = new WebManager(DeleteAccountView.this);
			webManager.setDoType("delete_user");

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
			// finish();
		}
	}

	public void showAlertView(String title, String Content) {

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(Content);
		builder.setPositiveButton("Exit", OkClick);
		@SuppressWarnings("unused")
		AlertDialog dialog = builder.show();
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

		if (requestType.equals("delete_user")) {
			try {
				// JSONObject data = (JSONObject) jsonObject.get("data");
				String response = jsonObject.get("Response").toString();
				if (response.equals("Request OK")) {
					UserDataAccess.setDataWithKeyAndEncrypt(this,
							Config.isLoginKey, "n");

					UserDataAccess.setDataWithKeyAndEncrypt(this,
							Config.nameKey, "");
					UserDataAccess.setDataWithKeyAndEncrypt(this,
							Config.genderKey, "");
					UserDataAccess.setDataWithKeyAndEncrypt(this,
							Config.photoKey, "");
					UserDataAccess.setDataWithKeyAndEncrypt(this,
							Config.GCMRegIdKey, "");
					UserDataAccess.mGCMRegId = "";

					showAlertView("Socialmatic", "Delete account success!");
				}
			} catch (Exception ex) {
			}
		}
	}
}

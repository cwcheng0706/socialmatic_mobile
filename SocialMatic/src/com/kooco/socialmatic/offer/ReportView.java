package com.kooco.socialmatic.offer;

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
import android.app.AlertDialog.Builder;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class ReportView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private Button mSendBtn;
	private Spinner mSpinner;
	private EditText mContentEditText;
	private TextView mTitleTextView;
	private TextView mContentLab;

	public static String mObjectType = "";
	public static String mObjectID = "";

	private int mSelectCategoryID = 0;

	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.report_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setTypeface(tf);
		mContentLab = (TextView) findViewById(R.id.content_lab);
		mContentLab.setTypeface(tf);

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mContentEditText = (EditText) findViewById(R.id.report_body_text);
		mContentEditText.setTypeface(tf);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		mSendBtn = (Button) findViewById(R.id.confirm_btn);
		mSendBtn.setOnClickListener(new SendBtnListener());

		mSpinner = (Spinner) findViewById(R.id.spinnner);

		// --------------------------------------------------
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {
						"Select category Value", "Inappropriate Content",
						"Spam", "Abuse", "Licensed Material", "Other" });

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mSpinner.setAdapter(adapter);

		mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			public void onItemSelected(AdapterView adapterView, View view,
					int position, long id) {
				mSelectCategoryID = position;
				/*
				 * Toast.makeText(MainActivity.this, "您選擇" +
				 * adapterView.getSelectedItem().toString(),
				 * Toast.LENGTH_LONG).show();
				 */
			}

			public void onNothingSelected(AdapterView arg0) {
				mSelectCategoryID = 0;
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


	private void sendReport() {

		if (mSelectCategoryID <= 0) {
			ShowMsgDialog("category Value is required and can't be empty",
					false);
			return;
		}

		if (mContentEditText.getText().toString().equals("")) {
			ShowMsgDialog("Description Value is required and can't be empty",
					false);
			return;
		}

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.report_abuse_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("categoryID", String
				.valueOf(mSelectCategoryID)));
		nameValuePairs.add(new BasicNameValuePair("subjectID", mObjectID));
		nameValuePairs.add(new BasicNameValuePair("subjectType", mObjectType));
		nameValuePairs.add(new BasicNameValuePair("description",
				mContentEditText.getText().toString()));
		
		Log.d("sendReport----------", "mObjectType-----" + mObjectType + "    mObjectID----" + mObjectID);

		WebManager webManager = new WebManager(ReportView.this);

		webManager.setDoType("report_abuse_url");

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
	}

	private void ShowMsgDialog(String Msg, final boolean leaveFlag) {

		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setTitle("SocialMatic");

		MyAlertDialog.setMessage(Msg);

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (leaveFlag)
					finish();
			}

		};

		MyAlertDialog.setNeutralButton("OK", OkClick);
		MyAlertDialog.show();

	}

	private class SendBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			sendReport();
		}
	}

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects[1].toString().equals("report_abuse_url")) {
			JSONParser parser = new JSONParser();

			JSONObject result;
			try {
				result = (JSONObject) parser.parse(objects[0].toString());
				String response = result.get("Response").toString();

				if (response.equals("Request OK")) {
					ShowMsgDialog("Your report has been sent!", true);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

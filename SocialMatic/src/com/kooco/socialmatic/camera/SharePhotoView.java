package com.kooco.socialmatic.camera;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.share.ShareAuthAdapter;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class SharePhotoView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	// view object def
	//---------------------------------------
	private Button mBackBtn;
	private Button mShareBtn;
	private TextView mTitleTextView;
	private ImageView mImageView;
	public static Bitmap mImg = null;
	private ScrollView mParentScrollView;
	private ScrollView mTagsScrollView;
	private LinearLayout mTagLinearLayout;
	private LinearLayout mCitiesLinearLayout;

	private Button mLocationBtn;
	private Button mAddTagBtn;
	private EditText mTagEditText;
	private EditText mDescriptionEditText;
	//---------------------------------------

	private ArrayList<String> mListTags = new ArrayList<String>();

	private boolean mLocationFlag = true;

	// for get loaction info
	//---------------------------------------
	LocationManager mLocationManager = null;
	LocationListener mLocationListener = null;
	Location mLastLocation = null;
	//---------------------------------------

	// the best gps get provider (gps or wifi)
	private String bestProvider = null;

	ShareAuthAdapter mSocialAuthAdapter;

	Button mFacebookBtn;
	Button mTwitterBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.share_photo_view);

		mLocationBtn = (Button) findViewById(R.id.add_location_button);

		mTagEditText = (EditText) findViewById(R.id.tag_edittext);
		mDescriptionEditText = (EditText) findViewById(R.id.description_edittext);

		mAddTagBtn = (Button) findViewById(R.id.add_tag_btn);

		mAddTagBtn.setOnClickListener(new addTagBtnClickListener());

		mTagLinearLayout = (LinearLayout) findViewById(R.id.add_tag_layout);
		mCitiesLinearLayout = (LinearLayout) findViewById(R.id.add_city_layout);

		mParentScrollView = (ScrollView) findViewById(R.id.parent_scrollview);
		mTagsScrollView = (ScrollView) findViewById(R.id.tags_scrollview);
		
		// ----------------------------------
		mParentScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				findViewById(R.id.tags_scrollview).getParent()
						.requestDisallowInterceptTouchEvent(false);
				return false;
			}
		});

		mTagsScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		// ----------------------------------

		mBackBtn = (Button) findViewById(R.id.back_button);
		mShareBtn = (Button) findViewById(R.id.share_button);
		mTitleTextView = (TextView) findViewById(R.id.title_textview);
		mImageView = (ImageView) findViewById(R.id.photo_imageview);
		mImageView.setImageBitmap(mImg);

		Typeface tf1 = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mTitleTextView.setTypeface(tf1);

		mLocationBtn.setOnClickListener(new locationBtnClickListener());
		mBackBtn.setOnClickListener(new BackClickListener());
		mShareBtn.setOnClickListener(new ShareBtnClickListener());

		mLocationListener = new myLocationListener();

		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // 取得系統定位服務

		Criteria criteria = new Criteria(); // 資訊提供者選取標準

		bestProvider = mLocationManager.getBestProvider(criteria, true); // 選擇精準度最高的提供者

		try {
			mLocationManager.requestLocationUpdates(bestProvider, 0, 0,
					mLocationListener);
			Location location = mLocationManager
					.getLastKnownLocation(bestProvider);
			getLocation(location);
		} catch (Exception ex) {
			mLocationFlag = false;
			setLocationStatus(null);
		}

		// share
		// ------------------------------------------
		mSocialAuthAdapter = ShareAuthAdapter.getInstance(this);
		mSocialAuthAdapter.getResponseListener().setAdapter(mSocialAuthAdapter);

		// Add providers
		if (mSocialAuthAdapter.providerCount() <= 0) {
			mSocialAuthAdapter.addProvider(Provider.FACEBOOK,
					R.drawable.facebook);
			mSocialAuthAdapter
					.addProvider(Provider.TWITTER, R.drawable.twitter);
		}
		// ------------------------------------------

		mFacebookBtn = (Button) findViewById(R.id.facebook_button);
		mFacebookBtn.setTag("facebook");
		mSocialAuthAdapter.enable(mFacebookBtn, mDescriptionEditText);
		mTwitterBtn = (Button) findViewById(R.id.twitter_button);
		mTwitterBtn.setTag("twitter");
		mSocialAuthAdapter.enable(mTwitterBtn, mDescriptionEditText);
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			mLocationManager.removeUpdates(mLocationListener);
		} catch (Exception ex) {

		}

		Criteria criteria = new Criteria(); // 資訊提供者選取標準
		bestProvider = mLocationManager.getBestProvider(criteria, true); // 選擇精準度最高的提供者

		try {
			Location location = mLocationManager
					.getLastKnownLocation(bestProvider);
			getLocation(location);
		} catch (Exception ex) {
			mLocationFlag = false;
			setLocationStatus(null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			mLocationManager.removeUpdates(mLocationListener);
		} catch (Exception ex) {

		}
	}

	private void setLocationStatus(Location location) {

		if (mLocationFlag && location != null) {
			mLocationFlag = true;
			Drawable bgImg = getResources().getDrawable(
					R.drawable.button_orange_selector);
			mLocationBtn.setBackground(bgImg);
			mLocationBtn.setText("Add Location");
		} else {
			mLocationFlag = false;
			Drawable bgImg = getResources().getDrawable(
					R.drawable.button_green_1_selector);
			mLocationBtn.setBackground(bgImg);
			mLocationBtn.setText("Don't Add Location");
		}
	}

	private void getLocation(Location location) { // 將定位資訊顯示在畫面中

		if (location != null) {

			Double longitude = location.getLongitude(); // 取得經度
			Double latitude = location.getLatitude(); // 取得緯度

			mLocationFlag = true;
			mLastLocation = location;
		} else {

		}
		setLocationStatus(location);
	}

	private class myLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				getLocation(location);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

	private void createListView(int type) {
		ArrayList<String> aryList = new ArrayList<String>();
		LinearLayout dealLinearLayout = null;
		String strTag = "";

		float d = getResources().getDisplayMetrics().density;

		aryList = mListTags;
		dealLinearLayout = mTagLinearLayout;
		strTag = "tag_";

		mTagLinearLayout.removeAllViews();
		// }

		LinearLayout addLinearLayout = null;

		for (int i = 0; i < aryList.size(); i++) {
			String strText = aryList.get(i).toString();

			if (i % 3 == 0) {
				addLinearLayout = new LinearLayout(SharePhotoView.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

				params.setMargins(0, 0, 0, 5);
				addLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				addLinearLayout.setLayoutParams(params);
			}

			TextView tv = new TextView(SharePhotoView.this);

			LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
					(int) (100 * d), (int) (30 * d));

			tvParams.setMargins(0, 0, 5, 0);
			tv.setLayoutParams(tvParams);
			tv.setTextSize(14);
			tv.setPadding(5, 0, 5, 0);
			tv.setTextColor(Color.parseColor("#BFBFBF"));
			tv.setGravity(Gravity.CENTER_VERTICAL);
			Drawable bgImg = getResources().getDrawable(R.drawable.tag_frame);
			tv.setBackground(bgImg);

			String strAddText = strText;
			if (strText.length() > 9)
				strAddText = strText.substring(0, 8) + "...";

			tv.setText(strAddText);

			tv.setTag(strTag + i);
			tv.setOnClickListener(new textViewClickListener());

			addLinearLayout.addView(tv);

			if (i % 3 == 2 || i == aryList.size() - 1)
				dealLinearLayout.addView(addLinearLayout);
		}
	}

	private class textViewClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String[] aryTag = v.getTag().toString().split("_");

			String strType = aryTag[0];
			int index = Integer.parseInt(aryTag[1]);

			mListTags.remove(index);
			createListView(1);
		}
	}

	private class locationBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (!mLocationFlag) {

				mLocationFlag = true;
				try {
					mLocationManager.removeUpdates(mLocationListener);
				} catch (Exception ex) {

				}

				Criteria criteria = new Criteria(); // 資訊提供者選取標準
				bestProvider = mLocationManager.getBestProvider(criteria, true); // 選擇精準度最高的提供者

				try {
					mLocationManager.requestLocationUpdates(bestProvider, 0, 0,
							mLocationListener);
					Location location = mLocationManager
							.getLastKnownLocation(bestProvider);
					getLocation(location);
				} catch (Exception ex) {
					setLocationStatus(null);

					startActivity(new Intent(
							Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}

			} else {
				mLocationFlag = false;
				setLocationStatus(null);
			}
		}
	}

	private class addTagBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (!mTagEditText.getText().toString().equals("")) {
				boolean isExistFlag = false;
				for (String str : mListTags) {
					if (str.equals(mTagEditText.getText().toString())) {
						isExistFlag = true;
						break;
					}
				}

				if (!isExistFlag) {
					mListTags.add(mTagEditText.getText().toString());
					createListView(1);
					mTagEditText.setText("");
				}
			}
		}
	}

	private void ShowMsgDialog(String Msg, final boolean goCameraFlag) {

		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setTitle("SocialMatic");

		MyAlertDialog.setMessage(Msg);

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				if (goCameraFlag) {
					Intent i = new Intent(SharePhotoView.this, MainView.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			}

		};

		MyAlertDialog.setNeutralButton("OK", OkClick);
		MyAlertDialog.show();

	}

	private class ShareBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (mDescriptionEditText.getText().toString().equals("")) {
				ShowMsgDialog("Please enter photo's description!", false);
				return;
			}

			String deviceId = UserDataAccess
					.getSystemUseDeviceId(SharePhotoView.this);

			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					SharePhotoView.this, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(
					SharePhotoView.this, Config.userIdKey);

			String queryUrl = SharePhotoView.this
					.getString(R.string.socialmatic_base_url)
					+ SharePhotoView.this.getString(R.string.uploadphoto_url);

			String strTags = "";

			for (int i = 0; i < mListTags.size(); i++) {
				strTags += mListTags.get(i);

				if (i != mListTags.size() - 1)
					strTags += ",";
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String currentDateandTime = sdf.format(new Date());

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("userID", userId));
			nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
			nameValuePairs.add(new BasicNameValuePair("photoID",
					currentDateandTime));
			nameValuePairs.add(new BasicNameValuePair("description",
					mDescriptionEditText.getText().toString()));

			nameValuePairs.add(new BasicNameValuePair("freeTags", strTags));

			if (mLastLocation != null && mLocationFlag) {

				Double longitude = mLastLocation.getLongitude(); // 取得經度
				Double latitude = mLastLocation.getLatitude(); // 取得緯度

				String strLocation = latitude + "," + longitude;

				nameValuePairs.add(new BasicNameValuePair("geo-localization",
						strLocation));
			}

			// image convert to base64 string
			// --------------------------------------------
			ByteArrayOutputStream full_stream = new ByteArrayOutputStream();
			mImg.compress(Bitmap.CompressFormat.PNG, 100, full_stream);
			byte[] full_bytes = full_stream.toByteArray();
			String img_full = Base64.encodeToString(full_bytes, Base64.DEFAULT);

			nameValuePairs.add(new BasicNameValuePair("byte", img_full));
			// --------------------------------------------

			WebManager webManager = new WebManager(SharePhotoView.this);

			webManager.setDoType("upload_photo");

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(
						nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
			}

			if (webManager.isNetworkAvailable()) { // do web connect...
				webManager.execute(queryUrl, "POST");
			}
		}
	}

	private class BackClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects[1].toString().equals("upload_photo")) {
			JSONParser parser_obj = new JSONParser();
			try {
				JSONObject resultObj = (JSONObject) parser_obj.parse(objects[0]
						.toString());

				if (resultObj.get("upload").toString().equals("OK")) {
					ShowMsgDialog("Upload photo success!", true);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

package com.kooco.socialmatic.profile;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.offer.OfferMapView;
import com.kooco.tool.Contents;
import com.kooco.tool.QRCodeEncoder;
import com.kooco.tool.ScrollGridView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class SelectUserProfileView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	ArrayList<JSONObject> mAryPhotoList = new ArrayList<JSONObject>();

	static JSONObject mProfileJSONObject = new JSONObject();
	static JSONArray mAlbumJSONArray = new JSONArray();

	private TextView mUserNameTextView;
	private TextView mStateText;

	private ImageView mBCImageview;
	private ImageView mUserPhotoImageview;

	private ProgressBar mUserPhotoProBar;

	private Button mEditBtn;
	private Button mLocationBtn;
	private Button mAlbumBtn;

	private TextView mFollowerLab;
	private TextView mFollowingLab;
	private TextView mMilvesTextview;
	private TextView mLocationTextView;

	FlowPhotoAdapter mPhoto_adapter;

	public ImageView mFlowImageview;
	private ProgressBar mFlowPhotoProBar;

	private ImageLoader imageLoader;

	JSONArray mFollowerJSONArray = new JSONArray();
	JSONArray mFollowingJSONArray = new JSONArray();

	public static String mQueryUserID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.profile_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		imageLoader = new ImageLoader(getApplicationContext());
		mFlowImageview = (ImageView) findViewById(R.id.flow_photo_imageview);
		mFlowPhotoProBar = (ProgressBar) findViewById(R.id.flow_photo_progressbar);

		mUserNameTextView = (TextView) findViewById(R.id.user_name_textview);
		mUserNameTextView.setTypeface(tf);

		mStateText = (TextView) findViewById(R.id.state_textview);
		mStateText.setTypeface(tf);

		mFollowerLab = (TextView) findViewById(R.id.follower_lab);
		mFollowerLab.setTypeface(tf);

		mFollowingLab = (TextView) findViewById(R.id.following_lab);
		mFollowingLab.setTypeface(tf);

		mLocationTextView = (TextView) findViewById(R.id.location_textview);
		mLocationTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mLocationTextView.getPaint().setAntiAlias(true);
		mLocationTextView.setTypeface(tf);

		mMilvesTextview = (TextView) findViewById(R.id.milves_textview);
		mMilvesTextview.setTypeface(tf);

		mBCImageview = (ImageView) findViewById(R.id.bc_imageview);
		mUserPhotoImageview = (ImageView) findViewById(R.id.user_photo_imageview);
		mUserPhotoProBar = (ProgressBar) findViewById(R.id.user_photo_progressbar);

		mEditBtn = (Button) findViewById(R.id.edit_btn);
		mEditBtn.setVisibility(View.GONE);
		// mEditBtn.setOnClickListener(new EditProfileBtnListener());

		mLocationBtn = (Button) findViewById(R.id.location_btn);

		mAlbumBtn = (Button) findViewById(R.id.album_btn);
		mAlbumBtn.setOnClickListener(new AlbumBtnListener());

		ScrollGridView scrollGridView = (ScrollGridView) findViewById(R.id.gridview);

		mPhoto_adapter = new FlowPhotoAdapter(this, mAryPhotoList);

		scrollGridView.setAdapter(mPhoto_adapter);

		System.gc();

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			Intent i = getIntent();
			mQueryUserID = i.getStringExtra("selectUserId");
		}
		// getFollowerData();
		// getFollowingData();

		getViewData();

		getAlbum();
	}

	private void getViewData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_user_info_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&subjectKey=" + mQueryUserID;

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_user_info");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	private void getAlbum() {
		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_album_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&albumOwnerID=" + mQueryUserID
				+ "&numOfPhoto=100";

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_album");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	private void getFollowerData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_follower_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&lastReceivedID=&limit=999";

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_follower");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private void getFollowingData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_following_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&lastReceivedID=&limit=999";

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_following");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private void createAlbumListView() throws JSONException, Exception {

		mAryPhotoList.clear();
		for (Object obj : mAlbumJSONArray)
			mAryPhotoList.add((JSONObject) obj);

		mPhoto_adapter.notifyDataSetChanged();
	}

	private void createProfileView() throws JSONException, Exception {

		String nickname = mProfileJSONObject.get("nickname").toString();
		String statusMsg = mProfileJSONObject.get("status_msg").toString();
		String qrContent = mProfileJSONObject.get("qr_details").toString();
		String userPhotoUrl = mProfileJSONObject.get("profile_photo_url")
				.toString();

		String flowPhotoUrl = mProfileJSONObject.get("flow_photo_url")
				.toString();

		String strLocation = mProfileJSONObject.get("location").toString();

		String gender = "";
		
		try {
			gender = mProfileJSONObject.get("gender").toString();
		} catch (Exception e)
		{
			gender = "B";
		}

		// ------------------------------------------------
		/*UserDataAccess.setDataWithKeyAndEncrypt(this, Config.nameKey,
				nickname);
		UserDataAccess.setDataWithKeyAndEncrypt(this, Config.genderKey,
				gender);
		UserDataAccess.setDataWithKeyAndEncrypt(this, Config.photoKey,
				userPhotoUrl);*/
		// ------------------------------------------------

		if (!flowPhotoUrl.equals("")) {
			
			Log.d("--------------","flowPhotoUrl-------------" + flowPhotoUrl);
			mFlowPhotoProBar.setVisibility(View.GONE);
			mFlowImageview.setTag(flowPhotoUrl);
			imageLoader.DisplayImage(flowPhotoUrl, mFlowImageview, 100);
			/*
			 * new com.kooco.tool.LoadImage(mActivity, mFlowImageview,
			 * mFlowPhotoProBar, flowPhotoUrl).execute();
			 */
		} else
			mFlowPhotoProBar.setVisibility(View.GONE);

		mLocationTextView.setText(strLocation);
		mLocationTextView.setTag(strLocation);

		mLocationTextView.setOnClickListener(new OfferLocationClickListener());
		
		String followerCount = mProfileJSONObject.get("follower_number")
				.toString();
		String followingCount = mProfileJSONObject.get("following_number")
				.toString();
		
		mFollowerLab.setText(followerCount + " Follower");
		mFollowingLab.setText(followingCount + " Following");

		mLocationBtn.setTag(strLocation);
		mLocationBtn.setOnClickListener(new OfferLocationClickListener());

		mUserNameTextView.setText(nickname);
		mStateText.setText(statusMsg);
		
		mAlbumBtn.setTag(nickname);

		try {
			QRCodeEncoder qrCodeEncoder = null;

			qrCodeEncoder = new QRCodeEncoder(qrContent, null,
					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 140);

			mBCImageview.setImageBitmap(qrCodeEncoder.encodeAsBitmap());

		} catch (WriterException e) {
			Log.e("ProfileView", "Could not encode barcode", e);
		} catch (IllegalArgumentException e) {
			Log.e("ProfileView", "Could not encode barcode", e);
		}

		mUserPhotoProBar.setVisibility(View.GONE);

		Log.d("gender-----------------------", "gender------------------------" + gender);
		if (userPhotoUrl.equals("")) {

			Drawable res;
			
			if (gender.equals("M"))
				res = getResources().getDrawable(R.drawable.account_photo_3);
			else if (gender.equals("W"))
				res = getResources().getDrawable(R.drawable.account_photo_1);
			else
				res = getResources().getDrawable(R.drawable.account_photo_2);

			mUserPhotoImageview.setImageDrawable(res);
		} else {
			mUserPhotoImageview.setTag(userPhotoUrl);

			imageLoader.DisplayImage(userPhotoUrl, mUserPhotoImageview, 80);
		}
	}
	
	private class AlbumBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			ProfileAlmunView.mAryPhotoList = mAryPhotoList;
			ProfileAlmunView.mOwner = v.getTag().toString() + "'s";
			Intent i = new Intent(SelectUserProfileView.this, ProfileAlmunView.class);
			startActivity(i);
		}
	}

	private class OfferLocationClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			OfferMapView.mSubject = v.getTag().toString();

			Intent i = new Intent(SelectUserProfileView.this, OfferMapView.class);
			startActivity(i);
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects[1].toString().equals("get_user_info")) {
			JSONParser parser = new JSONParser();
			try {
				mProfileJSONObject = (JSONObject) parser.parse(objects[0]
						.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				createProfileView();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (objects[1].toString().equals("get_album")) {

			JSONParser parser_obj = new JSONParser();
			try {
				JSONObject resultObj = (JSONObject) parser_obj.parse(objects[0]
						.toString());
				mAlbumJSONArray = (JSONArray) resultObj.get("photos");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (mAlbumJSONArray.size() > 0)
				try {
					createAlbumListView();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}

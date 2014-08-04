package com.kooco.socialmatic.profile;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.flow.FlowAdapter;
import com.kooco.socialmatic.flow.FlowCommentsView;
import com.kooco.socialmatic.flow.FlowMapView;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.offer.OfferMapView;
import com.kooco.socialmatic.search.SearchResultView;
import com.kooco.tool.Contents;
import com.kooco.tool.LoadImage;
import com.kooco.tool.QRCodeEncoder;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class PhotoContentView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	public static int mPhotoId = 0;
	public static int mUserId = 0;

	private ImageView mPostUserPhoto;
	private TextView mPostUserName;

	private ImageView mPhotoImageView;
	private ProgressBar mPhotoProgressBar;
	private ImageView mBCImageView;
	private ImageView mMapImageview;
	private TextView mShootedDate;
	private LinearLayout mTagLinearLayout;

	private LinearLayout mCitiesLinearLayout;

	private ScrollView mParentScrollView;
	private ScrollView mTagsScrollView;
	private ScrollView mCitiesScrollView;

	private Button mUnCoolBtn;
	private Button mPathBtn;
	private Button mCommentBtn;

	private TextView mCoolCountLab;
	private TextView mSeeCountLab;
	private TextView mPinCountLab;
	private TextView mDescriptionLab;

	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	private ArrayList<LoadImage> mAryImageLoader = new ArrayList<LoadImage>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.photo_content_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mMapImageview = (ImageView) findViewById(R.id.map_imageview);
		mPostUserPhoto = (ImageView) findViewById(R.id.post_userphoto_imageview);
		mPhotoImageView = (ImageView) findViewById(R.id.photo_image);
		mPhotoProgressBar = (ProgressBar) findViewById(R.id.photo_progressbar);
		mPostUserName = (TextView) findViewById(R.id.post_username_textview);
		mShootedDate = (TextView) findViewById(R.id.shot_date_textview);
		mTagLinearLayout = (LinearLayout) findViewById(R.id.add_tag_layout);
		mCitiesLinearLayout = (LinearLayout) findViewById(R.id.add_city_layout);
		
		mDescriptionLab = (TextView) findViewById(R.id.subject_textview);

		mParentScrollView = (ScrollView) findViewById(R.id.parent_scrollview);
		mTagsScrollView = (ScrollView) findViewById(R.id.tags_scrollview);
		mCitiesScrollView = (ScrollView) findViewById(R.id.cities_scrollview);

		mUnCoolBtn = (Button) findViewById(R.id.cool_btn);
		mPathBtn = (Button) findViewById(R.id.path_btn);
		mCommentBtn = (Button) findViewById(R.id.comment_btn);

		mCoolCountLab = (TextView) findViewById(R.id.cool_account_textview);
		mSeeCountLab = (TextView) findViewById(R.id.see_account_textview);
		mPinCountLab = (TextView) findViewById(R.id.pin_account_textview);

		mCommentBtn.setOnClickListener(new CommentBtnClickListener());
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

		mCitiesScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		// ----------------------------------

		mBCImageView = (ImageView) findViewById(R.id.bc_imageview);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		// getUserInfo();
		System.gc();

		//createUserData();
		getViewData();
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


	private class CommentBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			FlowCommentsView.mPhotoId = mPhotoId;
			Intent i = new Intent(PhotoContentView.this, FlowCommentsView.class);
			PhotoContentView.this.startActivity(i);
		}
	}

	private void getViewData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_photo_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&photoID=" + mPhotoId;

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_photo");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private void createUserData(JSONObject resultObj) throws JSONException,
			Exception, ParseException {

		String postUserName = resultObj.get("display_name").toString();
		mPostUserName.setText(postUserName);

		mPostUserName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mPostUserName.getPaint().setAntiAlias(true);

		String postUserPhoto = resultObj.get("photo_profile_url").toString();

		String gender = "M";

		try {
			gender = resultObj.get("gender").toString();
		} catch (Exception ex) {
			gender = "M";
		}

		String userID = resultObj.get("user_id").toString();

		mPostUserPhoto.setTag(postUserPhoto + "<>" + userID);
		if (postUserPhoto.equals("")) {
			Drawable res;

			if (gender.equals("M"))
				res = getResources().getDrawable(R.drawable.account_photo_3);
			else
				res = getResources().getDrawable(R.drawable.account_photo_1);

			mPostUserPhoto.setImageDrawable(res);
		} else {
			new com.kooco.tool.LoadImage(this, mPostUserPhoto, null,
					postUserPhoto).execute();
		}

		mPostUserName.setTag("<>" + userID);
		mPostUserName.setOnClickListener(new UserInfoBtnListener());
		mPostUserPhoto.setOnClickListener(new UserInfoBtnListener());
	}

	private class UserInfoBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String[] arySplit = v.getTag().toString().split("<>");
			String strQueryUserID = arySplit[1].toString();

			SelectUserProfileView.mQueryUserID = strQueryUserID;

			Intent i = new Intent(PhotoContentView.this,
					SelectUserProfileView.class);
			startActivity(i);
		}
	}

	private void createViewData(JSONObject resultObj) throws JSONException,
			Exception, ParseException {

		float d = getResources().getDisplayMetrics().density;

		String photoUrl = resultObj.get("photo_url").toString();

		// ------------------------------------------
		String coolNum = resultObj.get("user_cool").toString();
		String qr_details = resultObj.get("qr_details").toString();
		String shootedDate = resultObj.get("upload_date").toString();
		String discription = resultObj.get("photo_description").toString();
		
		mDescriptionLab.setText(discription);

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(shootedDate + "000"));

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		String strShootDate = formatter.format(cal.getTime());

		JSONParser parser_obj = new JSONParser();

		String[] aryCity = null;
		if (resultObj.get("upload_city") != null)
			aryCity = resultObj.get("upload_city").toString().split(",");

		JSONArray aryTags = null;
		if (resultObj.get("photo_tags") != null)
			aryTags = (JSONArray) parser_obj.parse(resultObj.get("photo_tags")
					.toString());

		JSONArray arySee = null;
		if (resultObj.get("photo_see") != null)
			arySee = (JSONArray) parser_obj.parse(resultObj.get("photo_see")
					.toString());

		int seeNum = 0;

		if (arySee != null)
			seeNum = arySee.size();

		mCoolCountLab.setText(coolNum);

		mSeeCountLab.setText(String.valueOf(seeNum));

		mShootedDate.setText("shooted on " + strShootDate);
		
		mCitiesLinearLayout.removeAllViews();

		if (aryCity != null) {
			LinearLayout cityAddLayout = null;

			for (int i = 0; i < aryCity.length; i++) {
				String strCity = aryCity[i].toString();

				if (i % 3 == 0) {
					cityAddLayout = new LinearLayout(PhotoContentView.this);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

					params.setMargins(0, 0, 0, 5);
					cityAddLayout.setOrientation(LinearLayout.HORIZONTAL);
					cityAddLayout.setLayoutParams(params);
				}

				TextView tv = new TextView(PhotoContentView.this);

				LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
						(int) (55 * d), (int) (20 * d));

				tvParams.setMargins(0, 0, 5, 0);
				tv.setLayoutParams(tvParams);
				tv.setTextSize(12);
				tv.setTextColor(Color.parseColor("#BFBFBF"));
				tv.setGravity(Gravity.CENTER);
				Drawable bgImg = getResources().getDrawable(
						R.drawable.flow_tag_city);
				tv.setBackground(bgImg);

				tv.setText(strCity);
				tv.setTag(strCity);
				tv.setOnClickListener(new LocationClickListener());

				cityAddLayout.addView(tv);

				if (i % 3 == 2 || i == aryCity.length - 1)
					mCitiesLinearLayout.addView(cityAddLayout);
			}
		}
		
		mTagLinearLayout.removeAllViews();

		if (aryTags != null) {
			LinearLayout layout = null;

			for (int i = 0; i < aryTags.size(); i++) {
				String strTag = aryTags.get(i).toString();
				
				if (strTag.equals(""))
					continue;

				if (i % 3 == 0) {
					layout = new LinearLayout(PhotoContentView.this);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

					params.setMargins(0, 0, 0, 5);
					layout.setOrientation(LinearLayout.HORIZONTAL);
					layout.setLayoutParams(params);
				}

				TextView tv = new TextView(PhotoContentView.this);

				LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
						(int) (55 * d), (int) (20 * d));
				tvParams.setMargins(0, 0, 5, 0);
				tv.setLayoutParams(tvParams);
				tv.setTextSize(12);
				tv.setTextColor(Color.parseColor("#BFBFBF"));
				tv.setGravity(Gravity.CENTER);
				Drawable bgImg = getResources()
						.getDrawable(R.drawable.flow_tag);
				tv.setBackground(bgImg);

				tv.setTag(strTag);
				tv.setOnClickListener(new searchBtnClickListener());

				if (strTag.length() > 6)
					tv.setText(strTag.substring(0, 5) + "...");
				else
					tv.setText(strTag);

				layout.addView(tv);

				if (i % 3 == 2 || i == aryTags.size() - 1) {
					mTagLinearLayout.addView(layout);
				}
			}
		}

		try {
			QRCodeEncoder qrCodeEncoder = null;

			int dimension = 140;
			float density = getResources().getDisplayMetrics().density;
			qrCodeEncoder = new QRCodeEncoder(qr_details, null,
					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
					(int) (dimension * (density / 1.5f)));

			mBCImageView.setImageBitmap(qrCodeEncoder.encodeAsBitmap());

		} catch (WriterException e) {
			Log.e("PhotoContentView", "Could not encode barcode", e);
		} catch (IllegalArgumentException e) {
			Log.e("PhotoContentView", "Could not encode barcode", e);
		}
		// ------------------------------------------

		mPathBtn.setOnClickListener(new MapBtnClickListener());
		mPathBtn.setTag(resultObj);

		mMapImageview.setOnClickListener(new MapBtnClickListener());
		mMapImageview.setTag(resultObj);

		mPhotoImageView.setTag(photoUrl);
		new com.kooco.tool.LoadImage(this, mPhotoImageView, mPhotoProgressBar,
				photoUrl).execute();

		String userCoolFlag = "-1";
		userCoolFlag = resultObj.get("user_cool").toString();

		if (userCoolFlag.equals("1")) {
			Drawable res = getResources().getDrawable(
					R.drawable.button_orange_selector);
			mUnCoolBtn.setBackground(res);
			mUnCoolBtn.setText("Uncool");

		} else {
			Drawable res = getResources().getDrawable(
					R.drawable.button_green_1_selector);
			mUnCoolBtn.setBackground(res);
			mUnCoolBtn.setText("Cool");
		}

		Object[] aryObjTag = { "album_photo" + "<>" + mPhotoId + "<>" };
		mUnCoolBtn.setTag(aryObjTag);
		mUnCoolBtn.setOnClickListener(new CoolBtnListener());

	}

	private void makeCool(String objectType, String objectID, String doType) {
		String deviceId = UserDataAccess
				.getSystemUseDeviceId(PhotoContentView.this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
				PhotoContentView.this, Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(
				PhotoContentView.this, Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url);

		if (doType.equals("Cool"))
			queryUrl += getString(R.string.make_cool_url);
		else
			queryUrl += getString(R.string.make_uncool_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("objectType", objectType));
		nameValuePairs.add(new BasicNameValuePair("objectID", objectID));

		WebManager webManager = new WebManager(PhotoContentView.this);

		webManager.setDoType(doType);

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
	}

	private class CoolBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			Object[] aryObjTag = (Object[]) ((Button) v).getTag();

			String btnTag = aryObjTag[0].toString();

			String[] aryTag = btnTag.split("<>");
			String btnText = ((Button) v).getText().toString();

			makeCool(aryTag[0], aryTag[1], btnText);

		}
	}

	private class MapBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			JSONParser parser = new JSONParser();

			try {
				FlowMapView.mJSONObject = (JSONObject) parser.parse(v.getTag()
						.toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Intent i = new Intent(PhotoContentView.this, FlowMapView.class);
			PhotoContentView.this.startActivity(i);
		}
	}

	private class LocationClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			Log.d("LocationClickListener---", "LocationClickListener---"
					+ v.getTag().toString());
			OfferMapView.mSubject = v.getTag().toString();

			Intent i = new Intent(PhotoContentView.this, OfferMapView.class);
			PhotoContentView.this.startActivity(i);
		}
	}

	private class searchBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			//String urlEncoded = Uri.encode(v.getTag().toString());
			SearchResultView.mKeyWord = v.getTag().toString();
			
			Intent i = new Intent(PhotoContentView.this, SearchResultView.class);
			startActivity(i);
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

		if (objects.length == 2) {
			String action = objects[1].toString();

			if (action.equals("get_photo")) {
				try {

					JSONParser parser = new JSONParser();

					JSONObject resultObj = (JSONObject) parser.parse(objects[0]
							.toString());

					createViewData((JSONObject) resultObj.get("photo"));

					createUserData((JSONObject) resultObj.get("owner"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (objects[1].toString().equals("Cool")
					|| objects[1].toString().equals("Uncool")) {

				getViewData();

			}
		}
	}
}

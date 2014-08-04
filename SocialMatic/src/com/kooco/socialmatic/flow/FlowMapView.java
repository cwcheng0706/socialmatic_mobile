package com.kooco.socialmatic.flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.offer.ReportView;
import com.kooco.tool.GeocoderTask;

public class FlowMapView extends SherlockFragmentActivity {

	private Button mBackBtn;
	private TextView mTitleTextView;
	public static JSONObject mJSONObject = null;

	ProgressBar mMapProgressBar;
	ProgressBar mPhotoProgressBar;
	ImageView mPhoto;
	TextView mOkCountTextView;
	TextView mSeeCountTextView;
	TextView mPinCountTextView;

	GoogleMap mGoogleMap;

	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	public Button mReportBtn;
	private String mObjectID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.flow_map_view);
		// ----------------------------------

		int photoId = 0;
		String photoUrl = "";
		String strQrcode = "";
		String strShootDate = "";
		String strSubject = "";
		String strCoolNum = "";
		String strCity = "";

		try {
			mObjectID = mJSONObject.get("object_id").toString();
			photoId = Integer.parseInt(mJSONObject.get("object_id").toString());
			photoUrl = mJSONObject.get("photo_url").toString();
			strQrcode = mJSONObject.get("qr_details").toString();
			strShootDate = mJSONObject.get("upload_date").toString();
			strSubject = mJSONObject.get("object_title").toString();
			strCoolNum = mJSONObject.get("object_cools").toString();

		} catch (Exception ex) {
			mObjectID = mJSONObject.get("photo_id").toString();
			photoId = Integer.parseInt(mJSONObject.get("photo_id").toString());
			photoUrl = mJSONObject.get("photo_url").toString();
			strQrcode = mJSONObject.get("qr_details").toString();
			strShootDate = mJSONObject.get("upload_date").toString();
			strSubject = mJSONObject.get("photo_description").toString();
			strCoolNum = mJSONObject.get("user_cool").toString();
		}

		try {
			strCity = mJSONObject.get("upload_city").toString();
		} catch (Exception ex) {
			strCity = "";
		}

		JSONParser parser = new JSONParser();

		JSONArray arySee = new JSONArray();

		int seeCount = 0;

		if (mJSONObject.get("photo_see") != null)
			try {
				arySee = (JSONArray) parser.parse(mJSONObject.get("photo_see")
						.toString());
				seeCount = arySee.size();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// ----------------------------------

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setText(strSubject);
		mTitleTextView.setTypeface(tf);

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		mMapProgressBar = (ProgressBar) findViewById(R.id.map_progressbar);
		mPhotoProgressBar = (ProgressBar) findViewById(R.id.flow_photo_progressbar);
		mPhoto = (ImageView) findViewById(R.id.photo_image);

		mOkCountTextView = (TextView) findViewById(R.id.ok_account_textview);
		mSeeCountTextView = (TextView) findViewById(R.id.see_account_textview);
		mPinCountTextView = (TextView) findViewById(R.id.pin_account_textview);

		mSeeCountTextView.setText(String.valueOf(seeCount));
		mOkCountTextView.setText(strCoolNum);

		photoUrl = photoUrl.replace("https:", "http:");
		mPhoto.setTag(photoUrl);
		new com.kooco.tool.LoadImage(this, mPhoto, mPhotoProgressBar, photoUrl)
				.execute();

		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		mGoogleMap = supportMapFragment.getMap();

		if (!strCity.equals("")) {
			new GeocoderTask(this, mGoogleMap, mMapProgressBar)
					.execute(strCity);
		} else
			mMapProgressBar.setVisibility(View.GONE);

		mReportBtn = (Button) findViewById(R.id.report_btn);
		mReportBtn.setOnClickListener(new ReportBtnListener());

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


	private class ReportBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			ReportView.mObjectType = "album_photo";
			ReportView.mObjectID = String.valueOf(mObjectID);

			Intent i = new Intent(FlowMapView.this, ReportView.class);
			startActivity(i);
		}
	}

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}

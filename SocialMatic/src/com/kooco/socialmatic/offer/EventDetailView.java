package com.kooco.socialmatic.offer;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Intent;
import android.graphics.Paint;
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
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class EventDetailView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	public static int mEventId = 0;

	private Button mBackBtn;

	private Button mNotAttendingBtn;
	private Button mMaybeAttendingBtn;
	private Button mAttendingBtn;

	private TextView mTitleTextView;
	private TextView mEventTitleLab;
	private TextView mEventTimeLab;
	private TextView mEventLocationLab;
	private TextView mEventCategoryLab;
	private TextView mEventContentLab;
	private TextView mEventAttendingCountLab;

	private ImageView mPhotoImage;
	private ProgressBar mPhotoProBar;

	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	public Button mReportBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.event_detail_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mEventAttendingCountLab = (TextView) findViewById(R.id.attend_num_lab);
		mEventAttendingCountLab.setTypeface(tf);
		mEventAttendingCountLab.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mEventAttendingCountLab.getPaint().setAntiAlias(true);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		mPhotoImage = (ImageView) findViewById(R.id.photo_image);
		mPhotoProBar = (ProgressBar) findViewById(R.id.photo_progressbar);
		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setTypeface(tf);
		mEventTitleLab = (TextView) findViewById(R.id.event_title_lab);
		mEventTitleLab.setTypeface(tf);
		mEventTimeLab = (TextView) findViewById(R.id.event_time_lab);
		mEventTimeLab.setTypeface(tf);

		mEventLocationLab = (TextView) findViewById(R.id.event_location_lab);
		mEventLocationLab.setTypeface(tf);

		mEventLocationLab.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mEventLocationLab.getPaint().setAntiAlias(true);

		mEventCategoryLab = (TextView) findViewById(R.id.event_category_lab);
		mEventCategoryLab.setTypeface(tf);
		mEventContentLab = (TextView) findViewById(R.id.event_content_lab);
		mEventContentLab.setTypeface(tf);

		mAttendingBtn = (Button) findViewById(R.id.attending_btn);
		mAttendingBtn.setOnClickListener(new AttendingBtnListener());

		mMaybeAttendingBtn = (Button) findViewById(R.id.maybe_attending_btn);
		mMaybeAttendingBtn.setOnClickListener(new AttendingBtnListener());

		mNotAttendingBtn = (Button) findViewById(R.id.not_attending_btn);
		mNotAttendingBtn.setOnClickListener(new AttendingBtnListener());

		mReportBtn = (Button) findViewById(R.id.report_btn);
		mReportBtn.setOnClickListener(new ReportBtnListener());

		getViewData();
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


	private void getViewData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_event_info_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&eventID=" + mEventId;

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_event_info");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private void createEventrInfoView(JSONObject resultObj) {

		JSONObject ownerObj = null;

		JSONParser parser = new JSONParser();

		try {
			ownerObj = (JSONObject) parser.parse(resultObj.get("owner")
					.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String title = resultObj.get("title").toString();
		String description = resultObj.get("description").toString();
		String startDate = resultObj.get("starttime").toString();
		String endDate = resultObj.get("endtime").toString();
		String photoUrl = resultObj.get("photo_url").toString();
		String location = resultObj.get("location").toString();
		String category = resultObj.get("category").toString();

		String strParticipantData = resultObj.get("participant").toString();

		JSONArray aryParticipantData = new JSONArray();

		String selfUserID = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		try {
			aryParticipantData = (JSONArray) parser.parse(strParticipantData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String attendType = "-1";
		int attendCount = 0;

		for (Object obj : aryParticipantData) {

			JSONObject jsonObj = (JSONObject) obj;

			JSONObject userObj = new JSONObject();
			String participantUserID = "";

			try {
				userObj = (JSONObject) parser.parse(jsonObj.get("user")
						.toString());
				participantUserID = userObj.get("user_id").toString();

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (selfUserID.equals(participantUserID)) {
				attendType = jsonObj.get("rsvp").toString();
			}

			String attendResponse = jsonObj.get("rsvp").toString();

			if (attendResponse.equals("2"))
				attendCount++;
		}

		mEventAttendingCountLab.setText(attendCount + " attend");
		mEventAttendingCountLab
				.setOnClickListener(new AcceptsBtnClickListener());

		Drawable orangeRes = getResources().getDrawable(
				R.drawable.button_orange_selector);
		Drawable greenRes = getResources().getDrawable(
				R.drawable.button_green_1_selector);

		switch (Integer.parseInt(attendType)) {
		case 0: { // no

			mNotAttendingBtn.setBackground(orangeRes);
			mNotAttendingBtn.setEnabled(false);
			mMaybeAttendingBtn.setBackground(greenRes);
			mMaybeAttendingBtn.setEnabled(true);
			mAttendingBtn.setBackground(greenRes);
			mAttendingBtn.setEnabled(true);
		}
			break;
		case 1: { // maybe
			mNotAttendingBtn.setBackground(greenRes);
			mNotAttendingBtn.setEnabled(true);
			mMaybeAttendingBtn.setBackground(orangeRes);
			mMaybeAttendingBtn.setEnabled(false);
			mAttendingBtn.setBackground(greenRes);
			mAttendingBtn.setEnabled(true);
		}
			break;
		case 2: { // yes
			mNotAttendingBtn.setBackground(greenRes);
			mNotAttendingBtn.setEnabled(true);
			mMaybeAttendingBtn.setBackground(greenRes);
			mMaybeAttendingBtn.setEnabled(true);
			mAttendingBtn.setBackground(orangeRes);
			mAttendingBtn.setEnabled(false);
		}
			break;
		default: // not answer
		{
			mNotAttendingBtn.setBackground(greenRes);
			mNotAttendingBtn.setEnabled(true);
			mMaybeAttendingBtn.setBackground(greenRes);
			mMaybeAttendingBtn.setEnabled(true);
			mAttendingBtn.setBackground(greenRes);
			mAttendingBtn.setEnabled(true);
		}
			break;
		}

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");

		// start Date
		// ----------------------------------------
		final Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(Long.valueOf(startDate + "000"));
		String strStartDate = formatter.format(startCal.getTime());
		// ----------------------------------------

		// end Date
		// ----------------------------------------
		final Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(Long.valueOf(endDate + "000"));
		String strEndDate = formatter.format(endCal.getTime());
		// ----------------------------------------

		mPhotoImage.setTag(photoUrl);
		new com.kooco.tool.LoadImage(this, mPhotoImage, mPhotoProBar, photoUrl)
				.execute();

		mEventTitleLab.setText(title);
		mEventContentLab.setText(description);
		mEventTimeLab.setText(strStartDate + " - " + strEndDate);
		mEventLocationLab.setText(location);
		mEventCategoryLab.setText(category);

		mEventLocationLab.setText(location);

		mEventLocationLab.setTag(location);
		mEventLocationLab.setOnClickListener(new EventLocationClickListener());
		/*
		 * String title = resultObj.get("title").toString(); String strDate =
		 * resultObj.get("creation_date").toString(); String userName =
		 * resultObj.get("user_display_name").toString(); String price =
		 * resultObj.get("price").toString(); String currency =
		 * resultObj.get("currency").toString(); String location =
		 * resultObj.get("location").toString(); String limited =
		 * resultObj.get("limited").toString(); String body =
		 * resultObj.get("body").toString(); String photoUrl =
		 * resultObj.get("photo_url").toString();
		 */
		/*
		 * try { String[] splitDate = strDate.split(","); String day =
		 * splitDate[0].replace(" ", "/"); String[] splitDate1 =
		 * splitDate[1].split(" "); String showDate = day + "/" +
		 * splitDate1[1].subSequence(2, 4); mOfferTimeLab.setText(showDate); }
		 * catch (Exception ex) {
		 * 
		 * }
		 * 
		 * mTitleTextView.setText("Offer Details");
		 * mOfferTitleLab.setText(title); mOfferUserLab.setText(userName);
		 * mOfferPriceLab.setText(price); mOfferCurrencyLab.setText(currency);
		 * mOfferLocationLab.setText(location);
		 * mOfferLimitedLab.setText(limited); mOfferContentLab.setText(body);
		 * 
		 * mOfferLocationLab.setTag(location);
		 * 
		 * mOfferLocationLab.setOnClickListener(new
		 * OfferLocationClickListener());
		 * 
		 * mPhotoImage.setTag(photoUrl); new com.kooco.tool.LoadImage(this,
		 * mPhotoImage, mPhotoProBar, photoUrl).execute();
		 */
	}

	private void setEventResponse(String option) {
		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.event_response_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("eventID", String
				.valueOf(mEventId)));
		nameValuePairs.add(new BasicNameValuePair("option", option));

		WebManager webManager = new WebManager(EventDetailView.this);

		webManager.setDoType("event_response_url");

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
	}

	private class AcceptsBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			OfferAcceptsView.mObjectType = "event";
			OfferAcceptsView.mObjectID = String.valueOf(mEventId);
			
			Intent i = new Intent(EventDetailView.this, OfferAcceptsView.class);
			startActivity(i);
		}
	}

	private class ReportBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			ReportView.mObjectType = "event";
			ReportView.mObjectID = String.valueOf(mEventId);

			Intent i = new Intent(EventDetailView.this, ReportView.class);
			startActivity(i);
		}
	}

	private class EventLocationClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			OfferMapView.mSubject = v.getTag().toString();

			Intent i = new Intent(EventDetailView.this, OfferMapView.class);
			startActivity(i);
		}
	}

	private class AttendingBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String option = "";

			if (v == mNotAttendingBtn)
				option = "0";
			else if (v == mMaybeAttendingBtn)
				option = "1";
			else if (v == mAttendingBtn)
				option = "2";

			setEventResponse(option);
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
			if (objects[1].toString().equals("get_event_info")) {

				JSONParser parser = new JSONParser();

				JSONObject result;
				try {
					result = (JSONObject) parser.parse(objects[0].toString());
					createEventrInfoView(result);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (objects[1].toString().equals("event_response_url"))
				getViewData();
		}
	}
}

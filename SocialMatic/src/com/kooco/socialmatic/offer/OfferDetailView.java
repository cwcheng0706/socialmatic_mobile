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
import com.kooco.socialmatic.flow.FlowAdapter;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.profile.SelectUserProfileView;
import com.kooco.socialmatic.search.SearchResultView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class OfferDetailView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private TextView mTitleTextView;
	private TextView mOfferTitleLab;
	private TextView mOfferTimeLab;
	private TextView mOfferUserLab;

	private TextView mOfferPriceLab;
	private TextView mOfferCurrencyLab;
	private TextView mOfferLocationLab;
	private TextView mOfferLimitedLab;
	private TextView mOfferContentLab;
	private TextView mOfferViewCountLab;
	private TextView mAcceptViewCountLab;
	private ImageView mPhotoImage;
	private ProgressBar mPhotoProBar;

	public static int mOfferId = 0;

	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	public Button mAcceptBtn;
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

		setContentView(R.layout.offer_detail_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mPhotoImage = (ImageView) findViewById(R.id.photo_image);
		mPhotoProBar = (ProgressBar) findViewById(R.id.photo_progressbar);

		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setTypeface(tf);

		mOfferTitleLab = (TextView) findViewById(R.id.offer_title_lab);
		mOfferTitleLab.setTypeface(tf);

		mOfferUserLab = (TextView) findViewById(R.id.offer_user_lab);
		mOfferUserLab.setTypeface(tf);

		mOfferPriceLab = (TextView) findViewById(R.id.offer_price_lab);
		mOfferPriceLab.setTypeface(tf);

		mOfferCurrencyLab = (TextView) findViewById(R.id.offer_currency_lab);
		mOfferCurrencyLab.setTypeface(tf);

		mOfferLimitedLab = (TextView) findViewById(R.id.offer_limit_time_lab);
		mOfferLimitedLab.setTypeface(tf);

		mOfferContentLab = (TextView) findViewById(R.id.offer_content_lab);
		mOfferContentLab.setTypeface(tf);

		mOfferViewCountLab = (TextView) findViewById(R.id.view_num_lab);
		mOfferViewCountLab.setTypeface(tf);
		
		mAcceptViewCountLab = (TextView) findViewById(R.id.accept_num_lab);
		mAcceptViewCountLab.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mAcceptViewCountLab.getPaint().setAntiAlias(true);
		mAcceptViewCountLab.setTypeface(tf);

		mOfferLocationLab = (TextView) findViewById(R.id.offer_location_lab);
		mOfferLocationLab.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mOfferLocationLab.getPaint().setAntiAlias(true);
		mOfferLocationLab.setTypeface(tf);

		mOfferTimeLab = (TextView) findViewById(R.id.offer_time_lab);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		mAcceptBtn = (Button) findViewById(R.id.accept_btn);
		mAcceptBtn.setOnClickListener(new AcceptBtnListener());
		
		mReportBtn= (Button) findViewById(R.id.report_btn);
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

	private void createOfferInfoView(JSONObject resultObj) {

		JSONObject ownerObj = null;
		JSONObject offerObj = null;

		JSONParser parser = new JSONParser();

		try {
			ownerObj = (JSONObject) parser.parse(resultObj.get("owner")
					.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			offerObj = (JSONObject) parser.parse(resultObj.get("offer")
					.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String title = offerObj.get("title").toString();
		String strDate = offerObj.get("creation_date").toString();
		String userName = ownerObj.get("display_name").toString();
		String ownerUserID = ownerObj.get("user_id").toString();
		String price = offerObj.get("price").toString();
		String currency = offerObj.get("currency").toString();
		String location = offerObj.get("location").toString();
		String limited = offerObj.get("limited").toString();
		String body = offerObj.get("body").toString();
		String photoUrl = offerObj.get("photo_url").toString();
		String viewCount = offerObj.get("view_count").toString();

		String strParticipantData = offerObj.get("participant").toString();

		JSONArray aryParticipantData = new JSONArray();

		String selfUserID = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		try {
			aryParticipantData = (JSONArray) parser.parse(strParticipantData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Object obj : aryParticipantData) {
			JSONObject jsonObj = (JSONObject) obj;

			String participantUserID = jsonObj.get("user_id").toString();

			if (selfUserID.equals(participantUserID)) {
				mAcceptBtn.setVisibility(View.GONE);
				break;
			}
		}
		
		mAcceptViewCountLab.setText(aryParticipantData.size() + " Accepts");
		mAcceptViewCountLab.setOnClickListener(new AcceptsBtnClickListener());

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(strDate + "000"));

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		String strCreateDate = formatter.format(cal.getTime());

		mOfferTimeLab.setText(strCreateDate);

		/*
		 * try { String[] splitDate = strDate.split(","); String day =
		 * splitDate[0].replace(" ", "/"); String[] splitDate1 =
		 * splitDate[1].split(" "); String showDate = day + "/" +
		 * splitDate1[1].subSequence(2, 4); mOfferTimeLab.setText(showDate); }
		 * catch (Exception ex) {
		 * 
		 * }
		 */

		if (Integer.parseInt(viewCount) == 1)
			mOfferViewCountLab.setText(viewCount + " view");
		else
			mOfferViewCountLab.setText(viewCount + " views");

		mTitleTextView.setText("Offer Details");
		mOfferTitleLab.setText(title);
		mOfferUserLab.setText(userName);
		mOfferPriceLab.setText(price);
		mOfferCurrencyLab.setText(currency);
		mOfferLocationLab.setText(location);
		mOfferLimitedLab.setText(limited);
		mOfferContentLab.setText(body);

		mOfferLocationLab.setTag(location);

		//mOfferLocationLab.setOnClickListener(new OfferLocationClickListener());
		mOfferLocationLab.setOnClickListener(new searchBtnClickListener());
		
		mPhotoImage.setTag(photoUrl);
		new com.kooco.tool.LoadImage(this, mPhotoImage, mPhotoProBar, photoUrl)
				.execute();
	}

	private void getViewData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_offer_info_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&offerID=" + mOfferId;

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_offer_info");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private void acceptOffer() {
		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.accept_offer_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("offerID", String
				.valueOf(mOfferId)));

		WebManager webManager = new WebManager(OfferDetailView.this);

		webManager.setDoType("accept_offer_url");

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
	}
	
	private class OfferLocationClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			OfferMapView.mSubject = v.getTag().toString();

			Intent i = new Intent(OfferDetailView.this, OfferMapView.class);
			startActivity(i);
		}
	}
	
	private class searchBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			SearchResultView.mKeyWord = v.getTag().toString();
			
			Intent i = new Intent(OfferDetailView.this, SearchResultView.class);
			startActivity(i);
		}
	}
	
	private class ReportBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			ReportView.mObjectType = "classified";
			ReportView.mObjectID = String.valueOf(mOfferId);

			Intent i = new Intent(OfferDetailView.this, ReportView.class);
			startActivity(i);
		}
	}
	
	private class AcceptsBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			OfferAcceptsView.mObjectType = "classified";
			OfferAcceptsView.mObjectID = String.valueOf(mOfferId);
			
			Intent i = new Intent(OfferDetailView.this, OfferAcceptsView.class);
			startActivity(i);
		}
	}

	private class AcceptBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			acceptOffer();
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
			if (objects[1].toString().equals("get_offer_info")) {

				JSONParser parser = new JSONParser();

				JSONObject result;
				try {
					result = (JSONObject) parser.parse(objects[0].toString());
					createOfferInfoView(result);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (objects[1].toString().equals("accept_offer_url")) {
				JSONParser parser = new JSONParser();

				JSONObject result;
				try {
					result = (JSONObject) parser.parse(objects[0].toString());
					String response = result.get("Response").toString();

					if (response.equals("Request OK"))
						getViewData();

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

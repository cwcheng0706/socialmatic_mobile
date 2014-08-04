package com.kooco.socialmatic.offer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.flow.FlowAdapter;
import com.kooco.socialmatic.follow.FollowerAdapter;
import com.kooco.socialmatic.follow.FollowingAdapter;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class OfferAcceptsView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private TextView mTitleTextView;

	OfferAcceptsAdapter mOfferAcceptsAdapter;
	private ListView mPeopleListView;

	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	public static String mObjectType = "";
	public static String mObjectID = "";

	JSONArray mParticipantJSONArray = new JSONArray();
	ArrayList<JSONObject> mAryParticipantList = new ArrayList<JSONObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.offer_accepts_view);

		mPeopleListView = (ListView) findViewById(R.id.people_listview);
		
		mOfferAcceptsAdapter = new OfferAcceptsAdapter(OfferAcceptsView.this,  mAryParticipantList, mObjectType);
		mPeopleListView.setAdapter(mOfferAcceptsAdapter);
		

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mTitleTextView = (TextView) findViewById(R.id.title_lab);

		if (mObjectType.equals("event"))
			mTitleTextView.setText("Attend Event List");

		mTitleTextView.setTypeface(tf);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		if (mObjectType.equals("classified"))
			getOfferData();
		else if (mObjectType.equals("event"))
			getEventData();

		//readJSonDataFromFile();

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


	public void getOfferData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_offer_info_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&offerID=" + mObjectID;

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_offer_info");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	public void getEventData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_event_info_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&eventID=" + mObjectID;

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_event_info");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	
	private void createParticipantListView() {

		mAryParticipantList.clear();
		for (Object obj : mParticipantJSONArray)
			mAryParticipantList.add((JSONObject) obj);

		mOfferAcceptsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects.length > 1) {
			if (objects[1].toString().equals("get_offer_info") || 
					objects[1].toString().equals("get_event_info")) {
				JSONParser parser_obj = new JSONParser();
				JSONObject resultObj = new JSONObject();

				try {
					resultObj = (JSONObject) parser_obj.parse(objects[0]
							.toString());
					
					if (mObjectType.equals("event"))
					    mParticipantJSONArray = (JSONArray) parser_obj
							.parse(resultObj.get("participant").toString());
					else
					{
						JSONObject offerData = (JSONObject) resultObj.get("offer");
						mParticipantJSONArray = (JSONArray) parser_obj
								.parse(offerData.get("participant").toString());
					}
					
					createParticipantListView();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

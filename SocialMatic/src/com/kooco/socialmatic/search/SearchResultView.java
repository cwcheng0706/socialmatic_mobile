package com.kooco.socialmatic.search;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.follow.FollowerAdapter;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.offer.EventAdapter;
import com.kooco.socialmatic.offer.OfferAdapter;
import com.kooco.socialmatic.profile.ProfileAlbumAdapter;
import com.kooco.tool.ScrollGridView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class SearchResultView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private TextView mTitleTextView;
	private TextView mKeywordTextView;
	public static String mKeyWord = "";
	private RadioGroup mRadiogroup;

	private RadioButton mPeopleRadio;
	private RadioButton mPhotosRadio;
	private RadioButton mOffersRadio;
	private RadioButton mEventsRadio;

	private FollowerAdapter mSearchPeopleAdapter;
	private ProfileAlbumAdapter mPhotoAdapter;
	private OfferAdapter mOfferAdapter;
	private EventAdapter mEventAdapter;

	private ListView mSearchPeopleListView;
	private ListView mOfferListView;

	private ScrollView mPhotoScrollView;
	private ScrollGridView mPhotoScrollGridView;
	private ScrollView mEventsScrollView;
	private ScrollGridView mEventsScrollGridView;

	private ImageView mUserPhotoImageView;
	public ImageLoader imageLoader;
	private TextView mUserNameLab;

	JSONArray mPhotoJSONArray = new JSONArray();
	ArrayList<JSONObject> mAryPhotoList = new ArrayList<JSONObject>();

	JSONArray mUserJSONArray = new JSONArray();
	ArrayList<JSONObject> mAryUserList = new ArrayList<JSONObject>();

	JSONArray mOffersJSONArray = new JSONArray();
	ArrayList<JSONObject> mAryOffersList = new ArrayList<JSONObject>();

	JSONArray mEventsJSONArray = new JSONArray();
	ArrayList<JSONObject> mAryEventsList = new ArrayList<JSONObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.search_result_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mSearchPeopleListView = (ListView) findViewById(R.id.people_listview);
		mPhotoScrollView = (ScrollView) findViewById(R.id.photo_scrollview);
		mPhotoScrollGridView = (ScrollGridView) findViewById(R.id.photo_gridview);

		mEventsScrollView = (ScrollView) findViewById(R.id.event_scrollview);
		mEventsScrollGridView = (ScrollGridView) findViewById(R.id.event_gridview);

		mOfferListView = (ListView) findViewById(R.id.offer_listview);

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setTypeface(tf);

		mKeywordTextView = (TextView) findViewById(R.id.keyword_lab);
		mKeywordTextView.setText(mKeyWord);
		mTitleTextView.setTypeface(tf);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		mPeopleRadio = (RadioButton) findViewById(R.id.people_radio);
		mPhotosRadio = (RadioButton) findViewById(R.id.photos_radio);
		mOffersRadio = (RadioButton) findViewById(R.id.offers_radio);
		mEventsRadio = (RadioButton) findViewById(R.id.events_radio);

		mRadiogroup = (RadioGroup) findViewById(R.id.radiogroup);

		mPhotosRadio.setTextColor(Color.parseColor("#F2A215"));
		mPeopleRadio.setTextColor(Color.parseColor("#BFBFBF"));
		mOffersRadio.setTextColor(Color.parseColor("#BFBFBF"));
		mEventsRadio.setTextColor(Color.parseColor("#BFBFBF"));

		mPeopleRadio.setTypeface(tf);
		mPhotosRadio.setTypeface(tf);
		mOffersRadio.setTypeface(tf);
		mEventsRadio.setTypeface(tf);

		mRadiogroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 透過id來辨認不同的radiobutton
						switch (checkedId) {
						case R.id.people_radio:
							mPeopleRadio.setTextColor(Color
									.parseColor("#F2A215"));
							mPhotosRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mOffersRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mEventsRadio.setTextColor(Color
									.parseColor("#BFBFBF"));

							getUserData();

							mEventsScrollView.setVisibility(View.GONE);
							mSearchPeopleListView.setVisibility(View.VISIBLE);
							mOfferListView.setVisibility(View.GONE);
							mPhotoScrollView.setVisibility(View.GONE);
							break;
						case R.id.photos_radio:
							mPeopleRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mPhotosRadio.setTextColor(Color
									.parseColor("#F2A215"));
							mOffersRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mEventsRadio.setTextColor(Color
									.parseColor("#BFBFBF"));

							mEventsScrollView.setVisibility(View.GONE);
							mOfferListView.setVisibility(View.GONE);
							mSearchPeopleListView.setVisibility(View.GONE);
							mPhotoScrollView.setVisibility(View.VISIBLE);
							break;
						case R.id.offers_radio:
							mPeopleRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mPhotosRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mOffersRadio.setTextColor(Color
									.parseColor("#F2A215"));
							mEventsRadio.setTextColor(Color
									.parseColor("#BFBFBF"));

							getOffersData();

							mEventsScrollView.setVisibility(View.GONE);
							mOfferListView.setVisibility(View.VISIBLE);
							mSearchPeopleListView.setVisibility(View.GONE);
							mPhotoScrollView.setVisibility(View.GONE);
							break;
						case R.id.events_radio:
							mPeopleRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mPhotosRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mOffersRadio.setTextColor(Color
									.parseColor("#BFBFBF"));
							mEventsRadio.setTextColor(Color
									.parseColor("#F2A215"));

							getEventsData();

							mEventsScrollView.setVisibility(View.VISIBLE);
							mOfferListView.setVisibility(View.GONE);
							mSearchPeopleListView.setVisibility(View.GONE);
							mPhotoScrollView.setVisibility(View.GONE);
							break;
						}
					}

				});

		//createUserData();
		getPhotoData();

		mPhotoAdapter = new ProfileAlbumAdapter(this, mAryPhotoList);
		mPhotoAdapter.setShowType("searchResult");

		mPhotoScrollGridView.setAdapter(mPhotoAdapter);
		mPhotoScrollGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});

		mSearchPeopleAdapter = new FollowerAdapter(this, mAryUserList, null);
		mSearchPeopleAdapter.setShowType("searchResult");
		mSearchPeopleListView.setAdapter(mSearchPeopleAdapter);
		mSearchPeopleListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});

		mOfferAdapter = new OfferAdapter(this, mAryOffersList);
		mOfferAdapter.setShowType("searchResult");
		mOfferListView.setAdapter(mOfferAdapter);
		mOfferListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});

		mEventAdapter = new EventAdapter(this, mAryEventsList);
		mEventAdapter.setShowType("searchResult");
		mEventsScrollGridView.setAdapter(mEventAdapter);
		mEventsScrollGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});

		mOfferListView.setVisibility(View.GONE);
		mSearchPeopleListView.setVisibility(View.GONE);
		mPhotoScrollView.setVisibility(View.GONE);
		mEventsScrollView.setVisibility(View.GONE);
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


	private void getPhotoData() {

		String deviceId = UserDataAccess
				.getSystemUseDeviceId(SearchResultView.this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_search);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&lastReceivedID="
				+ "&numOfResult=9999" + "&termToSearch=" + Uri.encode(mKeyWord)
				+ "&typeOfSearchedTerm=album_photo";

		WebManager webManager = new WebManager(SearchResultView.this);

		webManager.setDoType("get_search_photo");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	public void getUserData() {

		String deviceId = UserDataAccess
				.getSystemUseDeviceId(SearchResultView.this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_search);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&lastReceivedID="
				+ "&numOfResult=9999" + "&termToSearch=" + Uri.encode(mKeyWord)
				+ "&typeOfSearchedTerm=user";

		WebManager webManager = new WebManager(SearchResultView.this);

		webManager.setDoType("get_search_user");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	private void getOffersData() {

		String deviceId = UserDataAccess
				.getSystemUseDeviceId(SearchResultView.this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_search);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&lastReceivedID="
				+ "&numOfResult=9999" + "&termToSearch=" + Uri.encode(mKeyWord)
				+ "&typeOfSearchedTerm=classified";

		WebManager webManager = new WebManager(SearchResultView.this);

		webManager.setDoType("get_search_offers");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	private void getEventsData() {

		String deviceId = UserDataAccess
				.getSystemUseDeviceId(SearchResultView.this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(
				SearchResultView.this, Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_search);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&lastReceivedID="
				+ "&numOfResult=9999" + "&termToSearch=" + Uri.encode(mKeyWord)
				+ "&typeOfSearchedTerm=event";

		WebManager webManager = new WebManager(SearchResultView.this);

		webManager.setDoType("get_search_events");

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

	private void createPhotoView() {
		mAryPhotoList.clear();
		for (Object obj : mPhotoJSONArray)
			mAryPhotoList.add((JSONObject) obj);

		mPhotoAdapter.notifyDataSetChanged();

		mEventsScrollView.setVisibility(View.GONE);
		mOfferListView.setVisibility(View.GONE);
		mSearchPeopleListView.setVisibility(View.GONE);
		mPhotoScrollView.setVisibility(View.VISIBLE);

	}

	private void createUserView() {
		mAryUserList.clear();
		for (Object obj : mUserJSONArray)
			mAryUserList.add((JSONObject) obj);

		mSearchPeopleAdapter.notifyDataSetChanged();

		mEventsScrollView.setVisibility(View.GONE);
		mOfferListView.setVisibility(View.GONE);
		mSearchPeopleListView.setVisibility(View.VISIBLE);
		mPhotoScrollView.setVisibility(View.GONE);
	}

	private void createOffersListView() {

		mAryOffersList.clear();
		for (Object obj : mOffersJSONArray)
			mAryOffersList.add((JSONObject) obj);

		mOfferAdapter.notifyDataSetChanged();

		mEventsScrollView.setVisibility(View.GONE);
		mOfferListView.setVisibility(View.VISIBLE);
		mSearchPeopleListView.setVisibility(View.GONE);
		mPhotoScrollView.setVisibility(View.GONE);
	}

	private void createEventsListView() {

		mAryEventsList.clear();
		for (Object obj : mEventsJSONArray) {
			JSONObject jsonObj = (JSONObject) obj;

			try {
				String eventID = jsonObj.get("event_id").toString();
				mAryEventsList.add((JSONObject) obj);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		mEventAdapter.notifyDataSetChanged();

		mEventsScrollView.setVisibility(View.VISIBLE);
		mOfferListView.setVisibility(View.GONE);
		mSearchPeopleListView.setVisibility(View.GONE);
		mPhotoScrollView.setVisibility(View.GONE);
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects.length == 2) {
			if (objects[1].toString().equals("get_search_photo")) {

				JSONParser parser = new JSONParser();
				try {
					mPhotoJSONArray = (JSONArray) parser.parse(objects[0]
							.toString());
					createPhotoView();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (objects[1].toString().equals("get_search_user")) {
				JSONParser parser = new JSONParser();

				try {
					mUserJSONArray = (JSONArray) parser.parse(objects[0]
							.toString());
					createUserView();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (objects[1].toString().equals("get_search_offers")) {
				JSONParser parser = new JSONParser();

				try {
					mOffersJSONArray = (JSONArray) parser.parse(objects[0]
							.toString());
					createOffersListView();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (objects[1].toString().equals("get_search_events")) {
				JSONParser parser = new JSONParser();

				try {
					mEventsJSONArray = (JSONArray) parser.parse(objects[0]
							.toString());
					createEventsListView();
				} catch (org.json.simple.parser.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

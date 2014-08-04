package com.kooco.socialmatic.offer;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.flow.FlowView.MainFragment;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.ScrollGridView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class OfferView extends SherlockFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			MainFragment fragment = new MainFragment();
			fm.beginTransaction().add(android.R.id.content, fragment).commit();
		}
	}

	public static class MainFragment extends SherlockFragment implements
			WebManagerCallbackInterface {

		static Context mContext;
		public Activity mActivity;

		private View mView;
		Bundle mSavedInstanceState;
		LayoutInflater mInflater;
		ViewGroup mContainer;

		private ListView mOfferListView;
		private OfferAdapter mOfferAdapter;

		private RadioGroup mRadiogroup;

		private RadioButton mOfferRadio;
		private RadioButton mEventsRadio;

		private RadioGroup mEventsRadiogroup;

		private RadioButton mUpcomingRadio;
		private RadioButton mPastRadio;
		private RadioButton mMyRadio;
		private RadioButton mCreateRadio;
		private LinearLayout mEventLayout;
		private LinearLayout mOfferLayout;

		private ScrollGridView mUpcomingSrollGridView;
		private ScrollView mUpcomingSrollView;

		static JSONArray mOffersJSONArray = new JSONArray();
		ArrayList<JSONObject> mAryOffersList = new ArrayList<JSONObject>();

		private EventAdapter mEventAdapter;
		static JSONArray mEventsJSONArray = new JSONArray();
		ArrayList<JSONObject> mAryEventsList = new ArrayList<JSONObject>();

		private ImageView mUserPhotoImageView;
		private TextView mUserNameLab;
		public ImageLoader imageLoader;

		@Override
		public void onResume() {
			super.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mActivity = activity;

			mEventsJSONArray.clear();
			mOffersJSONArray.clear();

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(false);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);

			mInflater = inflater;
			mContainer = container;
			mSavedInstanceState = savedInstanceState;

			mView = inflater.inflate(R.layout.offer_view, container, false);
			
			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			mEventLayout = (LinearLayout) mView
					.findViewById(R.id.events_layout);
			mOfferLayout = (LinearLayout) mView
					.findViewById(R.id.offers_layout);

			imageLoader = new ImageLoader(mActivity.getApplicationContext());
			/*mUserPhotoImageView = (ImageView) mView
					.findViewById(R.id.userphoto_imageview);
			mUserNameLab = (TextView) mView
					.findViewById(R.id.username_textview);
			mUserNameLab.setTypeface(tf);
			*/

			mRadiogroup = (RadioGroup) mView.findViewById(R.id.radiogroup);
			mOfferRadio = (RadioButton) mView.findViewById(R.id.offer_radio);
			mEventsRadio = (RadioButton) mView.findViewById(R.id.events_radio);

			mEventsRadiogroup = (RadioGroup) mView
					.findViewById(R.id.event_radiogroup);
			mUpcomingRadio = (RadioButton) mView
					.findViewById(R.id.upcoming_radio);
			mPastRadio = (RadioButton) mView.findViewById(R.id.past_radio);
			mMyRadio = (RadioButton) mView.findViewById(R.id.my_radio);
			mCreateRadio = (RadioButton) mView.findViewById(R.id.create_radio);

			mUpcomingRadio.setTextColor(Color.parseColor("#F2A215"));
			mPastRadio.setTextColor(Color.parseColor("#BFBFBF"));
			mMyRadio.setTextColor(Color.parseColor("#BFBFBF"));
			mCreateRadio.setTextColor(Color.parseColor("#BFBFBF"));

			mOfferRadio.setTextColor(Color.parseColor("#F2A215"));
			mEventsRadio.setTextColor(Color.parseColor("#BFBFBF"));

			mOfferListView = (ListView) mView.findViewById(R.id.offer_listview);

			mOfferAdapter = new OfferAdapter(mActivity, mAryOffersList);
			mOfferListView.setAdapter(mOfferAdapter);

			mEventAdapter = new EventAdapter(mActivity, mAryEventsList);
			mUpcomingSrollView = (ScrollView) mView
					.findViewById(R.id.upcoming_scrollview);
			mUpcomingSrollGridView = (ScrollGridView) mView
					.findViewById(R.id.upcoming_gridview);

			mUpcomingSrollGridView.setAdapter(mEventAdapter);
			mUpcomingSrollView.setVisibility(View.VISIBLE);

			mRadiogroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							// 透過id來辨認不同的radiobutton
							switch (checkedId) {
							case R.id.offer_radio:
								mOfferRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mEventsRadio.setTextColor(Color
										.parseColor("#BFBFBF"));

								mOfferLayout.setVisibility(View.VISIBLE);
								mEventLayout.setVisibility(View.GONE);
								break;
							case R.id.events_radio:
								mOfferRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mEventsRadio.setTextColor(Color
										.parseColor("#F2A215"));

								mOfferLayout.setVisibility(View.GONE);
								mEventLayout.setVisibility(View.VISIBLE);
								break;
							}
						}

					});

			mEventsRadiogroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							// 透過id來辨認不同的radiobutton
							switch (checkedId) {
							case R.id.upcoming_radio:
								mUpcomingRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mPastRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mMyRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mCreateRadio.setTextColor(Color
										.parseColor("#BFBFBF"));

								mUpcomingSrollView.setVisibility(View.VISIBLE);
								break;
							case R.id.past_radio:
								mUpcomingRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mPastRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mMyRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mCreateRadio.setTextColor(Color
										.parseColor("#BFBFBF"));

								mUpcomingSrollView.setVisibility(View.GONE);
								break;
							case R.id.my_radio:
								mUpcomingRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mPastRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mMyRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mCreateRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mUpcomingSrollView.setVisibility(View.GONE);
								break;
							case R.id.create_radio:
								mUpcomingRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mPastRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mMyRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mCreateRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mUpcomingSrollView.setVisibility(View.GONE);
								break;

							}

						}

					});

			getViewData();

			getEventData();

			//createUserData();
			return mView;
		}

		private void createUserData() {
			String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.photoKey);
			String gender = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.genderKey);
			String userName = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
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

		private void createOffersListView() {

			mAryOffersList.clear();
			for (Object obj : mOffersJSONArray)
				mAryOffersList.add((JSONObject) obj);

			mOfferAdapter.notifyDataSetChanged();
		}

		private void createEventsListView() {

			mAryEventsList.clear();
			for (Object obj : mEventsJSONArray)
				mAryEventsList.add((JSONObject) obj);

			mEventAdapter.notifyDataSetChanged();
		}

		private void getEventData() {
			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mActivity
					.getString(R.string.socialmatic_base_url)
					+ mActivity.getString(R.string.get_events_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken;

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_events");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}
		}

		private void getViewData() {

			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mActivity
					.getString(R.string.socialmatic_base_url)
					+ mActivity.getString(R.string.get_offers_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken;

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_offers");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}

		}

		@Override
		public void onRequestComplete(Object... objects) {
			// TODO Auto-generated method stub
			if (objects.length == 2) {
				if (objects[1].toString().equals("get_offers")) {

					JSONParser parser_obj = new JSONParser();
					try {
						mOffersJSONArray = (JSONArray) parser_obj
								.parse(objects[0].toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mOffersJSONArray.size() > 0)
						createOffersListView();
				} else if (objects[1].toString().equals("get_events")) {
					
					JSONParser parser_obj = new JSONParser();
					try {
						mEventsJSONArray = (JSONArray) parser_obj
								.parse(objects[0].toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mEventsJSONArray.size() > 0)
						createEventsListView();
				}
			}
		}

	}

}

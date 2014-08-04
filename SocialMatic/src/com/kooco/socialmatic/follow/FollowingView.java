package com.kooco.socialmatic.follow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.camera.SharePhotoView;
import com.kooco.socialmatic.flow.FlowView.MainFragment;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class FollowingView extends SherlockFragmentActivity {

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

		FollowingAdapter mFollowingAdapter = null;
		FollowerAdapter mFollowerAdapter = null;

		private View mView;
		Bundle mSavedInstanceState;
		LayoutInflater mInflater;
		ViewGroup mContainer;
		TextView mTitleTextView;
		private ListView mFollowingListView;
		private ListView mFollowerListView;
		private Switch mSwitch;

		private RadioButton mFollowerRadio;
		private RadioButton mFollowingRadio;

		private RadioGroup mRadiogroup;

		private ImageView mUserPhotoImageView;
		private TextView mUserNameLab;
		public ImageLoader imageLoader;

		JSONArray mFollowerJSONArray = new JSONArray();
		JSONArray mFollowingJSONArray = new JSONArray();

		ArrayList<JSONObject> mAryFollowerList = new ArrayList<JSONObject>();
		ArrayList<JSONObject> mAryFollowingList = new ArrayList<JSONObject>();

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(false);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			mActivity = MainActivity.mActivity;

			mInflater = inflater;
			mContainer = container;
			mSavedInstanceState = savedInstanceState;

			imageLoader = new ImageLoader(mActivity.getApplicationContext());

			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			mView = inflater.inflate(R.layout.following, container, false);
			mTitleTextView = (TextView) mView.findViewById(R.id.title);
			mTitleTextView.setTypeface(tf);

			/*mUserNameLab = (TextView) mView
					.findViewById(R.id.username_textview);
			mUserNameLab.setTypeface(tf);

			mUserPhotoImageView = (ImageView) mView
					.findViewById(R.id.userphoto_imageview);
			*/

			mFollowingListView = (ListView) mView
					.findViewById(R.id.following_listview);
			mFollowerListView = (ListView) mView
					.findViewById(R.id.follower_listview);

			mFollowerRadio = (RadioButton) mView
					.findViewById(R.id.follower_radio);
			mFollowingRadio = (RadioButton) mView
					.findViewById(R.id.following_radio);

			mFollowerRadio.setTypeface(tf);
			mFollowingRadio.setTypeface(tf);

			mFollowerRadio.setTextColor(Color.parseColor("#F2A215"));

			mRadiogroup = (RadioGroup) mView.findViewById(R.id.radiogroup);

			mSwitch = (Switch) mView.findViewById(R.id.list_switch);
			mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (isChecked) {
						mTitleTextView.setText("My Follower");
						mFollowingListView.setVisibility(View.GONE);
						mFollowerListView.setVisibility(View.VISIBLE);
					} else {

						mTitleTextView.setText("My Following");
						mFollowerListView.setVisibility(View.GONE);
						mFollowingListView.setVisibility(View.VISIBLE);
					}

				}
			});

			mRadiogroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							// 透過id來辨認不同的radiobutton
							switch (checkedId) {
							case R.id.follower_radio:
								mFollowerRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mFollowingRadio.setTextColor(Color
										.parseColor("#BFBFBF"));

								mFollowingListView.setVisibility(View.GONE);
								mFollowerListView.setVisibility(View.VISIBLE);
								break;
							case R.id.following_radio:
								mFollowerRadio.setTextColor(Color
										.parseColor("#BFBFBF"));
								mFollowingRadio.setTextColor(Color
										.parseColor("#F2A215"));
								mFollowerListView.setVisibility(View.GONE);
								mFollowingListView.setVisibility(View.VISIBLE);
								break;
							}
						}

					});

			mContext = mView.getContext();

			readJSonDataFromFile();

			mFollowingListView.setVisibility(View.GONE);
			mFollowerListView.setVisibility(View.VISIBLE);

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


		private void getFollowerData() {

			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mContext.getString(R.string.socialmatic_base_url)
					+ mContext.getString(R.string.get_follower_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&lastReceivedID=&limit=999";

			WebManager webManager = new WebManager(MainFragment.this, mContext);

			webManager.setDoType("get_follower");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}

		}

		private void getFollowingData() {

			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mContext.getString(R.string.socialmatic_base_url)
					+ mContext.getString(R.string.get_following_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&lastReceivedID=&limit=999";

			WebManager webManager = new WebManager(MainFragment.this, mContext);

			webManager.setDoType("get_following");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}

		}

		public void readJSonDataFromFile() {

			getFollowerData();
			getFollowingData();


				mFollowingAdapter = new FollowingAdapter(mActivity,
						mAryFollowingList, this);
				mFollowerAdapter = new FollowerAdapter(mActivity,
						mAryFollowerList, this);

				mFollowingListView.setAdapter(mFollowingAdapter);

				mFollowingListView
						.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View v, int position, long id) {
							}
						});

				mFollowerListView.setAdapter(mFollowerAdapter);

				mFollowerListView
						.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View v, int position, long id) {

							}
						});
			
		}

		private void createFollowerListView() {

			mAryFollowerList.clear();
			for (Object obj : mFollowerJSONArray)
				mAryFollowerList.add((JSONObject) obj);

			mFollowerAdapter.notifyDataSetChanged();

			mFollowerRadio.setText(mAryFollowerList.size() <= 0 ? "0" + " Follower"
					: mAryFollowerList.size() + " Follower");
		}

		private void createFollowingListView() {

			mAryFollowingList.clear();
			for (Object obj : mFollowingJSONArray)
				mAryFollowingList.add((JSONObject) obj);

			mFollowingAdapter.notifyDataSetChanged();

			mFollowingRadio.setText(mAryFollowingList.size() <= 0 ? "0" + " Follower"
					: mAryFollowingList.size() + " Following");
		}

		@Override
		public void onRequestComplete(Object... objects) {
			// TODO Auto-generated method stub

			if (objects.length > 1) {
				if (objects[1].toString().equals("get_follower")) {
					JSONParser parser_obj = new JSONParser();
					try {
						mFollowerJSONArray = (JSONArray) parser_obj
								.parse(objects[0].toString());
					} catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				    createFollowerListView();
				} else if (objects[1].toString().equals("get_following")) {
					JSONParser parser_obj = new JSONParser();
					try {
						mFollowingJSONArray = (JSONArray) parser_obj
								.parse(objects[0].toString());
					} catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					createFollowingListView();
				}
			}

		}
	}
}

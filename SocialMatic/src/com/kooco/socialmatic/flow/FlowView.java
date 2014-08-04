package com.kooco.socialmatic.flow;

import java.util.ArrayList;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.share.ShareAuthAdapter;
import com.kooco.socialmatic.share.ShareResponseListener;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class FlowView extends SherlockFragmentActivity {
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

		private ListView mFlowListView;
		private FlowAdapter mFlowAdapter;

		private final int mRecordsNum = 20;
		private int mLastReceiveID = -1;

		static JSONArray mFlowJSONArray = new JSONArray();
		ArrayList<JSONObject> mAryFlowList = new ArrayList<JSONObject>();

		private ImageView mUserPhotoImageView;
		private TextView mUserNameLab;
		public ImageLoader imageLoader;
		
		//SocialAuthAdapter mSocialAuthAdapter;
		ShareAuthAdapter mSocialAuthAdapter;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mActivity = activity;

			mFlowJSONArray.clear();
		}

		@Override
		public void onResume() {
			super.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
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

			mActivity = MainActivity.mActivity;

			mInflater = inflater;
			mContainer = container;
			mSavedInstanceState = savedInstanceState;


			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			mView = inflater.inflate(R.layout.flow_view, container, false);

			imageLoader = new ImageLoader(mActivity.getApplicationContext());

			mFlowListView = (ListView) mView.findViewById(R.id.flow_listview);
			
			// share
			//------------------------------------------
			mSocialAuthAdapter = ShareAuthAdapter.getInstance(mActivity);
			mSocialAuthAdapter.getResponseListener().setAdapter(mSocialAuthAdapter);

			// Add providers
			
			if (mSocialAuthAdapter.providerCount() <= 0)
			{
			    mSocialAuthAdapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
			    mSocialAuthAdapter.addProvider(Provider.TWITTER, R.drawable.twitter);
			}
			//------------------------------------------
						
			mFlowAdapter = new FlowAdapter(mActivity, mAryFlowList, mSocialAuthAdapter, mSocialAuthAdapter.getResponseListener());
			mFlowListView.setAdapter(mFlowAdapter);

		    getViewData();

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

		private void createFlowListView() {

			mAryFlowList.clear();
			for (Object obj : mFlowJSONArray)
				mAryFlowList.add((JSONObject) obj);

			mFlowAdapter.notifyDataSetChanged();
		}

		private void getViewData() {

			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mActivity
					.getString(R.string.socialmatic_base_url)
					+ mActivity.getString(R.string.get_flow_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&numOfActions="
					+ mRecordsNum;

			if (mLastReceiveID > 0)
				queryUrl += "&lastReceivedID=" + mLastReceiveID;

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_flow");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}

		}

		@Override
		public void onRequestComplete(Object... objects) {
			// TODO Auto-generated method stub

			if (objects.length == 2) {
				if (objects[1].toString().equals("get_flow")) {

					JSONParser parser_obj = new JSONParser();
					try {
						mFlowJSONArray = (JSONArray) parser_obj
								.parse(objects[0].toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mFlowJSONArray.size() > 0)
						createFlowListView();
				}
			}
		}

	}
}

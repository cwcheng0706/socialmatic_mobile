package com.kooco.socialmatic.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.follow.FollowingView.MainFragment;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.offer.OfferMapView;
import com.kooco.tool.Contents;
import com.kooco.tool.QRCodeEncoder;
import com.kooco.tool.ScrollGridView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ProfileView extends SherlockFragmentActivity {

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

		public Activity mActivity;

		private static View mView;
		Bundle mSavedInstanceState;
		LayoutInflater mInflater;
		ViewGroup mContainer;
		ArrayList<JSONObject> mAryPhotoList = new ArrayList<JSONObject>();

		static JSONObject mProfileJSONObject = new JSONObject();
		static JSONArray mAlbumJSONArray = new JSONArray();

		TextView mUserNameTextView;
		TextView mStateText;

		ImageView mBCImageview;
		ImageView mUserPhotoImageview;

		ProgressBar mUserPhotoProBar;

		Button mEditBtn;
		Button mLocationBtn;
		Button mAlbumBtn;

		TextView mFollowerLab;
		TextView mFollowingLab;
		TextView mMilvesTextview;
		TextView mLocationTextView;

		FlowPhotoAdapter mPhoto_adapter;

		ImageView mFlowImageview;
		ProgressBar mFlowPhotoProBar;

		public ImageLoader imageLoader;

		// JSONArray mFollowerJSONArray = new JSONArray();
		// JSONArray mFollowingJSONArray = new JSONArray();

		// public static String mQueryUserID = "";

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mActivity = activity;

			mProfileJSONObject.clear();
			mAlbumJSONArray.clear();

		}

		@Override
		public void onResume() {
			super.onResume();
			/*
			 * if (mPhoto_adapter != null) { if
			 * (mPhoto_adapter.mMapImageLoader.size() <= 0) {
			 * mPhoto_adapter.setListData(mAryPhotoList);
			 * mPhoto_adapter.notifyDataSetChanged(); } }
			 */
		}

		@Override
		public void onPause() {
			/*
			 * if (mPhoto_adapter != null) mPhoto_adapter.clearImageArray();
			 */
			AnimateFirstDisplayListener.displayedImages.clear();
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

			mInflater = inflater;
			mContainer = container;
			mSavedInstanceState = savedInstanceState;

			imageLoader = new ImageLoader(mActivity.getApplicationContext());

			mView = mInflater.inflate(R.layout.profile_view, container, false);

			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			mFlowImageview = (ImageView) mView
					.findViewById(R.id.flow_photo_imageview);
			mFlowPhotoProBar = (ProgressBar) mView
					.findViewById(R.id.flow_photo_progressbar);

			mUserNameTextView = (TextView) mView
					.findViewById(R.id.user_name_textview);
			mUserNameTextView.setTypeface(tf);

			mStateText = (TextView) mView.findViewById(R.id.state_textview);
			mStateText.setTypeface(tf);

			mFollowerLab = (TextView) mView.findViewById(R.id.follower_lab);
			mFollowerLab.setTypeface(tf);

			mFollowingLab = (TextView) mView.findViewById(R.id.following_lab);
			mFollowingLab.setTypeface(tf);

			mLocationTextView = (TextView) mView
					.findViewById(R.id.location_textview);
			mLocationTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			mLocationTextView.getPaint().setAntiAlias(true);
			mLocationTextView.setTypeface(tf);

			mMilvesTextview = (TextView) mView
					.findViewById(R.id.milves_textview);
			mMilvesTextview.setTypeface(tf);

			mBCImageview = (ImageView) mView.findViewById(R.id.bc_imageview);
			mUserPhotoImageview = (ImageView) mView
					.findViewById(R.id.user_photo_imageview);
			mUserPhotoProBar = (ProgressBar) mView
					.findViewById(R.id.user_photo_progressbar);

			mEditBtn = (Button) mView.findViewById(R.id.edit_btn);
			mEditBtn.setOnClickListener(new EditProfileBtnListener());

			mLocationBtn = (Button) mView.findViewById(R.id.location_btn);
			// mLocationBtn.setOnClickListener(new LocationBtnListener());

			mAlbumBtn = (Button) mView.findViewById(R.id.album_btn);
			mAlbumBtn.setOnClickListener(new AlbumBtnListener());

			ScrollGridView scrollGridView = (ScrollGridView) mView
					.findViewById(R.id.gridview);

			mPhoto_adapter = new FlowPhotoAdapter(mActivity, mAryPhotoList);

			scrollGridView.setAdapter(mPhoto_adapter);

			System.gc();

			// getFollowerData();
			// getFollowingData();

			// if (mProfileJSONObject.isEmpty())
			getViewData();
			/*
			 * else try { createProfileView(); } catch (JSONException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); } catch
			 * (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */

			// if (mAlbumJSONArray.isEmpty())
			getAlbum();
			/*
			 * else try { createAlbumListView(); } catch (JSONException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); } catch
			 * (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			return mView;
		}

		private static class AnimateFirstDisplayListener extends
				SimpleImageLoadingListener {

			static final List displayedImages = Collections
					.synchronizedList(new LinkedList());

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
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
			} catch (Exception e) {
				gender = "B";
			}

			// ------------------------------------------------
			UserDataAccess.setDataWithKeyAndEncrypt(mActivity, Config.nameKey,
					nickname);
			UserDataAccess.setDataWithKeyAndEncrypt(mActivity,
					Config.genderKey, gender);
			UserDataAccess.setDataWithKeyAndEncrypt(mActivity, Config.photoKey,
					userPhotoUrl);
			// ------------------------------------------------

			if (!flowPhotoUrl.equals("")) {
				mFlowPhotoProBar.setVisibility(View.GONE);
				mFlowImageview.setTag(flowPhotoUrl);
				imageLoader.DisplayImage(flowPhotoUrl, mFlowImageview, 100);
			} else
				mFlowPhotoProBar.setVisibility(View.GONE);

			mLocationTextView.setText(strLocation);
			mLocationTextView.setTag(strLocation);

			mLocationTextView
					.setOnClickListener(new OfferLocationClickListener());

			mLocationBtn.setTag(strLocation);
			mLocationBtn.setOnClickListener(new OfferLocationClickListener());

			mUserNameTextView.setText(nickname);
			mStateText.setText(statusMsg);

			String followerCount = mProfileJSONObject.get("follower_number")
					.toString();
			String followingCount = mProfileJSONObject.get("following_number")
					.toString();

			mFollowerLab.setText(followerCount + " Follower");
			mFollowingLab.setText(followingCount + " Following");

			try {
				QRCodeEncoder qrCodeEncoder = null;

				int dimension = 140;
				float density = mActivity.getResources().getDisplayMetrics().density;
				
				qrCodeEncoder = new QRCodeEncoder(qrContent, null,
						Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
						(int)(dimension * (density/1.5f)));

				Bitmap qrBitmap = qrCodeEncoder.encodeAsBitmap();

				mBCImageview.setImageBitmap(qrBitmap);

			} catch (WriterException e) {
				Log.e("ProfileView", "Could not encode barcode", e);
			} catch (IllegalArgumentException e) {
				Log.e("ProfileView", "Could not encode barcode", e);
			}

			mUserPhotoProBar.setVisibility(View.GONE);

			if (userPhotoUrl.equals("")) {

				Drawable res;

				if (gender.equals("M"))
					res = getResources()
							.getDrawable(R.drawable.account_photo_3);
				else if (gender.equals("W"))
					res = getResources()
							.getDrawable(R.drawable.account_photo_1);
				else
					res = getResources().getDrawable(R.drawable.account_photo_2);

				mUserPhotoImageview.setImageDrawable(res);
			} else {
				mUserPhotoImageview.setTag(userPhotoUrl);

				imageLoader.DisplayImage(userPhotoUrl, mUserPhotoImageview, 80);
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
					+ mActivity.getString(R.string.get_user_info_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&subjectKey=" + userId;

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_user_info");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}
		}

		private void getAlbum() {
			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = mActivity
					.getString(R.string.socialmatic_base_url)
					+ mActivity.getString(R.string.get_album_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&albumOwnerID=" + userId
					+ "&numOfPhoto=100";

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_album");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}
		}

		/*
		 * private void getFollowerData() {
		 * 
		 * String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
		 * String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
		 * mActivity, Config.authTokenKey); String userId =
		 * UserDataAccess.getDataWithKeyAndEncrypt(mActivity, Config.userIdKey);
		 * 
		 * String queryUrl = getString(R.string.socialmatic_base_url) +
		 * getString(R.string.get_follower_url);
		 * 
		 * queryUrl += "?deviceID=" + deviceId + "&userID=" + userId +
		 * "&authtoken=" + authToken + "&lastReceivedID=&limit=999";
		 * 
		 * WebManager webManager = new WebManager(MainFragment.this, mActivity);
		 * 
		 * webManager.setDoType("get_follower");
		 * 
		 * if (webManager.isNetworkAvailable()) { // do web connect...
		 * webManager.execute(queryUrl, "GET"); }
		 * 
		 * }
		 * 
		 * private void getFollowingData() {
		 * 
		 * String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
		 * String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
		 * mActivity, Config.authTokenKey); String userId =
		 * UserDataAccess.getDataWithKeyAndEncrypt(mActivity, Config.userIdKey);
		 * 
		 * String queryUrl = mActivity .getString(R.string.socialmatic_base_url)
		 * + mActivity.getString(R.string.get_following_url);
		 * 
		 * queryUrl += "?deviceID=" + deviceId + "&userID=" + userId +
		 * "&authtoken=" + authToken + "&lastReceivedID=&limit=999";
		 * 
		 * WebManager webManager = new WebManager(MainFragment.this, mActivity);
		 * 
		 * webManager.setDoType("get_following");
		 * 
		 * if (webManager.isNetworkAvailable()) { // do web connect...
		 * webManager.execute(queryUrl, "GET"); }
		 * 
		 * }
		 */
		private class EditProfileBtnListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mActivity, EditProfileView.class);
				startActivity(i);
			}
		}

		private class AlbumBtnListener implements OnClickListener {
			@Override
			public void onClick(View v) {

				ProfileAlmunView.mAryPhotoList = mAryPhotoList;
				ProfileAlmunView.mOwner = "My";
				Intent i = new Intent(mActivity, ProfileAlmunView.class);
				startActivity(i);
			}
		}

		private class OfferLocationClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {

				OfferMapView.mSubject = v.getTag().toString();

				Intent i = new Intent(mActivity, OfferMapView.class);
				startActivity(i);
			}
		}

		/*
		 * private void createFollowerListView() {
		 * mFollowerLab.setText(mFollowerJSONArray.size() + " Follower"); }
		 * 
		 * private void createFollowingListView() {
		 * mFollowingLab.setText(mFollowingJSONArray.size() + " Following"); }
		 */
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
					JSONObject resultObj = (JSONObject) parser_obj
							.parse(objects[0].toString());
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
			/*
			 * else if (objects[1].toString().equals("get_follower")) {
			 * JSONParser parser_obj = new JSONParser(); try {
			 * mFollowerJSONArray = (JSONArray) parser_obj
			 * .parse(objects[0].toString()); } catch
			 * (org.json.simple.parser.ParseException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * 
			 * if (mFollowerJSONArray.size() > 0) createFollowerListView(); }
			 * else if (objects[1].toString().equals("get_following")) {
			 * JSONParser parser_obj = new JSONParser(); try {
			 * mFollowingJSONArray = (JSONArray) parser_obj
			 * .parse(objects[0].toString()); } catch
			 * (org.json.simple.parser.ParseException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * 
			 * if (mFollowingJSONArray.size() > 0) createFollowingListView(); }
			 */
		}

	}
}

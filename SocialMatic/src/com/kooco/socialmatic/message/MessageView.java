package com.kooco.socialmatic.message;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class MessageView extends SherlockFragmentActivity {

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

		TextView mTitleTextView;
		ListView mMessageListView;

		private ImageView mUserPhotoImageView;
		private TextView mUserNameLab;
		public ImageLoader imageLoader;

		Button mNewMessageBtn;

		MessageAdapter mMessageAdapter;

		JSONArray mMsgJSONArray = new JSONArray();
		ArrayList<JSONObject> mAryMsgList = new ArrayList<JSONObject>();

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

			mView = inflater.inflate(R.layout.message_view, container, false);

			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			/*mUserPhotoImageView = (ImageView) mView
					.findViewById(R.id.userphoto_imageview);
			mUserNameLab = (TextView) mView
					.findViewById(R.id.username_textview);
			mUserNameLab.setTypeface(tf);
			*/

			mTitleTextView = (TextView) mView.findViewById(R.id.title_lab);
			mTitleTextView.setTypeface(tf);

			mMessageListView = (ListView) mView
					.findViewById(R.id.message_listview);

			mNewMessageBtn = (Button) mView.findViewById(R.id.new_message_btn);

			mNewMessageBtn.setOnClickListener(new MessageBtnListener());

			mMessageAdapter = new MessageAdapter(mActivity, mAryMsgList);
			mMessageListView.setAdapter(mMessageAdapter);

			mMessageListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String conversationID = ((JSONObject) parent
							.getItemAtPosition(position))
							.get("conversation_id").toString();

					SendMessageToUser.mConversationID = conversationID;

					Intent i = new Intent(mActivity, SendMessageToUser.class);
					startActivity(i);
					// TODO Auto-generated method stub
				}

			});

			getViewData();
			getFriendsData();

			//createUserData();

			mContext = mView.getContext();
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


		private void createMsgListView() {

			mAryMsgList.clear();
			for (Object obj : mMsgJSONArray)
				mAryMsgList.add((JSONObject) obj);

			mMessageAdapter.notifyDataSetChanged();
		}

		private void getFriendsData() {

			String deviceId = UserDataAccess.getSystemUseDeviceId(mActivity);
			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					mActivity, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(mActivity,
					Config.userIdKey);

			String queryUrl = getString(R.string.socialmatic_base_url)
					+ getString(R.string.get_following_list_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken + "&limit=999";

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("getallfriends_url");

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
					+ mActivity.getString(R.string.get_all_conversations_url);

			queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
					+ "&authtoken=" + authToken;

			WebManager webManager = new WebManager(MainFragment.this, mActivity);

			webManager.setDoType("get_all_conversations_url");

			if (webManager.isNetworkAvailable()) {
				// do web connect...
				webManager.execute(queryUrl, "GET");
			}
		}

		private class MessageBtnListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mActivity, NewMessageView.class);
				startActivity(i);
			}
		}

		@Override
		public void onRequestComplete(Object... objects) {
			// TODO Auto-generated method stub
			if (objects.length == 2) {
				if (objects[1].toString().equals("get_all_conversations_url")) {

					JSONParser parser_obj = new JSONParser();

					try {
						mMsgJSONArray = (JSONArray) parser_obj.parse(objects[0]
								.toString());
					} catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (mMsgJSONArray.size() > 0)
						createMsgListView();
				} else if (objects[1].toString().equals("getallfriends_url")) {
					JSONParser parser_obj = new JSONParser();

					JSONArray friendsJSONArray = new JSONArray();

					try {
						friendsJSONArray = (JSONArray) parser_obj
								.parse(objects[0].toString());
					} catch (org.json.simple.parser.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (friendsJSONArray.size() > 0) {
						NewMessageView.mFriendsJSONArray = friendsJSONArray;
						mNewMessageBtn.setEnabled(true);
					} else
						mNewMessageBtn.setEnabled(false);

				}
			}
		}

	}
}

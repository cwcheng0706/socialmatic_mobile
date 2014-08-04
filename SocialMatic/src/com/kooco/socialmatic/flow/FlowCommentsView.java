package com.kooco.socialmatic.flow;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.message.MessageHistoryAdapter;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class FlowCommentsView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {
	private Button mBackBtn;
	private TextView mTitleTextView;
	private EditText mMessageText;
	public static String mUserName = "";
	private ListView mCommentsListView;
	private FlowCommentsAdapter mFlowCommentsAdapter;
	
	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

	ArrayList<JSONObject> mAryCommentsList = new ArrayList<JSONObject>();

	public static int mPhotoId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.send_message_to_user_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setText("Comments");
		mTitleTextView.setTypeface(tf);

		mMessageText = (EditText) findViewById(R.id.message_text);
		mMessageText.setHint("Write a comment...");

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());

		Button sendBtn = (Button) findViewById(R.id.send_btn);
		sendBtn.setText("Send");
		sendBtn.setOnClickListener(new SendMsgBtnListener());

		mCommentsListView = (ListView) findViewById(R.id.message_listview);

		mFlowCommentsAdapter = new FlowCommentsAdapter(this, mAryCommentsList);
		mCommentsListView.setAdapter(mFlowCommentsAdapter);

		//createUserData();
		getViewData();
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

	
	private void createViewData(JSONArray commentsJSONArray) {

		mAryCommentsList.clear();
		for (Object obj : commentsJSONArray)
			mAryCommentsList.add((JSONObject) obj);

		mFlowCommentsAdapter.notifyDataSetChanged();
	}

	private void getViewData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_comments_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&objectID=" + mPhotoId
				+ "&objectType=classified";

		WebManager webManager = new WebManager(this);

		webManager.setDoType("get_comments");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}

	}

	private class SendMsgBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			if (!mMessageText.getText().toString().equals("")) {
				
				String deviceId = UserDataAccess
						.getSystemUseDeviceId(FlowCommentsView.this);
				String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
						FlowCommentsView.this, Config.authTokenKey);
				String userId = UserDataAccess.getDataWithKeyAndEncrypt(
						FlowCommentsView.this, Config.userIdKey);

				String queryUrl = getString(R.string.socialmatic_base_url)
						+ getString(R.string.post_comment_url);

				/*
				 * queryUrl += "?deviceID=" + deviceId + "&userID=" + userId +
				 * "&authtoken=" + authToken + "&photoID=" + mPhotoId;
				 */

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs
						.add(new BasicNameValuePair("deviceID", deviceId));
				nameValuePairs.add(new BasicNameValuePair("userID", userId));
				nameValuePairs.add(new BasicNameValuePair("authtoken",
						authToken));
				nameValuePairs.add(new BasicNameValuePair("objectID", Integer
						.toString(mPhotoId)));
				nameValuePairs.add(new BasicNameValuePair("comment",
						mMessageText.getText().toString()));
				
				nameValuePairs.add(new BasicNameValuePair("objectType","classified"));

				WebManager webManager = new WebManager(FlowCommentsView.this);

				webManager.setDoType("post_comments");
				
				try {
					webManager.setQueryEntity(new UrlEncodedFormEntity(
							nameValuePairs, HTTP.UTF_8));
				} catch (UnsupportedEncodingException e1) {
				}

				if (webManager.isNetworkAvailable()) {
					// do web connect...
					webManager.execute(queryUrl, "POST");
				}
			}
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
			if (objects[1].toString().equals("get_comments")) {
				JSONParser parser_obj = new JSONParser();
				JSONArray commentsJSONArray = new JSONArray();
				try {
					commentsJSONArray = (JSONArray) parser_obj.parse(objects[0]
							.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (commentsJSONArray.size() > 0)
					createViewData(commentsJSONArray);
				// createViewData(objects[0].toString());
			} else if (objects[1].toString().equals("post_comments")) {
				
				mMessageText.setText("");
				getViewData();
			}
		}
	}
}

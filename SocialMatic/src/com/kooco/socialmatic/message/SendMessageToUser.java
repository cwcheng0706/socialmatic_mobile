package com.kooco.socialmatic.message;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.camera.SharePhotoView;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.message.MessageView.MainFragment;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class SendMessageToUser extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private TextView mTitleTextView;
	private TextView mMsgTextView;
	private Button mSendBtn;
	public static String mConversationID = "";
	private ListView mMessageListView;
	private MessageHistoryAdapter mMessageHistoryAdapter;
	
	JSONArray mMsgJSONArray = new JSONArray();
	ArrayList<JSONObject> mAryMsgList = new ArrayList<JSONObject>();
	
	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;

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
		mMsgTextView = (TextView) findViewById(R.id.message_text);
		mTitleTextView.setText("Messages History");
		mTitleTextView.setTypeface(tf);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());
		
		mSendBtn = (Button) findViewById(R.id.send_btn);
		mSendBtn.setOnClickListener(new SendBtnListener());

		mMessageListView = (ListView) findViewById(R.id.message_listview);

		mMessageHistoryAdapter = new MessageHistoryAdapter(this, mAryMsgList);
		mMessageListView.setAdapter(mMessageHistoryAdapter);
		
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			Intent i = getIntent();
			mConversationID = i.getStringExtra("conversationId");
		}
		
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

	
	private void ShowMsgDialog(String Msg) {

		Builder MyAlertDialog = new AlertDialog.Builder(this);

		MyAlertDialog.setTitle("SocialMatic");

		MyAlertDialog.setMessage(Msg);

		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}

		};

		MyAlertDialog.setNeutralButton("OK", OkClick);
		MyAlertDialog.show();

	}
	
	private class SendBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mMsgTextView.getText().toString().equals(""))
			{
				ShowMsgDialog("Please enter a message to be send!");
				return;
			}
			
			String deviceId = UserDataAccess
					.getSystemUseDeviceId(SendMessageToUser.this);

			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
					SendMessageToUser.this, Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(
					SendMessageToUser.this, Config.userIdKey);

			String queryUrl = SendMessageToUser.this
					.getString(R.string.socialmatic_base_url)
					+ SendMessageToUser.this.getString(R.string.send_new_conv_msg_url);


			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("userID", userId));
			nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
			nameValuePairs.add(new BasicNameValuePair("conversationID", mConversationID));
			nameValuePairs.add(new BasicNameValuePair("comment", mMsgTextView.getText().toString()));
			
			WebManager webManager = new WebManager(SendMessageToUser.this);

			webManager.setDoType("send_new_conv_msg_url");

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(
						nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
			}

			if (webManager.isNetworkAvailable()) { // do web connect...
				webManager.execute(queryUrl, "POST");
			}
		}
	}
	
	private void getViewData() {

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(
				this, Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.get_conversation_history_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authToken + "&conversationID=" + mConversationID
				+ "&endDate=2000-01-01";

		WebManager webManager = new WebManager(SendMessageToUser.this);

		webManager.setDoType("get_conversation_history_url");

		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	private void createMsgListView() {

		mAryMsgList.clear();
		for (Object obj : mMsgJSONArray)
			mAryMsgList.add((JSONObject) obj);

		mMessageHistoryAdapter.notifyDataSetChanged();
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
			if (objects[1].toString().equals("get_conversation_history_url")) {

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
			}
			else if (objects[1].toString().equals("send_new_conv_msg_url"))
			{
				mMsgTextView.setText("");
				getViewData();
			}
		}
	}
}

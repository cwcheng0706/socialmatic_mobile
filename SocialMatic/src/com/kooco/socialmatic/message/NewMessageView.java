package com.kooco.socialmatic.message;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.follow.SendMessageToFollowView;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class NewMessageView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private Button mBackBtn;
	private Button mSendBtn;
	private EditText mMsgTextView;
	//private AutoCompleteTextView mSearchText;
	private ScrollView mScrollView;
	private LinearLayout mTagLinearLayout;
	
	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;
	
	public static JSONArray mFriendsJSONArray = new JSONArray();

	private Spinner mFriendSpinner;
	
	ArrayList<String> mAryReceiverID = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.new_message_view);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mSendBtn = (Button) findViewById(R.id.send_btn);
		mMsgTextView = (EditText) findViewById(R.id.message_text);
		
		//mSearchText = (AutoCompleteTextView) findViewById(R.id.search_text);
		mScrollView = (ScrollView) findViewById(R.id.scrollview);
		mTagLinearLayout = (LinearLayout) findViewById(R.id.add_layout);
		
		mFriendSpinner = (Spinner) findViewById(R.id.friend_spinner);
		mFriendSpinner.setAdapter(new FriendsListAdapter(this, mFriendsJSONArray));
		mFriendSpinner.setPrompt("Select message receiver");
		
		mFriendSpinner.setOnItemSelectedListener (new OnItemSelectedListener () {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (position == 0)
					return;
				
				String userID = ((JSONObject)parent.getItemAtPosition(position)).get("user_id").toString();
				
				if (!isUserIDInArray(userID))
				{
					// TODO Auto-generated method stub
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	
					final TextView tv = new TextView(NewMessageView.this);
	
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(0, 0, 0, 10);
	
					tv.setLayoutParams(params);
	
					tv.setTextSize(16);
					tv.setTextColor(Color.WHITE);
					tv.setBackgroundColor(Color.parseColor("#F2A215"));
					
					String userName = ((JSONObject)parent.getItemAtPosition(position)).get("display_name").toString();
					
					
					tv.setText(userName + "     X");
					tv.setTag(userID);
	
					tv.setClickable(true);
	
					tv.setOnClickListener(new OnClickListener() {
	
						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							String userID = view.getTag().toString();
							removeUserIDFromArray(userID);
							mTagLinearLayout.removeView(tv);
						}
					});
	
					mAryReceiverID.add(userID);
					mTagLinearLayout.addView(tv);
				}
			}


			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mBackBtn.setOnClickListener(new BackBtnListener());
		mSendBtn.setOnClickListener(new SendMsgBtnListener());
		
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

	
	private boolean isUserIDInArray (String userID)
	{
		boolean flag = false;
		
		for (String strID: mAryReceiverID)
		{
			if (strID.equals(userID))
			{
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	private void removeUserIDFromArray (String userID)
	{
		for (String strID: mAryReceiverID)
		{
			if (strID.equals(userID))
			{
				mAryReceiverID.remove(strID);
				break;
			}
		}
	}

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
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
	
	private class SendMsgBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			
			if (mAryReceiverID.size() <= 0)
			{
				ShowMsgDialog("Please select message receiver!");
				return;
			}
			
			if (mMsgTextView.getText().toString().equals(""))
			{
				ShowMsgDialog("Please enter a message to be send!");
				return;
			}
			
			String strReceiverID = "";
			
			for (int i = 0; i < mAryReceiverID.size(); i++)
			{
				strReceiverID += mAryReceiverID.get(i);
				
				if (i != mAryReceiverID.size() - 1)
					strReceiverID += ",";
			}

			String deviceId = UserDataAccess.getSystemUseDeviceId(NewMessageView.this);

			String authToken = UserDataAccess.getDataWithKeyAndEncrypt(NewMessageView.this,
					Config.authTokenKey);
			String userId = UserDataAccess.getDataWithKeyAndEncrypt(NewMessageView.this,
					Config.userIdKey);

			String queryUrl = getString(R.string.socialmatic_base_url)
					+ getString(R.string.send_new_msg_url);

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
			nameValuePairs.add(new BasicNameValuePair("userID", userId));
			nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
			
			nameValuePairs.add(new BasicNameValuePair("recipientList", strReceiverID));

			nameValuePairs.add(new BasicNameValuePair("comment", mMsgTextView
					.getText().toString()));

			WebManager webManager = new WebManager(NewMessageView.this);

			webManager.setDoType("send_new_msg_url");

			try {
				webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
			}

			if (webManager.isNetworkAvailable()) { // do web connect...
				webManager.execute(queryUrl, "POST");
			}

		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects.length == 2) {
			if (objects[1].toString().equals("send_new_msg_url")) {
				finish();
			}
		}
	}
}

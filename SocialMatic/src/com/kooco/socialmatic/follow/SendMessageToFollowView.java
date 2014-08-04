package com.kooco.socialmatic.follow;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class SendMessageToFollowView extends SherlockFragmentActivity implements
		WebManagerCallbackInterface {

	private TextView mTitleLab;
	private TextView mSendUserNameLab;
	private EditText mMsgBodyText;
	private Button mBackBtn;
	private Button mConfirmBtn;

	public static String mUserName = "";
	public static String mUserId = "";

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

		setContentView(R.layout.send_messageto_follow_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mTitleLab = (TextView) findViewById(R.id.title_lab);
		mMsgBodyText = (EditText) findViewById(R.id.msg_body_text);
		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
		mSendUserNameLab = (TextView) findViewById(R.id.user_name_lab);

		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mSendUserNameLab.setText(mUserName);

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		
		mTitleLab.setTypeface(tf);

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

	private void sendMsg() {

		if (mMsgBodyText.getText().toString().equals("")) {
			ShowMsgDialog("Please enter a message to be send!");
			return;
		}

		String deviceId = UserDataAccess.getSystemUseDeviceId(this);

		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);

		String queryUrl = getString(R.string.socialmatic_base_url)
				+ getString(R.string.send_new_msg_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("recipientList", mUserId));

		nameValuePairs.add(new BasicNameValuePair("comment", mMsgBodyText
				.getText().toString()));

		WebManager webManager = new WebManager(SendMessageToFollowView.this);

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

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class ConfirmBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			sendMsg();
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

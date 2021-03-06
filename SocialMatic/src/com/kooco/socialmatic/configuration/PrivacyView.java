package com.kooco.socialmatic.configuration;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;

public class PrivacyView extends SherlockFragmentActivity {

	private Button mBackBtn;
	private Button mConfirmBtn;

	private TextView mTitleLab;
	private TextView mProfilePrivacyLab;
	private TextView mContentLab;
	private RadioButton mOnleMeRadio;
	private RadioButton mEveryoneRadio;
	
	private ImageView mUserPhotoImageView;
	
	private ImageLoader imageLoader;
	
	private TextView mUserNameLab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		imageLoader = new ImageLoader(getApplicationContext());
		
		setContentView(R.layout.privacy_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mTitleLab = (TextView) findViewById(R.id.title_lab);
		mProfilePrivacyLab = (TextView) findViewById(R.id.profile_privacy_lab);
		mContentLab = (TextView) findViewById(R.id.content_lab);
		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
		
		mOnleMeRadio = (RadioButton) findViewById(R.id.onle_me_radio);
		mEveryoneRadio = (RadioButton) findViewById(R.id.everyone_radio);

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		
		mTitleLab.setTypeface(tf);
		mProfilePrivacyLab.setTypeface(tf);
		mContentLab.setTypeface(tf);
		
		mOnleMeRadio.setTypeface(tf);
		mEveryoneRadio.setTypeface(tf);
		
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


	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class ConfirmBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}

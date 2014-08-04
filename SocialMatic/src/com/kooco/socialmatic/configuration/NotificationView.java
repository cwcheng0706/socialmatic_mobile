package com.kooco.socialmatic.configuration;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;

public class NotificationView extends SherlockFragmentActivity {

	private Button mBackBtn;
	private Button mConfirmBtn;
	private TextView mTitleLab;
	private TextView mContentLab;
	private TextView mGeneralLab;
	private TextView mSubscribeLab;
	private CheckBox mGenerlSelect1;
	private CheckBox mGenerlSelect2;
	private CheckBox mGenerlSelect3;
	private CheckBox mGenerlSelect4;
	private CheckBox mGenerlSelect5;

	private TextView mNotificationSettingsLab;
	private TextView mSubscribeSelect1;
	private TextView mSubscribeSelect2;

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

		setContentView(R.layout.notification_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		/*
		 * mUserPhotoImageView = (ImageView)
		 * findViewById(R.id.userphoto_imageview); mUserNameLab = (TextView)
		 * findViewById(R.id.username_textview); mUserNameLab.setTypeface(tf);
		 */

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		mTitleLab = (TextView) findViewById(R.id.title_lab);
		mNotificationSettingsLab = (TextView) findViewById(R.id.notification_setting_lab);

		mContentLab = (TextView) findViewById(R.id.content_lab);
		mGeneralLab = (TextView) findViewById(R.id.general_lab);

		mGenerlSelect1 = (CheckBox) findViewById(R.id.generl_select_1);
		mGenerlSelect2 = (CheckBox) findViewById(R.id.generl_select_2);
		mGenerlSelect3 = (CheckBox) findViewById(R.id.generl_select_3);
		mGenerlSelect4 = (CheckBox) findViewById(R.id.generl_select_4);
		mGenerlSelect5 = (CheckBox) findViewById(R.id.generl_select_5);

		mSubscribeLab = (TextView) findViewById(R.id.subscribe_lab);

		mSubscribeSelect1 = (TextView) findViewById(R.id.subscribe_select_1);
		mSubscribeSelect2 = (TextView) findViewById(R.id.subscribe_select_2);

		mTitleLab.setTypeface(tf);
		mContentLab.setTypeface(tf);
		mGeneralLab.setTypeface(tf);
		mGenerlSelect1.setTypeface(tf);
		mGenerlSelect2.setTypeface(tf);
		mGenerlSelect3.setTypeface(tf);
		mGenerlSelect4.setTypeface(tf);
		mGenerlSelect5.setTypeface(tf);
		mNotificationSettingsLab.setTypeface(tf);

		mSubscribeLab.setTypeface(tf);

		mSubscribeSelect1.setTypeface(tf);
		mSubscribeSelect2.setTypeface(tf);

		// createUserData();
	}

	private CheckBox.OnCheckedChangeListener mCheckBoxlistener = new CheckBox.OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (mGenerlSelect1.isChecked() == true) {

			}
			if (mGenerlSelect2.isChecked() == true) {
				
			}
			if (mGenerlSelect3.isChecked() == true) {
				
			}
			if (mGenerlSelect4.isChecked() == true) {
				
			}
			if (mGenerlSelect5.isChecked() == true) {
				
			}
		}

	};

	private void createUserData() {
		String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.photoKey);
		String gender = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.genderKey);
		String userName = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.nameKey);

		if (photoUrl.equals("")) {
			Drawable res;

			if (gender.equals("M"))
				res = getResources().getDrawable(R.drawable.account_photo_3);
			else if (gender.equals("W"))
				res = getResources().getDrawable(R.drawable.account_photo_1);
			else
				res = getResources().getDrawable(R.drawable.account_photo_2);
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

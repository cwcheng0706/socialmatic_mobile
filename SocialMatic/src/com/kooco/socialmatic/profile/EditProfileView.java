package com.kooco.socialmatic.profile;

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
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;

public class EditProfileView extends SherlockFragmentActivity {

	private Button mBackBtn;
	private TextView mTitleLab;
	
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

		setContentView(R.layout.edit_profile_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());
		
		Button personalInfoBtn = (Button) findViewById(R.id.personal_info_btn);
		Button editMyProfilePhotoBtn = (Button) findViewById(R.id.edit_my_profile_photo_btn);
		editMyProfilePhotoBtn.setOnClickListener(new EditProfilePhotoBtnListener());
		personalInfoBtn.setOnClickListener(new PersonalInfoBtnListener());
		//Button editMyFlowPhotoBtn = (Button) findViewById(R.id.edit_my_flow_photo_btn);
		personalInfoBtn.setTypeface(tf);
		editMyProfilePhotoBtn.setTypeface(tf);
		//editMyFlowPhotoBtn.setTypeface(tf);

		mTitleLab = (TextView) findViewById(R.id.title_lab);
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

	
	private class EditProfilePhotoBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(EditProfileView.this, EditProfilePhotoView.class);
			startActivity(i);
		}
	}
	
	private class PersonalInfoBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(EditProfileView.this, PersonalInfoView.class);
			startActivity(i);
		}
	}

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}

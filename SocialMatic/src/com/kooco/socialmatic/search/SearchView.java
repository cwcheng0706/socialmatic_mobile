package com.kooco.socialmatic.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.message.NewMessageView;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class SearchView extends SherlockFragmentActivity {
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
		TextView mContentLabel;
		EditText mKeywordText;
		Button mSearchBtn;
		
		private ImageView mUserPhotoImageView;
		
		private ImageLoader imageLoader;
		
		private TextView mUserNameLab;

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

			mView = inflater.inflate(R.layout.search_view, container, false);

			Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
					"fonts/31513_RCKWL.ttf");
			
			/*mUserPhotoImageView = (ImageView) mView
					.findViewById(R.id.userphoto_imageview);
			mUserNameLab = (TextView) mView.findViewById(R.id.username_textview);
			mUserNameLab.setTypeface(tf);
			*/

			mTitleTextView = (TextView) mView.findViewById(R.id.title_lab);
			mTitleTextView.setTypeface(tf);

			mContentLabel = (TextView) mView.findViewById(R.id.content_lab);
			mTitleTextView.setTypeface(tf);
			
			mKeywordText = (EditText) mView.findViewById(R.id.search_text);

			mSearchBtn = (Button) mView.findViewById(R.id.search_btn);
			mSearchBtn.setOnClickListener(new SearchBtnListener());

			mContext = mView.getContext();
			
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

		
		public void showAlertView(String title, String Content) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(title);
			builder.setMessage(Content);
			builder.setPositiveButton("Exit", null);
			@SuppressWarnings("unused")
			AlertDialog dialog = builder.show();
		}

		private class SearchBtnListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				
				if (mKeywordText.getText().toString().equals(""))
				{
					showAlertView("Socailmatic",
							"For further information, please enter the keyword.");
				}
				else
				{
					SearchResultView.mKeyWord = mKeywordText.getText().toString();
				    Intent i = new Intent(mActivity, SearchResultView.class);
				    startActivity(i);
				}
			}
		}

		@Override
		public void onRequestComplete(Object... objects) {
			// TODO Auto-generated method stub

		}
	}
}

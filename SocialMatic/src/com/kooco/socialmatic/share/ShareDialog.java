package com.kooco.socialmatic.share;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import com.kooco.socialmatic.R;
import com.kooco.socialmatic.camera.SharePhotoView;
import com.kooco.socialmatic.lazylist.ImageLoader;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ShareDialog extends Dialog implements
		android.view.View.OnClickListener {

	public Activity mActivity;
	public Dialog d;
	public Button mBtnExit, mBtnShare;
	
	
	public static String mPostData;
	public static String mShareType;
	public static String mUploadType;
	
	private EditText mDescriptionEditText;
	private LinearLayout mFBLinearLayout;
	private ImageView mShareImageView;
	private ProgressBar mPhotoProgressbar;
	private TextView mSubjectTextview;
	private TextView mTitleTextview;
	private ImageLoader imageLoader;
	
	private TextView mTxt_dia;

	SocialAuthAdapter mAdapter;
	
	private String[] mAryShareData;

	public ShareDialog(Activity a, SocialAuthAdapter adapter) {
		super(a);
		// TODO Auto-generated constructor stub
		this.mActivity = a;
		mAdapter = adapter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_dialog);
		mBtnExit = (Button) findViewById(R.id.btn_exit);
		mBtnShare = (Button) findViewById(R.id.btn_share);
		mFBLinearLayout = (LinearLayout) findViewById(R.id.fb_share_linearlayout);
		mShareImageView = (ImageView) findViewById(R.id.photo_image);
		mPhotoProgressbar = (ProgressBar) findViewById(R.id.photo_progressbar);
		mSubjectTextview = (TextView) findViewById(R.id.subject_textview);
		mTitleTextview = (TextView) findViewById(R.id.title_textview);
		mTxt_dia = (TextView) findViewById(R.id.txt_dia);
		
		mDescriptionEditText = (EditText) findViewById(R.id.description_editTxt);

		mBtnExit.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		
		mPhotoProgressbar.setVisibility(View.GONE);
		
		if (mUploadType.equals("updateStory"))
		{
			mAryShareData = mPostData.split("<>");
			
			if (mShareType.equalsIgnoreCase("facebook"))
			{
			    imageLoader = new ImageLoader(mActivity);
			    imageLoader.DisplayImage(mAryShareData[0], mShareImageView, 200);
			}
			
			if (mShareType.equalsIgnoreCase("twitter"))
			{
				mFBLinearLayout.setVisibility(View.GONE);
				mDescriptionEditText.setText(mAryShareData[1] +"\n" + mAryShareData[2]);
			}
			
			mTitleTextview.setText(mAryShareData[1]);
		}
		else
		{
			mShareImageView.setImageBitmap(SharePhotoView.mImg);
		
			mTitleTextview.setText(mPostData);
		}
		
		mTxt_dia.setText("Share to " + mShareType);
		/*mBtnShare.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mAdapter.updateStatus(
						mDescriptionEditText.getText().toString(),
						new MessageListener(), false);
			}
		});
		*/
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
			{
				Toast.makeText(mActivity,
						"Message posted on " + provider, Toast.LENGTH_LONG)
						.show();
			    dismiss();
			}
			else
				Toast.makeText(mActivity,
						"Message not posted on " + provider, Toast.LENGTH_LONG)
						.show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_exit:
			// c.finish();
			dismiss();
			break;
		case R.id.btn_share:
			try {
				if (mUploadType.equals("updateStory"))
				{
					if (mShareType.equalsIgnoreCase("facebook"))
					{
						Log.d("onClick.....","mDescriptionEditText.getText().toString()--->" + mDescriptionEditText.getText().toString()
								             + "   mSubjectTextview.getText().toString()--->" + mSubjectTextview.getText().toString()
								             + "   mTitleTextview.getText().toString()--->" + mTitleTextview.getText().toString()
								             + "   mAryShareData[2]--->" + mAryShareData[2] + "   mAryShareData[0]--->" + mAryShareData[0]);
						Log.d("onClick------", "mAdapter.getCurrentProvider().getProviderId()-------->" + mAdapter.getCurrentProvider().getProviderId());
					    mAdapter.updateStory(mDescriptionEditText.getText().toString(), mSubjectTextview.getText().toString(), mTitleTextview.getText().toString(), 
							     "", mAryShareData[2], mAryShareData[0], new MessageListener());
					}
					else
					{
						mAdapter.updateStatus(
								mDescriptionEditText.getText().toString(),
								new MessageListener(), false);
					}
				}
				else
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String currentDateandTime = sdf.format(new Date());
					
					String fileName = "Socialmatic_Share_" + currentDateandTime + ".png";
					try {
						mAdapter.uploadImageAsync(mTitleTextview.getText().toString(), fileName, SharePhotoView.mImg, 100, new MessageListener());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*mAdapter.updateStatus(
					mDescriptionEditText.getText().toString(),
					new MessageListener(), false);
					*/
			break;
		default:
			break;
		}
		//dismiss();
	}
}

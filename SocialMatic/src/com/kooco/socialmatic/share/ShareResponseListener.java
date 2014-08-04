package com.kooco.socialmatic.share;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ShareResponseListener implements DialogListener {
	
	private String mPostData;
	private String mShareType;
	private Activity mActivity;
	private String mUploadType;
	private SocialAuthAdapter mSocialAuthAdapter;
	
	public ShareResponseListener (Activity a)
	{
		mActivity = a;
	}
	
	public void setActivity (Activity a)
	{
		mActivity = a;
	}
	
	public void setPostData (String postData)
	{
		mPostData = postData;
	}
	
	public void SetShareType (String shareType)
	{
		mShareType = shareType;
	}
	
	public void setAdapter (SocialAuthAdapter adapter)
	{
		mSocialAuthAdapter = adapter;
	}
	
	public void setUploadType (String type)
	{
		mUploadType = type;
	}
	
	@Override
	public void onComplete(Bundle values) {

		final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
		Toast.makeText(mActivity, providerName + " connected", Toast.LENGTH_LONG).show();

		ShareDialog.mPostData = mPostData;
		ShareDialog.mShareType = providerName;
		ShareDialog.mUploadType = mUploadType;
		
		ShareDialog sd=new ShareDialog(mActivity, mSocialAuthAdapter);
		sd.show();  
	}

	@Override
	public void onError(SocialAuthError error) {
		Log.d("ShareButton", "Authentication Error: " + error.getMessage());
	}

	@Override
	public void onCancel() {
		Log.d("ShareButton", "Authentication Cancelled");
	}

	@Override
	public void onBack() {
		Log.d("Share-Button", "Dialog Closed by pressing Back Key");
	}

}

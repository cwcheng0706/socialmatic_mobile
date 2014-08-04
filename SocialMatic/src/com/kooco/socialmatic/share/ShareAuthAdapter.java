package com.kooco.socialmatic.share;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.ShareButtonAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.Util;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.kooco.socialmatic.R;


public class ShareAuthAdapter extends SocialAuthAdapter{
	
	private Provider authProviders[];
	int authProviderLogos[];
	private int providerCount = 0;

	ShareResponseListener mDialogListener;
	

    private static ShareAuthAdapter instance = null;
    
    public static ShareAuthAdapter getInstance(Activity activity) {
    	ShareResponseListener srl = new ShareResponseListener(activity);
        if(instance == null)
		     instance = new ShareAuthAdapter(srl);
        else
        	instance.mDialogListener.setActivity(activity);
        
		 return instance;
    }
    
    public ShareResponseListener getResponseListener()
    {
    	return mDialogListener;
    }
	
	private ShareAuthAdapter(DialogListener listener) {
		super(listener);
		
		authProviders = new Provider[2];
		authProviderLogos = new int[2];
		
		mDialogListener = (ShareResponseListener)listener;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addProvider(Provider provider, int logo) {
		authProviders[providerCount] = provider;
		authProviderLogos[providerCount] = logo;
		providerCount++;
	}
	
	public int providerCount()
	{
		return providerCount;
	}

	@Override
	public void enable(Button sharebtn) {
		final Context ctx = sharebtn.getContext();
		
		// Click Listener For Share Button
		sharebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				mDialogListener.setPostData(v.getTag().toString());
				mDialogListener.setUploadType("updateStory");
				
				// Creating dialog to show list of all providers
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle("Share via");
				builder.setCancelable(true);
				builder.setIcon(android.R.drawable.ic_menu_more);

				String[] providerNames = new String[providerCount];
				int[] providerLogos = new int[providerCount];
				
				for (int i = 0; i < providerCount; i++) {
				    providerNames[i] = authProviders[i].toString();
				    providerLogos[i] = authProviderLogos[i];
			    }

				// Handle click events
				builder.setSingleChoiceItems(new ShareButtonAdapter(ctx, providerNames, providerLogos), 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								if (authProviders[item].toString().startsWith("share_mail")
										|| authProviders[item].toString().startsWith("share_mms")) {
									// Getting selected provider email or mms
									Bundle bundle = new Bundle();
									bundle.putString(SocialAuthAdapter.PROVIDER, authProviders[item].toString());
									
									mDialogListener.SetShareType(authProviders[item].toString());
									
									mDialogListener.onComplete(bundle);
								} else {
									// Getting selected provider and starting
									// authentication
									authorize(ctx, authProviders[item]);
								}
								dialog.dismiss();
							}
						});
				final AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		// If network not available show message
		if (!Util.isNetworkAvailable(ctx)) {
			mDialogListener.onError(new SocialAuthError("Please check your Internet connection", new Exception("")));
			return;
		}
	}
	
	public void enable(Button sharebtn, final EditText msgEditText)
	{
		final Context ctx = sharebtn.getContext();
		
		final String providerName = sharebtn.getTag().toString();
		
		sharebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//String strTag = v.getTag().toString();
				//String[] aryTag = strTag.split("<|>");
				
				mDialogListener.setPostData("");
				mDialogListener.setUploadType("uploadImage");
				
				if (providerName.startsWith("share_mail")
						|| providerName.startsWith("share_mms")) {
					// Getting selected provider email or mms
					Bundle bundle = new Bundle();
					bundle.putString(SocialAuthAdapter.PROVIDER, providerName);
					
					mDialogListener.SetShareType(providerName);
					mDialogListener.onComplete(bundle);
				} else {
					// Getting selected provider and starting
					// authentication
					Provider provider = authProviders[0];
					
					for (int i = 0; i < providerCount; i++)
					{
						if (authProviders[i].toString().equalsIgnoreCase(providerName))
						{
							provider = authProviders[i];
							break;
						}
					}
					authorize(ctx, provider);
				}
				
			}
		});
	}
}

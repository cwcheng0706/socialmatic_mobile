package com.kooco.socialmatic.profile;

import com.kooco.socialmatic.R;
import com.kooco.socialmatic.configuration.ForgotPasswordDialog;
import com.kooco.tool.LoadImage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PhotoWatchDialog extends Dialog{

	private Button mOkButton;
	private Context mContext;
	
	public PhotoWatchDialog(Context context, String photoUrl) {
		super(context);
		// TODO Auto-generated constructor stub

		mContext = context;
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.photo_watch_dialog);
		
		ImageView imageView = (ImageView) findViewById(R.id.imageview);
		imageView.setTag(photoUrl);
		
		ProgressBar pgBar = (ProgressBar) findViewById(R.id.photo_progressbar);
		
		mOkButton = (Button) findViewById(R.id.ok_button);
		
		new LoadImage((Activity)context, imageView,
				pgBar, photoUrl).execute();
		
		mOkButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PhotoWatchDialog.this.dismiss();
			}
		});
	}
}

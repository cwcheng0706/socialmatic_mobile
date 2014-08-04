package com.kooco.socialmatic.camera;

import java.io.File;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.configuration.RegisterView;
import com.kooco.socialmatic.lazylist.LazyAdapter;
import com.kooco.tool.ScrollGridView;

public class SelectPhotoView extends SherlockFragmentActivity {

	private File[] mListFile;
	private String[] mFileStrings;
	private ScrollGridView mScrollGridView;
	private LazyAdapter mPhoto_adapter;

	private Button mCancelBtn;

	private TextView mTitleTextView;

	public static String mCallView = "CameraMainView";
	
	public static RegisterView mRegisterView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.select_photo_view);

		Typeface tf1 = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setTypeface(tf1);

		mCancelBtn = (Button) findViewById(R.id.back_btn);
		mCancelBtn.setOnClickListener(new CancelClickListener());

		mScrollGridView = (ScrollGridView) findViewById(R.id.gridview);

		getPhotoFromDisk();

		mPhoto_adapter = new LazyAdapter(this, mFileStrings, "Select_Photo");
		mScrollGridView.setAdapter(mPhoto_adapter);

		mScrollGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				// Intent i = null;

				if (mCallView.equals("RegisterView")) {
					/*
					mRegisterView.setUserPhotoImage("file://"
							+ mPhoto_adapter.data[position]);
					finish();
					*/
				} else {
					Intent i = new Intent(SelectPhotoView.this,
							EditPictureView.class);
					i.putExtra("ImageURI", "file://"
							+ mPhoto_adapter.data[position]);

					startActivity(i);
				}

			}
		});
	}

	@Override
	public void onRestart() {
		super.onRestart();

		getPhotoFromDisk();
		mPhoto_adapter.setData(mFileStrings);
		mPhoto_adapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {

		mScrollGridView.setAdapter(null);
		super.onDestroy();
	}

	private void getPhotoFromDisk() {
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		File file = new File(path, "SocialMatic/");

		if (file.isDirectory()) {
			mListFile = file.listFiles();

			mFileStrings = new String[mListFile.length];

			for (int i = 0; i < mListFile.length; i++) {
				mFileStrings[(mListFile.length - 1) - i] = mListFile[i]
						.getAbsolutePath();
			}
		}
	}

	private class CancelClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}

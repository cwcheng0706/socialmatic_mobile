package com.kooco.socialmatic.profile;

import java.io.IOException;
import java.util.List;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kooco.socialmatic.R;
import com.kooco.tool.GeocoderTask;

public class ProfileMapView extends SherlockFragmentActivity{
	private Button mBackBtn;
	private TextView mTitleTextView;
	public static String mSubject = "";
	
	GoogleMap mGoogleMap;
	ProgressBar mProgressBar;
	public static String mLocation = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.profile_location_view);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");

		mTitleTextView = (TextView) findViewById(R.id.title_lab);
		mTitleTextView.setTypeface(tf);

		mBackBtn = (Button) findViewById(R.id.back_btn);
		mBackBtn.setOnClickListener(new BackBtnListener());
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		
		SupportMapFragment supportMapFragment = (SupportMapFragment)
		getSupportFragmentManager().findFragmentById(R.id.map);
		 
		// Getting a reference to the map
		mGoogleMap = supportMapFragment.getMap();

		if (!mLocation.equals(""))
		{
		    new GeocoderTask(this, mGoogleMap, mProgressBar).execute(mLocation);
		}
		else
			mProgressBar.setVisibility(View.GONE);
	}
	
	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}

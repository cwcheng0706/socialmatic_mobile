package com.kooco.tool;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;

public class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

	Activity mActivity;
	GoogleMap mGoogleMap;
	ProgressBar mProgressBar;
	
	LatLng latLng;
	//MarkerOptions markerOptions;
	
	public GeocoderTask(Activity activity, GoogleMap googleMap, ProgressBar progressBar) {
		mActivity = activity;
		mGoogleMap = googleMap;
		mProgressBar = progressBar;
	}
	
    @Override
    protected List<Address> doInBackground(String... locationName) {
        // Creating an instance of Geocoder class
        Geocoder geocoder = new Geocoder(mActivity.getBaseContext());
        List<Address> addresses = null;

        try {
            // Getting a maximum of 3 Address that matches the input text
            addresses = geocoder.getFromLocationName(locationName[0], 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {

        if(addresses==null || addresses.size()==0){
            Toast.makeText(mActivity.getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        // Clears all the existing markers on the map
        mGoogleMap.clear();

        // Adding Markers on Google Map for each matching address
        for(int i=0;i<addresses.size();i++){
        	

            Address address = (Address) addresses.get(i);

            // Creating an instance of GeoPoint, to display in Google Map
            latLng = new LatLng(address.getLatitude(), address.getLongitude());

            String addressText = String.format("%s, %s",
            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
            address.getCountryName());

            /*markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(addressText);
            */
            mGoogleMap.addMarker(new MarkerOptions()
            .position(latLng)
            .title(addressText)
            .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.flow_location_map_city, ""))));
            
            //mGoogleMap.addMarker(markerOptions);

            // Locate the first location
            if(i==0)
            	mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
        }
        
        if (mProgressBar != null)
        	mProgressBar.setVisibility(View.GONE);
    }
    
    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(convertToPixels((Context)mActivity, 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels((Context)mActivity, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;  

        canvas.drawText(text, xPos, yPos, paint);

        return  bm;
    }
    
    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

}

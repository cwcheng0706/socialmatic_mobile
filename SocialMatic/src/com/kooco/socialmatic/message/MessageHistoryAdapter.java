package com.kooco.socialmatic.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kooco.socialmatic.R;
import com.kooco.socialmatic.lazylist.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageHistoryAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater = null;

	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();

	public ImageLoader imageLoader;

	public MessageHistoryAdapter(Activity a, ArrayList<JSONObject> d) {
		activity = a;
		data = d;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub

		convertView = mInflater.inflate(R.layout.message_history_item, null);

		Typeface tf = Typeface.createFromAsset(activity.getAssets(),
				"fonts/31513_RCKWL.ttf");

		ImageView userImageview = (ImageView) convertView
				.findViewById(R.id.user_imageview);

		TextView nameTextView = (TextView) convertView
				.findViewById(R.id.name_textview);
		TextView contentTextView = (TextView) convertView
				.findViewById(R.id.content_textview);

		TextView dateTextView = (TextView) convertView
				.findViewById(R.id.date_textview);

		TextView weekText = (TextView) convertView
				.findViewById(R.id.week_textview);

		nameTextView.setTypeface(tf);
		dateTextView.setTypeface(tf);
		
		int index = data.size() - (position + 1);

		JSONArray userData = (JSONArray) data.get(index).get("sender");

		String userGender = "";

		try {
			userGender = ((JSONObject)userData.get(0)).get("user_gender").toString();
		} catch (Exception ex) {
			userGender = "B";
		}

		// --------------------------------------
		
		String userPhoto = ((JSONObject)userData.get(0)).get("photo_profile_url").toString();
		
		if (userPhoto.equals("")) {
			Drawable res;

			if (userGender.equals("M"))
				res = activity.getResources().getDrawable(
						R.drawable.account_photo_3);
			else if (userGender.equals("W"))
				res = activity.getResources().getDrawable(
						R.drawable.account_photo_1);
			else
				res = activity.getResources().getDrawable(R.drawable.account_photo_2);

			userImageview.setImageDrawable(res);

		} else {
			userImageview.setTag(userPhoto);

			imageLoader.DisplayImage(userPhoto, userImageview, 40);
		}
		
		String userName = ((JSONObject)userData.get(0)).get("displayname").toString();
		String msg = data.get(index).get("message").toString();
		
		String strMsgDate = data.get(index).get("sending_date").toString();
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(strMsgDate + "000"));

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");  
        String strSendingDate = formatter.format(cal.getTime()); 
        
        nameTextView.setText(userName);
        contentTextView.setText(msg);
        dateTextView.setText(strSendingDate);
        
        SimpleDateFormat weekFormatter = new SimpleDateFormat("E");
        String strSendingDay = weekFormatter.format(cal.getTime()); 
        weekText.setText(strSendingDay);
		// --------------------------------------

		/*
		 * if (position == 0)
		 * userImageview.setBackground(mContext.getResources().getDrawable(
		 * R.drawable.account_photo_2)); else if (position == 1)
		 * userImageview.setBackground(mContext.getResources().getDrawable(
		 * R.drawable.account_photo_1)); else
		 * userImageview.setBackground(mContext.getResources().getDrawable(
		 * R.drawable.account_photo_3));
		 * 
		 * 
		 * if (position == 0) { nameTextView.setText("Socialmatic LLC");
		 * weekText.setText("Thu"); dateTextView.setText("3/13/14 10:00 AM");
		 * contentTextView.setText("Welcome to Socialmatic photo network!"); }
		 * else { nameTextView.setText("TestAccount " + (position + 1));
		 * weekText.setText("Fri"); dateTextView.setText("3/13/14 11:30 AM");
		 * contentTextView.setText("Welcome!"); }
		 */

		contentTextView.setTypeface(tf);

		convertView.setTag(nameTextView.getText().toString());

		return convertView;
	}
}

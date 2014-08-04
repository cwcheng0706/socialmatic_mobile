package com.kooco.socialmatic.message;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kooco.socialmatic.R;
import com.kooco.socialmatic.lazylist.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class FriendsListAdapter extends BaseAdapter implements SpinnerAdapter {

	private Activity activity;
	LayoutInflater mInflater;
	public ImageLoader imageLoader;
	public JSONArray mFriendsJSONArray = new JSONArray();

	public FriendsListAdapter(Context context, JSONArray aryfriends) {
		super();
		// TODO Auto-generated constructor stub
		activity = (Activity) context;
		mFriendsJSONArray = aryfriends;
		mInflater = LayoutInflater.from(context);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		return mFriendsJSONArray.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		
		if (position == 0 || position == -1)
            return null;

		return mFriendsJSONArray.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		
        if (position == 0 || position == -1)
            return -1;

		return position - 1;
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		if (position == 0 || position == -1) {

			convertView = mInflater.inflate(R.layout.friends_item, null);
			return convertView;
        }

		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friends_item, null);
		}
		
		Typeface tf = Typeface.createFromAsset(activity.getAssets(),
				"fonts/31513_RCKWL.ttf");


		ImageView userPhoto = (ImageView) convertView
				.findViewById(R.id.user_image);
		TextView userNamelabel = (TextView) convertView
				.findViewById(R.id.user_name_textview);

		String userName = ((JSONObject) mFriendsJSONArray.get(position - 1)).get(
				"display_name").toString();

		String userID = ((JSONObject) mFriendsJSONArray.get(position - 1)).get(
				"user_id").toString();

		userNamelabel.setText(userName);

		String userGender = "";

		try {
			userGender = ((JSONObject) mFriendsJSONArray.get(position - 1)).get(
					"gender").toString();
		} catch (Exception ex) {
			userGender = "B";
		}

		String userPhotoUrl = ((JSONObject) mFriendsJSONArray.get(position - 1))
				.get("photo_profile_url").toString();

		// --------------------------------------
		if (userPhotoUrl.equals("")) {
			Drawable res;

			if (userGender.equals("M"))
				res = activity.getResources().getDrawable(
						R.drawable.account_photo_3);
			else if (userGender.equals("W"))
				res = activity.getResources().getDrawable(
						R.drawable.account_photo_1);
			else
				res = activity.getResources().getDrawable(R.drawable.account_photo_2);

			userPhoto.setImageDrawable(res);

		} else {
			userPhoto.setTag(userPhotoUrl);

			imageLoader.DisplayImage(userPhotoUrl, userPhoto, 40);
		}

		return convertView;

	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

}

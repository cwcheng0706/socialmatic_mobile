package com.kooco.socialmatic.message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kooco.socialmatic.R;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.profile.SelectUserProfileView;

public class MessageAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater = null;

	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();

	public ImageLoader imageLoader;

	private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;

	static class ViewHolder {
		ImageView mUserPhotoImage;
		ProgressBar mPhotoProBar;
		TextView mNameTextView;
		TextView mContentTextView;
		TextView mDayTextview;
		Button mDeleteBtn;
		// LinearLayout mMainLinearLayout;
	}

	public MessageAdapter(Activity a, ArrayList<JSONObject> d) {
		activity = a;
		data = d;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public void setListData(ArrayList<JSONObject> d) {
		data = d;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub

		// convertView = mInflater.inflate(R.layout.message_item, null);

		Typeface tf = Typeface.createFromAsset(activity.getAssets(),
				"fonts/31513_RCKWL.ttf");

		View vi = convertView;

		ViewHolder holder = null;

		// Object[] tagObj = new Object[2];

		if (vi == null) {
			vi = mInflater.inflate(R.layout.message_item, null);
			holder = new ViewHolder();

			holder.mUserPhotoImage = (ImageView) vi
					.findViewById(R.id.user_imageview);

			holder.mNameTextView = (TextView) vi
					.findViewById(R.id.name_textview);
			holder.mContentTextView = (TextView) vi
					.findViewById(R.id.content_textview);
			holder.mDayTextview = (TextView) vi
					.findViewById(R.id.day_ago_textview);

			holder.mDeleteBtn = (Button) vi.findViewById(R.id.delete_btn);

			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		holder.mNameTextView.setTypeface(tf);
		holder.mDayTextview.setTypeface(tf);

		String conversationId = data.get(position).get("conversation_id")
				.toString();

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String dateString = data.get(position).get("last_message_date")
				.toString();

		Log.d("dateString-----", "dateString-----" + dateString);

		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date nowDate = new Date();

		int difference = ((int) ((nowDate.getTime() / (MILLISECS_PER_DAY)) - (int) (convertedDate
				.getTime() / (MILLISECS_PER_DAY))));

		holder.mDayTextview.setText(difference + " days ago");

		String lastMessageBody = data.get(position).get("last_message_body")
				.toString();

		holder.mContentTextView.setText(lastMessageBody);
		holder.mContentTextView.setTypeface(tf);

		String lastMsgUserId = data.get(position).get("last_message_userID")
				.toString();

		JSONParser parser_obj = new JSONParser();
		JSONArray userJSONArray = new JSONArray();

		try {
			userJSONArray = (JSONArray) parser_obj.parse(data.get(position)
					.get("participant").toString());
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject lastUserObj = null;

		for (Object obj : userJSONArray) {
			try {
				JSONArray tmpObj = (JSONArray) parser_obj.parse(obj
						.toString());

				if (((JSONObject)(tmpObj.get(0))).get("user_id").toString().equals(lastMsgUserId)) {
					lastUserObj = ((JSONObject)(tmpObj.get(0)));
				}

			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (lastUserObj != null) {
			String userName = lastUserObj.get("displayname").toString();
			String userPhoto = lastUserObj.get("photo_profile_url").toString();
			holder.mNameTextView.setText(userName);
			holder.mNameTextView.setTag("<>" + lastMsgUserId);
			holder.mNameTextView.setOnClickListener(new UserInfoBtnListener());

			String userGender = "";

			try {
				userGender = data.get(position).get("user_gender").toString();
			} catch (Exception ex) {
				userGender = "B";
			}

			// --------------------------------------
			holder.mUserPhotoImage.setTag(userPhoto + "<>" + lastMsgUserId);

			if (userPhoto.equals("")) {
				Drawable res;

				if (userGender.equals("M"))
					res = activity.getResources().getDrawable(
							R.drawable.account_photo_3);
				else if (userGender.equals("W"))
					res = activity.getResources().getDrawable(
							R.drawable.account_photo_1);
				else
					res = activity.getResources().getDrawable(
							R.drawable.account_photo_2);

				holder.mUserPhotoImage.setImageDrawable(res);

			} else {

				imageLoader.DisplayImage(userPhoto, holder.mUserPhotoImage, 40);
			}

			holder.mUserPhotoImage
					.setOnClickListener(new UserInfoBtnListener());
			// --------------------------------------

		}

		return vi;
	}

	private class UserInfoBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String[] arySplit = v.getTag().toString().split("<>");
			String strQueryUserID = arySplit[1].toString();

			SelectUserProfileView.mQueryUserID = strQueryUserID;

			Intent i = new Intent(activity, SelectUserProfileView.class);
			activity.startActivity(i);
		}
	}
}

package com.kooco.socialmatic.flow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONObject;

import com.kooco.socialmatic.R;

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
import android.widget.ProgressBar;
import android.widget.TextView;

public class FlowCommentsAdapter extends BaseAdapter {
	private Activity mActivity;
	private LayoutInflater mInflater = null;

	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();

	public FlowCommentsAdapter(Activity a, ArrayList<JSONObject> d) {
		mActivity = a;
		data = d;

		mInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		convertView = mInflater.inflate(R.layout.comment_item, null);

		Typeface tf = Typeface.createFromAsset(mActivity.getAssets(),
				"fonts/31513_RCKWL.ttf");

		ImageView userImageview = (ImageView) convertView
				.findViewById(R.id.user_imageview);

		ProgressBar userPhotoProBar = (ProgressBar) convertView
				.findViewById(R.id.user_photo_progressbar);

		TextView nameTextView = (TextView) convertView
				.findViewById(R.id.name_textview);
		TextView contentTextView = (TextView) convertView
				.findViewById(R.id.content_textview);

		TextView dateTextView = (TextView) convertView
				.findViewById(R.id.date_textview);

		nameTextView.setTypeface(tf);
		dateTextView.setTypeface(tf);

		JSONObject result = (JSONObject) data.get(position);
		
		JSONObject posterObj = (JSONObject) result.get("poster");

		String photoUrl = posterObj.get("photo_profile_url").toString();
		String nickname = posterObj.get("display_name").toString();
		String content = result.get("comment_body").toString();
		String date = result.get("comment_date").toString();
		
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(date + "000"));

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");  
        String strCommentedDate = formatter.format(cal.getTime());
        
		nameTextView.setText(nickname);
		contentTextView.setText(content);
		dateTextView.setText(strCommentedDate);

		if (!photoUrl.equals("")) {
			
			photoUrl = photoUrl.replace("https:", "http:");
			
			userImageview.setTag(photoUrl);
			
			new com.kooco.tool.LoadImage(mActivity, userImageview,
					userPhotoProBar, photoUrl).execute();
		}
		else
		{
			userPhotoProBar.setVisibility(View.GONE);
			Drawable res = mActivity.getResources().getDrawable(R.drawable.account_photo_1);
				
			userImageview.setImageDrawable(res);	
		}
		
		
		/*
		 * if (position == 0)
		 * userImageview.setBackground(mActivity.getResources().getDrawable(
		 * R.drawable.account_photo_2)); else if (position == 1)
		 * userImageview.setBackground(mActivity.getResources().getDrawable(
		 * R.drawable.account_photo_1)); else
		 * userImageview.setBackground(mActivity.getResources().getDrawable(
		 * R.drawable.account_photo_3));
		 * 
		 * 
		 * if (position == 0) { nameTextView.setText("Socialmatic");
		 * dateTextView.setText("3/13/14 10:00 AM");
		 * contentTextView.setText("Beautiful!"); } else {
		 * nameTextView.setText("TestAccount " + (position + 1));
		 * dateTextView.setText("3/13/14 11:30 AM");
		 * contentTextView.setText("Fantastic!"); }
		 */

		contentTextView.setTypeface(tf);

		convertView.setTag(nameTextView.getText().toString());

		return convertView;
	}
}

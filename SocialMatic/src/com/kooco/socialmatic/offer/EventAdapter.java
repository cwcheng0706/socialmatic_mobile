package com.kooco.socialmatic.offer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.kooco.socialmatic.offer.OfferAdapter.ViewHolder;
import com.kooco.tool.LoadImage;

public class EventAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater mInflater = null;

	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();

	public ImageLoader imageLoader;

	private String mShowType = "offer";

	static class ViewHolder {
		TextView titleTextview;
		TextView dateTextview;
		ImageView photoImage;
		ProgressBar photoProBar;
	}

	public EventAdapter(Activity a, ArrayList<JSONObject> d) {
		activity = a;
		data = d;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public void setShowType(String showType) {
		mShowType = showType;
	}

	public void setListData(ArrayList<JSONObject> d) {
		data = d;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		System.gc();

		View vi = convertView;

		ViewHolder holder = null;

		if (vi == null) {
			vi = mInflater.inflate(R.layout.event_item, null);
			holder = new ViewHolder();

			holder.titleTextview = (TextView) vi.findViewById(R.id.title_lab);
			holder.dateTextview = (TextView) vi.findViewById(R.id.date_lab);

			holder.photoImage = (ImageView) vi.findViewById(R.id.photo_image);
			holder.photoProBar = (ProgressBar) vi
					.findViewById(R.id.photo_progressbar);

			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		JSONParser parser = new JSONParser();

		JSONObject ownerObj = null;
		JSONObject eventObj = null;

		if (mShowType.equals("offer")) {
			try {
				ownerObj = (JSONObject) parser.parse(data.get(position)
						.get("owner").toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				eventObj = (JSONObject) parser.parse(data.get(position)
						.get("event").toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				eventObj = (JSONObject) parser.parse(data.get(position)
						.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				ownerObj = (JSONObject) parser.parse(data.get(position)
						.get("owner").toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			// get data from web api
			// --------------------------------------------
			int eventId = Integer.parseInt(eventObj.get("event_id").toString());
			String title = eventObj.get("title").toString();
			String date = eventObj.get("starttime").toString();
			String photoUrl = eventObj.get("photo_url").toString();
			// --------------------------------------------

			Typeface tf = Typeface.createFromAsset(activity.getAssets(),
					"fonts/31513_RCKWL.ttf");

			holder.titleTextview.setTypeface(tf);
			holder.dateTextview.setTypeface(tf);

			holder.titleTextview.setText(title);
			holder.titleTextview.setTag("<>" + eventId);
			holder.titleTextview
					.setOnClickListener(new EventDetailsBtnClickListener());

			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.valueOf(date + "000"));

			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
			String strCreateDate = formatter.format(cal.getTime());
			holder.dateTextview.setText(strCreateDate);
			holder.photoProBar.setVisibility(View.GONE);
			// --------------------------------------
			photoUrl = photoUrl.replace("https:", "http:");
			holder.photoImage.setTag(photoUrl + "<>" + eventId);
			imageLoader.DisplayImage(photoUrl, holder.photoImage, 200);
			// --------------------------------------

			holder.photoImage
					.setOnClickListener(new EventDetailsBtnClickListener());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return vi;
	}

	private class EventDetailsBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String[] arySplit = v.getTag().toString().split("<>");
			String strEventId = arySplit[1].toString();

			EventDetailView.mEventId = Integer.parseInt(strEventId);
			Intent i = new Intent(activity, EventDetailView.class);
			activity.startActivity(i);
		}
	}

}

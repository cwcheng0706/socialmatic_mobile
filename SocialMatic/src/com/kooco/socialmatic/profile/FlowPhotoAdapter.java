package com.kooco.socialmatic.profile;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;

public class FlowPhotoAdapter extends BaseAdapter {

	private Activity activity;
	// public String[] data;
	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();
	private static LayoutInflater mInflater = null;

	public ImageLoader imageLoader;

	static class ViewHolder {
		TextView titleText;
		ProgressBar photoProBar;
		ImageView image;
		Button mZoomBtn;
	}

	public FlowPhotoAdapter(Activity a, ArrayList<JSONObject> d) {
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
			vi = mInflater.inflate(R.layout.photo_item_with_title, null);
			holder = new ViewHolder();

			holder.titleText = (TextView) vi.findViewById(R.id.title_lab);
			holder.photoProBar = (ProgressBar) vi
					.findViewById(R.id.user_photo_progressbar);

			holder.image = (ImageView) vi.findViewById(R.id.photo_image);
			holder.mZoomBtn = (Button) vi.findViewById(R.id.zoom_button);
			
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		holder.photoProBar.setVisibility(View.GONE);

		int photoId = Integer.parseInt(data.get(position).get("photo_id")
				.toString());
		
		String title = data.get(position).get("photo_description")
				.toString();
		
		String imgUrl = data.get(position).get("photo_url_thumb").toString();
		
		holder.titleText.setText(title);
		holder.titleText.setTag("<>" + photoId);
		holder.titleText.setOnClickListener(new PhotoContentClickListener());
		
		// --------------------------------------
		imgUrl = imgUrl.replace("https:", "http:");
		holder.image.setTag(imgUrl + "<>" + photoId);
		imageLoader.DisplayImage(imgUrl, holder.image, 150);
		// --------------------------------------

		holder.image.setOnClickListener(new PhotoContentClickListener());

		holder.mZoomBtn.setTag(imgUrl + "<>" + "Socialmatic");
		holder.mZoomBtn.setOnClickListener(new WatchPhotoClickListener());

		return vi;
	}

	private class PhotoContentClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String userId = UserDataAccess.getDataWithKeyAndEncrypt(activity,
					Config.userIdKey);

			String[] arySplit = v.getTag().toString().split("<>");
			int photoId = Integer.parseInt(arySplit[1].toString());

			PhotoContentView.mPhotoId = photoId;
			PhotoContentView.mUserId = Integer.parseInt(userId);
			Intent i = new Intent(activity, PhotoContentView.class);
			activity.startActivity(i);
		}
	}

	private class WatchPhotoClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String[] arySplit = v.getTag().toString().split("<>");
			String photoUrl = arySplit[0].toString();

			PhotoWatchDialog dialog = new PhotoWatchDialog(activity, photoUrl);
			dialog.setTitle(arySplit[1]);
			dialog.show();
		}
	}
}

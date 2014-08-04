package com.kooco.socialmatic.search;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.kooco.socialmatic.lazylist.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SearchPhotosAdapter extends BaseAdapter {
	
	private Activity activity;
	private LayoutInflater mInflater = null;
	
	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();

	public ImageLoader imageLoader;
	
	public SearchPhotosAdapter(Activity a, ArrayList<JSONObject> d) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}

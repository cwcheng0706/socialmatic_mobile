package com.kooco.socialmatic.camera;

import com.kooco.socialmatic.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AlternateListViewAdapter extends ArrayAdapter {
	private LayoutInflater mInflater;

	private String[] mStrings;

	private int mViewResourceId;
	
	private Object[] mObjects;
	
	public int mSelectIndex = -1;

	@SuppressWarnings("unchecked")
	public AlternateListViewAdapter(Context context, int resource,
			Object[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mObjects = objects;
		mViewResourceId = resource;

	}
	
	@Override
    public int getCount() {
        return mObjects.length + 1;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//View view = super.getView(position, convertView, parent);
		
		convertView = mInflater.inflate(mViewResourceId, null);
		convertView.setMinimumHeight(40);
		
		TextView tv = (TextView)convertView.findViewById(R.id.name_textview); //Give Id to your textview
		
		if (position < mObjects.length)
		    
            tv.setText(mObjects[position].toString());
		
		if (position == mSelectIndex)
			tv.setTextColor(Color.parseColor("#00C5CD"));

		if (position % 2 == 1) {
			convertView.setBackgroundColor(Color.LTGRAY);
		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}

		return convertView;
	}
}

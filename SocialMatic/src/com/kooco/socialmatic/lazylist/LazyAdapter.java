package com.kooco.socialmatic.lazylist;

import com.kooco.socialmatic.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    public String[] data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    private String mType = "Select_Photo";
    
    public LazyAdapter(Activity a, String[] d, String type) {
        activity = a;
        data=d;
        mType = type;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }
    
    public void setData (String[] d)
    {
    	data=d;
    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        if(convertView==null)
        {
        	if (mType.equals("Select_Photo"))
        		vi = inflater.inflate(R.layout.select_photo_item, null);
        	else
                vi = inflater.inflate(R.layout.item, null);
        }

        ImageView image=(ImageView)vi.findViewById(R.id.image);
        imageLoader.DisplayImage(data[position], image, 70);
        return vi;
    }
}
package com.kooco.socialmatic.search;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.follow.SendMessageToFollowView;
import com.kooco.tool.Contents;
import com.kooco.tool.QRCodeEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchPeopleAdapter extends BaseAdapter{
	private Context mContext;
	// public static Map<String, JSONObject> mMap = null;
	public static ArrayList<JSONObject> mList = null;

	private LayoutInflater inflater = null;

	private LruCache<String, Bitmap> mMemoryCache;

	int mCellWidth = 0;

	int mType = 0;

	static class ViewHolder {

		ProgressBar progressbar;
		TextView textview;
		ImageView imageView;
		// int position;
	}

	public SearchPeopleAdapter(Context c) {
		mContext = c;

		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// mList = new ArrayList<JSONObject>(mMap.values());

		Point point = Config.getDisplaySize((Activity) mContext);
		mCellWidth = (int) (point.x / 6f);

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		final int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
			}
		};
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return mList.size();
		return 3;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.follow_item, null);

		LinearLayout layout = (LinearLayout) convertView
				.findViewById(R.id.item_linearlayout);
		ImageView bCImageview = (ImageView) convertView
				.findViewById(R.id.bc_imageview);
		ImageView userImageview = (ImageView) convertView
				.findViewById(R.id.user_imageview);

		TextView nameTextView = (TextView) convertView
				.findViewById(R.id.name_textview);
		TextView contentTextView = (TextView) convertView
				.findViewById(R.id.content_textview);

		Button mMessageBtn = (Button) convertView
				.findViewById(R.id.message_btn);
		Button mFollowBtn = (Button) convertView.findViewById(R.id.follow_btn);
		Button mAuthTypeBtn = (Button) convertView
				.findViewById(R.id.auth_type_btn);

		Typeface tf = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/31513_RCKWL.ttf");

		if (position == 0)
			userImageview.setBackground(mContext.getResources().getDrawable(
					R.drawable.account_photo_1));
		else if (position == 1)
			userImageview.setBackground(mContext.getResources().getDrawable(
					R.drawable.account_photo_2));
		else
			userImageview.setBackground(mContext.getResources().getDrawable(
					R.drawable.account_photo_3));

		if (position == 1)
			nameTextView.setText("Socialmatic LLC");
		else
			nameTextView.setText("TestAccount " + (position + 1));

		try {
			QRCodeEncoder qrCodeEncoder = null;

			qrCodeEncoder = new QRCodeEncoder(
					nameTextView.getText().toString(), null,
					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 115);

			bCImageview.setImageBitmap(qrCodeEncoder.encodeAsBitmap());

		} catch (WriterException e) {
			Log.e("EditPictureView", "Could not encode barcode", e);
		} catch (IllegalArgumentException e) {
			Log.e("EditPictureView", "Could not encode barcode", e);
		}

		nameTextView.setTypeface(tf);

		contentTextView.setText("Welcome!");
		contentTextView.setTypeface(tf);

		mMessageBtn.setText("Message");
		mMessageBtn.setTag(nameTextView.getText().toString());
		mMessageBtn.setOnClickListener(new messageBtnListener());

		mFollowBtn.setText("Unfollow");

		if (position == 1)
			mAuthTypeBtn.setText("Account Business");
		else
			mAuthTypeBtn.setText("Account Personal");


		return convertView;
	}

	private class messageBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String userName = v.getTag().toString();
			SendMessageToFollowView.mUserName = userName;

			Intent i = new Intent(mContext, SendMessageToFollowView.class);
			mContext.startActivity(i);
		}
	}

	// cache image & image optimize
	// ------------------------------------------------
	public Bitmap decodeSampledBitmapFromResource(String url, int reqWidth,
			int reqHeight) {

		Bitmap bitmap = null;
		InputStream is = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			is = (InputStream) new URL(url).getContent();
			is.mark(is.available());

			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(is, null, options);

			is = null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// First decode with inJustDecodeBounds=true to check dimensions

		// BitmapFactory.decodeResource(res, resId, options);

		try {
			is = (InputStream) new URL(url).getContent();
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(is, null, options);
			is.close();
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			try {

				mMemoryCache.put(key, bitmap);
			} catch (Exception ex) {

			}
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	// ------------------------------------------------

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) { // Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	class LoadImage extends AsyncTask<Object, Void, Bitmap> {

		private ImageView mImv;
		private String mpath;
		private ProgressBar mProgressBar;

		private LruCache<String, Bitmap> mMemoryCache;

		public LoadImage(ImageView imv, ProgressBar progressBar, String path) {
			this.mImv = imv;
			this.mpath = path;
			this.mProgressBar = progressBar;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {

			return decodeSampledBitmapFromResource(mpath, mCellWidth,
					mCellWidth);
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			if (result != null && mImv != null) {

				if (mImv.getTag().equals(mpath)) {
					addBitmapToMemoryCache(mpath, result);
					mImv.setVisibility(View.VISIBLE);
					mImv.setImageBitmap(result);
					mProgressBar.setVisibility(View.GONE);
				}
			} else {
				mImv.setVisibility(View.GONE);
			}
		}

	}
}

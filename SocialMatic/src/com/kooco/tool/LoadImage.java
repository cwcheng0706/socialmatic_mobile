package com.kooco.tool;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kooco.socialmatic.Config;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoadImage extends AsyncTask<Object, Void, Bitmap> {

	private ImageView mImv;
	private String mpath;
	private ProgressBar mProgressBar;
	private Activity mActivity;

	public static LruCache<String, Bitmap> mMemoryCache;
	
	Map<String, LoadImage> mMapImageLoader = new HashMap<String, LoadImage>();

	int mCellWidth = 0;

	public LoadImage(Activity activity, ImageView imv, ProgressBar progressBar,
			String path) {
		this.mImv = imv;
		this.mpath = path;
		this.mProgressBar = progressBar;

		mActivity = activity;

		Point point = Config.getDisplaySize(mActivity);
		// mCellWidth = (int)(point.x);
		mCellWidth = 400;

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		final int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
			}
		};
	}
	
	public void setMapList(Map<String, LoadImage> mapImageLoader)
	{
		mMapImageLoader = mapImageLoader;
	}

	// cache image & image optimize
	// ------------------------------------------------
	public Bitmap decodeSampledBitmapFromResource(String url, int reqWidth,
			int reqHeight) throws MalformedURLException {

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
		} catch (RuntimeException ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			try {

				mMemoryCache.put(key, bitmap);
			} catch (Exception ex) {

			}
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

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

	// ------------------------------------------------
	@Override
	protected Bitmap doInBackground(Object... params) {

		Bitmap bitmap = null;

		if (!isCancelled()) {
			try {

				if (getBitmapFromMemCache(mpath) != null)
					bitmap = getBitmapFromMemCache(mpath);
				else
				    bitmap = decodeSampledBitmapFromResource(mpath, mCellWidth,
						mCellWidth);
			}
			catch (Exception ex) {
				Toast.makeText(mActivity, "load image failure",
						Toast.LENGTH_SHORT).show();

			}
		}
		
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		
			
		if (result != null && mImv != null) {

			String[] arySplit = mImv.getTag().toString().split("<>");
			String strTag = arySplit[0].toString();

			if (strTag.equals(mpath)) {
				if (getBitmapFromMemCache(mpath) == null)
				    addBitmapToMemoryCache(mpath, result);
				
				mImv.setVisibility(View.VISIBLE);
				mImv.setImageBitmap(result);

				if (mProgressBar != null)
					mProgressBar.setVisibility(View.GONE);
			}
		} else {
			mImv.setVisibility(View.GONE);
		}
	}

}

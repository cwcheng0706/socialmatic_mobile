package com.kooco.socialmatic.flow;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.ShareButtonAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.configuration.ForgotPasswordDialog;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.offer.EventDetailView;
import com.kooco.socialmatic.offer.OfferDetailView;
import com.kooco.socialmatic.offer.OfferMapView;
import com.kooco.socialmatic.profile.SelectUserProfileView;
import com.kooco.socialmatic.search.SearchResultView;
import com.kooco.socialmatic.share.ShareAuthAdapter;
import com.kooco.socialmatic.share.ShareDialog;
import com.kooco.socialmatic.share.ShareResponseListener;
import com.kooco.tool.AndroidBmpUtil;
import com.kooco.tool.Contents;
import com.kooco.tool.QRCodeEncoder;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class FlowAdapter extends BaseAdapter implements
		WebManagerCallbackInterface {

	private Activity activity;
	private LayoutInflater mInflater = null;

	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();
	
	

	public ImageLoader imageLoader;
	
	ShareAuthAdapter adapter;
	ShareResponseListener mShareResponseListener;
	//private String mObjectType = "";
	//private String mObjectID;

	static class ViewHolder {
		LinearLayout mTagLinearLayout;
		LinearLayout mCitiesLinearLayout;
		LinearLayout mFlowLinearLayout;
		LinearLayout mOfferLinearLayout;
		LinearLayout mEventLinearLayout;
		ScrollView mTagsScrollView;
		ScrollView mCitiesScrollView;

		ScrollView mEventCitiesScrollView;
		LinearLayout mEventCitiesLinearLayout;

		TextView mAcceptedTextView;

		TextView mViewsTextView;
		TextView mAttendedUsersTextview;

		TextView mSubjectTextView;
		TextView photoNameTextview;
		TextView coolNumTextview;
		TextView seeNumTextview;
		TextView fitNumTextview;
		TextView milvesTextview;
		ImageView mTmpImageview;
		ImageView mapImageview;
		ImageView photoImage;
		ProgressBar photoProBar;
		ImageView userPhotoImageview;
		TextView shootDateTextView;
		Button mCoolBtn;
		Button mCommestsBtn;
		Button mPathBtn;
		Button mShareBtn;
		ImageView bCImageview;
	}

	public Button mCoolButton = null;

	public FlowAdapter(Activity a, ArrayList<JSONObject> d, ShareAuthAdapter sa, ShareResponseListener srl) {
		activity = a;
		data = d;
		adapter = sa;
		mShareResponseListener = srl;
		
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

		float d = activity.getResources().getDisplayMetrics().density;

		if (vi == null) {
			vi = mInflater.inflate(R.layout.flow_item, null);
			holder = new ViewHolder();

			holder.mTagLinearLayout = (LinearLayout) vi
					.findViewById(R.id.add_tags_layout);
			holder.mCitiesLinearLayout = (LinearLayout) vi
					.findViewById(R.id.add_city_layout);

			holder.mFlowLinearLayout = (LinearLayout) vi
					.findViewById(R.id.flow_linearlyout);
			holder.mOfferLinearLayout = (LinearLayout) vi
					.findViewById(R.id.offer_linearlyout);
			holder.mEventLinearLayout = (LinearLayout) vi
					.findViewById(R.id.event_linearlyout);

			holder.mEventCitiesLinearLayout = (LinearLayout) vi
					.findViewById(R.id.event_add_city_layout);

			holder.mEventCitiesScrollView = (ScrollView) vi
					.findViewById(R.id.event_cities_scrollview);
			holder.mAcceptedTextView = (TextView) vi
					.findViewById(R.id.accepted_textview);
			holder.mViewsTextView = (TextView) vi
					.findViewById(R.id.views_textview);
			holder.mAttendedUsersTextview = (TextView) vi
					.findViewById(R.id.attended_users_textview);
			
			holder.milvesTextview = (TextView) vi
					.findViewById(R.id.milves_textview);

			holder.mTagsScrollView = (ScrollView) vi
					.findViewById(R.id.tags_scrollview);
			holder.mCitiesScrollView = (ScrollView) vi
					.findViewById(R.id.cities_scrollview);

			holder.photoNameTextview = (TextView) vi
					.findViewById(R.id.photo_name_textview);
			holder.coolNumTextview = (TextView) vi
					.findViewById(R.id.cool_account_textview);
			holder.seeNumTextview = (TextView) vi
					.findViewById(R.id.see_account_textview);
			holder.fitNumTextview = (TextView) vi
					.findViewById(R.id.fit_account_textview);
			holder.mTmpImageview = (ImageView) vi
					.findViewById(R.id.tmp_imageview);
			holder.mapImageview = (ImageView) vi
					.findViewById(R.id.map_imageview);

			holder.photoImage = (ImageView) vi.findViewById(R.id.photo_image);
			holder.photoProBar = (ProgressBar) vi
					.findViewById(R.id.photo_progressbar);
			holder.userPhotoImageview = (ImageView) vi
					.findViewById(R.id.userphoto_imageview);

			holder.mSubjectTextView = (TextView) vi
					.findViewById(R.id.subject_textview);
			holder.shootDateTextView = (TextView) vi
					.findViewById(R.id.shot_date_textview);
			holder.bCImageview = (ImageView) vi.findViewById(R.id.bc_imageview);

			holder.mCommestsBtn = (Button) vi.findViewById(R.id.comment_btn);
			holder.mPathBtn = (Button) vi.findViewById(R.id.path_btn);
			holder.mCoolBtn = (Button) vi.findViewById(R.id.cool_btn);
			holder.mShareBtn = (Button) vi.findViewById(R.id.share_btn);

			vi.setTag(holder);

		} else {
			holder = (ViewHolder) vi.getTag();
		}
		
		

		Typeface tf = Typeface.createFromAsset(activity.getAssets(),
				"fonts/31513_RCKWL.ttf");

		// ----------------------------------
		holder.mTagsScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		holder.mCitiesScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		JSONParser parser_obj = new JSONParser();
		JSONArray arySee = new JSONArray();

		String objectType = data.get(position).get("object_type").toString();
		String objectID = data.get(position).get("object_id").toString();

		if (objectType.equals("classified")) {
			
			String acceptingUersCount = data.get(position).get("accepting_users").toString();
			holder.mAcceptedTextView.setText(acceptingUersCount);
			
			holder.mFlowLinearLayout.setVisibility(View.GONE);
			holder.mOfferLinearLayout.setVisibility(View.VISIBLE);
			holder.mEventLinearLayout.setVisibility(View.GONE);
		} else if (objectType.equals("event")) {
			
			String views = data.get(position).get("views").toString();
			String attendedUsersCount = data.get(position).get("attended_users").toString();
			holder.mViewsTextView.setText(views);
			holder.mAttendedUsersTextview.setText(attendedUsersCount);

			holder.mFlowLinearLayout.setVisibility(View.GONE);
			holder.mOfferLinearLayout.setVisibility(View.GONE);
			holder.mEventLinearLayout.setVisibility(View.VISIBLE);
		} else {
			holder.mFlowLinearLayout.setVisibility(View.VISIBLE);
			holder.mOfferLinearLayout.setVisibility(View.GONE);
			holder.mEventLinearLayout.setVisibility(View.GONE);
			
			String strMiles = data.get(position).get("photo_miles").toString();
			holder.milvesTextview.setText(strMiles);
		}

		try {
			if (data.get(position).get("photo_see") != null)
				arySee = (JSONArray) parser_obj.parse(data.get(position)
						.get("photo_see").toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			String strCity = "";

			String[] aryCity = null;
			if (data.get(position).get("upload_city") != null)
				strCity = data.get(position).get("upload_city").toString();

			if (!strCity.equals(""))
				aryCity = strCity.split(",");

			if (objectType.equals("event"))
				holder.mEventCitiesLinearLayout.removeAllViews();
			else
				holder.mCitiesLinearLayout.removeAllViews();

			if (aryCity != null) {
				LinearLayout cityAddLayout = null;

				for (int i = 0; i < aryCity.length; i++) {

					if (i % 3 == 0) {
						cityAddLayout = new LinearLayout(activity);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);

						params.setMargins(0, 0, 0, 5);
						cityAddLayout.setOrientation(LinearLayout.HORIZONTAL);
						cityAddLayout.setLayoutParams(params);
					}

					TextView tv = new TextView(activity);

					LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
							(int) (55 * d), (int) (20 * d));

					tvParams.setMargins(0, 0, 5, 0);
					tv.setLayoutParams(tvParams);
					tv.setTextSize(12);
					tv.setTextColor(Color.parseColor("#BFBFBF"));
					tv.setGravity(Gravity.CENTER);
					Drawable bgImg = activity.getResources().getDrawable(
							R.drawable.flow_tag_city);
					tv.setBackground(bgImg);

					tv.setTag(aryCity[i]);
					//tv.setOnClickListener(new LocationClickListener());
					tv.setOnClickListener(new searchBtnClickListener());
					
					if (aryCity[i].length() > 6)
						tv.setText(aryCity[i].substring(0, 5) + "...");
					else
						tv.setText(aryCity[i]);

					cityAddLayout.addView(tv);

					if (i % 3 == 2 || i == aryCity.length - 1) {
						if (objectType.equals("event"))
							holder.mEventCitiesLinearLayout
									.addView(cityAddLayout);
						else
							holder.mCitiesLinearLayout.addView(cityAddLayout);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {

			JSONArray aryTags = null;
			if (data.get(position).get("photo_tags") != null)
				aryTags = (JSONArray) parser_obj.parse(data.get(position)
						.get("photo_tags").toString());

			holder.mTagLinearLayout.removeAllViews();

			if (aryTags != null) {
				LinearLayout layout = null;

				for (int i = 0; i < aryTags.size(); i++) {
					
					if (aryTags.get(i).toString().equals(""))
						continue;
					
					String strTag = aryTags.get(i).toString();

					if (i % 3 == 0) {
						layout = new LinearLayout(activity);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);

						params.setMargins(0, 0, 0, 5);
						layout.setOrientation(LinearLayout.HORIZONTAL);
						layout.setLayoutParams(params);
					}

					TextView tv = new TextView(activity);

					LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
							(int) (55 * d), (int) (20 * d));

					tvParams.setMargins(0, 0, 5, 0);
					tv.setLayoutParams(tvParams);
					tv.setTextSize(12);
					tv.setTextColor(Color.parseColor("#BFBFBF"));
					tv.setGravity(Gravity.CENTER);
					Drawable bgImg = activity.getResources().getDrawable(
							R.drawable.flow_tag);
					tv.setBackground(bgImg);

					tv.setTag(strTag);
					tv.setOnClickListener(new searchBtnClickListener());

					if (strTag.length() > 6)
						tv.setText(strTag.substring(0, 5) + "...");
					else
						tv.setText(strTag);

					layout.addView(tv);

					if (i % 3 == 2 || i == aryTags.size() - 1)
						holder.mTagLinearLayout.addView(layout);
				}
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// ----------------------------------

		// ----------------------------------
		int photoId = Integer.parseInt(data.get(position).get("object_id")
				.toString());
		String photoUrl = data.get(position).get("photo_url").toString();
		
		String userPhotoUrl = data.get(position)
				.get("subject_photo_profile_url").toString();
		
		String username = data.get(position).get("subject_display_name")
				.toString();

		String userGender = "";

		try {
			userGender = data.get(position).get("subject_user_gender")
					.toString();
		} catch (Exception ex) {
			userGender = "B";
		}

		String strQrcode = data.get(position).get("qr_details").toString();
		
		String strShootDate = data.get(position).get("upload_date").toString();
		String strSubject = data.get(position).get("object_title").toString();

		String strCoolNum = data.get(position).get("object_cools").toString();

		String userID = data.get(position).get("subject_id").toString();
		String userCoolFlag = "-1";

		Log.d("data.get(position).get(\"user_cool\").toString()",
				"data.get(position).get(\"user_cool\").toString()-----"
						+ data.get(position).get("user_cool").toString());

		/*if (holder.mCoolBtn.getTag() == null) {
			userCoolFlag = Boolean.parseBoolean(data.get(position).get("user_cool").toString());
		} else {
			if (holder.mCoolBtn.getTag().toString().equals(""))
				userCoolFlag = Boolean.parseBoolean(data.get(position).get("user_cool").toString());
			else {
				
				String strTag = holder.mCoolBtn.getTag().toString();
				String[] aryTag = strTag.split("<>");
				if (aryTag[0].equals("Cool"))
					userCoolFlag = true;
				else
					userCoolFlag = false;
			}
		}
		*/
		
		/*if (holder.mCoolBtn.getTag() == null)
		{*/
		    userCoolFlag = data.get(position).get("user_cool").toString();
		/*}
		else
		{
			String strTag = holder.mCoolBtn.getTag().toString();
			
			Log.d("strSubject--------------", "strSubject---" + strSubject + "------strTag--->" + strTag
					                           + "----objectType---" + objectType + "-----objectID-----" + objectID);
			
			String[] aryTag = strTag.split("<>");
			
			Log.d("strSubject--------------", "aryTag[0]-----" + aryTag[0] + "----aryTag[1]---" + aryTag[1] + "---aryTag[2]----" + aryTag[2]);
			
			if (aryTag[0].equals(objectType) && aryTag[1].endsWith(objectID))
			    userCoolFlag = aryTag[2];
			else
				userCoolFlag = data.get(position).get("user_cool").toString();
		}*/
		

		if (userCoolFlag.equals("1")) {
			Drawable res = activity.getResources().getDrawable(
					R.drawable.button_orange_selector);
			holder.mCoolBtn.setBackground(res);
			holder.mCoolBtn.setText("Uncool");

		} else {
			Drawable res = activity.getResources().getDrawable(
					R.drawable.button_green_1_selector);
			holder.mCoolBtn.setBackground(res);
			holder.mCoolBtn.setText("Cool");
		}
		Object[] aryObjTag = {objectType + "<>" + objectID + "<>" + String.valueOf(position), holder.coolNumTextview};
		holder.mCoolBtn.setTag(aryObjTag);
		holder.mCoolBtn.setOnClickListener(new CoolBtnListener());
		
		int seeNum = arySee.size();
		// ----------------------------------

		holder.photoProBar.setVisibility(View.GONE);

		holder.coolNumTextview.setText(strCoolNum);
		holder.seeNumTextview.setText(String.valueOf(seeNum));

		// --------------------------------------
		holder.userPhotoImageview.setTag(userPhotoUrl + "<>" + userID);

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
				
			holder.userPhotoImageview.setImageDrawable(res);

		} else {

			imageLoader.DisplayImage(userPhotoUrl, holder.userPhotoImageview,
					40);
		}
		holder.userPhotoImageview.setOnClickListener(new UserInfoBtnListener());
		// --------------------------------------

		holder.mSubjectTextView.setText(strSubject);

		/*final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(strShootDate + "000"));

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		String strShootedDate = formatter.format(cal.getTime());
		*/
		String[] aryShootDate = strShootDate.split(" ");

		holder.shootDateTextView.setText("shooted on " + aryShootDate[0]);

		holder.mCommestsBtn.setTag(photoId);

		holder.mCommestsBtn.setOnClickListener(new CommentBtnClickListener());

		if (objectType.equals("classified")) {
			holder.mTmpImageview.setVisibility(View.VISIBLE);
			holder.mPathBtn.setVisibility(View.GONE);

			String offerID = data.get(position).get("object_id").toString();

			Drawable res = activity.getResources().getDrawable(
					R.drawable.account_photo_2);

			// --------------------------------------
			photoUrl = photoUrl.replace("https:", "http:");
			holder.photoImage.setTag(photoUrl + "<>" + offerID);
			imageLoader.DisplayImage(photoUrl, holder.photoImage, 200);
			holder.photoImage
					.setOnClickListener(new OfferDetailsBtnClickListener());
			// --------------------------------------

			holder.mSubjectTextView.setTag("<>" + offerID);
			holder.mSubjectTextView
					.setOnClickListener(new OfferDetailsBtnClickListener());

			holder.mapImageview.setImageDrawable(res);
			holder.mapImageview.setTag("<>" + offerID);
			holder.mapImageview
					.setOnClickListener(new OfferDetailsBtnClickListener());

		} else {
			holder.mTmpImageview.setVisibility(View.GONE);
			holder.mPathBtn.setVisibility(View.VISIBLE);

			if (objectType.equals("event")) {
				String eventID = data.get(position).get("object_id").toString();
				// --------------------------------------
				photoUrl = photoUrl.replace("https:", "http:");
				holder.photoImage.setTag(photoUrl + "<>" + eventID);
				imageLoader.DisplayImage(photoUrl, holder.photoImage, 200);
				// --------------------------------------
				holder.photoImage
						.setOnClickListener(new EventDetailsBtnClickListener());

				holder.mSubjectTextView.setTag("<>" + eventID);
				holder.mSubjectTextView
						.setOnClickListener(new EventDetailsBtnClickListener());

				holder.mPathBtn.setText("Detail");
				holder.mPathBtn.setTag("<>" + eventID);
				holder.mPathBtn
						.setOnClickListener(new EventDetailsBtnClickListener());
			} else {

				// --------------------------------------
				photoUrl = photoUrl.replace("https:", "http:");
				holder.photoImage.setTag(photoUrl + "<>" + data.get(position));
				imageLoader.DisplayImage(photoUrl, holder.photoImage, 200);
				// --------------------------------------
				holder.photoImage.setOnClickListener(new MapBtnClickListener());

				holder.mSubjectTextView.setTag("<>" + data.get(position));
				holder.mSubjectTextView
						.setOnClickListener(new MapBtnClickListener());
				holder.mPathBtn.setOnClickListener(new MapBtnClickListener());
				holder.mPathBtn.setTag("<>" + data.get(position));
			}

			Drawable res = activity.getResources().getDrawable(
					R.drawable.flow_google_pin);
			holder.mapImageview.setImageDrawable(res);
			holder.mapImageview.setTag("<>" + data.get(position));
			holder.mapImageview.setOnClickListener(new MapBtnClickListener());
		}

		holder.photoNameTextview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		holder.photoNameTextview.getPaint().setAntiAlias(true);
		holder.photoNameTextview.setText(username);
		holder.photoNameTextview.setTag("<>" + userID);
		holder.photoNameTextview.setOnClickListener(new UserInfoBtnListener());

		try {
			QRCodeEncoder qrCodeEncoder = null;

			Resources r = activity.getResources();
			float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
			
			qrCodeEncoder = new QRCodeEncoder(strQrcode, null,
					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), (int)size);

			
			Bitmap newBitmap = getResizedBitmap(qrCodeEncoder.encodeAsBitmap(), 120, 120);
			
			holder.bCImageview.setImageBitmap(newBitmap);

		} catch (WriterException e) {
			Log.e("FlowAdapter", "Could not encode barcode", e);
		} catch (IllegalArgumentException e) {
			Log.e("FlowAdapter", "Could not encode barcode", e);
		}
		
		holder.mShareBtn.setTag(photoUrl + "<>" + strSubject + "<>" + strQrcode);
		adapter.enable(holder.mShareBtn);
		
		return vi;
	}
	
	/*public static final int BUFFER_SIZE = 1024 * 8;
	 static void writeExternalToCache(Bitmap bitmap, File file) {
	    try {
	        file.createNewFile();
	        FileOutputStream fos = new FileOutputStream(file);
	        final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
	        bitmap.compress(CompressFormat.JPEG, 100, bos);
	        bos.flush();
	        bos.close();
	        fos.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {

	    }

	}*/
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}

	private class CoolBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			mCoolButton = (Button) v;
			
			Object[] aryObjTag = (Object[]) ((Button) v).getTag();
			
			/*String btnTag = ((Button) v).getTag().toString();
			 * 
			 */
			String btnTag = aryObjTag[0].toString();
			
			String[] aryTag = btnTag.split("<>");
			String btnText = ((Button) v).getText().toString();

			makeCool(aryTag[0], aryTag[1], btnText);

		}
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

	private class OfferDetailsBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			String[] arySplit = v.getTag().toString().split("<>");
			String strOfferID = arySplit[1].toString();

			OfferDetailView.mOfferId = Integer.parseInt(strOfferID);
			Intent i = new Intent(activity, OfferDetailView.class);
			activity.startActivity(i);
		}
	}

	private class LocationClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			OfferMapView.mSubject = v.getTag().toString();

			Intent i = new Intent(activity, OfferMapView.class);
			activity.startActivity(i);
		}
	}

	private class searchBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			//String urlEncoded = Uri.encode(v.getTag().toString());
			SearchResultView.mKeyWord = v.getTag().toString();
			
			Intent i = new Intent(activity, SearchResultView.class);
			activity.startActivity(i);
		}
	}

	private class CommentBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			FlowCommentsView.mPhotoId = Integer.parseInt(v.getTag().toString());
			Intent i = new Intent(activity, FlowCommentsView.class);
			activity.startActivity(i);
		}
	}

	private class MapBtnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			Object[] arySplit = v.getTag().toString().split("<>");
			String strMapData = arySplit[1].toString();

			JSONParser parser = new JSONParser();

			try {
				FlowMapView.mJSONObject = (JSONObject) parser.parse(strMapData);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Intent i = new Intent(activity, FlowMapView.class);
			activity.startActivity(i);
		}
	}

	private void makeCool(String objectType, String objectID, String doType) {
		String deviceId = UserDataAccess.getSystemUseDeviceId(activity);
		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(activity,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(activity,
				Config.userIdKey);

		String queryUrl = activity.getString(R.string.socialmatic_base_url);

		if (doType.equals("Cool"))
			queryUrl += activity.getString(R.string.make_cool_url);
		else
			queryUrl += activity.getString(R.string.make_uncool_url);


		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("objectType", objectType));
		nameValuePairs.add(new BasicNameValuePair("objectID", objectID));

		WebManager webManager = new WebManager(FlowAdapter.this, activity);

		webManager.setDoType(doType);

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub

		/*JSONParser parser = new JSONParser();
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject = (JSONObject) parser.parse(objects[0].toString());
			String response = jsonObject.get("numOfCool").toString();

			Drawable res;

			if (objects[1].toString().equals("Cool")) {
				res = activity.getResources().getDrawable(
						R.drawable.button_orange_selector);
				mCoolButton.setBackground(res);
				mCoolButton.setText("Uncool");
				mCoolButton.setTag("Cool");
			} else {
				res = activity.getResources().getDrawable(
						R.drawable.button_green_1_selector);
				mCoolButton.setBackground(res);
				mCoolButton.setText("Cool");
				mCoolButton.setTag("Uncool");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}*/

		Drawable res;

		if (objects[1].toString().equals("Cool")) {
			res = activity.getResources().getDrawable(
					R.drawable.button_orange_selector);
			
			mCoolButton.setBackground(res);
			mCoolButton.setText("Uncool");
			
			Object[] aryObjTag = (Object[]) (mCoolButton.getTag());
			String strTag = aryObjTag[0].toString();
			String[] aryTag = strTag.split("<>");
			
			int index = Integer.parseInt(aryTag[2]);
			int coolNum = Integer.parseInt(data.get(index).get("object_cools").toString());
			
			coolNum += 1;
			
			data.get(index).remove("user_cool");
			data.get(index).put("user_cool", 1);
			
			data.get(index).remove("object_cools");
			data.get(index).put("object_cools", coolNum);
			
			((TextView)aryObjTag[1]).setText(coolNum);
			
			
		} else {
			res = activity.getResources().getDrawable(
					R.drawable.button_green_1_selector);
			
			mCoolButton.setBackground(res);
			mCoolButton.setText("Cool");
			
			Object[] aryObjTag = (Object[]) (mCoolButton.getTag());
			String strTag = aryObjTag[0].toString();
			String[] aryTag = strTag.split("<>");
			
			int index = Integer.parseInt(aryTag[2]);
			int coolNum = Integer.parseInt(data.get(index).get("object_cools").toString());
			
			coolNum -= 1;
			
			coolNum = coolNum < 0? 0:coolNum;
			
			data.get(index).remove("user_cool");
			data.get(index).put("user_cool", 0);
			
			data.get(index).remove("object_cools");
			data.get(index).put("object_cools", coolNum);
			
		}

	}
}

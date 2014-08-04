package com.kooco.socialmatic.offer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.follow.FollowerAdapter;
import com.kooco.socialmatic.follow.SendMessageToFollowView;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.profile.SelectUserProfileView;
import com.kooco.socialmatic.search.SearchResultView;
import com.kooco.tool.Contents;
import com.kooco.tool.QRCodeEncoder;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class OfferAcceptsAdapter extends BaseAdapter implements
		WebManagerCallbackInterface {

	private Activity activity;
	// public static Map<String, JSONObject> mMap = null;
	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();
	private static LayoutInflater mInflater = null;

	public ImageLoader imageLoader;
	private String mObjectType = "";

	static class ViewHolder {
		LinearLayout mLinearLayout;
		TextView mAttendTypeTextView;
		ImageView mUserImageview;
		ImageView mBCImageview;
		TextView mNameTextview;
		TextView mContentTextView;
		Button mAuthTypeBtn;
		Button mFollowBtn;
		Button mMessageBtn;
	}

	public OfferAcceptsAdapter(Activity a, ArrayList<JSONObject> d,
			String objectType) {
		activity = a;
		data = d;
		mObjectType = objectType;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return mList.size();
		return data.size();
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

		System.gc();

		View vi = convertView;
		ViewHolder holder = null;

		if (vi == null) {
			vi = mInflater.inflate(R.layout.follow_item, null);
			holder = new ViewHolder();

			holder.mLinearLayout = (LinearLayout) vi
					.findViewById(R.id.item_linearlayout);

			holder.mAttendTypeTextView = (TextView) vi
					.findViewById(R.id.accept_type_textview);

			holder.mNameTextview = (TextView) vi
					.findViewById(R.id.name_textview);
			holder.mContentTextView = (TextView) vi
					.findViewById(R.id.content_textview);

			holder.mUserImageview = (ImageView) vi
					.findViewById(R.id.user_imageview);
			holder.mBCImageview = (ImageView) vi
					.findViewById(R.id.bc_imageview);
			holder.mAuthTypeBtn = (Button) vi.findViewById(R.id.auth_type_btn);
			holder.mFollowBtn = (Button) vi.findViewById(R.id.follow_btn);
			holder.mMessageBtn = (Button) vi.findViewById(R.id.message_btn);

			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		Typeface tf = Typeface.createFromAsset(activity.getAssets(),
				"fonts/31513_RCKWL.ttf");

		JSONObject userObj = new JSONObject();
		JSONParser parser = new JSONParser();

		if (mObjectType.equals("event")) {
			try {
				userObj = (JSONObject) parser.parse(data.get(position)
						.get("user").toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			userObj = data.get(position);
		}

		String userGender = "";

		String userID = userObj.get("user_id").toString();

		try {
			userGender = userObj.get("gender").toString();
		} catch (Exception ex) {
			userGender = "B";
		}

		String userPhotoUrl = "";

		userPhotoUrl = userObj.get("photo_profile_url").toString();

		String userType = "";

		userType = userObj.get("user_type").toString();

		String alreadyFollowed = "false";

		alreadyFollowed = data.get(position).get("already_followed").toString();

		String userName = userObj.get("display_name").toString();

		holder.mNameTextview.setText(userName);
		holder.mNameTextview.setTag("<>" + userID);
		holder.mNameTextview.setOnClickListener(new UserInfoBtnListener());

		String status = userObj.get("status").toString();

		holder.mContentTextView.setText(status);

		// --------------------------------------
		holder.mUserImageview.setTag(userPhotoUrl + "<>" + userID);

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

			holder.mUserImageview.setImageDrawable(res);

		} else {

			imageLoader.DisplayImage(userPhotoUrl, holder.mUserImageview, 40);
		}

		holder.mUserImageview.setOnClickListener(new UserInfoBtnListener());
		// --------------------------------------

		String qrCode = userObj.get("qr_details").toString();

		try {
			QRCodeEncoder qrCodeEncoder = null;

			int dimension = 130;
			float density = activity.getResources().getDisplayMetrics().density;

			qrCodeEncoder = new QRCodeEncoder(qrCode, null, Contents.Type.TEXT,
					BarcodeFormat.QR_CODE.toString(),
					(int) (dimension * (density / 1.5f)));

			Bitmap pq = qrCodeEncoder.encodeAsBitmap();
			pq = Bitmap.createScaledBitmap(pq, 135, 135, true);
			holder.mBCImageview.setImageBitmap(pq);

		} catch (WriterException e) {
			Log.e("FollowerAdapter", "Could not encode barcode", e);
		} catch (IllegalArgumentException e) {
			Log.e("FollowerAdapter", "Could not encode barcode", e);
		}

		if (userType.equals("Personal"))
			holder.mAuthTypeBtn.setText("Account Personal");
		else
			holder.mAuthTypeBtn.setText("Account Business");

		if (alreadyFollowed.equals("false"))
			holder.mFollowBtn.setText("Follow");
		else
			holder.mFollowBtn.setText("Unfollow");

		holder.mFollowBtn.setOnClickListener(new DealFollowBtnListener());

		holder.mFollowBtn.setTag(userID + "," + alreadyFollowed);

		String userId = UserDataAccess.getDataWithKeyAndEncrypt(activity,
				Config.userIdKey);

		holder.mMessageBtn.setText("Message");
		holder.mMessageBtn.setTag(holder.mNameTextview.getText().toString()
				+ "," + userID);
		holder.mMessageBtn.setOnClickListener(new messageBtnListener());

		if (userId.equals(userID)) {
			holder.mFollowBtn.setVisibility(View.GONE);
			holder.mMessageBtn.setVisibility(View.GONE);
		} else {
			holder.mFollowBtn.setVisibility(View.VISIBLE);
			holder.mMessageBtn.setVisibility(View.VISIBLE);
		}

		if (alreadyFollowed.equals("false"))
			holder.mMessageBtn.setVisibility(View.GONE);
		else
			holder.mMessageBtn.setVisibility(View.VISIBLE);

		if (mObjectType.equals("event")) {
			String attendType = data.get(position).get("rsvp").toString();

			Drawable greenBackRes = activity.getResources().getDrawable(
					R.drawable.event_status_green);
			Drawable grayBackRes = activity.getResources().getDrawable(
					R.drawable.event_status_gray);

			String showText = "";
			switch (Integer.parseInt(attendType)) {
			case 3:
				holder.mAttendTypeTextView.setBackground(grayBackRes);
				showText = "Awaiting Reply";
				break;
			case 2:
				holder.mAttendTypeTextView.setBackground(greenBackRes);
				showText = "Attending";
				break;
			case 1:
				holder.mAttendTypeTextView.setBackground(grayBackRes);
				showText = "Maybe Attending";
				break;
			}

			holder.mAttendTypeTextView.setText(showText);
		} else {
			holder.mAttendTypeTextView.setVisibility(View.GONE);

			Drawable backRes = activity.getResources().getDrawable(
					R.drawable.follow_list_frame);

			holder.mLinearLayout.setBackground(backRes);
		}
		return vi;
	}

	private void dealFollow(View v) {

		String tag = v.getTag().toString();
		String[] aryTag = tag.split(",");
		String dealUserID = aryTag[0];

		String deviceId = UserDataAccess.getSystemUseDeviceId(activity);

		String authToken = UserDataAccess.getDataWithKeyAndEncrypt(activity,
				Config.authTokenKey);
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(activity,
				Config.userIdKey);

		String queryUrl = activity.getString(R.string.socialmatic_base_url);

		if (aryTag[1].equals("false"))
			queryUrl += activity.getString(R.string.add_follow_url);
		else
			queryUrl += activity.getString(R.string.unfollow_url);

		Log.d("queryUrl---------", "queryUrl------------" + queryUrl
				+ "---------aryTag[1]---" + aryTag[1]);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceId));
		nameValuePairs.add(new BasicNameValuePair("userID", userId));
		nameValuePairs.add(new BasicNameValuePair("authtoken", authToken));

		if (aryTag[1].equals("false"))
			nameValuePairs.add(new BasicNameValuePair("userIDToFollow",
					dealUserID));
		else
			nameValuePairs.add(new BasicNameValuePair("userIDToUnfollow",
					dealUserID));

		WebManager webManager = new WebManager(OfferAcceptsAdapter.this,
				activity);

		webManager.setDoType("deal_follow");

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
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

	private class DealFollowBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			dealFollow(v);
		}
	}

	private class messageBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String tag = v.getTag().toString();
			String[] aryTag = tag.split(",");

			String userName = aryTag[0];
			SendMessageToFollowView.mUserName = userName;
			SendMessageToFollowView.mUserId = aryTag[1];

			Intent i = new Intent(activity, SendMessageToFollowView.class);
			activity.startActivity(i);
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (mObjectType.equals("event"))
			((OfferAcceptsView) activity).getEventData();
		else
			((OfferAcceptsView) activity).getOfferData();

		this.notifyDataSetChanged();
	}
}

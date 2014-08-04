package com.kooco.socialmatic.offer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.lazylist.ImageLoader;
import com.kooco.socialmatic.profile.SelectUserProfileView;
import com.kooco.socialmatic.search.SearchResultView;
import com.kooco.tool.Contents;
import com.kooco.tool.QRCodeEncoder;

public class OfferAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater = null;

	public ArrayList<JSONObject> data = new ArrayList<JSONObject>();

	public ImageLoader imageLoader;

	private String mShowType = "offer";

	static class ViewHolder {
		ImageView userPhotoImage;
		TextView photoNameTextview;
		TextView priceTextView;
		TextView currencyView;
		TextView locationView;
		TextView limitedView;
		TextView createDateTextView;
		ImageView photoImage;
		ProgressBar photoProBar;
		ImageView typeImageview;
		TextView subjectTextView;
		TextView userNameTextView;
		//Button mPathBtn;
		//Button acceptsBtn;
		//Button offerDatailsBtn;
		ImageView bCImageview;

	}

	public OfferAdapter(Activity a, ArrayList<JSONObject> d) {
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
			vi = mInflater.inflate(R.layout.offer_item, null);
			holder = new ViewHolder();

			holder.userPhotoImage = (ImageView) vi
					.findViewById(R.id.userphoto_imageview);
			holder.photoNameTextview = (TextView) vi
					.findViewById(R.id.photo_name_textview);
			holder.priceTextView = (TextView) vi
					.findViewById(R.id.price_textview);
			holder.currencyView = (TextView) vi
					.findViewById(R.id.currency_textview);
			holder.locationView = (TextView) vi
					.findViewById(R.id.location_textview);
			holder.limitedView = (TextView) vi
					.findViewById(R.id.limit_time_textview);
			holder.createDateTextView = (TextView) vi
					.findViewById(R.id.create_date_textview);

			holder.photoImage = (ImageView) vi.findViewById(R.id.photo_image);
			holder.photoProBar = (ProgressBar) vi
					.findViewById(R.id.photo_progressbar);

			holder.typeImageview = (ImageView) vi
					.findViewById(R.id.type_imageview);

			holder.subjectTextView = (TextView) vi
					.findViewById(R.id.subject_textview);

			holder.userNameTextView = (TextView) vi
					.findViewById(R.id.photo_name_textview);

			//holder.mPathBtn = (Button) vi.findViewById(R.id.path_btn);

			// holder.acceptsBtn = (Button)
			// vi.findViewById(R.id.accept_users_btn);

			holder.bCImageview = (ImageView) vi.findViewById(R.id.bc_imageview);

			/*holder.offerDatailsBtn = (Button) vi
					.findViewById(R.id.offer_details_btn);
            */
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		JSONParser parser = new JSONParser();

		JSONObject ownerObj = null;
		JSONObject offerObj = null;
		try {
			ownerObj = (JSONObject) parser.parse(data.get(position)
					.get("owner").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			offerObj = (JSONObject) parser.parse(data.get(position)
					.get("offer").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get data from web api
		// --------------------------------------------

		String offerID = "";

		if (mShowType.equals("searchResult"))
			offerID = offerObj.get("classified_id").toString();
		else
			offerID = offerObj.get("offer_id").toString();

		int offerId = Integer.parseInt(offerID);

		String title = offerObj.get("title").toString();
		String createDate = offerObj.get("creation_date").toString();
		String photoUrl = offerObj.get("photo_url").toString();
		String userName = ownerObj.get("display_name").toString();
		String userPhotoUrl = ownerObj.get("photo_profile_url").toString();
		String currency = offerObj.get("currency").toString();
		String location = offerObj.get("location").toString();
		String limited = offerObj.get("limited").toString();
		String userId = ownerObj.get("user_id").toString();
		String price = offerObj.get("price").toString();

		String qrCode = "";

		if (mShowType.equals("searchResult"))
			qrCode = offerObj.get("photo_url").toString();
		else
			qrCode = offerObj.get("qr_details").toString();

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(createDate + "000"));

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		String strCreateDate = formatter.format(cal.getTime());
		// --------------------------------------------

		String userGender = "";

		try {
			userGender = data.get(position).get("subject_user_gender")
					.toString();
		} catch (Exception ex) {
			userGender = "B";
		}

		holder.userPhotoImage.setTag(userPhotoUrl + "<>" + userId);

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
			
			holder.userPhotoImage.setImageDrawable(res);

		} else {
			imageLoader.DisplayImage(userPhotoUrl, holder.userPhotoImage, 40);
		}
		holder.userPhotoImage.setOnClickListener(new UserInfoBtnListener());

		holder.priceTextView.setText(price);
		holder.currencyView.setText(currency);

		holder.locationView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		holder.locationView.getPaint().setAntiAlias(true);
		holder.locationView.setText(location);

		holder.locationView.setTag(location);
		holder.locationView
				.setOnClickListener(new searchBtnClickListener());

		holder.limitedView.setText(limited);

		holder.createDateTextView.setText("created on " + strCreateDate);

		holder.photoProBar.setVisibility(View.GONE);

		// --------------------------------------
		photoUrl = photoUrl.replace("https:", "http:");
		holder.photoImage.setTag(photoUrl + "<>" + offerId);
		imageLoader.DisplayImage(photoUrl, holder.photoImage, 200);
		holder.photoImage
				.setOnClickListener(new OfferDetailsBtnClickListener());
		// --------------------------------------

		holder.userNameTextView.setText(userName);
		holder.userNameTextView.setTag("<>" + userId);
		holder.userNameTextView.setOnClickListener(new UserInfoBtnListener());

		holder.typeImageview.setTag("<>" + offerId);
		holder.typeImageview
				.setOnClickListener(new OfferDetailsBtnClickListener());

		holder.subjectTextView.setText(title);
		holder.subjectTextView.setTag("<>" + offerId);
		holder.subjectTextView
				.setOnClickListener(new OfferDetailsBtnClickListener());
		// holder.acceptsBtn.setOnClickListener(new AcceptsBtnClickListener());

		// photoImage.setOnClickListener(new OfferDetailsBtnClickListener());
		/*holder.offerDatailsBtn.setTag("<>" + offerId);
		holder.offerDatailsBtn
				.setOnClickListener(new OfferDetailsBtnClickListener());
        */
		try {
			QRCodeEncoder qrCodeEncoder = null;

			int dimension = 140;
			float density = activity.getResources().getDisplayMetrics().density;
			
			qrCodeEncoder = new QRCodeEncoder(qrCode, null, Contents.Type.TEXT,
					BarcodeFormat.QR_CODE.toString(), (int)(dimension * (density/1.5f)));

			holder.bCImageview.setImageBitmap(qrCodeEncoder.encodeAsBitmap());

		} catch (WriterException e) {
			Log.e("OfferAdapter", "Could not encode barcode", e);
		} catch (IllegalArgumentException e) {
			Log.e("OfferAdapter", "Could not encode barcode", e);
		}

		holder.photoNameTextview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		holder.photoNameTextview.getPaint().setAntiAlias(true);

		Typeface tf = Typeface.createFromAsset(activity.getAssets(),
				"fonts/31513_RCKWL.ttf");

		return vi;
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

	private class OfferLocationClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {

			OfferMapView.mSubject = v.getTag().toString();

			Intent i = new Intent(activity, OfferMapView.class);
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

}

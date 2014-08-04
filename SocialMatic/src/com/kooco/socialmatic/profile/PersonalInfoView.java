package com.kooco.socialmatic.profile;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.R;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.lazylist.ImageLoader;

public class PersonalInfoView extends SherlockFragmentActivity {
	private Button mBackBtn;
	private Button mConfirmBtn;

	private TextView mTitleLab;
	private RadioButton mOnleMeRadio;
	
	private ImageView mUserPhotoImageView;
	private TextView mUserNameLab;
	public ImageLoader imageLoader;
	
	private RadioButton mEveryoneRadio;

	private Spinner mLocationSpinner;
	private EditText mBirthdayText;

	static final int DATE_DIALOG_ID = 100;

	private int year;
	private int month;
	private int day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// hide status bar
		// ---------------------------------------------
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		setContentView(R.layout.personal_info_view);

		final Calendar calendar = Calendar.getInstance();

		year = calendar.get(Calendar.YEAR);

		month = calendar.get(Calendar.MONTH);

		day = calendar.get(Calendar.DAY_OF_MONTH);

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/31513_RCKWL.ttf");
		
		imageLoader = new ImageLoader(getApplicationContext());
		/*mUserPhotoImageView = (ImageView) findViewById(R.id.userphoto_imageview);
		mUserNameLab = (TextView) findViewById(R.id.username_textview);
		mUserNameLab.setTypeface(tf);
		*/

		mTitleLab = (TextView) findViewById(R.id.title_lab);
		mBackBtn = (Button) findViewById(R.id.back_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);

		mBirthdayText = (EditText) findViewById(R.id.birthday_text);
		mBirthdayText.setOnClickListener(new BirthDayTextListener());

		mLocationSpinner = (Spinner) findViewById(R.id.location_spinnner);
		// --------------------------------------------------
		ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "Rome",
						"Washington D.C.", "Taipei" });

		locationAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mLocationSpinner.setAdapter(locationAdapter);

		mLocationSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {

						/*
						 * Toast.makeText(MainActivity.this, "您選擇" +
						 * adapterView.getSelectedItem().toString(),
						 * Toast.LENGTH_LONG).show();
						 */
					}

					public void onNothingSelected(AdapterView arg0) {

						/*
						 * Toast.makeText(MainActivity.this, "您沒有選擇任何項目",
						 * Toast.LENGTH_LONG).show();
						 */
					}

				});
		// --------------------------------------------------

		mBackBtn.setOnClickListener(new BackBtnListener());
		mConfirmBtn.setOnClickListener(new ConfirmBtnListener());

		mTitleLab.setTypeface(tf);
		
		//createUserData();
	}
	
	private void createUserData() {
		String photoUrl = UserDataAccess.getDataWithKeyAndEncrypt(
				this, Config.photoKey);
		String gender = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.genderKey);
		String userName = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.nameKey);

		if (photoUrl.equals("")) {
			Drawable res;

			if (gender.equals("M"))
				res = getResources()
						.getDrawable(R.drawable.account_photo_3);
			else if (gender.equals("W"))
				res = getResources()
						.getDrawable(R.drawable.account_photo_1);
			else
				res = getResources()
						.getDrawable(R.drawable.account_photo_2);
			mUserPhotoImageView.setImageDrawable(res);

		} else {
			mUserPhotoImageView.setTag(photoUrl);

			imageLoader.DisplayImage(photoUrl, mUserPhotoImageView, 40);
		}

		mUserNameLab.setText("Hi! " + userName);
	}


	private class BirthDayTextListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			showDialog(DATE_DIALOG_ID);
		}
	}

	private class BackBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class ConfirmBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {

		case DATE_DIALOG_ID:

			// set date picker as current date

			return new DatePickerDialog(this, datePickerListener, year, month,
					day);

		}

		return null;

	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.

		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;

			month = selectedMonth;

			day = selectedDay;

			// set selected date into Text View

			mBirthdayText.setText(new StringBuilder()
					.append(String.format("%02d", month + 1))

					.append("-").append(String.format("%02d", day)).append("-")
					.append(year).append(" "));

			// set selected date into Date Picker

		}

	};

}

package com.kooco.socialmatic.camera;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CropImageView extends SherlockFragmentActivity {

	private static final int CROP_FROM_CAMERA = 1;

	private Uri mUri;

	public static boolean mCropFlag = true;

	private boolean mGoBackFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().hide();

		/*
		 * Intent intent = getIntent(); String strImageUri =
		 * intent.getStringExtra("ImageURI"); mUri = Uri.parse(strImageUri);
		 * 
		 * doCrop();
		 */
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mCropFlag) {
			mCropFlag = false;
			Intent intent = getIntent();
			String strImageUri = intent.getStringExtra("ImageURI");
			mUri = Uri.parse(strImageUri);

			doCrop();
		} else {
			if (mGoBackFlag)
				finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();

			if (extras != null) {

				mCropFlag = false;

				Bitmap photo = extras.getParcelable("data");

				EditPictureView.mImg = photo;
				/*
				 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
				 * photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
				 * byte[] byteArray = stream.toByteArray();
				 */

				mGoBackFlag = false;

				Intent i = new Intent(this, EditPictureView.class);

				// i.putExtra("ImageBitmap", byteArray);
				startActivity(i);
				/*
				 * Bitmap photo = extras.getParcelable("data");
				 * 
				 * mImageView.setImageBitmap(photo);
				 */
			}

			// File f = new File(mImageCaptureUri.getPath());

			// if (f.exists()) f.delete();

			break;
		}
	}

	private void doCrop() {
		mGoBackFlag = true;

		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		// mUri = Uri.parse(mImageUri);

		Intent intent = new Intent("com.android.camera.action.CROP");
		// intent
		// Intent intent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		// intent.set

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {

			intent.setData(mUri);
			// intent.putExtra("orientation", "portrait");

			intent.putExtra("outputX", 300);
			intent.putExtra("outputY", 300);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));
				// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mUri != null) {
							getContentResolver().delete(mUri, null, null);
							mUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}
}

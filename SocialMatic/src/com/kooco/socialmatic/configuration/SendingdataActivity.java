package com.kooco.socialmatic.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kooco.socialmatic.R;

public class SendingdataActivity extends Activity {
	/** Called when the activity is first created. */
	private BluetoothAdapter mBluetoothAdapter = null;
	static final UUID MY_UUID = UUID
			.fromString("7CA32A4C-5F35-4F86-BD3F-FD8D06921D4A");
	static String address = "00:07:AB:6C:74:7B";
	                     

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_bluetooth);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this,
					"Please enable your BT and re-run this program.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		final SendData sendData = new SendData();
		Button sendButton = (Button) findViewById(R.id.send);
		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				sendData.sendMessage();
			}
		});
	}

	class SendData extends Thread {
		private BluetoothDevice device = null;
		private BluetoothSocket btSocket = null;
		private OutputStream outStream = null;

		public SendData() {
			device = mBluetoothAdapter.getRemoteDevice(address);
			try {
				btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (Exception e) {
				// TODO: handle exception
			}
			mBluetoothAdapter.cancelDiscovery();
			try {
				btSocket.connect();
			} catch (IOException e) {
				try {
					btSocket.close();
				} catch (IOException e2) {
				}
			}
			Toast.makeText(getBaseContext(),
					"Connected to " + device.getName(), Toast.LENGTH_SHORT)
					.show();
			try {
				outStream = btSocket.getOutputStream();
			} catch (IOException e) {
			}
		}

		public void sendMessage() {
			try {
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				Bitmap bm = BitmapFactory.decodeResource(getResources(),
						R.drawable.icon);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the
																	// bitmap
																	// object
				byte[] b = baos.toByteArray();
				Toast.makeText(getBaseContext(), String.valueOf(b.length),
						Toast.LENGTH_SHORT).show();
				outStream.write(b);
				outStream.flush();
			} catch (IOException e) {
			}
		}
	}
}

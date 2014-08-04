package com.kooco.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.devsmart.android.IOUtils;
import com.kooco.socialmatic.MainActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PrinterAcceptService extends Service {
	/**
	 * 创建 Handler 对象，作为进程 传递 postDelayed 之用
	 */
	public static MainActivity mActivity;
	
	private Handler myhandler = new Handler();
	AcceptData acceptData = null;
	BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
	private static final UUID MY_UUID =
			 UUID.fromString("7CA32A4C-5F35-4F86-BD3F-FD8D06921D4A");
			 private byte[] buffer = new byte[8192];

	/**
	 * 为了确认系统服务运行情况
	 */
	private int intCounter = 0;

	/**
	 * 成员变量 myTasks为Runnable对象，作为Timer之用
	 */
	private Runnable myTasks = new Runnable() {
		/**
		 * 进程运行
		 */
		@Override
		public void run() {

			if (bluetooth == null){
				bluetooth = BluetoothAdapter.getDefaultAdapter();
				
			}

			if (bluetooth != null) {
				
				if (acceptData != null)
				{
				    acceptData = null;
				}
				
				String status;
				if (bluetooth.isEnabled()) {
					String mydeviceaddress = bluetooth.getAddress();
					String mydevicename = bluetooth.getName();
					status = mydevicename + ":" + mydeviceaddress;
				} else {
					status = "Bluetooth is not Enabled.";
				}

				Log.d("+++++++++++++++++++++++++++++++PrinterAcceptService...", status);
				
				acceptData = new AcceptData();
				acceptData.start();
				Bitmap bm1 = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
				Log.d("8888888888888888888", "888888888888888888888");
				Log.d("9999999999999999999", "buffer----->length-----------" + bm1);
				//mActivity.setImageFromBluetooth(bm1);
				//image.setImageBitmap(bm1);

			}

			myhandler.postDelayed(myTasks, 1000);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		myhandler.postDelayed(myTasks, 1000);
		super.onStart(intent, startId);
		Log.d("Start Service", "onStart");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("Create Service", "onCreate");
	}

	@Override
	public void onDestroy() {
		// 当服务结束，删除 mTasks 运行线程
		myhandler.removeCallbacks(myTasks);
		super.onDestroy();
		Log.d("Destroy Service.", "onDestroy");
	}
	
	   

	class AcceptData extends Thread {
		private final BluetoothServerSocket mmServerSocket;
		private BluetoothSocket socket = null;
		private InputStream mmInStream;
		private String device;

		public AcceptData() {
			BluetoothServerSocket tmp = null;
			try {
				Log.d("22222222222", "222222222222");
				tmp = bluetooth.listenUsingRfcommWithServiceRecord("Bluetooth",
						MY_UUID);
			} catch (IOException e) {
				//
				Log.d("3333333333311", "333333333333333");
			}
			mmServerSocket = tmp;
			try {
				socket = mmServerSocket.accept();
				Log.d("444444444444", "4444444444444");
			} catch (IOException e) {
				//
			}
			device = socket.getRemoteDevice().getName();
			Log.d("-----------------------------------------------" ,"Connected to " + device);
			InputStream tmpIn = null;
			try {
				tmpIn = socket.getInputStream();
			} catch (IOException e) {
				//
			}
			mmInStream = tmpIn;
			int byteNo;
			try {
				Log.d("555555555555.", "55555555555555");
				byteNo = mmInStream.read(buffer);
				if (byteNo != -1) {
					// ensure DATAMAXSIZE Byte is read.
					int byteNo2 = byteNo;
					int bufferSize = 7340;
					Log.d("777777777777", "7777777777777777");
					
					Log.d("1111111111111111111111+++++++++++++++++++++++++++++++PrinterAcceptService", "...");
					while (byteNo2 != bufferSize) {
						Log.d("0000000000000000000000", "000000000000000000");
						bufferSize = bufferSize - byteNo2;
						byteNo2 = mmInStream.read(buffer, byteNo, bufferSize);
						if (byteNo2 == -1) {
							break;
						}
						byteNo = byteNo + byteNo2;
					}
					Log.d("666666666666", "666666666666666");
				}
				if (socket != null) {
					try {
						mmServerSocket.close();
						bluetooth = null;
					} catch (IOException e) {
						//
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.kooco.socialmatic.qrcode;

import com.kooco.socialmatic.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Button;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import android.widget.TextView;
import android.graphics.ImageFormat;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class QRCodeReaderView extends Activity {
	private static Camera mCamera;
	private CameraPreview mPreview;
	private static Handler autoFocusHandler;

	TextView scanText;
	// Button scanButton;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.qrcode_reader_view);

		getActionBar().hide();

		// hide status bar
		// ---------------------------------------------
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ---------------------------------------------

		
        if (mCamera == null)
        {
		    autoFocusHandler = new Handler();
		    mCamera = getCameraInstance();
        }
        
		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
		
		scanText = (TextView) findViewById(R.id.scanText);
		
        
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
		FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(mPreview);
        
		/*
		 * scanButton = (Button)findViewById(R.id.ScanButton);
		 * 
		 * scanButton.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { if (barcodeScanned) { barcodeScanned = false;
		 * scanText.setText("Scanning...");
		 * mCamera.setPreviewCallback(previewCb); mCamera.startPreview();
		 * previewing = true; mCamera.autoFocus(autoFocusCB); } } });
		 */
	}
	
	public void onBackPressed()
	{
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
		
		if (autoFocusHandler != null)
		{
			autoFocusHandler.removeCallbacks(doAutoFocus);
			autoFocusHandler = null;
		}
		
		finish();
	}
	
	public void onPause() {
		super.onPause();
		releaseCamera();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			//mCamera.release();
			//mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				/*
				 * previewing = false; mCamera.setPreviewCallback(null);
				 * mCamera.stopPreview();
				 */
				SymbolSet syms = scanner.getResults();

				for (Symbol sym : syms) {

					String webUrl = getString(R.string.socialmatic_base_url);
					String scanString = sym.getData();

					if (scanString.contains(webUrl.substring(0, 22))) {
						previewing = false;
						mCamera.setPreviewCallback(null);
						mCamera.stopPreview();

						/*scanText.setText(".....barcode result "
								+ sym.getData().substring(0, 22));*/
						// scanText.setText("barcode result " + sym.getData());
						barcodeScanned = true;

						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(scanString));
						startActivity(browserIntent);
					}
				}
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
}

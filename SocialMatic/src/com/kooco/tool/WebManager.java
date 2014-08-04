package com.kooco.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.kooco.socialmatic.MainActivity;
import com.kooco.socialmatic.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;


public class WebManager extends AsyncTask<String, String, String> {

	private ProgressDialog progressDialog;
	protected Context applicationContext;

	private boolean mShowProDialogFlag = true;
	
	private String mDoType = "";

	@SuppressWarnings("unused")
	private boolean mSendIsJson = false;

	private List<NameValuePair> mQueryParamsList = null;
	
	private HttpEntity mHttpEntity = null;

	public interface WebManagerCallbackInterface {
		public void onRequestComplete(Object... objects);
	}

	private WebManagerCallbackInterface mCallback;

	public WebManager()
	{
		applicationContext = MainActivity.mActivity;
	}
	
	public WebManager(WebManagerCallbackInterface callback) {
		
		mCallback = callback;
		applicationContext = (Context) callback;
	}

	public WebManager(WebManagerCallbackInterface callback, boolean flag) {
		mCallback = callback;
		applicationContext = (Context) callback;
		mShowProDialogFlag = flag;
	}

	public WebManager(WebManagerCallbackInterface callback, Context context) {
		mCallback = callback;
		applicationContext = (Context) context;
	}

	public WebManager(WebManagerCallbackInterface callback, Context context,
			boolean flag) {
		mCallback = callback;
		applicationContext = (Context) context;
		mShowProDialogFlag = flag;
	}
	
	public void setDoType(String doType) {
		mDoType = doType;
	}

	public void setSendDataIsJson() {
		mSendIsJson = true;
	}

	public void setQueryParamList(List<NameValuePair> queryParamsList) {
		mQueryParamsList = queryParamsList;
	}
	
	public void setQueryEntity(HttpEntity entity)
	{
		mHttpEntity = entity;
	}

	public boolean isNetworkAvailable() {

		ConnectivityManager cm = (ConnectivityManager) applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;

		} else {
			showNetworkExeceptionAlert();
			return false;
		}
	}

	public String getJSONFromUrl(String url, String method) {
		InputStream is = null;
		String result = "";

		// JSONObject jsonObject = new JSONObject();
		try {
			//HttpClient httpclient = new DefaultHttpClient();
			HttpClient httpclient = WebSSLSocketFactory.createMyHttpClient();  // https

			HttpResponse response = null;

			if (method.equals("GET")) {
				response = httpclient.execute(new HttpGet(
						mQueryParamsList == null
								|| mQueryParamsList.size() == 0 ? url : url
								+ "?"
								+ URLEncodedUtils.format(mQueryParamsList,
										"utf-8")));
			} else if (method.equals("POST")) {
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httppost.setHeader("Accept", "application/image");
				
				httppost.setEntity(mHttpEntity);
				
				response = httpclient.execute(httppost);
			} 
			else if (method.equals("POST-JSON")) {
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httppost.setHeader("Accept", "application/json");		
				httppost.setEntity(mHttpEntity);
				
				response = httpclient.execute(httppost);
			} 
			else if (method.equals("DELETE")) {
				response = httpclient.execute(new HttpDelete(
						mQueryParamsList == null
								|| mQueryParamsList.size() == 0 ? url : url
								+ "?"
								+ URLEncodedUtils.format(mQueryParamsList,
										"utf-8")));
			}

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
		}

		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			Log.e("....................result", "result " + result.toString());
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}

		return result;
	}

	@Override
	protected void onPreExecute() {
		if (mShowProDialogFlag)
			this.progressDialog = ProgressDialog.show(applicationContext,
					null, "Retrieving data...", true);
	}

	@Override
	protected String doInBackground(String... params) {


		String url = params[0];
		String method = params[1];

		return getJSONFromUrl(url, method);
	}

	@Override
	protected void onPostExecute(String result) {

		// In here, call back to Activity or other listener that things are done
		if (mShowProDialogFlag)
			this.progressDialog.dismiss();

		try {
			/*JSONObject jsonObject = new JSONObject();
			JSONParser parser = new JSONParser();
			jsonObject = (JSONObject) parser.parse(result);
            */
			/*try {
				if (jsonObject.get("status").toString().equals("401")) {
					if (applicationContext != null) {
						// accessToken invalidate
						MemberInfoDeal.signOut((Context) mCallback);
						showAlert("error", "Your session has timeout. Please sign-in again");
						return;
					}
				}
			} catch (Exception e) {

			}*/
			if (mCallback != null && result != null) {
				mCallback.onRequestComplete(result, mDoType);
			}
		} catch (Exception e) {
			if (applicationContext != null)
				//showAlert("error", "Network connection error, Please try again latter");
				//showErrorResultAlert();
			return;
		}

	}
	
	private void showAlert(String title, String content)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(
				(Context) mCallback);
		builder.setTitle(title);
		builder.setMessage(content);
		builder.setPositiveButton("Exit", null);
		@SuppressWarnings("unused")
		AlertDialog dialog = builder.show();
	}

	private void showNetworkExeceptionAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				applicationContext);

		// Setting Dialog
		alertDialog.setTitle(R.string.internet_err_title);
		alertDialog.setMessage(R.string.internet_err_desc);
		alertDialog.setNegativeButton(R.string.internet_err_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}
}
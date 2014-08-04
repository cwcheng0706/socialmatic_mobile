package com.kooco.socialmatic.configuration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kooco.socialmatic.R;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class ForgotPasswordDialog extends Dialog implements
		WebManagerCallbackInterface {

	public static String mAccount = "";
	private EditText mAccountEditText;
	private Button mOkButton;
	private Button mCancelButton;
	private Context mContext;

	public ForgotPasswordDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		mContext = context;
		setContentView(R.layout.forgot_password_dialog);

		mAccountEditText = (EditText) findViewById(R.id.account_edittext);

		if (!mAccount.equals(""))
			mAccountEditText.setText(mAccount);

		mOkButton = (Button) findViewById(R.id.ok_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);

		mOkButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doRegetPassword();
			}
		});

		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ForgotPasswordDialog.this.dismiss();
				// MoreMemberForgotPassword.this.dismiss();
			}
		});
	}

	public void showAlertView(String title, String Content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setMessage(Content);
		builder.setPositiveButton("Exit", null);
		@SuppressWarnings("unused")
		AlertDialog dialog = builder.show();
	}

	private void doRegetPassword() {

		String account = mAccountEditText.getText().toString();

		if (account.equals("")) {
			showAlertView("Socialmatic", "Please input the email account!");
			return;
		}

		if (!RegisterView.isEmailValid(account)) {
			showAlertView("Socialmatic", "Account must email format.");

			return;
		}

		String queryUrl = mContext.getString(R.string.socialmatic_base_url)
				+ mContext.getString(R.string.forgot_psw_url);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("email", account));

		WebManager webManager = new WebManager(ForgotPasswordDialog.this,
				mContext);

		webManager.setDoType("forgot_psw");

		try {
			webManager.setQueryEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
		}

		if (webManager.isNetworkAvailable()) { // do web connect...
			webManager.execute(queryUrl, "POST");
		}
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub
		if (objects[1].toString().equals("forgot_psw")) {

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = new JSONObject();

			String errorMsg = "";
			try {
				jsonObject = (JSONObject) parser.parse(objects[0].toString());
				String response = jsonObject.get("Response").toString();
				if (response.equals("Request OK")) {
					showAlertView("Socialmatic",
							"Please obtain a password to open the email!");

					this.dismiss();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				jsonObject = (JSONObject) parser.parse(objects[0].toString());
				errorMsg = jsonObject.get("message").toString();
				showAlertView("Socialmatic", errorMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}

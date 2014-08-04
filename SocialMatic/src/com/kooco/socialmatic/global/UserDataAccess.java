package com.kooco.socialmatic.global;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.google.android.gcm.GCMRegistrar;
import com.kooco.socialmatic.Config;
import com.kooco.socialmatic.GCMIntentService;
import com.kooco.tool.AESEncryptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;


public class UserDataAccess {
	
	public static final String GSF_PACKAGE = "com.google.android.gsf";
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	public static String mGCMRegId = "";

	public static void setDataWithKey(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences("SocialMaticPreferences",
				0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getDataWithKey(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("SocialMaticPreferences",
				0);

		return sp.getString(key, "");
	}

	public static void setDataWithKeyAndEncrypt(Context context, String key,
			String value) {
		SharedPreferences sp = context.getSharedPreferences("SocialMaticPreferences",
				0);
		Editor editor = sp.edit();

		try {
			value = AESEncryptor.encrypt(Config.aesEncryptorSeed, value);
		} catch (Exception ex) {
			Log.d("encrypt", key + " encrypt fail!");
		}
		editor.putString(key, value);
		editor.commit();
	}

	public static String getDataWithKeyAndEncrypt(Context context, String key) {

		SharedPreferences sp = context.getSharedPreferences("SocialMaticPreferences",
				0);

		String value = sp.getString(key, "");

		try {
			value = AESEncryptor.decrypt(Config.aesEncryptorSeed, value);
		} catch (Exception ex) {

			value = "";
		}

		return value;
	}

	public static String getAndroidId(Context context) {
		String android_id = "";
		try {
			android_id = Settings.Secure.getString(
					context.getContentResolver(), Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
			Log.d("DEVICE", e.getMessage());
		}
		Log.d("DEVICE", android_id);
		return android_id;
	}
	
	public static String getSystemUseDeviceId(Context context)
	{
		String deviceId = getDeviceId(context);
		String newDeviceId = deviceId.replace("-", "").toUpperCase();
		
		int X1 = 0, X2 = 0, X3 = 0, X4 = 0;
		
		int count = 0;
		for (int i = 0; i < 32; i++)
		{
			int num = (int) newDeviceId.charAt(i);
			
			if (num < 65)
				num = Character.getNumericValue(newDeviceId.charAt(i));
			
			count += num;
			
		    if (i == 7)
		    {
		    	X1 = count % 9;
		    	count = 0;
		    }
		    else if (i == 15)
		    {
		    	X2 = count % 9;
		    	count = 0;
		    }
		    else if (i == 19)
		    {
		    	X3 = count % 9;
		    	count = 0;
		    }
		    else if (i == 31)
		    {
		    	X4 = count % 9;
		    	count = 0;
		    }
		}
		
		return deviceId.toUpperCase() + "-" + Integer.toString(X1) + Integer.toString(X2) 
				                               + Integer.toString(X3) + Integer.toString(X4);
	}

	public static String getDeviceId(Context context) {

		if (getDataWithKeyAndEncrypt(context, Config.deviceIdKey).equals("")) {

			String deviceId = UUID.randomUUID().toString();
			setDataWithKeyAndEncrypt(context, Config.deviceIdKey, deviceId);
			return deviceId;
		} else
			return getDataWithKeyAndEncrypt(context, Config.deviceIdKey);
	}

	public static void removeValueWithKey(Context context, String key) {
		SharedPreferences sp = 
				PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().remove(key).commit();
	}

	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.substring(8, 24).toString().toUpperCase();
	}
	
	public static void requestGCMRegId(Context context) {
		Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
		registrationIntent.setPackage(GSF_PACKAGE);

		GCMIntentService.mContext = context;
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		String regId = GCMRegistrar.getRegistrationId(context);

		if (regId.equals("")) {
			GCMRegistrar.register(context, GCMIntentService.senderId());
		} else {
			setDataWithKeyAndEncrypt(context, Config.GCMRegIdKey, regId);
			mGCMRegId = regId;
		}
	}

	public static void unRegisterGCMRegId(Context context) {
		Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
		registrationIntent.setPackage(GSF_PACKAGE);
		GCMIntentService.mContext = context;
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		String regId = GCMRegistrar.getRegistrationId(context);

		if (!regId.equals(""))
			GCMRegistrar.unregister(context);
	}
}

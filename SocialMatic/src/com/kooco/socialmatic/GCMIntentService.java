package com.kooco.socialmatic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.kooco.socialmatic.global.UserDataAccess;
import com.kooco.socialmatic.message.SendMessageToUser;
import com.kooco.socialmatic.profile.SelectUserProfileView;
import com.kooco.tool.WebManager;
import com.kooco.tool.WebManager.WebManagerCallbackInterface;

public class GCMIntentService extends GCMBaseIntentService implements
		WebManagerCallbackInterface {

	private static String senderId = "289093023916";
	public static Context mContext;

	public GCMIntentService() {
		super(senderId);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

		String action = arg1.getAction();

		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {

			Bundle bData = arg1.getExtras();

			String message = bData.getString("message");
			try {
				generateNotification(mContext, message);
			} catch (Exception e) {

			}
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	private void generateNotification(Context context, String message) {

		JSONParser parser = new JSONParser();
		JSONObject jsonObject;

		try {
			jsonObject = (JSONObject) parser.parse(message);
			JSONArray jsonArray = (JSONArray) jsonObject
					.get("notifications_payload");

			for (Object aryObj : jsonArray) {
				JSONArray jsonAry = (JSONArray) aryObj;

				for (Object jsonObj : jsonAry) {
					JSONObject jo = (JSONObject) jsonObj;

					String strNonotificationType = jo.get("notification_type")
							.toString();

					int time = (int) (System.currentTimeMillis());

					if (strNonotificationType.equals("friend_follow")) {

						String followingName = jo.get("display_name")
								.toString();
						Intent resultIntent = new Intent(context,
								SelectUserProfileView.class);
						resultIntent.putExtra("selectUserId",
								jo.get("object_id").toString());

						resultIntent.setAction("android.intent.action.MAIN");
						resultIntent
								.addCategory("android.intent.category.LAUNCHER");
						resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						resultIntent
								.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

						PendingIntent in = PendingIntent.getActivity(
								getApplicationContext(), time, resultIntent, 0);

						Notification notification = new Notification();

						notification.icon = R.drawable.ic_launcher;
						notification.tickerText = followingName
								+ " is now following you.";
						notification.defaults = Notification.DEFAULT_ALL;
						notification.setLatestEventInfo(mContext,
								"Socialmatic", followingName
										+ " is now following you.", in);

						mNotificationManager.notify(time, notification);

					} else if (strNonotificationType.equals("message_new")) {
						String followingName = jo.get("display_name")
								.toString();
						JSONObject unReadMsgNumObj = (JSONObject) parser
								.parse(jo.get("parameters").toString());
						String unReadMsgNum = unReadMsgNumObj.get("unread_msg")
								.toString();

						Intent resultIntent = new Intent(context,
								SendMessageToUser.class);
						resultIntent.putExtra("conversationId",
								jo.get("object_id").toString());

						resultIntent.setAction("android.intent.action.MAIN");
						resultIntent
								.addCategory("android.intent.category.LAUNCHER");
						resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						resultIntent
								.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

						PendingIntent in = PendingIntent.getActivity(
								getApplicationContext(), time, resultIntent, 0);

						Notification notification = new Notification();

						notification.icon = R.drawable.ic_launcher;
						notification.tickerText = followingName
								+ " has sent you (" + unReadMsgNum
								+ ") message.";
						notification.defaults = Notification.DEFAULT_ALL;
						notification.setLatestEventInfo(mContext,
								"Socialmatic", followingName
										+ " has sent you (" + unReadMsgNum
										+ ") message.", in);

						mNotificationManager.notify(time, notification);
					}

				}

			}

			doGetNotification();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doGetNotification() {
		String deviceId = UserDataAccess.getDeviceId(mContext);
		String deviceType = Config.deviceType;
		String userId = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.userIdKey);
		String authtoken = UserDataAccess.getDataWithKeyAndEncrypt(this,
				Config.authTokenKey);

		String queryUrl = mContext.getString(R.string.socialmatic_base_url)
				+ mContext.getString(R.string.get_messages_notifications_url);

		queryUrl += "?deviceID=" + deviceId + "&userID=" + userId
				+ "&authtoken=" + authtoken;

		WebManager webManager = new WebManager(this, false);
		if (webManager.isNetworkAvailable()) {
			// do web connect...
			webManager.execute(queryUrl, "GET");
		}
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		UserDataAccess.setDataWithKeyAndEncrypt(arg0, Config.GCMRegIdKey, arg1);
		UserDataAccess.mGCMRegId = arg1;

		/*
		 * MemberInfoDeal.mGCMRegId = arg1; Log.d("onRegistered", "mGCMRegId = "
		 * + MemberInfoDeal.mGCMRegId);
		 * 
		 * String deviceId = UserDataAccess.getDeviceId(mContext); String
		 * deviceType = Config.deviceType; String regId = arg1;
		 * 
		 * String signature = UserDataAccess.getMD5Str(deviceId + deviceType +
		 * regId);
		 * 
		 * List<NameValuePair> paramsList = new LinkedList<NameValuePair>(); //
		 * ArrayList<NameValuePair>(); paramsList.add(new
		 * BasicNameValuePair("deviceId", deviceId)); paramsList.add(new
		 * BasicNameValuePair("deviceType", deviceType)); paramsList.add(new
		 * BasicNameValuePair("regId", regId)); paramsList.add(new
		 * BasicNameValuePair("signature", signature));
		 * 
		 * // http query // --------------------------------------- String
		 * queryUrl = mContext.getString(R.string.gearq_base_url) +
		 * mContext.getString(R.string.gearq_device_link); WebManager webManager
		 * = new WebManager(this, false);
		 * 
		 * webManager.setQueryParamList(paramsList); if
		 * (webManager.isNetworkAvailable()) { // do web connect...
		 * webManager.execute(queryUrl, "POST"); }
		 */
		// ---------------------------------------
	}

	public final void onReceive(Context context, Intent intent) {
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d("onUnregistered", "Device registered: regId = " + arg1);
		// MemberInfoDeal.mGCMRegId = "";
	}

	public static String senderId() {
		// TODO Auto-generated method stub
		return senderId;
	}

	@Override
	public void onRequestComplete(Object... objects) {
		// TODO Auto-generated method stub

	}
}

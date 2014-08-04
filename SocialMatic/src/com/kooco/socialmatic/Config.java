package com.kooco.socialmatic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class Config {
	
	public static final String loginPreferenceFileName = "SocialMatic_MEMBER_PREFERENCE_FILE";

	public static final String SettingPreferenceFileName = "SocialMatic_SETTING_PREFERENCE_FILE";

	public static int DeviceType = 2;   // 1-> IOS, 2->Android, 3->Socialmatic Camera
	// login
	// ------------------------------------
	public static final String aesEncryptorSeed = "SocialMatic_ENCRYPTOR_SEED";
	public static final String accessTokenKey = "SocialMatic_MEMBER_ACCESSTOKEN";
	public static final String uidKey = "SocialMatic_MEMBER_UID";
	public static final String nameKey = "SocialMatic_MEMBER_NAME";
	public static final String genderKey = "SocialMatic_MEMBER_GENDER";
	public static final String photoKey = "SocialMatic_MEMBER_PHOTO";

	public static final String pushNotificationKey = "SocialMatic_MEMBER_PushNotification";
	public static final String promoemailsKey = "SocialMatic_MEMBER_PromotionalEmails";
	
	public static final String GCMRegIdKey = "SocialMatic_USER_RegId";
	// ------------------------------------

	public static final String deviceIdKey = "SocialMatic_USER_DeviceId";
	public static final String deviceType = "2";
	
	public static final String isLoginKey = "SocialMatic_isLogin";
	public static final String userIdKey = "SocialMatic_userId";
	public static final String authTokenKey = "SocialMatic_AuthToken";
	public static final String accountnKey = "SocialMatic_Account";
	public static final String passwordKey = "SocialMatic_Password";
	
	
    static int mPhotoMinX = 102;
    static int mPhotoMaxX = 698;
    static int mPhotoMinY = 0;
    static int mPhotoMaxY = 440;
    
    public static Point getScreenSize(Activity activity)
    {
    	Display display = activity.getWindowManager().getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	return size;
    }
    
	public static Point getDisplaySize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		
	    final Point point = new Point();
	    try {
	        display.getSize(point);
	    } catch (java.lang.NoSuchMethodError ignore) { // Older device
	        point.x = display.getWidth();
	        point.y = display.getHeight();
	    }
	    return point;
	}
	
	public static float getDisplayDensity()
	{
		
		return MainActivity.mActivity.getResources().getDisplayMetrics().density;
	}
}

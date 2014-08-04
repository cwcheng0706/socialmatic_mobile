package com.kooco.tool;
import android.app.Application;

import com.testflightapp.lib.TestFlight;

public class TestFlightApplication extends Application {
	@Override
    public void onCreate() {
        super.onCreate();
        //Initialize TestFlight with your app token.
        
        String token1 = "4247b74f-8647-43e2-a860-d31b2efbfd8e";  // social matic
        String token2 = "e4108390-6616-4b02-b9bb-3c479f9d8a2e";  // kooco
        
        TestFlight.takeOff(this, token1);
    }

}

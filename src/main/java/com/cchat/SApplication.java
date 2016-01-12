
package com.cchat;

import android.app.Application;

public class SApplication extends Application {
    private static final String TAG = SApplication.class.getSimpleName();

    public SApplication() {
    }

    public void onCreate() {
        super.onCreate();
//        SLogger.f(TAG, "SApplication:onCreate");
    }

    public void onLowMemory() {
        super.onLowMemory();
//        SLogger.f(TAG, "SApplication:onLowMemory");
    }

    public void onTerminate() {
        super.onTerminate();
//        SLogger.f(TAG, "SApplication:onTerminate");
    }
}

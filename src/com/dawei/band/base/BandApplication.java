package com.dawei.band.base;

import android.app.Application;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;

import com.microsoft.band.BandClient;

/**
 * Created by qizhenghao on 16/6/23.
 */
public class BandApplication extends Application {

    public static BandApplication INSTANCE;
    public static BandClient client;
    private static BandApplication mContext;
    private static Handler mApplicationHandler;
    private Typeface tfLight;

    @Override
    public void onCreate() {
        INSTANCE = this;
        mContext = this;
        super.onCreate();
    }

    public static BandApplication getContext() {
        return mContext;
    }

    public static Handler getApplicationHandler() {
        if (mApplicationHandler == null) {
            mApplicationHandler = new Handler(Looper.getMainLooper());
        }
        return mApplicationHandler;
    }

    public Typeface getTfLight() {
        if (tfLight == null)
            tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        return tfLight;
    }
}

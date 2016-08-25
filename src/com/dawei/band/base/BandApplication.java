package com.dawei.band.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.microsoft.band.BandClient;

/**
 * Created by qizhenghao on 16/6/23.
 */
public class BandApplication extends Application {

    public static BandApplication INSTANCE;
    private static BandApplication mContext;
    private static Handler mApplicationHandler;

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
}

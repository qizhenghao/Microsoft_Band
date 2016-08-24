package com.dawei.band.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.microsoft.band.BandClient;

/**
 * Created by qizhenghao on 16/6/23.
 */
public class BandApplication extends Application {

    private static BandApplication mContext;
    private static Handler mApplicationHandler;

    private BandClient client;

    @Override
    public void onCreate() {
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

    public BandClient getClient() {
        return client;
    }

    public void setClient(BandClient client) {
        this.client = client;
    }


}

package com.dawei.band.skinSignal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawei.band.R;
import com.dawei.band.base.BandApplication;
import com.dawei.band.base.BaseFragment;
import com.github.mikephil.charting.data.Entry;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.SampleRate;

/**
 * Created by qizhenghao on 16/8/26.
 */
public class SkinSignalFragment extends BaseFragment {

    private static final int TEMP_REFRESH_TIME = 1000;
    private static final int RESIST_REFRESH_TIME = 200;

    private static final int LENGTH = 300;

    private BandClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_angular_velocity_layout, null);
        return mContentView;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        client = BandApplication.client;

    }



    @Override
    protected void initListener() {

    }

    @Override
    public void refresh() {

    }

}

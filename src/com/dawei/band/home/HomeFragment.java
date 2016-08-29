package com.dawei.band.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawei.band.R;
import com.dawei.band.base.BandApplication;
import com.dawei.band.base.BaseFragment;
import com.microsoft.band.BandClient;

/**
 * Created by qizhenghao on 16/8/26.
 */
public class HomeFragment extends BaseFragment {

    private static final int TEMP_REFRESH_TIME = 1000;
    private static final int RESIST_REFRESH_TIME = 200;

    private static final int LENGTH = 300;

    private BandClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home_layout, null);
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

package com.dawei.band.heartRate.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawei.band.R;
import com.dawei.band.base.BaseFragment;

/**
 * Created by qizhenghao on 16/8/24.
 */
public class HeartRateFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_heart_rate_layout, null);
        return mContentView;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void refresh() {

    }
}

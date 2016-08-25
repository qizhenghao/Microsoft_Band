package com.dawei.band.accelerate;

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
 * Created by qizhenghao on 16/8/25.
 */
public class AccelerateFragment extends BaseFragment {

    private BandClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_accelerate_layout, null);
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

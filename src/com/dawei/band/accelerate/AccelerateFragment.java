package com.dawei.band.accelerate;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawei.band.R;
import com.dawei.band.base.BandApplication;
import com.dawei.band.base.BaseFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qizhenghao on 16/8/25.
 */
public class AccelerateFragment extends BaseFragment {

    private static final int REFRESH_TIME = 200;
    private static final int LENGTH = 300;

    private BandClient client;
    private BarChart xChart;
    private BarChart yChart;
    private BarChart zChart;
    private List<BarEntry> xEntryList;
    private List<BarEntry> yEntryList;
    private List<BarEntry> zEntryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_accelerate_layout, null);
        return mContentView;
    }

    @Override
    protected void initView() {
        xChart = (BarChart) mContentView.findViewById(R.id.fragment_accelerate_x_barchar);
        yChart = (BarChart) mContentView.findViewById(R.id.fragment_accelerate_y_barchar);
        zChart = (BarChart) mContentView.findViewById(R.id.fragment_accelerate_z_barchar);
    }

    @Override
    protected void initData() {
        client = BandApplication.client;
        initCharts(xChart);
        initCharts(yChart);
        initCharts(zChart);
        xEntryList = new ArrayList<>();
        yEntryList = new ArrayList<>();
        zEntryList = new ArrayList<>();
        for (int i = 0; i < LENGTH; i++) {
            BarEntry entry = new BarEntry(i, 0);
            xEntryList.add(entry);
            yEntryList.add(entry);
            zEntryList.add(entry);
        }

        setData(xChart, xEntryList);
        setData(yChart, yEntryList);
        setData(zChart, zEntryList);
        if (client == null)
            return;
        try {
            client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
        } catch (BandException e) {
            e.printStackTrace();
        }
    }

    private void initCharts(BarChart mChart) {
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        leftAxis.setLabelCount(6, false);
//        leftAxis.setAxisMinimum(-2.5f);
//        leftAxis.setAxisMaximum(2.5f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(0.1f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        rightAxis.setLabelCount(6, false);
//        rightAxis.setAxisMinimum(-2.5f);
//        rightAxis.setAxisMaximum(2.5f);
        rightAxis.setGranularity(0.1f);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        mChart.animateXY(2000, 2000);
    }

    private void setData(BarChart mChart, List<BarEntry> entries) {
        BarDataSet set;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set.setValues(entries);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(entries, "Sinus Function");
            set.setColor(Color.rgb(240, 120, 124));
        }

        BarData data = new BarData(set);
        data.setValueTextSize(10f);
        data.setValueTypeface(BandApplication.INSTANCE.getTfLight());
        data.setDrawValues(false);
        data.setBarWidth(0.8f);

        mChart.setData(data);
        mChart.invalidate();
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void refresh() {

    }

    private long lastRefreshTime;
    /**
     * 加速度
     */
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            if (event != null) {
//                appendToUI(String.format(" 加速度:\n X = %.6f \n Y = %.6f\n Z = %.6f\n", event.getAccelerationX(),
//                        event.getAccelerationY(), event.getAccelerationZ()));
//                SendSensorData(String.format("a %.6f %.6f %.6f", event.getAccelerationX(),
//                        event.getAccelerationY(), event.getAccelerationZ()));
                if (System.currentTimeMillis() - lastRefreshTime > REFRESH_TIME) {
                    lastRefreshTime = System.currentTimeMillis();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            xEntryList.add(new BarEntry(xEntryList.size(), event.getAccelerationX()));
                            setData(xChart, xEntryList.subList(xEntryList.size() - 60, xEntryList.size()));
                            yEntryList.add(new BarEntry(yEntryList.size(), event.getAccelerationY()));
                            setData(yChart, yEntryList.subList(yEntryList.size() - 60, yEntryList.size()));
                            zEntryList.add(new BarEntry(zEntryList.size(), event.getAccelerationZ()));
                            setData(zChart, zEntryList.subList(zEntryList.size() - 60, zEntryList.size()));
                        }
                    });
                }
            }
        }
    };


    @Override
    public void onDestroy() {
        if (client != null) {
            try {
                client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
            } catch (BandIOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}

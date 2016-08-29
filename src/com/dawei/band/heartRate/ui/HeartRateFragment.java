package com.dawei.band.heartRate.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawei.band.R;
import com.dawei.band.base.BandApplication;
import com.dawei.band.base.BaseFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.GsrSampleRate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qizhenghao on 16/8/24.
 */
public class HeartRateFragment extends BaseFragment {

    private static final int HEART_REFRESH_TIME = 1000;
    private static final int TEMP_REFRESH_TIME = 1000;
    private static final int RESIST_REFRESH_TIME = 200;

    private static final int HEART_LENGTH = 60;
    private static final int TEMPRA_LENGTH = 60;
    private static final int RESIST_LENGTH = 300;

    private BandClient client;
    private LineChart heartRateChart;//心率
    private LineChart temperatureChart;//温度
    private LineChart resistanceChart;//电阻


    private long lastHeartRefreshTime;
    private List<Entry> heartRateList;
    private List<Entry> tempratureList;
    private List<Entry> resistanceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_heart_rate_layout, null);
        return mContentView;
    }

    @Override
    protected void initView() {
        heartRateChart = (LineChart) mContentView.findViewById(R.id.fragment_heart_rate_line_chart);
        temperatureChart = (LineChart) mContentView.findViewById(R.id.fragment_heart_rate_temperature_chart);
        resistanceChart = (LineChart) mContentView.findViewById(R.id.fragment_heart_rate_resistance_chart);
        initHeartRateChart(heartRateChart);
        initTemperatureChart(temperatureChart);
        initResistanceChart(resistanceChart);
    }

    private void initHeartRateChart(LineChart mChart) {
        mChart.setViewPortOffsets(0, 0, 0, 0);
        mChart.setBackgroundColor(getResources().getColor(R.color.gold_pressed));
        mChart.setDescription("Heart Rate");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.setMaxHighlightDistance(300);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis y = mChart.getAxisLeft();
        y.setTypeface(BandApplication.INSTANCE.getTfLight());
        y.setLabelCount(6, false);
        y.setTextColor(Color.RED);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        y.setLabelCount(6, false);
        y.setTextColor(Color.RED);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        mChart.getLegend().setEnabled(false);
        mChart.animateXY(2000, 2000);
    }

    private void initTemperatureChart(LineChart mChart) {
//        mChart.setOnChartValueSelectedListener(this);
        mChart.setDescription("Skin Temperature");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(getResources().getColor(R.color.blue_light_pressed));
        mChart.animateX(2500);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(BandApplication.INSTANCE.getTfLight());
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        mChart.getXAxis().setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        leftAxis.setTextColor(Color.RED);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        rightAxis.setTextColor(Color.RED);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }

    private void initResistanceChart(LineChart mChart) {
        //        mChart.setOnChartValueSelectedListener(this);
        mChart.setDescription("Skin Resistance");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(getResources().getColor(R.color.green_pressed));
        mChart.animateX(2500);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(BandApplication.INSTANCE.getTfLight());
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        mChart.getXAxis().setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        leftAxis.setTextColor(Color.RED);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(BandApplication.INSTANCE.getTfLight());
        rightAxis.setTextColor(Color.RED);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }

    @Override
    protected void initData() {
        client = BandApplication.client;
        heartRateList = new ArrayList<>();
        tempratureList = new ArrayList<>();
        resistanceList = new ArrayList<>();
        for (int i = 0; i < HEART_LENGTH; i++) {
            Entry entry = new Entry(i, 70);
            heartRateList.add(entry);
        }
        for (int i = 0; i < TEMPRA_LENGTH; i++) {
            Entry entry = new Entry(i, 37.5f);
            tempratureList.add(entry);
        }
        for (int i = 0; i < RESIST_LENGTH; i++) {
            Entry entry = new Entry(i, 0);
            resistanceList.add(entry);
        }

        // add data
        setData(heartRateChart, heartRateList);
        setData(temperatureChart, tempratureList);
        setData(resistanceChart, resistanceList);
    }

    @Override
    protected void initListener() {
        client = BandApplication.client;
        if (client == null)
            return;
        try {
            client.getSensorManager().registerSkinTemperatureEventListener(skinTemperatureEventListener);
            client.getSensorManager().registerGsrEventListener(gsrEventListener, GsrSampleRate.MS200);
            client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
        } catch (InvalidBandVersionException e) {
            e.printStackTrace();
        } catch (BandIOException e) {
            e.printStackTrace();
        } catch (BandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {

    }

    /**
     * 心率
     */
    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
                if (System.currentTimeMillis() - lastHeartRefreshTime > HEART_REFRESH_TIME) {
                    lastHeartRefreshTime = System.currentTimeMillis();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            heartRateList.add(new Entry(heartRateList.size(), event.getHeartRate()));
                            setData(heartRateChart, heartRateList.subList(heartRateList.size() - HEART_LENGTH, heartRateList.size()));
                        }
                    });
                }
            }
        }
    };

    private long lastTempRefreshTime;
    private BandSkinTemperatureEventListener skinTemperatureEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(final BandSkinTemperatureEvent bandSkinTemperatureEvent) {
            if (System.currentTimeMillis() - lastTempRefreshTime > TEMP_REFRESH_TIME) {
                lastTempRefreshTime = System.currentTimeMillis();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tempratureList.add(new Entry(tempratureList.size(), bandSkinTemperatureEvent.getTemperature()));
                        setData(temperatureChart, tempratureList.subList(tempratureList.size() - TEMPRA_LENGTH, tempratureList.size()));
                    }
                });
            }
        }
    };

    private long lastResistRefreshTime;
    private BandGsrEventListener gsrEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(final BandGsrEvent bandGsrEvent) {
            if (System.currentTimeMillis() - lastResistRefreshTime > RESIST_REFRESH_TIME) {
                lastResistRefreshTime = System.currentTimeMillis();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resistanceList.add(new Entry(resistanceList.size(), bandGsrEvent.getResistance()));
                        setData(resistanceChart, resistanceList.subList(resistanceList.size() - RESIST_LENGTH, resistanceList.size()));
                    }
                });
            }
        }
    };

    private void setData(LineChart chart, List<Entry> yVals) {
        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new FillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTypeface(BandApplication.INSTANCE.getTfLight());
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
        chart.invalidate();
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            try {
                client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
                client.getSensorManager().unregisterSkinTemperatureEventListener(skinTemperatureEventListener);
                client.getSensorManager().unregisterGsrEventListener(gsrEventListener);
            } catch (BandIOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}

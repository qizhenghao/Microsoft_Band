package com.dawei.band.welcome;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dawei.band.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.SampleRate;

public class WelcomeActivity extends Activity {

    private BandClient client = null;
    private Button btnStart;
    private TextView txtStatus;
    private TextView txtStatusGyr;
    private TextView txtStatusAlti;
    private TextView txtStatusHeartRate;
    private DatagramSocket datasocket;

    private EditText txtIpInpuText;

    private String ServerIP = "192.168.1.70";
    private int SeverPort = 8888;
    static int num = 0;
    private LineChart mChart;
    private Typeface mTfLight;

    public WelcomeActivity(){
        try {
            datasocket = new DatagramSocket(10000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * UDP发送数据
     * @param str
     */
    public void SendSensorData(String str) {
        try {
            DatagramPacket packet = new DatagramPacket(str.getBytes(), str.getBytes().length, new InetSocketAddress(ServerIP, SeverPort));
            datasocket.send(packet);
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加速度
     */
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            if (event != null) {
                appendToUI(String.format(" 加速度:\n X = %.6f \n Y = %.6f\n Z = %.6f\n", event.getAccelerationX(),
                        event.getAccelerationY(), event.getAccelerationZ()));
                SendSensorData(String.format("a %.6f %.6f %.6f", event.getAccelerationX(),
                        event.getAccelerationY(), event.getAccelerationZ()));
            }
        }
    };

    /**
     * 角速度
     */
    private BandGyroscopeEventListener mGyroscopeEventListener = new BandGyroscopeEventListener(){
        @Override
        public void onBandGyroscopeChanged(final BandGyroscopeEvent event) {

            if (event != null) {
                appendToUI(String.format(" 角速度：\n X = %.6f \n Y = %.6f\n Z = %.6f\n", event.getAngularVelocityX(),
                        event.getAngularVelocityY(), event.getAngularVelocityZ()), txtStatusGyr);
                SendSensorData(String.format("g %.6f %.6f %.6f", event.getAngularVelocityX(),
                        event.getAngularVelocityY(), event.getAngularVelocityZ()));
            }
        }
    };
    /**
     * 高度信息
     */
    private BandAltimeterEventListener mAltimeterEventListener = new BandAltimeterEventListener(){
        @Override
        public void onBandAltimeterChanged(final BandAltimeterEvent event) {
            // TODO Auto-generated method stub
            if (event != null) {
                appendToUI(new StringBuilder().append(String.format(" 高度信息：\n Total Gain = %d cm\n", event.getTotalGain()))
                        .append(String.format(" Total Loss = %d cm\n", event.getTotalLoss())).toString(), txtStatusAlti);

                SendSensorData(new StringBuilder().append(String.format("h %d ", event.getTotalGain()))
                        .append(String.format("%d", event.getTotalLoss())).toString());
            }
        }
    };

    static List<Entry> heartRateList = new ArrayList<>();
    static {
        for (int i=0;i<60;i++) {
            Entry entry = new Entry(i, 70);
            heartRateList.add(entry);
        }
    }
    private long lastRefreshTime;

    /**
     * 心率
     */
    @SuppressWarnings("unused")
    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener(){
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if(event != null){
                appendToUI(String.format(" 心率：\n heart rate = %d", event.getHeartRate()), txtStatusHeartRate);
                SendSensorData(String.format("r %d", event.getHeartRate()));
                if (System.currentTimeMillis() - lastRefreshTime > 200) {
                    lastRefreshTime = System.currentTimeMillis();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            heartRateList.add(new Entry(heartRateList.size(), event.getHeartRate()));
                            setData(heartRateList.subList(heartRateList.size() - 60, heartRateList.size()));
                            mChart.invalidate();
                        }
                    });
                }
            }
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtStatusGyr = (TextView) findViewById(R.id.txtStatus_Gyr);
        txtStatusAlti = (TextView) findViewById(R.id.txtStatus_Alti);
        txtStatusHeartRate = (TextView) findViewById(R.id.txtStatus_HeartRate);
        txtIpInpuText = (EditText) findViewById(R.id.txtIp_input);


        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerIP = txtIpInpuText.getText().toString();//获取输入字符串
                txtStatus.setText("");
                new AccelerometerSubscriptionTask().execute();
            }
        });

        final WeakReference<Activity> reference = new WeakReference<Activity>(WelcomeActivity.this);
        new HeartRateConsentTask().execute(reference);
        initViews();
    }

    private void initViews() {

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setViewPortOffsets(0, 0, 0, 0);
        mChart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        mChart.setDescription("");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setMaxHighlightDistance(300);

        XAxis x = mChart.getXAxis();
        x.setEnabled(false);

        YAxis y = mChart.getAxisLeft();
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        y.setTypeface(mTfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        mChart.getAxisRight().setEnabled(false);

        // add data
//        testSetData(45, 100);
        setData(heartRateList);

        mChart.getLegend().setEnabled(false);

        mChart.animateXY(2000, 2000);

        // dont forget to refresh the drawing
        mChart.invalidate();
    }

    private void setData(List<Entry> yVals) {

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
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
            data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            mChart.setData(data);
        }
    }


    private void testSetData(int count, float range) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
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
            data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            mChart.setData(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtStatus.setText("");
        appendToUI("",txtStatusGyr);
        appendToUI("",txtStatusAlti);
        appendToUI("",txtStatusHeartRate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null) {
            try {
                client.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerEventListener);
                client.getSensorManager().unregisterGyroscopeEventListener(mGyroscopeEventListener);
                client.getSensorManager().unregisterAltimeterEventListener(mAltimeterEventListener);
                //client.getSensorManager().unregisterHeartRateEventListener(mHeartRateEventListener);
            } catch (BandIOException e) {
                appendToUI(e.getMessage());
            }
        }
    }
    /**
     *
     * 连接band，绑定事件
     *
     */
    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
                    client.getSensorManager().registerGyroscopeEventListener(mGyroscopeEventListener, SampleRate.MS16);
                    client.getSensorManager().registerAltimeterEventListener(mAltimeterEventListener);
                    client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    /**
     * 输出信息
     */
    private void appendToUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(string);
            }
        });
    }
    private void appendToUI(final String string, final TextView text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(string);
            }
        });
    }
    /**
     * 连接band
     */
    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        //appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }
    /**
     * 接入心率传感器
     */
    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (getConnectedBandClient()) {
                    if (params[0].get() != null) {
                        client.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.\n";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.\n";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage() + "\n";
                        break;
                }
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }
}


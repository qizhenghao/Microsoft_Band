package com.dawei.band.desktop;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;


import com.dawei.band.R;
import com.dawei.band.accelerate.AccelerateFragment;
import com.dawei.band.angularVelocity.AngularVelocityFragment;
import com.dawei.band.base.BandApplication;
import com.dawei.band.base.BaseFragment;
import com.dawei.band.desktop.adapter.BaseFramentPagerAdapter;
import com.dawei.band.desktop.view.DesktopTabHost;
import com.dawei.band.heartRate.ui.HeartRateFragment;
import com.dawei.band.listeners.OnRefreshFragmentListener;
import com.dawei.band.listeners.OnTabItemClickListener;
import com.dawei.band.home.HomeFragment;
import com.dawei.band.utils.Methods;
import com.dawei.band.utils.SystemStatusManager;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;

import java.util.ArrayList;
import java.util.List;

public class DesktopActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshFragmentListener, OnTabItemClickListener {

    private ViewPager viewPager;
    private DesktopTabHost desktopTabHost;
    private BaseFramentPagerAdapter viewPagerAdapter;
    private List<BaseFragment> fragmentList;

    private HeartRateFragment heartRateFragment;
    private AccelerateFragment accelerateFragment;
    private AngularVelocityFragment angularVelocityFragment;
    private HomeFragment homeFragment;
    private BandClient client;
    private Dialog connectDialog;
    private long mExitTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_desktop_layout);
        initViews();
        setListener();
        if (BandApplication.client == null)
            showConnectDialog();
        else
            initData();
    }

    private void setListener() {

    }

    private void initData() {
        heartRateFragment = new HeartRateFragment();
        accelerateFragment = new AccelerateFragment();
        angularVelocityFragment = new AngularVelocityFragment();
        homeFragment = new HomeFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(homeFragment);
        fragmentList.add(heartRateFragment);
        fragmentList.add(accelerateFragment);
        fragmentList.add(angularVelocityFragment);

        viewPagerAdapter = new BaseFramentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
        desktopTabHost.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(fragmentList.size());

        desktopTabHost.setCurrentItem(1);
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        desktopTabHost = (DesktopTabHost) findViewById(R.id.tab_page_indicator);
        desktopTabHost.setViewIds(new int[]{R.id.tab_line_layout, R.id.tab_one, R.id.tab_two, R.id.tab_three, R.id.tab_four});
    }

    @Override
    public void onClick(View v) {

    }

    private void showConnectDialog() {
        if (connectDialog == null) {
            connectDialog = new Dialog(DesktopActivity.this, R.style.common_dialog_style) {
                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    View dialogView = getLayoutInflater().inflate(R.layout.activity_desktop_dialog_layout, null);
                    setContentView(dialogView);

                    dialogView.findViewById(R.id.activity_desktop_dialog_start_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AccelerometerSubscriptionTask().execute();
                        }
                    });
                    dialogView.findViewById(R.id.activity_desktop_dialog_cancle_iv).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DesktopActivity.this.finish();
                        }
                    });
                }
            };
            connectDialog.setCanceledOnTouchOutside(false);
            connectDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return true;
                }
            });
        }
        connectDialog.show();
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
                    Methods.showToast("Band is connected.\n", false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectDialog.dismiss();
                            initData();
                        }
                    });
//                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS16);
//                    client.getSensorManager().registerGyroscopeEventListener(mGyroscopeEventListener, SampleRate.MS16);
//                    client.getSensorManager().registerAltimeterEventListener(mAltimeterEventListener);
//                    client.getSensorManager().registerHeartRateEventListener(mHeartRateEventListener);
                } else {
                    Methods.showToast("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n", true);
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
                Methods.showToast(exceptionMessage, true);

            } catch (Exception e) {
                e.printStackTrace();
                Methods.showToast("未知异常", false);
            }
            return null;
        }
    }

    /**
     * 连接band
     */
    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Methods.showToast("Band isn't paired with your phone.\n", false);
                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
            BandApplication.client = client;
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }
        Methods.showToast("Band is connecting...\n", false);
        return ConnectionState.CONNECTED == client.connect().await();
    }

    // 需要setContentView之前调用
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemStatusManager tintManager = new SystemStatusManager(this);
            tintManager.setStatusBarTintEnabled(true);
            // 设置状态栏的颜色
            tintManager.setStatusBarTintResource(R.color.blue_light);
            getWindow().getDecorView().setFitsSystemWindows(true);
        }
    }

    @Override
    public void onRefresh(Class fragmentClass) {
        viewPagerAdapter.refresh(fragmentClass);
    }

    @Override
    public void onTabItemSelected(int index, Bundle args) {
        desktopTabHost.setCurrentItem(index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Bruce", "onDestroy");
        if (client != null) {
            try {
                client.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Bruce", "onConfigurationChanged");
        //切换为竖屏
        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {

        } else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Methods.showToast("再按一次退出程序", false);
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

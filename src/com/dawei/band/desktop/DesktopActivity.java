package com.dawei.band.desktop;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.dawei.band.R;
import com.dawei.band.base.BaseFragment;
import com.dawei.band.desktop.adapter.BaseFramentPagerAdapter;
import com.dawei.band.desktop.view.DesktopTabHost;
import com.dawei.band.heartRate.ui.HeartRateFragment;
import com.dawei.band.listeners.OnRefreshFragmentListener;
import com.dawei.band.listeners.OnTabItemClickListener;
import com.dawei.band.utils.SystemStatusManager;

import java.util.ArrayList;
import java.util.List;

public class DesktopActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshFragmentListener, OnTabItemClickListener {

    public static final float NORMAL_BUSINESS_RATE = 4.90f;
    public static final float NORMAL_HOUSING_RATE = 3.25f;
    public static final float DEFAULT_FIRST_PAY = 3;
    public static final float DEFAULT_YEAR = 30;

    private ViewPager viewPager;
    private DesktopTabHost desktopTabHost;
    private BaseFramentPagerAdapter viewPagerAdapter;
    private List<BaseFragment> fragmentList;

    private HeartRateFragment mortgageCalculateFragment;
    private HeartRateFragment myMortgageFragment;
    private HeartRateFragment recommendFragment;
    private boolean isFromWelcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_desktop_layout);
        initViews();
        setListener();
        initData();
    }

    private void setListener() {

    }

    private void initData() {
        isFromWelcome = getIntent().getBooleanExtra("is_from_welcome", true);
        mortgageCalculateFragment = new HeartRateFragment();
        myMortgageFragment = new HeartRateFragment();
        recommendFragment = new HeartRateFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(mortgageCalculateFragment);
        fragmentList.add(myMortgageFragment);
        fragmentList.add(recommendFragment);

        viewPagerAdapter = new BaseFramentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
        desktopTabHost.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(fragmentList.size());

        desktopTabHost.setCurrentItem(0);
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        desktopTabHost = (DesktopTabHost) findViewById(R.id.tab_page_indicator);
        desktopTabHost.setViewIds(new int[]{R.id.tab_line_layout, R.id.tab_one, R.id.tab_two, R.id.tab_three});
    }

    @Override
    public void onClick(View v) {

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);//不销毁activity，重新回到视野后保持原样
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabItemSelected(int index, Bundle args) {
        desktopTabHost.setCurrentItem(index);
    }
}

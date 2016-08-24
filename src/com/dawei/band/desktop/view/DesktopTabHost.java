package com.dawei.band.desktop.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenghao.qi on 2016/6/23.
 * <p/>
 * 发现多个tab切换效果
 */
public class DesktopTabHost extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private final String TAG = "DesktopTabHost";

    private static final int TAB_COUNT = 2;

    private List<TextView> mTextItems;

    private LineViewPagerIndicator mLineIndicator;

    private Activity mParentActivity;

    private int[] mViewIds;

    // TAB类型
    public static interface TabType {
        int MORTGAGE_CALCULATE = 0;
        int MY_MORTGAGE = 1;
//        int CHAT = 2;
//        int MINE = 3; // 个人页
    }

    public DesktopTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * set the view ids and initialize the tab page indicator.
     * NOTE: this method MUST be called in initializing, otherwise the view would not be functional.
     * @param ids an array containing the ids of the line indicator (index 0 in the array)
     *            and the text items (index from 1).
     */
    public void setViewIds(int[] ids) {
        if (ids != null && ids.length > 1) {
            mViewIds = ids;
            initViews();
        } else {
            Log.e(TAG, "error in setViewIds(): the given \"ids\" is invalid!!!");
        }
    }

    public void setViewPager(ViewPager pager) {
        mLineIndicator.setViewPager(pager);
        mLineIndicator.setOnPageChangeListener(this);
    }

    public void setParentActivty(Activity parentActivity) {
        mParentActivity = parentActivity;
    }

    private void initViews() {
        if (mViewIds != null && mViewIds.length > 1) {
            mTextItems = new ArrayList<TextView>(mViewIds.length - 1);
            mLineIndicator = (LineViewPagerIndicator) findViewById(mViewIds[0]);
            for (int i = 1; i < mViewIds.length; i++) {
                mTextItems.add((TextView) findViewById(mViewIds[i]));
            }
        } else {
            Log.e(TAG, "error in initViews(): mViewIds is invalid!!!");
        }
        bindListener();
    }


    private void bindListener() {
        if (mTextItems != null && mTextItems.size() > 0) {
            for (int i = 0; i < mTextItems.size(); i++) {
                final int currTab = i;
                mTextItems.get(currTab).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View dv) {
                        if (mLineIndicator.getCurrentPage() == currTab) {
                            onTabClicked(currTab);
                        }
                        mLineIndicator.setCurrentItem(currTab);
                    }
                });
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int index) {
        for (int i=0;i<mTextItems.size();i++)
            mTextItems.get(i).setSelected(i==index);
    }

    private void onTabClicked(int index) {

    }

    public void setCurrentItem(int position) {
        mLineIndicator.setCurrentItem(position);
        onPageSelected(position);
    }

}

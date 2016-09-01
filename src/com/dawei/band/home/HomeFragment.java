package com.dawei.band.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dawei.band.R;
import com.dawei.band.base.BandApplication;
import com.dawei.band.base.BaseFragment;
import com.dawei.band.home.view.BottomPushPopupWindow;
import com.dawei.band.utils.AppConfig;
import com.dawei.band.utils.Methods;
import com.microsoft.band.BandClient;

/**
 * Created by qizhenghao on 16/8/26.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener{

    private static final int TEMP_REFRESH_TIME = 1000;
    private static final int RESIST_REFRESH_TIME = 200;

    private static final int LENGTH = 300;

    private BandClient client;

    private EditText ipEdit;
    private EditText portEdit;
    private TextView typeTv;
    private Button recordBtn;
    private View recordingLayout;
    private Button stopBtn;
    private CustomPopupWindow popupWindow;
    private int type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home_layout, null);
        return mContentView;
    }

    @Override
    protected void initView() {
        ipEdit = (EditText) mContentView.findViewById(R.id.fragment_home_ip_edit);
        portEdit = (EditText) mContentView.findViewById(R.id.fragment_home_port_edit);
        typeTv = (TextView) mContentView.findViewById(R.id.fragment_home_type_tv);
        recordBtn = (Button) mContentView.findViewById(R.id.fragment_home_start_btn);
        recordingLayout = mContentView.findViewById(R.id.fragment_home_recoding_layout);
        stopBtn = (Button) mContentView.findViewById(R.id.fragment_home_stop_btn);
    }

    @Override
    protected void initData() {
        client = BandApplication.client;

    }



    @Override
    protected void initListener() {
        typeTv.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_home_type_tv:
                if (popupWindow == null)
                    popupWindow = new CustomPopupWindow(mActivity);
                popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.fragment_home_start_btn:
                String ip = ipEdit.getText().toString();
                String port = portEdit.getText().toString();
                if (TextUtils.isEmpty(ip)) {
                    Methods.showToast(ipEdit.getHint(), false);
                    return;
                }
                if (TextUtils.isEmpty(port)) {
                    Methods.showToast(portEdit.getHint(), false);
                    return;
                }
                if (type == -1) {
                    Methods.showToast(typeTv.getText().toString(), false);
                    return;
                }
                AppConfig.SERVER_IP = ip;
                AppConfig.SERVER_PORT = port;
                AppConfig.IS_UPLOADING.set(true);
                recordingLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_home_stop_btn:
                recordingLayout.setVisibility(View.GONE);
                AppConfig.IS_UPLOADING.set(false);
                break;
        }
    }

    class CustomPopupWindow extends BottomPushPopupWindow {

        public CustomPopupWindow(Context context) {
            super(context);
        }

        @Override
        protected View generateCustomView() {
            View root = View.inflate(mActivity, R.layout.fragment_home_down_dialog_layout, null);
            View runTV = root.findViewById(R.id.fragmnet_home_down_dialog_run_tv);
            runTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeTv.setText("跑步");
                    type = 0;
                    dismiss();
                }
            });
            View walkTv = root.findViewById(R.id.fragmnet_home_down_dialog_walk_tv);
            walkTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeTv.setText("慢走");
                    type = 1;
                    dismiss();
                }
            });
            View cancelView = root.findViewById(R.id.fragmnet_home_down_dialog_cancel_tv);
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return root;
        }
    }
}

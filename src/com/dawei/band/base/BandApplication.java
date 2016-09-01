package com.dawei.band.base;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dawei.band.net.ApiHttpClient;
import com.dawei.band.net.ServerApi;
import com.dawei.band.utils.AppConfig;
import com.dawei.band.utils.StringUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.microsoft.band.BandClient;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

/**
 * Created by qizhenghao on 16/6/23.
 */
public class BandApplication extends Application {

    public static BandApplication INSTANCE;
    public static BandClient client;
    private static BandApplication mContext;
    private static Handler mApplicationHandler;
    private Typeface tfLight;

    @Override
    public void onCreate() {
        INSTANCE = this;
        mContext = this;
        super.onCreate();

        init();
//        testHttp();//测试当前http是否通，可以自行观察log，如果打印出下面类似的xml的字符串，说明是成功的
//        <catalog>0</catalog>
//        <newsCount>0</newsCount>
//        <pagesize>20</pagesize>
//        <newslist>
//        <news>
//        <id>75980</id>
//        <title><![CDATA[阿里云宣布开放开源 AliSQL 数据库，性能可提升 70%]]></title>
//        <body><![CDATA[继开源跨平台开发框架 Weex 之后，阿里巴巴再次宣布另一个重大开源项目 ...]]></body>
//        <commentCount>79</commentCount>
//        <author><![CDATA[oschina]]></author>
//        <authorid>1</authorid>
//        <pubDate>2016-08-09 16:39:52</pubDate>
//        <url></url>
    }

    private void init() {
        // 初始化网络请求
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));
    }


    public static BandApplication getContext() {
        return mContext;
    }

    public static Handler getApplicationHandler() {
        if (mApplicationHandler == null) {
            mApplicationHandler = new Handler(Looper.getMainLooper());
        }
        return mApplicationHandler;
    }

    public Typeface getTfLight() {
        if (tfLight == null)
            tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        return tfLight;
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    private void testHttp() {
        ServerApi.getNewsList(0, 1, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("Bruce", "onSuccess");
                    Log.d("Bruce", new String(responseBody, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Log.d("Bruce", "onFailure");
                    Log.d("Bruce", new String(responseBody, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

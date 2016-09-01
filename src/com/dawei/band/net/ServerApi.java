package com.dawei.band.net;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by qizhenghao on 16/9/1.
 */
public class ServerApi {

    /**
     *
     * 网络请求接口例子
     *
     * 登陆
     *
     * @param username
     * @param password
     * @param handler
     */
    public static void login(String username, String password,
                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("pwd", password);
        params.put("keep_login", 1);
        String loginurl = "action/api/login_validate";
        ApiHttpClient.post(loginurl, params, handler);
    }

    /**
     * 获取新闻列表
     *
     * @param catalog 类别 （1，2，3）
     * @param page    第几页
     * @param handler
     */
    public static void getNewsList(int catalog, int page,
                                   AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
//        params.put("catalog", catalog);
        params.put("pageIndex", page);
        params.put("pageSize", 20);
        params.put("show", "month");
        ApiHttpClient.get("action/api/news_list", params, handler);
    }
}

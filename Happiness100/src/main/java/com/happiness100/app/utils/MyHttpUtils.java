package com.happiness100.app.utils;/**
 * Created by Administrator on 2016/8/9.
 */

/**
 * 作者：justin on 2016/8/9 11:04
 */
public final class MyHttpUtils {
    private static final String INDEX = "http://192.168.1.78:8080/xf100/web/";

    public static String getUrl(String servlet) {
        return INDEX + servlet;
    }


}

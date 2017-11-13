package com.justin.utils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.volley.UTFStringRequest;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HTTPUtils {
    private static final String TAG = "HTTPUtils";
    private static RequestQueue mRequestQueue;

    private HTTPUtils() {
    }

    private static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static void post(Context context, String url,
                            final Map<String, String> params, final VolleyListener listener) throws Exception {
        StringBuilder sb = new StringBuilder();

        int type = Method.POST;
        if (params != null) {

            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            Log.e(TAG, url + "?" + sb.toString().substring(0, sb.toString().length() - 1));
        }else{
            type = Method.GET;
            Log.e(TAG, url );
        }

        StringRequest myReq = new UTFStringRequest(type, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }

        // 请用缓存
        myReq.setShouldCache(true);
        myReq.setRetryPolicy(new DefaultRetryPolicy(25 * 1000, 1, 1.0f));
        // 设置缓存时间10分钟
        // myReq.setCacheTime(10*60);
        mRequestQueue.add(myReq);
    }

    public static void post(Context context, String url, final Map<String, String> headers, final Map<String, String> params, final VolleyListener listener) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (params != null) {

            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        Log.e(TAG, url + "?" + sb.toString().substring(0, sb.toString().length() - 1));
        StringRequest myReq = new UTFStringRequest(Method.POST, url,
                new Listener<String>() {
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        if (mRequestQueue == null) {
            init(context);
        }

        // 请用缓存
        myReq.setShouldCache(true);
        // 设置缓存时间10分钟
        // myReq.setCacheTime(10*60);
        mRequestQueue.add(myReq);
    }

    public static void get(Context context, String url,
                           Map<String, String> params, final VolleyListener listener) {
        StringBuilder buildParams = new StringBuilder();

        int times = 0;
        if (params != null) {
            int size = params.size();
            if (size > 0) {
                Set<String> keys = params.keySet();
                Iterator<String> itr = keys.iterator();
                buildParams.append('?');
                while (itr.hasNext()) {
                    String key = itr.next();
                    String value = params.get(key);
                    if (times != size - 1) {
                        buildParams.append(key).append('=').append(value).append('&');
                    }
                    times++;
                }
            }
        }
        String param = buildParams.toString();
        Log.e(TAG, url + param);
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null or empty");
        }

        StringRequest myReq = new UTFStringRequest(Method.GET, url + param,
                new Listener<String>() {
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        if (mRequestQueue == null) {
            init(context);
        }
        mRequestQueue.add(myReq);
    }

    private static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static void cancelAll(Context context) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(context);
        }
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache}
     * which effectively means that no memory caching is used. This is useful
     * for images that you know that will be show only once.
     *
     * @return
     */
    // public static ImageLoader getImageLoader() {
    // if (mImageLoader != null) {
    // return mImageLoader;
    // } else {
    // throw new IllegalStateException("ImageLoader not initialized");
    // }
    // }
}

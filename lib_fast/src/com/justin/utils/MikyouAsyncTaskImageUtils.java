package com.justin.utils;/**
 * Created by Administrator on 2016/9/9.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.yuchen.lib.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：jiangsheng on 2016/9/9 10:38
 */
public class MikyouAsyncTaskImageUtils extends AsyncTask<List<String>, Void, List<Bitmap>> {
    private List<Bitmap> mBitmapList;
    private OnAsyncTaskImageListener listener;
    private int UriCount = 0;
    Context mContext;

    public MikyouAsyncTaskImageUtils(Context context)
    {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {//在执行异步任务之前调用，做一些初始化的操作
        super.onPreExecute();
    }
    @Override
    protected List<Bitmap> doInBackground(List<String>... params) {
        //写耗时的网络请求操作
        UriCount = params[0].size();
        mBitmapList = new ArrayList<Bitmap>();

        for (int i = 0 ; i < params[0].size();++i)
        {
            String url=params[0].get(i);
            Log.e("url","url = "+url);
            try {
                URL mURL=new URL(url);
                HttpURLConnection conn=(HttpURLConnection) mURL.openConnection();
                conn.setRequestMethod("GET");
                InputStream is=conn.getInputStream();
                BufferedInputStream bis=new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                mBitmapList.add(bitmap);
                is.close();
            } catch (Exception e) {
                mBitmapList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_default_man));
                e.printStackTrace();
            }
        }
        return mBitmapList;
    }
    @Override
    protected void onPostExecute(List<Bitmap> result) {
        //拿到Bitmap的返回值，就需要将它传递给MainActivity中的iv让它设置这个mBitmap对象
        /**
         * 这里有三种方法实现：第一直接把该类作为内部类放入Activity中就不需要传递mBitmap
         * 因为iv在MainActivity中是全局的直接设置就可以了，实现数据共享
         *
         *                                         第二将该类放到另外一个包内，此时就需要将外面的ImageView对象，通过构造器传入
         *                                         然后再该类中去直接给ImageView对象设置Bitmap即可
         *
         *                                         第三则是我这里要使用的，这个类也是在另外一个包内，采用的是把这个Bitmap对象
         *                                         通过自定义一个监听器，通过监听器的回调方法将我们该方法中的result的Bitmap对象
         *                                         传出去，让外面直接在回调方法中设置BItmap
         * 有的人就要疑问，为何我不能直接通过该类公布出去一个mBItmap对象的getter方法，让那个外面
         * 直接通过getter方法拿到mBitmap,有这种想法，可能你忽略了一个很基本的问题就是，我们
         * 这个网路请求的任务是异步的，也就是这个BItmap不知道什么时候有值，当主线程去调用
         * getter方法时候，子线程的网络请求还来不及拿到Bitmap，那么此时主线程拿到的只能为空
         * 那就会报空指针的错误了，所以我们自己定义一个监听器，写一个回调方法，当网络请求拿到了
         * Bitmap才会回调即可。
         * 自定一个监听器：
         *  1、首先得去写一个接口
         *  2 然后在类中保存一个接口类型的引用
         *  3 然后写一个setOnAsyncTaskImageListener
         * 方法初始化那个接口类型的引用
         * 4、最后在我们的onPostExecute方法中调用接口中的抽象方法，
         * 并且把我们得到的result作为参数传入即可
         *
         * */
        if (listener!=null) {
            listener.asyncTaskImageListener(result);
        }
            super.onPostExecute(result);
    }
    //
    public void setOnAsyncTaskImageListener(OnAsyncTaskImageListener listener){
        this.listener=listener;
    }

    public interface OnAsyncTaskImageListener {
       public void asyncTaskImageListener(List<Bitmap> bitmaps);
    }

}
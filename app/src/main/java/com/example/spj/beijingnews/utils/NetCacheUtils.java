package com.example.spj.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by spj on 2016/8/26.18:48
 */
public  class NetCacheUtils {

    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
    private final Handler handler;
    private final ExecutorService service;
    private final LocalCacheUtils localCacheUtils;
    private final MemoryCaheUtils memoryCaheUtils;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCaheUtils memoryCaheUtils) {

        this.handler = handler;
        service = Executors.newFixedThreadPool(10);
        this.localCacheUtils = localCacheUtils;
        this.memoryCaheUtils = memoryCaheUtils;

    }

    //联网请求得到图片
    public  void getBitmapFromNet(String imageUrl, int position) {

        service.execute(new MyRunnable(imageUrl,position));
    }

    private class MyRunnable implements Runnable {


        private final String imageUrl;
        private int position;

        public MyRunnable(String imageUrl, int position) {
            this.imageUrl = imageUrl;
            this.position = position;
        }

        @Override
        public void run() {
            //子线程，请求网络图片
            try {

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                if(responseCode == 200) {
                    //响应成功
                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //显示到控件上
                    Message msg =Message.obtain();
                    msg.what = SUCCESS;
                    msg.arg1 = position;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);

                    //在本地文件中缓存一份
                    localCacheUtils.putBitmap(imageUrl,bitmap);

                    //在内存缓存一份
                    memoryCaheUtils.putBitmap(imageUrl,bitmap);


                }
            } catch (Exception e) {
                e.printStackTrace();
                //显示到控件上
                Message msg = Message.obtain();
                msg.what = FAIL;
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        }
    }
}

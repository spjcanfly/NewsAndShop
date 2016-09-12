package com.example.spj.beijingnews.utils;

import android.graphics.Bitmap;
import android.os.Handler;


/**
 * Created by spj on 2016/8/26.18:43
 */
public class BitmapCacheUtils {

    //网络缓存工具类
    private NetCacheUtils netCacheUtils;

    //本地缓存工具类(文件)
    private LocalCacheUtils localCacheUtils;

    //内存缓存工具类
    private MemoryCaheUtils memoryCaheUtils;

    public BitmapCacheUtils(Handler handler) {

        memoryCaheUtils = new MemoryCaheUtils();
        localCacheUtils = new LocalCacheUtils(memoryCaheUtils);
        netCacheUtils = new NetCacheUtils(handler,localCacheUtils,memoryCaheUtils);
    }
    /**
     * 三级缓存的原理
     * 1.先从内存中找图片，不行的话走 2
     * 2.从本地文件中找图片，如果有显示并往内存里缓存，没有走 3
     * 3.从网络请求图片，显示，然后往内存存一份，本地文件存一份
     *
     */
    public Bitmap getBitmap(String imageUrl,int position){
        //1.从内存中取图片
        if(memoryCaheUtils != null) {
            Bitmap bitmap = memoryCaheUtils.getBitmapFromUrl(imageUrl);
            if(bitmap != null) {
                LogUtil.e("内存加载图片成功"+position);
                return bitmap;
            }
        }

        //2.从本地文件中取图片
        if(localCacheUtils != null) {
            Bitmap bitmap = localCacheUtils.getBitmapFromUrl(imageUrl);
            if(bitmap != null) {
                LogUtil.e("本地加载图片成功"+position);
                return  bitmap;
            }
        }

        //3.请求网络图片
        netCacheUtils.getBitmapFromNet(imageUrl,position);
        return null;
    }
}

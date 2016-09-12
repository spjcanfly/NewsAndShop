package com.example.spj.beijingnews.utils;

import android.graphics.Bitmap;

import org.xutils.cache.LruCache;

/**
 * Created by spj on 2016/8/26.18:49
 */
public class MemoryCaheUtils {

    private final LruCache<String, Bitmap> lruCache;

    public MemoryCaheUtils() {
        //使用系统分配给应用程序的八分之一内存来作为缓存大小
        int maxSize = (int) Runtime.getRuntime().maxMemory();// 1024/8
        lruCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return (value.getRowBytes() * value.getHeight())/1024;
            }
        };

    }

    //根据url从内存中获取图片
    public Bitmap getBitmapFromUrl(String imageUrl) {

        return lruCache.get(imageUrl);
    }

    public void putBitmap(String imageUrl, Bitmap bitmap) {
        lruCache.put(imageUrl,bitmap);
    }
}

package com.example.spj.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by spj on 2016/8/26.18:49
 */
public class LocalCacheUtils {

    private MemoryCaheUtils memoryCaheUtils;

    public LocalCacheUtils(MemoryCaheUtils memoryCaheUtils) {
        this.memoryCaheUtils = memoryCaheUtils;
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        //判断sd卡是否挂载
        boolean b=Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        LogUtil.e("bbbbbbbbbbbb===== "+b);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(imageUrl);

                File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);
                
                if(file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    if(bitmap != null) {
                        memoryCaheUtils.putBitmap(imageUrl,bitmap);
                    }
                    return bitmap;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //保存从网络下载的图片
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        //判断sd卡是否挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //保存图片在/mnt/sdcard/beijingnews/http://192.168.21.165:8080/xsxxxx.png
            //保存图片在/mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
            try {
                //用MD5加密图片的类型，使得图片管理读取不了保存的图片
                String fileName = MD5Encoder.encode(imageUrl);//llkskljskljklsjklsllsl
                ///mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory(),fileName);

                File parentFile = file.getParentFile();//mnt/sdcard/beijingnews
                if(!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }
                //保存图片
                bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

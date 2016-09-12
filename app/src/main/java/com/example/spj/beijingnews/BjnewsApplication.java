package com.example.spj.beijingnews;

import android.app.Application;

import com.example.spj.beijingnews.volley.VolleyManager;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by spj on 2016/8/15.
 * 所有组件被创建之前执行
 */
public class BjnewsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.setDebug(true);
        x.Ext.init(this);

        //初始化Volley
        VolleyManager.init(this);
        //初始化极光推送
        JPushInterface.setDebugMode(true);//设置开启日志，发布时请关闭日志
        JPushInterface.init(this);//初始化 JPush
    }
}

package com.example.spj.beijingnews.base;

import android.content.Context;
import android.view.View;

/**
 * Created by spj on 2016/8/15.22:00
 */
public abstract class MenuDetailBasePager {
    public Context context;
    public View rootView;

    public MenuDetailBasePager(Context context) {
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();

    public void initData(){

    }
}

package com.example.spj.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.example.spj.beijingnews.base.BasePager;
import com.example.spj.beijingnews.utils.LogUtil;

/**
 * Created by spj on 2016/8/15.
 */
public class HomePager extends BasePager {


    public HomePager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        //设置标题
        tv_title.setText("主页面");
        //联网请求，得到数据，创建视图
        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        tv.setTextSize(25);
        //把子试图添加到BasePager上的Fragment上
        fl_content.addView(tv);
        //绑定数据
        tv.setText("主页面内容");
    }
}

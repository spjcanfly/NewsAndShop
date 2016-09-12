package com.example.spj.beijingnews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.spj.beijingnews.base.BasePager;

import java.util.List;

/**
 * Created by spj on 2016/8/15.16:59
 */
public class ContentFragmentAdapter extends PagerAdapter {
    private List<BasePager> basePagers;

    public ContentFragmentAdapter(List<BasePager> basePagers) {
        this.basePagers = basePagers;
    }

    @Override
    public int getCount() {
        return basePagers == null ? 0 : basePagers.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePager basePager = basePagers.get(position);//各个页面的实例
        View rootView = basePager.rootView;//各个子页面
//        basePager.initData();//初始化各个页面的数据
        container.addView(rootView);


        return rootView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}

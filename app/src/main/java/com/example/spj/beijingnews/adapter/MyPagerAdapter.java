package com.example.spj.beijingnews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by spj on 2016/8/15.
 */
public class MyPagerAdapter extends PagerAdapter {

    List<ImageView> list;

    public MyPagerAdapter(List<ImageView> list) {
        this.list = list;
    }

    //返回数据的总个数
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * //作用跟getView（）一样
     *
     * @param container viewPager
     * @param position  创建页面的位置
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = list.get(position);
        //添加到容器中去
        container.addView(imageView);
        return imageView;
    }

    /**
     * @param view   当前创建的视图
     * @param object 上面instant方法返回的结果值
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }

    /**
     * @param container ViewPager
     * @param position  要销毁的页面的位置
     * @param object    要销毁的页面
     *                  记住super的方法一定要删除
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}

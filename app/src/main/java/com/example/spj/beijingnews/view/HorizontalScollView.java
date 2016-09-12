package com.example.spj.beijingnews.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by spj on 2016/8/23.18:17
 * 作用：轮播图，北京那个标题，左拉的时候侧滑会出来
 */
public class HorizontalScollView extends ViewPager{

    public HorizontalScollView(Context context) {
        super(context);
    }

    public HorizontalScollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //起始坐标
    private float startX;
    private float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
               //请求父视图不拦截,把事件传给当前控件（HorizontalScrollViewPager）
                getParent().requestDisallowInterceptTouchEvent(true);
                //1.记录起始坐标
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE :
               //2.记录结束的坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //3.计算偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //4.1判断滑动的方向
                if(Math.abs(distanceX) > Math.abs(distanceY)) {
                    //2.1水平方向的滑动，如果是ViewPager的第0个页面，并且是从左往右滑动
                    if(getCurrentItem() == 0 && distanceX>0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    //2.2 当滑动到ViewPager的最后一个页面，并且是从右到左滑动
                    else if(getCurrentItem() == getAdapter().getCount()-1 && distanceX <0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    //2.3 其它 中间部分
                    else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                }else {
                    //竖直方向滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP :

                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}

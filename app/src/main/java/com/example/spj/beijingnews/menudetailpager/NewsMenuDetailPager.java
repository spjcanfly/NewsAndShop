package com.example.spj.beijingnews.menudetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.activity.MainActivity;
import com.example.spj.beijingnews.base.MenuDetailBasePager;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.menudetailpager.tabdetailpager.TabDetailPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/15.22:03
 */
public class NewsMenuDetailPager extends MenuDetailBasePager{

    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator tabPageIndicator;

    @ViewInject(R.id.news_memu_detali_viewpager)
    private ViewPager news_memu_detali_viewpager;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;

    private ArrayList<TabDetailPager> tabDetailPagers;
    //页签页面的集合-页面
    private final List<NewsCenterPagerBean.DataEntity.ChildrenData> children;


    public NewsMenuDetailPager(Context context, NewsCenterPagerBean.DataEntity dataEntity) {
        super(context);
        children = dataEntity.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.newsmenu_detail_pager, null);
        x.view().inject(this,view);
        //设置点击事件
        ib_tab_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                news_memu_detali_viewpager.setCurrentItem(news_memu_detali_viewpager.getCurrentItem()+1);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //准备新闻详情页面的数据
        tabDetailPagers = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            tabDetailPagers.add(new TabDetailPager(context,children.get(i)));
        }
        //设置ViewPager的适配器
        news_memu_detali_viewpager.setAdapter(new MyNewsMenuDetailPagerAdapter());

        //ViewPager 和TabPageIndicator关联
        tabPageIndicator.setViewPager(news_memu_detali_viewpager);

        //注意以后监听页面的变化，TabPageIndicator监听页面的变化
        tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());

        tabDetailPagers.get(0).initData();
    }


    private class MyNewsMenuDetailPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return tabDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager tabDetailPager = tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
//            tabDetailPager.initData();//初始化数据
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView((View) object);
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            if(position == 0) {
                //侧滑菜单可以全屏滑动
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
            }else{
                //不可以全屏滑动
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
            }
            tabDetailPagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);

    }
}

package com.example.spj.beijingnews.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.activity.MainActivity;
import com.example.spj.beijingnews.adapter.ContentFragmentAdapter;
import com.example.spj.beijingnews.base.BaseFragment;
import com.example.spj.beijingnews.base.BasePager;
import com.example.spj.beijingnews.pager.GovaffairPager;
import com.example.spj.beijingnews.pager.HomePager;
import com.example.spj.beijingnews.pager.NewsCenterPager;
import com.example.spj.beijingnews.pager.SettingPager;
import com.example.spj.beijingnews.pager.SmartServicePager;
import com.example.spj.beijingnews.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/13.
 */
public class ContentFragment extends BaseFragment {
    /**
     * 用xUtils初始化控件
     */
    @ViewInject(R.id.viewPager)
    private NoScrollViewPager viewpager;
    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;
    private List<BasePager> basePagers;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.content_fragment, null);
//        viewpager = (ViewPager) view.findViewById(R.id.viewPager);
        //1.把视图注入到框架中，让ContentFrament.this和View关联起来
        x.view().inject(ContentFragment.this, view);
//        rg_main = (RadioGroup) view.findViewById(R.id.rg_main);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        //初始化五个页面，并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(mContext));
        basePagers.add(new NewsCenterPager(mContext));
        basePagers.add(new SmartServicePager(mContext));
        basePagers.add(new GovaffairPager(mContext));
        basePagers.add(new SettingPager(mContext));
        //设置ViewAdapter的适配器
        viewpager.setAdapter(new ContentFragmentAdapter(basePagers));

        //设置RadioGRoup的选中状态改变的监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听某个页面被选中，初始化对应的页面的数据
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());
        //默认选中第一个
        rg_main.check(R.id.rb_home);
        basePagers.get(0).initData();
        //设置模式不可以滑动
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
    }

    public NewsCenterPager getNewsCenterPager() {

        return (NewsCenterPager) basePagers.get(1);
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_home :
                    viewpager.setCurrentItem(0,false);//false表示切换没有动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_newscenter :
                    viewpager.setCurrentItem(1, false);//false表示切换没有动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);//只有在新闻中心才可以滑动侧滑菜单
                    break;
                case R.id.rb_smart :
                    viewpager.setCurrentItem(2,false);//false表示切换没有动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_govafair :
                    viewpager.setCurrentItem(3,false);//false表示切换没有动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_setting :
                    viewpager.setCurrentItem(4,false);//false表示切换没有动画
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
            }

        }
    }

    private void isEnableSlidingMenu(int touchMode) {
        MainActivity mainActivity = (MainActivity) mContext;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchMode);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        //当某个页面被选中的时候
        @Override
        public void onPageSelected(int position) {

            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

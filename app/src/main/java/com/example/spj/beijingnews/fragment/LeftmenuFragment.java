package com.example.spj.beijingnews.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.activity.MainActivity;
import com.example.spj.beijingnews.base.BaseFragment;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.pager.NewsCenterPager;
import com.example.spj.beijingnews.utils.DensityUtil;

import java.util.List;

/**
 * Created by spj on 2016/8/13.
 */
public class LeftmenuFragment extends BaseFragment{

    private List<NewsCenterPagerBean.DataEntity> data;
    private LeftmenuFragmentAdapter adapter;
    private int prePosition;
    private ListView listView;

    @Override
    public View initView() {
        listView = new ListView(mContext);
        listView.setPadding(0, DensityUtil.dip2px(mContext, 40), 0, 0);
        listView.setDividerHeight(0);//没有分割线
        listView.setCacheColorHint(Color.TRANSPARENT);
        //设置按下listView条目不变色
        listView.setSelector(android.R.color.transparent);
        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //记录点击的位置，变成红色
                prePosition = position;
                adapter.notifyDataSetChanged();

                //把左侧菜单关闭了
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.getSlidingMenu().toggle();//开关菜单

                //切换到对应的详情页面
                switchPager(prePosition);
            }
        });
        return listView;
    }

    private void switchPager(int position) {
        MainActivity mainActivity = (MainActivity) mContext;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.switchPager(position);
    }

    @Override
    public void initData() {
        super.initData();

    }

    public void setData(List<NewsCenterPagerBean.DataEntity> data) {
        this.data=data;
        for (int i = 0; i < data.size(); i++) {

        }
        //设置适配器
        adapter = new LeftmenuFragmentAdapter();
        listView.setAdapter(adapter);
        //设置默认的页面
        switchPager(prePosition);
    }
    class LeftmenuFragmentAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            TextView textView = (TextView) View.inflate(mContext, R.layout.item_leftmenu,null);
            textView.setText(data.get(position).getTitle());
            textView.setEnabled(position == prePosition);
            return textView;
        }
    }
}

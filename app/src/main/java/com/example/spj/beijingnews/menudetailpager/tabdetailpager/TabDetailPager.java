package com.example.spj.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.activity.NewsDetailActivity;
import com.example.spj.beijingnews.base.MenuDetailBasePager;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.domain.TabDetailPagerBean;
import com.example.spj.beijingnews.utils.CacheUtils;
import com.example.spj.beijingnews.utils.Constants;
import com.example.spj.beijingnews.utils.DensityUtil;
import com.example.spj.beijingnews.view.HorizontalScollView;
import com.example.spj.refreshlistview.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by spj on 2016/8/16.19:03
 * 页签详情页面
 */
public class TabDetailPager extends MenuDetailBasePager {

    public static final String READ_ARRAY_ID = "read_array_id";
    private final NewsCenterPagerBean.DataEntity.ChildrenData childrenData;
    private final ImageOptions imageOptions;
    private String url;
    private RefreshListView listView;
    private HorizontalScollView viewpage;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    //顶部轮播图的部分数据
    private List<TabDetailPagerBean.DataEntity.TopnewsData> topnews;

    private List<TabDetailPagerBean.DataEntity.NewsData> news;
    //加载更多的链接
    private String moreUrl;
    //加载更多数据成功了
    private boolean isLoadMore = false;
    private TabDetailPagerListAdapter adapter;
    private InternalHandler handler;

    public TabDetailPager(Context context, NewsCenterPagerBean.DataEntity.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(context, 100), DensityUtil.dip2px(context, 100))
                .setRadius(DensityUtil.dip2px(context, 5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        listView = (RefreshListView ) view.findViewById(R.id.tabdetail_lv);

        //轮播图的视图
        View view1 = View.inflate(context, R.layout.topnews, null);
        viewpage = (HorizontalScollView) view1.findViewById(R.id.topnews_viewpager);
        tv_title = (TextView) view1.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) view1.findViewById(R.id.ll_point_group);

        //把顶部轮播部分视图，以头的方式添加到ListView中
//        listView.addHeaderView(view1);
        listView.addTopNews(view1);

        //监听控件刷新
        listView.setOnRefreshListener(new MyOnRefreshListener());

        //设置ListView的item的点击监听
        listView.setOnItemClickListener(new MyOnItemClickListener());

        return view;

    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        //把之前的缓存取出
        String saveJason = CacheUtils.getSring(context, url);
        if (!TextUtils.isEmpty(saveJason)) {
            //解析数据和处理数据
            processData(saveJason);
        }
        //联网请求数据
        getDataFromNet();
    }

    private void getDataFromNet() {

        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(3000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                //解析和处理显示的数据
                processData(result);

                listView.onRefreshFinish(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new MyRunnale(),3000);

                listView.onRefreshFinish(true);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }
    private int prePosition;
    private void processData(String json) {

        //发消息每隔3秒切换一次ViewPager页面
        if(handler == null) {
            handler = new InternalHandler();
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new MyRunnale(), 3000);

        TabDetailPagerBean bean = parsedJson(json);
        String more = bean.getData().getMore();
        if (TextUtils.isEmpty(more)) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + more;
        }
        if (!isLoadMore) {
             //原来的加载

            //顶部轮播图数据
            topnews = bean.getData().getTopnews();
            //设置ViewPager的适配器
            viewpage.setAdapter(new TabDetailPagerTopNewsAdapter());

            //添加红点
            addPoint();

            //监听页面的改变，设置红点变化和文本变化

            viewpage.addOnPageChangeListener(new MyOnPageChangeListener());

            tv_title.setText(topnews.get(prePosition).getTitle());

            //准备ListView对应的集合的数据
            news = bean.getData().getNews();
            //设置ListView的适配器
            adapter = new TabDetailPagerListAdapter();
            listView.setAdapter(adapter);
        } else {
            //加载更多
            isLoadMore = false;
            //把全部添加到原来的集合
            news.addAll(bean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();
        }





    }

    class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换ViewPager的下一个页面
            int item = (viewpage.getCurrentItem()+1) % topnews.size();
            viewpage.setCurrentItem(item);
            //自动切换就会调用ViewPager变化的监听
            handler.postDelayed(new MyRunnale(),3000);
        }
    }

    class MyRunnale implements Runnable{

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    private void addPoint() {

        ll_point_group.removeAllViews();//移除所有的红点
        for (int i = 0; i < topnews.size(); i++) {
            ImageView imageView = new ImageView(context);
            //设置背景选择器
            imageView.setBackgroundResource(R.drawable.piont_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 5), DensityUtil.dip2px(context, 5));
            if (i == 0) {
                imageView.setEnabled(true);
                prePosition = 0;
            } else {
                imageView.setEnabled(false);
                params.leftMargin = DensityUtil.dip2px(context, 8);
            }
            imageView.setLayoutParams(params);
            ll_point_group.addView(imageView);
        }
    }

    private TabDetailPagerBean parsedJson(String json) {

        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }


    private class TabDetailPagerTopNewsAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置图片默认北京
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            //x轴和y轴拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //把图片添加到容器中去
            container.addView(imageView);

            TabDetailPagerBean.DataEntity.TopnewsData topnewsData = topnews.get(position);
            //图片请求的地址
            String imageUrl = Constants.BASE_URL + topnewsData.getTopimage();
            //联网请求图片
            x.image().bind(imageView, imageUrl);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case  MotionEvent.ACTION_DOWN://按下
                            //把消息队列所有的消息和回调消除
                            handler.removeCallbacksAndMessages(null);

                            break;
                        case  MotionEvent.ACTION_UP://离开
                            //把消息队列所有的消息和回调消除
                            handler.removeCallbacksAndMessages(null);
                            handler.postDelayed(new MyRunnale(),3000);
                            break;

                    }
                    return true;
                }
            });

            return imageView;
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

            //1.设置文本
            tv_title.setText(topnews.get(position).getTitle());
            //2.对应页面的点高亮 红色
            //把之前的变成灰色
            ll_point_group.getChildAt(prePosition).setEnabled(false);

            //当前的设置为红色
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

               if(state == ViewPager.SCROLL_STATE_DRAGGING) {
                   //拖拽，移除消息
                   handler.removeCallbacksAndMessages(null);
               }else if(state == ViewPager.SCROLL_STATE_SETTLING ) {
                   //惯性
                   handler.removeCallbacksAndMessages(null);
                   handler.postDelayed(new MyRunnale(), 3000);
                   
               }else if(state == ViewPager.SCROLL_STATE_IDLE ) {
                   //静止状态
                  handler.removeCallbacksAndMessages(null);
                   handler.postDelayed(new MyRunnale(),3000);
               }
            
        }
    }

     class TabDetailPagerListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return news.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tabdetail_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //根据位置得到数据
            TabDetailPagerBean.DataEntity.NewsData newsData = news.get(position);
            String imageUrl = Constants.BASE_URL + newsData.getListimage();
            //请求图片
            x.image().bind(viewHolder.iv_icon, imageUrl, imageOptions);
            //设置标题
            viewHolder.tv_title.setText(newsData.getTitle());
            //设置更新时间
            viewHolder.tv_time.setText(newsData.getPubdate());

            String idArray = CacheUtils.getSring(context, READ_ARRAY_ID);

            if(idArray.contains(newsData.getId()+"")) {
                viewHolder.tv_title.setTextColor(Color.GRAY);
            }else {
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    private class MyOnRefreshListener implements RefreshListView.OnRefreshListener {

        @Override
        public void onPullDownRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {

            if (TextUtils.isEmpty(moreUrl)) {
                //没有加载更多
                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                listView.onRefreshFinish(false);
            } else {
                //加载更多
                getMoreDataFromNet();
            }
        }

        private void getMoreDataFromNet() {

            RequestParams params = new RequestParams(moreUrl);
            params.setConnectTimeout(4000);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    isLoadMore = true;
                    //解析和处理显示的数据
                    processData(result);
                    listView.onRefreshFinish(false);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                    listView.onRefreshFinish(false);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        }

    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            int realPosition = position - 1;
            TabDetailPagerBean.DataEntity.NewsData newsData = news.get(realPosition);
            //1.取出保存id的集合
            String idArray = CacheUtils.getSring(context, READ_ARRAY_ID);
            //2.判断是否存在，如果不存在，才保存，刷新适配器
            if(!idArray.contains(newsData.getId()+"")) {

                CacheUtils.putString(context, READ_ARRAY_ID, idArray + newsData.getId() + ",");
                //刷新适配器
                adapter.notifyDataSetChanged();
            }
            //跳转到新闻浏览的页面
            Intent intent = new Intent(context,NewsDetailActivity.class);
            intent.putExtra("url",Constants.BASE_URL+newsData.getUrl());
            context.startActivity(intent);

        }
    }
}

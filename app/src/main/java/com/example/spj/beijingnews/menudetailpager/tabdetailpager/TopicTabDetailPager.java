package com.example.spj.beijingnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.base.MenuDetailBasePager;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.domain.TabDetailPagerBean;
import com.example.spj.beijingnews.utils.CacheUtils;
import com.example.spj.beijingnews.utils.Constants;
import com.example.spj.beijingnews.utils.DensityUtil;
import com.example.spj.beijingnews.view.HorizontalScollView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by spj on 2016/8/16.19:03
 * 页签详情页面
 */
public class TopicTabDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean.DataEntity.ChildrenData childrenData;
    private final ImageOptions imageOptions;
    private String url;
    private ListView listView;
    private HorizontalScollView viewpage;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    //顶部轮播图的部分数据
    private List<TabDetailPagerBean.DataEntity.TopnewsData> topnews;
    private int prePosition;
    private List<TabDetailPagerBean.DataEntity.NewsData> news;
    //加载更多的链接
    private String moreUrl;
    //加载更多数据成功了
    private boolean isLoadMore = false;
    private TabDetailPagerListAdapter adapter;
    private PullToRefreshListView pull_refresh_list;

    public TopicTabDetailPager(Context context, NewsCenterPagerBean.DataEntity.ChildrenData childrenData) {
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
        View view = View.inflate(context, R.layout.topicdetail_pager, null);
        pull_refresh_list = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        listView = pull_refresh_list.getRefreshableView();

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        pull_refresh_list.setOnPullEventListener(soundListener);

        //轮播图的视图
        View view1 = View.inflate(context, R.layout.topnews, null);
        viewpage = (HorizontalScollView) view1.findViewById(R.id.topnews_viewpager);
        tv_title = (TextView) view1.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) view1.findViewById(R.id.ll_point_group);

        //把顶部轮播部分视图，以头的方式添加到ListView中
        listView.addHeaderView(view1);
//        listView.addTopNews(view1);

        //监听控件刷新
//        listView.setOnRefreshListener(new MyOnRefreshListener());
        pull_refresh_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                getDataFromNet();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (TextUtils.isEmpty(moreUrl)) {
                    //没有加载更多
                    Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
//                    listView.onRefreshFinish(false);
                    pull_refresh_list.onRefreshComplete();
                } else {
                    //加载更多
                    getMoreDataFromNet();
                }
            }
        });
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
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                //解析和处理显示的数据
                processData(result);
//                listView.onRefreshFinish(true);
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

//                listView.onRefreshFinish(true);
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    private void processData(String json) {

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

        }
    }

    private class TabDetailPagerListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

//    private class MyOnRefreshListener implements RefreshListView.OnRefreshListener {
//
//        @Override
//        public void onPullDownRefresh() {
//            getDataFromNet();
//        }
//
//        @Override
//        public void onLoadMore() {
//
//            if (TextUtils.isEmpty(moreUrl)) {
//                //没有加载更多
//                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
//                listView.onRefreshFinish(false);
//            } else {
//                //加载更多
//                getMoreDataFromNet();
//            }
//        }

    private void getMoreDataFromNet() {

        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isLoadMore = true;
                //解析和处理显示的数据
                processData(result);
//                    listView.onRefreshFinish(false);
                pull_refresh_list.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

//                    listView.onRefreshFinish(false);
                pull_refresh_list.onRefreshComplete();
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


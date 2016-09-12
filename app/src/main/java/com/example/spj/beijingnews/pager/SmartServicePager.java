package com.example.spj.beijingnews.pager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.adapter.SmartServicePagerAdapter;
import com.example.spj.beijingnews.base.BasePager;
import com.example.spj.beijingnews.domain.SmartServicePagerBean;
import com.example.spj.beijingnews.utils.CacheUtils;
import com.example.spj.beijingnews.utils.Constants;
import com.example.spj.beijingnews.utils.LogUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by spj on 2016/8/15.
 */
public class SmartServicePager extends BasePager {



    private MaterialRefreshLayout refreshLayout;
    private RecyclerView recyclerview;
    private ProgressBar pb_loading;

    //    默认状态
    private static final int STATE_NORMAL = 1;

    //    下拉刷新状态
    private static final int STATE_REFRES = 2;

    //    上拉刷新（加载更多）状态
    private static final int STATE_LOADMORE = 3;

    //     默认是正常状态
    private int state = STATE_NORMAL;

    //     每页的数据的个数
    private int pageSize = 10;

    //    当前页
    private int curPage = 1;

    //    总页数
    private int totalPage = 1;
    private String url;
    private List<SmartServicePagerBean.Wares> datas;
    private int currentPage;
    private SmartServicePagerAdapter adapter;

    public SmartServicePager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        //设置标题
        tv_title.setText("商城热卖");
        //联网请求，得到数据，创建视图
        View view = View.inflate(mContext, R.layout.smartservice_pager, null);
        refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refreshLayout);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //把子试图添加到BasePager上的Fragment上
        if (fl_content != null) {
            fl_content.removeAllViews();
        }
        fl_content.addView(view);

        initRefresh();

        setRequestParams();

        getDataFromNet();
    }

    private void getDataFromNet() {

        //获取保持的数据
        String json = CacheUtils.getSring(mContext, Constants.WARES_HOT_URL);
        if (!TextUtils.isEmpty(json)) {
            processedData(json);
        }

        //使用OKhttp第三方封装库请求网络
        OkHttpUtils.get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    private void processedData(String json) {
       SmartServicePagerBean bean = parsedJson(json);
        datas = bean.getList();
        currentPage = bean.getCurrentPage();
        totalPage = bean.getTotalPage();

        showData();
    }

    private void showData() {

        switch (state) {
            case STATE_NORMAL :

                //默认
                //设置适配器
                adapter = new SmartServicePagerAdapter(mContext,datas);
                recyclerview.setAdapter(adapter);

                //布局管理器
                recyclerview.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
                break;
            case STATE_REFRES://下拉刷新
                //1.把之前的数据清楚
                adapter.clearData();
                //2.添加新的数据 刷新
                adapter.addData(0,datas);
                //3.把状态还原
                refreshLayout.finishRefresh();
                break;
            case STATE_LOADMORE://加载更多
                //1.把新的数据添加到原来的数据末尾
                adapter.addData(adapter.getDataCount(),datas);
                //2.把状态还原
                refreshLayout.finishRefreshLoadMore();
                break;
        }
        pb_loading.setVisibility(View.GONE);
    }

    //使用Gson解析商城热卖的json数据
    private SmartServicePagerBean parsedJson(String json) {

        return new Gson().fromJson(json,SmartServicePagerBean.class);

    }

    private void setRequestParams() {

        state = STATE_NORMAL;
        curPage = 1;
        url = Constants.WARES_HOT_URL + pageSize + "&curPage=" + curPage;
    }

    private void initRefresh() {

        //设置下拉和上啦刷新
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                state = STATE_REFRES;
                curPage = 1;
                url = Constants.WARES_HOT_URL + pageSize + "&curPage=" + curPage;
                getDataFromNet();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (curPage < totalPage) {
                    state = STATE_LOADMORE;
                    curPage += 1;
                    url = Constants.WARES_HOT_URL + pageSize + "&curPage=" + curPage;
                    getDataFromNet();
                } else {
                    Toast.makeText(mContext, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }


    public class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            LogUtil.e("使用okhttp联网请求失败==" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {

            LogUtil.e("使用okhttp联网请求数据成功==" + response);
            //缓存数据
            CacheUtils.putString(mContext, Constants.WARES_HOT_URL, response);

            processedData(response);

            //设置 适配器
            switch (id) {
                case 100:
                    Toast.makeText(mContext, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(mContext, "https", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}

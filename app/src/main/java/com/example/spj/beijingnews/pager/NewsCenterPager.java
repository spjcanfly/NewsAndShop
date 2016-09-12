package com.example.spj.beijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.spj.beijingnews.activity.MainActivity;
import com.example.spj.beijingnews.base.BasePager;
import com.example.spj.beijingnews.base.MenuDetailBasePager;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.fragment.LeftmenuFragment;
import com.example.spj.beijingnews.menudetailpager.InteracMenuDetailPager;
import com.example.spj.beijingnews.menudetailpager.NewsMenuDetailPager;
import com.example.spj.beijingnews.menudetailpager.PhotosMenuDetailPager;
import com.example.spj.beijingnews.menudetailpager.TopicMenuDetailPager;
import com.example.spj.beijingnews.utils.CacheUtils;
import com.example.spj.beijingnews.utils.Constants;
import com.example.spj.beijingnews.utils.LogUtil;
import com.example.spj.beijingnews.volley.VolleyManager;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by spj on 2016/8/15.
 */
public class NewsCenterPager extends BasePager {

    //左侧菜单对应的数据集合
    private List<NewsCenterPagerBean.DataEntity> data;
    //详情页面的集合
    private ArrayList<MenuDetailBasePager> detaiBasePagers;
    private long startTime;

    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        ib_menu.setVisibility(View.VISIBLE);
        //设置标题
        tv_title.setText("新闻中心");
        //联网请求，得到数据，创建视图
        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.RED);
        tv.setTextSize(25);
        //把子试图添加到BasePager上的Fragment上
        fl_content.addView(tv);
        //绑定数据
        tv.setText("新闻中心内容");

        //得到缓存数据
        String saveJason = CacheUtils.getSring(mContext, Constants.NEWSCENTER_PAGER_URL);
        if (!TextUtils.isEmpty(saveJason)) {
            processData(saveJason);
        }

        startTime = SystemClock.uptimeMillis();
        //联网请求数据
        getDataFromNet();

//        getDataByVolley();
    }

    private void getDataByVolley() {
        //String 请求
        StringRequest request = new StringRequest(Request.Method.GET, Constants.NEWSCENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                LogUtil.e("passTime === " + passTime);

                //缓存数据
                CacheUtils.putString(mContext,Constants.NEWSCENTER_PAGER_URL,result);

                processData(result);
                //设置适配器
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            //解决乱码
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                try {
                    String parsed = new String(response.data,"UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        //添加到队列
        VolleyManager.getRequestQueue().add(request);

    }


    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NEWSCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                LogUtil.e("passTime === " + passTime);

                //缓存的数据放到sp存储中
                CacheUtils.putString(mContext, Constants.NEWSCENTER_PAGER_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

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

        NewsCenterPagerBean bean = parsedJson(json);
        String title = bean.getData().get(0).getChildren().get(1).getTitle();
        //给左侧菜单传递数据
        data = bean.getData();
        MainActivity mainActivity = (MainActivity) mContext;
        //得到左侧菜单
        LeftmenuFragment leftmenuFragment = mainActivity.getLeftmenuFragment();
        //添加详情页面
        detaiBasePagers = new ArrayList<>();
        detaiBasePagers.add(new NewsMenuDetailPager(mContext, data.get(0)));
        detaiBasePagers.add(new TopicMenuDetailPager(mContext, data.get(0)));
        detaiBasePagers.add(new PhotosMenuDetailPager(mContext,data.get(2)));
        detaiBasePagers.add(new InteracMenuDetailPager(mContext,data.get(2)));

        leftmenuFragment.setData(data);
    }

    //解析json数据，
    private NewsCenterPagerBean parsedJson(String json) {
        Gson gson = new Gson();
        NewsCenterPagerBean bean = gson.fromJson(json, NewsCenterPagerBean.class);
        return bean;
    }

    public void switchPager(int position) {
        //1.设置标题
        tv_title.setText(data.get(position).getTitle());
        //2.移除之前内容
        fl_content.removeAllViews();//移除之前的视图

        //3.添加新内容
        final MenuDetailBasePager detaiBasePager = detaiBasePagers.get(position);
        View rootView = detaiBasePager.rootView;
        detaiBasePager.initData();//初始化数据
        fl_content.addView(rootView);
        if(position == 2 ) {
            //图组详情页面
            ib_swich_list_grid.setVisibility(View.VISIBLE);
            //设置点击事件
            ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //1.得到图组详情页面对象
                    PhotosMenuDetailPager detaiPager = (PhotosMenuDetailPager) detaiBasePagers.get(2);
                    //2.调用图组对象的切换ListView和GridView的方法
                    detaiPager.swichListAndGrid(ib_swich_list_grid);
                }
            });
        }
    }
}

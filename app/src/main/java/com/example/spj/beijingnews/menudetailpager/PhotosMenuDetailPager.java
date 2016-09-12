package com.example.spj.beijingnews.menudetailpager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.activity.ShowImageActivity;
import com.example.spj.beijingnews.base.MenuDetailBasePager;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.domain.PhotosMenuDetailPagerBean;
import com.example.spj.beijingnews.utils.CacheUtils;
import com.example.spj.beijingnews.utils.Constants;
import com.example.spj.beijingnews.utils.LogUtil;
import com.example.spj.beijingnews.volley.VolleyManager;
import com.google.gson.Gson;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by spj on 2016/8/15.22:03
 */
public class PhotosMenuDetailPager extends MenuDetailBasePager{

    @ViewInject(R.id.listview)
    private ListView listView;

    @ViewInject(R.id.gridview)
    private GridView gridview;

    private NewsCenterPagerBean.DataEntity data;
    private String url;
    private boolean isShowListView;
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;
    private PhotosMenuDetailPagerAdapter adapter;

    public PhotosMenuDetailPager(Context context, NewsCenterPagerBean.DataEntity dataEntity) {
        super(context);
        this.data = dataEntity;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photos_menudetail_pager, null);
        x.view().inject(this, view);
        //设置某条item的监听
        listView.setOnItemClickListener(new MyOnItemClickListener());
        gridview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + data.getUrl();
        String saveJson = CacheUtils.getSring(context, url);
        if(!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {

        //String请求
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                //解析数据
                processData(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley联网请求失败");
            }
        }){
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
        VolleyManager.getRequestQueue().add(stringRequest);

    }

    private void processData(String json) {

        PhotosMenuDetailPagerBean photosMenuDetailPagerBean = parsedJson(json);
        isShowListView = true;
        //设置适配器

        news = photosMenuDetailPagerBean.getData().getNews();
        adapter = new PhotosMenuDetailPagerAdapter();
        listView.setAdapter(adapter);
    }

    private PhotosMenuDetailPagerBean parsedJson(String json) {
          return new Gson().fromJson(json,PhotosMenuDetailPagerBean.class);
    }

    public void swichListAndGrid(ImageButton ib_swich_list_grid) {

        if(isShowListView) {

            isShowListView = false;
            //显示GridView，隐藏Listview
            gridview.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            adapter = new PhotosMenuDetailPagerAdapter();
            gridview.setAdapter(adapter);
            //按钮显示 -- Listview
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);

        }else {

            isShowListView = true;
            //显示listVIew,隐藏GridView
            listView.setVisibility(View.VISIBLE);
            gridview.setVisibility(View.GONE);
            adapter = new PhotosMenuDetailPagerAdapter();
            listView.setAdapter(adapter);
            //按钮显示
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);

        }
    }


     class PhotosMenuDetailPagerAdapter extends BaseAdapter{


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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if(convertView == null) {
                convertView = View.inflate(context,R.layout.item_photos_menudetail,null);
                viewHolder = new ViewHolder();

                viewHolder.iv_photo_icon = (ImageView) convertView.findViewById(R.id.iv_photo_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的数据
            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            viewHolder.tv_title.setText(newsEntity.getTitle());
            String imageUrl = Constants.BASE_URL + newsEntity.getSmallimage();
           //使用Volley加载图片
            loaderImage(viewHolder, imageUrl);
            return convertView;
        }
    }

    static class ViewHolder{

        ImageView iv_photo_icon;
        TextView tv_title;
    }


    private void loaderImage(final ViewHolder viewHolder, String imageUrl) {

        //设置Tag
        viewHolder.iv_photo_icon.setTag(imageUrl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener listener = new ImageLoader.ImageListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，设置出错的图片
                viewHolder.iv_photo_icon.setImageResource(R.drawable.home_scroll_default);
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if(imageContainer != null) {
                    if(viewHolder.iv_photo_icon != null) {
                        if(imageContainer.getBitmap() != null) {
                            //设置图片
                            viewHolder.iv_photo_icon.setImageBitmap(imageContainer.getBitmap());
                        }else {
                            //设置默认图片
                            viewHolder.iv_photo_icon.setImageResource(R.drawable.home_scroll_default);
                        }
                    }
                }
            }
        };
        VolleyManager.getImageLoader().get(imageUrl,listener);
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            PhotosMenuDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            String imageUrl = Constants.BASE_URL + newsEntity.getLargeimage();
            Intent intent = new Intent(context, ShowImageActivity.class);
            intent.putExtra("url",imageUrl);
            context.startActivity(intent);
        }
    }
}

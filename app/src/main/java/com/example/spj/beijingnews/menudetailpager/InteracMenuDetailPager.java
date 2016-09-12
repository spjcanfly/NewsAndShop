package com.example.spj.beijingnews.menudetailpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.base.MenuDetailBasePager;
import com.example.spj.beijingnews.domain.NewsCenterPagerBean;
import com.example.spj.beijingnews.domain.PhotosMenuDetailPagerBean;
import com.example.spj.beijingnews.utils.BitmapCacheUtils;
import com.example.spj.beijingnews.utils.CacheUtils;
import com.example.spj.beijingnews.utils.Constants;
import com.example.spj.beijingnews.utils.LogUtil;
import com.example.spj.beijingnews.utils.NetCacheUtils;
import com.example.spj.beijingnews.volley.VolleyManager;
import com.google.gson.Gson;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by spj on 2016/8/15.22:03
 */
public class InteracMenuDetailPager extends MenuDetailBasePager{


    @ViewInject(R.id.listview)
    private ListView listView;

    @ViewInject(R.id.gridview)
    private GridView gridview;

    private NewsCenterPagerBean.DataEntity data;
    private String url;
    private boolean isShowListView;
    private List<PhotosMenuDetailPagerBean.DataEntity.NewsEntity> news;
    private PhotosMenuDetailPagerAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NetCacheUtils.SUCCESS:

                    position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;

                    if(listView.isShown()) {
                        ImageView iv_icon = (ImageView) listView.findViewWithTag(position);
                        if(iv_icon != null && bitmap != null) {
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }


                    if(gridview.isShown()) {
                        ImageView iv_icon = (ImageView) gridview.findViewWithTag(position);
                        if(iv_icon != null && bitmap != null) {
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }
                    LogUtil.e("网络请求图片成功"+position);
                    break;
                case NetCacheUtils.FAIL:

                    position = msg.arg1;
                    LogUtil.e("请求图片失败: "+position);
                    break;
            }
        }
    };
    private final BitmapCacheUtils bitmapCacheUtils;
    private int position;

    public InteracMenuDetailPager(Context context, NewsCenterPagerBean.DataEntity dataEntity) {
        super(context);
        this.data = dataEntity;
        bitmapCacheUtils = new BitmapCacheUtils(handler);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photos_menudetail_pager, null);
        x.view().inject(this, view);
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


    class PhotosMenuDetailPagerAdapter extends BaseAdapter {


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
//            //1.使用Volley加载图片
//            loaderImage(viewHolder, imageUrl);
            //2.使用自定义的三级缓存请求图片
//            viewHolder.iv_photo_icon.setTag(position);
//            Bitmap bitmap = bitmapCacheUtils.getBitmap(imageUrl, position);
//            if(bitmap != null) {
//                viewHolder.iv_photo_icon.setImageBitmap(bitmap);
//            }
            //使用Glide请求图片
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.home_scroll_default)//正在加载的图片
                    .error(R.drawable.home_scroll_default)//失败的默认图片
                    .into(viewHolder.iv_photo_icon);
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
}

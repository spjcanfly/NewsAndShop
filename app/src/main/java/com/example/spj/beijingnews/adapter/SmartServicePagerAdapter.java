package com.example.spj.beijingnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.domain.ShoppingCart;
import com.example.spj.beijingnews.domain.SmartServicePagerBean;
import com.example.spj.beijingnews.utils.CartProvider;

import java.util.List;

/**
 * Created by spj on 2016/8/29.
 */
public class SmartServicePagerAdapter extends RecyclerView.Adapter<SmartServicePagerAdapter.ViewHolder> {

    private final Context context;
    private final List<SmartServicePagerBean.Wares> datas;

    /**
     * 数据存储类
     */
    private CartProvider cartProvider;

    public SmartServicePagerAdapter(Context mContext, List<SmartServicePagerBean.Wares> datas) {
        this.context = mContext;
        this.datas = datas;
        cartProvider = new CartProvider(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_smartservice_pager, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        final SmartServicePagerBean.Wares wares = datas.get(position);

        //2.绑定数据
        Glide.with(context)
                .load(wares.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.news_pic_default)//真正加载的默认图片
                .error(R.drawable.news_pic_default)//失败的默认图片
                .into(holder.iv_icon);
        holder.tv_name.setText(wares.getName());
        holder.tv_price.setText("￥" + wares.getPrice());

        //设置点击时间
        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //把商品转换成ShoppingCart
                ShoppingCart cart = cartProvider.conversion(wares);

                cartProvider.addData(cart);
                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void clearData() {

        datas.clear();
        notifyItemRangeChanged(0, datas.size());
    }

    //根据制定位置添加数据
    public void addData(int position, List<SmartServicePagerBean.Wares> data) {
        if (data != null && data.size() > 0) {
            datas.addAll(position, data);
            notifyItemRangeChanged(position, datas.size());

        }
    }

    public int getDataCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private Button btn_buy;

        public ViewHolder(View itemView) {
            super(itemView);

            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            btn_buy = (Button) itemView.findViewById(R.id.btn_buy);
        }
    }
}

package com.example.spj.beijingnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.domain.ShoppingCart;
import com.example.spj.beijingnews.utils.CartProvider;
import com.example.spj.beijingnews.view.NumberAddSubView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by spj on 2016/8/30.
 */
public class GovaffairPagerAdapter extends RecyclerView.Adapter<GovaffairPagerAdapter.ViewHolder>{

    private final Context context;
    private final List<ShoppingCart> carts;
    private final CheckBox checkbox_all;
    private final TextView tv_total_price;
    private final CartProvider cartProvider;

    public GovaffairPagerAdapter(Context mContext, final List<ShoppingCart> carts, final CheckBox checkbox_all, TextView tv_total_price) {
        this.context = mContext;
        this.carts = carts;
        this.checkbox_all = checkbox_all;
        this.tv_total_price = tv_total_price;
        cartProvider = new CartProvider(context);
        showTotalPrice();

        //设置某个item的监听
       setmOnItemClickListener(new OnItemClickListener() {
           @Override
           public void onItemClick(View view, int position) {
               //得到对应位置的对象
               ShoppingCart cart = carts.get(position);
               //2.勾选对象改变状态
               cart.setIsCheck(!cart.isCheck());
               //3.状态刷新
               notifyItemChanged(position);
               //4.校验全选和非全选
                checkAll();
               //显示总价格
               showTotalPrice();
           }
       });

        //校验全选
        checkAll();

        //设置点击事件
        checkbox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.得到是否选中的状态
                boolean isChecked = checkbox_all.isChecked();
                //2.设置全选和非全选
                checkAll_none(isChecked);
                //3.重新计算总价格
                showTotalPrice();
            }
        });
    }

    //设置全选和非全选
    public void checkAll_none(boolean isChecked) {
        if(carts != null && carts.size()>0) {
            for (int i = 0; i < carts.size(); i++) {
                ShoppingCart cart = carts.get(i);
                cart.setIsCheck(isChecked);
                notifyItemChanged(i);
            }
        }
    }

    //校验全选
    public void checkAll() {
        if(carts !=null && carts.size()>0) {
            int number = 0;
            for (int i = 0; i < carts.size(); i++) {
                ShoppingCart cart = carts.get(i);
                if(cart.isCheck()) {
                    number++;
                }else {
                    //如果有一个没选中，那么全选按钮为false
                    checkbox_all.setChecked(false);
                }
            }
            if(number == carts.size()) {
                //如果全部选中了，全选按钮为true
                checkbox_all.setChecked(true);
            }

        }else {
            checkbox_all.setChecked(false);
        }
    }

    public void showTotalPrice() {
        tv_total_price.setText("合计：￥" + getToalPrice());
    }

    public double getToalPrice() {
        double totalPrice = 0;
        if(carts != null && carts.size()>0) {
            for (int i = 0; i < carts.size(); i++) {
                ShoppingCart cart = carts.get(i);
                //计算选中的商品的价格
                if(cart.isCheck()) {
                    totalPrice += cart.getPrice() * cart.getCount();
                }
            }
        }
        return totalPrice;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_govaffair_pager, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        final ShoppingCart cart = carts.get(position);
        //2.绑定数据
        Glide.with(context)
                .load(cart.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.news_pic_default)
                .error(R.drawable.news_pic_default)
                .into(holder.iv_icon);

        holder.tv_name.setText(cart.getName());
        holder.tv_price.setText("￥" + cart.getPrice());
        holder.numberAddSeubView.setValue(cart.getCount());
        holder.checkbox.setChecked(cart.isCheck());

        //设置增加减少按钮的点击监听
        holder.numberAddSeubView.setOnNumberClickListener(new NumberAddSubView.OnNumberClickListener() {
            @Override
            public void onButtonSub(View view, int value) {
                //减少，1.更新数据
                cart.setCount(value);
                cartProvider.updataData(cart);
                //2.重新显示价格
                showTotalPrice();
            }

            @Override
            public void onButtonAdd(View view, int value) {
               //增加.1.更新数据
                cart.setCount(value);
                //重新更新到本地
                cartProvider.updataData(cart);

                //2.重新显示价格
                showTotalPrice();
            }
        });


    }

    @Override
    public int getItemCount() {

        return carts.size();
    }

    public void deleteData() {
         if(carts != null && carts.size()>0) {
             for (Iterator iterator = carts.iterator();iterator.hasNext();){

                 ShoppingCart cart = (ShoppingCart) iterator.next();
                 if(cart.isCheck()) {
                     //根据对象找到它的位置
                     int position = carts.indexOf(cart);
                     //1.删除本地缓存
                     cartProvider.DeleteData(cart);
                     //2.删除当前内存的
                     iterator.remove();
                     //3.刷新数据
                     notifyItemRemoved(position);

                 }
             }
         }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private CheckBox checkbox;
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private NumberAddSubView numberAddSeubView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            numberAddSeubView = (NumberAddSubView) itemView.findViewById(R.id.numberAddSubView);

            //设置点击某条的监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view,getLayoutPosition());
                    }
                }
            });

        }
    }

    //点击某个的监听
     public interface OnItemClickListener{
        //点击某个item的时候回调
         void onItemClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}

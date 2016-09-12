package com.example.spj.beijingnews.base;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.activity.MainActivity;

/**
 * Created by spj on 2016/8/14.
 */
public class BasePager {

    public  Context mContext;//MainActivity
    public View rootView;
    public ImageButton ib_menu;
    public TextView tv_title;
    public FrameLayout fl_content;
    public ImageButton ib_swich_list_grid;
    public Button btn_cart;

    public BasePager(Context context) {
        this.mContext = context;
        //构造执行，视图初始化
        rootView = initView();
    }

    private View initView() {
        View view = View.inflate(mContext, R.layout.basepager,null);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        ib_swich_list_grid = (ImageButton) view.findViewById(R.id.ib_swich_list_grid);
        btn_cart = (Button) view.findViewById(R.id.btn_cart);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) mContext;
                //切换左侧的开和关
                mainActivity.getSlidingMenu().toggle();
            }
        });

        return view;
    }

    /**
     * 初始化数据;当孩子需要初始化数据;或者绑定数据;联网请求数据并且绑定的时候，重写该方法
     */
    public void initData(){

    }

}

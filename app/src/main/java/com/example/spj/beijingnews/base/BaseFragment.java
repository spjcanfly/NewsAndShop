package com.example.spj.beijingnews.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by spj on 2016/8/13.
 */
public abstract class BaseFragment extends Fragment{


    public Context mContext;

    //当Fragment被创建的时候调用这个方法
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    //当视图被创建的时候回调
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();

    }
    //让继承它的类实现自己的视图，达到自己特有的效果
    public abstract View initView();

    //当activity被创建的时候回调
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //如果自己的页面没有数据，联网请求数据，并且绑定到initView初始化的视图上
    public void initData() {

    }

}

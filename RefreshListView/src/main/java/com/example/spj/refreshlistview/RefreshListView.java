package com.example.spj.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by spj on 2016/8/18.18:19
 */
public class RefreshListView extends ListView {

    private LinearLayout headerView;
    private ImageView iv_red_arrow;
    private ProgressBar pb_status;
    private TextView tv_status;
    private TextView tv_refresh_time;
    private LinearLayout ll_pull_down;

    private Animation upAnimation;
    private Animation downAnimation;

    //顶部轮播图部分
    private View topnewsView;
    //下拉刷新控件的高
    private int headerViewHeight;
    //listView在Y轴的坐标
    private int listViewOnScreenY = -1;
    //下拉刷新状态
    private static final int PULL_DOWN_REFRESH = 1;
    //手松刷新状态
    private static final int RELEASE_REFRESH = 2;
    //正在刷新
    private static final int REFRESHING = 3;
    //当前的状态
    private   int currentState = PULL_DOWN_REFRESH;
    private String systemTime;
    private boolean isLoadMore = false;
    private View footView;
    private int footViewHeight;

    //改成this的话，不管怎么样都会走第三个构造的方法
    public RefreshListView(Context context) {
        this(context, null);
    }

    //改成this的话，不管怎么样都会走第三个构造的方法
    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //因为这个构造必走，所以在这里写初始化
    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //添加头布局
        initHeaderView(context);
        //初始化动画
        initAnimation();
        //添加脚布局
        initFooterView(context);
    }

    private void initFooterView(Context context) {
        footView = View.inflate(context, R.layout.refresh_footer, null);
        footView.measure(0, 0);
        footViewHeight = footView.getMeasuredHeight();
        /**
         View.setPadding(0,-控件高，0,0）；//完成隐藏
         View.setPadding(0,0，0,0）；//完成显示
         View.setPadding(0,控件高，0,0）；//两倍完全显示
         */
        footView.setPadding(0,-footViewHeight,0,0);
        addFooterView(footView);
        //监听滑动到ListView最后一个可见的item
        setOnScrollListener(new MyOnScrollListener());
    }


    //初始化动画
    private void initAnimation() {
        //-180 逆时针旋转，相对于自身，旋转点中间。
        upAnimation = new RotateAnimation(0,-180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(500);
        //保存旋转后的状态
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180,-360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(500);
        //保存旋转后的状态
        downAnimation.setFillAfter(true);
    }

    private void initHeaderView(Context context) {

        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header, null);
        ll_pull_down =  (LinearLayout) headerView.findViewById(R.id.ll_pull_down);
        iv_red_arrow = (ImageView) headerView.findViewById(R.id.iv_red_arrow);
        pb_status = (ProgressBar) headerView.findViewById(R.id.pb_status);
        tv_status = (TextView) headerView.findViewById(R.id.tv_status);
        tv_refresh_time = (TextView) headerView.findViewById(R.id.tv_refresh_time);

        ll_pull_down.measure(0,0);//测量-为了调用测量这个方法，参数无所谓
        headerViewHeight = ll_pull_down.getMeasuredHeight();

        //隐藏下拉刷新的布局
        ll_pull_down.setPadding(0,-headerViewHeight,0,0);

        RefreshListView.this.addHeaderView(headerView);
    }

    private  float startY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :

                //1.记录起始坐标
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE :
                //说明startY因为事件的冲突而没有获得值，所以需要在这里重新给它值
                if(startY == 0) {
                    startY = ev.getY();
                }
                //判断顶部轮播图是否完全显示
                boolean isDisplayTopNews = isDisplayTopNews();//完全显示就是下拉刷新
                if(!isDisplayTopNews) {
                    //上拉刷新
                    break;
                }
                //2.记录结束坐标
                float endY = ev.getY();
                //3.计算偏移量
                float distanceY = endY - startY;
                if(distanceY > 0 ) {
                    //向下滑动
                    //动态显示下拉刷新控件距离顶部的位置
                    int paddingTop = (int) (-headerViewHeight + distanceY);
                    if(paddingTop < 0 && currentState != PULL_DOWN_REFRESH) {
                        //下拉刷新
                        currentState = PULL_DOWN_REFRESH;
                        //更新状态
                        refreshStatus();
                    }else if(paddingTop > 0 && currentState != RELEASE_REFRESH){
                        //松手刷新
                        currentState = RELEASE_REFRESH;
                        //更新状态
                        refreshStatus();
                    }
                    ll_pull_down.setPadding(0,paddingTop,0,0);
                }

                break;
            case MotionEvent.ACTION_UP :
                startY = 0;
                if(currentState == PULL_DOWN_REFRESH) {
                    //View.setPadding(0，-控件高，0，0); 完成隐藏
                    ll_pull_down.setPadding(0,-headerViewHeight,0,0);
                }else if(currentState == RELEASE_REFRESH) {
                    currentState = REFRESHING;

                    //View.setPadding(0,0,0,0);//完成显示
                    ll_pull_down.setPadding(0,0,0,0);
                    //状态要更新
                    refreshStatus();
                    //回调接口
                    if(mOnRefreshListener != null) {
                        mOnRefreshListener.onPullDownRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshStatus() {

        switch (currentState) {
            //下拉刷新
            case PULL_DOWN_REFRESH :

                tv_status.setText("下拉刷新...");
                pb_status.setVisibility(GONE);
                iv_red_arrow.clearAnimation();
                iv_red_arrow.startAnimation(downAnimation);
                break;
            //手松刷新
            case RELEASE_REFRESH:

                tv_status.setText("手松刷新...");
                iv_red_arrow.clearAnimation();
                iv_red_arrow.setAnimation(upAnimation);
                break;
            //正在刷新
            case REFRESHING :

                pb_status.setVisibility(VISIBLE);
                iv_red_arrow.setVisibility(GONE);
                iv_red_arrow.clearAnimation();
                tv_status.setText("正在刷新...");
                break;
        }
    }

    //判断顶部轮播图是否完全显示
    //当ListView在Y轴的坐标小于或者等于顶部轮播图在Y轴的坐标的时候，就是完全显示
    private boolean isDisplayTopNews() {

        int[] location = new int[2];
        //说明没有初始化，所以得走一下，以后不用走
        if(listViewOnScreenY == -1) {
            this.getLocationOnScreen(location);
            listViewOnScreenY = location[1];
        }
       //得到顶部轮播图部分在Y周的坐标
        topnewsView.getLocationOnScreen(location);
        int topnewsViewOnScreenY = location[1];
//        if(listViewOnScreenY <= topnewsViewOnScreenY) {
//            return  true;
//        }else {
//            return  false;
//        }
        return listViewOnScreenY <= topnewsViewOnScreenY;
    }


    public void addTopNews(View view1) {
        this.topnewsView = view1;
        if(view1 != null && headerView != null) {
            //添加顶部轮播图
            headerView.addView(view1);
        }
    }

    //把下拉刷新状态恢复成初始的状态
    public void onRefreshFinish(boolean success) {




        if(isLoadMore) {
              //加载更多
            isLoadMore = false;
            footView.setPadding(0, -footViewHeight, 0, 0);
        }else{
            //下拉刷新
            iv_red_arrow.clearAnimation();
            iv_red_arrow.setVisibility(VISIBLE);
            pb_status.setVisibility(GONE);
            tv_status.setText("下拉刷新");
            ll_pull_down.setPadding(0, -headerViewHeight, 0, 0);
            currentState=PULL_DOWN_REFRESH;
            if(success) {
//                LogUtil.e(tv_refresh_time+"    1111111111");
                tv_refresh_time.setText("更新时间：" + getSystemTime());
            }
        }

    }

    public String getSystemTime() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(new Date());
    }

    public interface OnRefreshListener{
        //下拉刷新的时候调用这个方法
        void onPullDownRefresh();

        //当加载更多的时候回调这个方法
        void onLoadMore();

    }
    private OnRefreshListener mOnRefreshListener;

    //监听视图刷新的监听
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.mOnRefreshListener = onRefreshListener;
    }

    private class MyOnScrollListener implements OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //静止或者惯性滚动，并且是最后一个可见的时候
            if(scrollState  == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                 //到最后一个可见
                if(getLastVisiblePosition() == getAdapter().getCount()-1 && !isLoadMore) {

                    //显示加载更多控件
                    footView.setPadding(10,10,10,10);
                    //设置状态
                    isLoadMore = true;
                    //回调接口
                    if(mOnRefreshListener != null) {
                        mOnRefreshListener.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }
}

package com.example.spj.beijingnews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.example.spj.beijingnews.R;
import com.example.spj.beijingnews.utils.CacheUtils;

public class SplashActivity extends Activity {

    //静态常量
    public static final String START_MAIN = "start_main";
    private RelativeLayout rl_splash_root;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rl_splash_root = (RelativeLayout) findViewById(R.id.rl_splash_root);

        //渐变动画,缩放动画，旋转动画
        AlphaAnimation aa = new AlphaAnimation(0,1);
//        aa.setDuration(500);//持续播放时间
        aa.setFillAfter(true);

        ScaleAnimation sa = new ScaleAnimation(0,1,0,1,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
//        sa.setDuration(500);
        sa.setFillAfter(true);

        RotateAnimation ra = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
//        ra.setDuration(500);
        ra.setFillAfter(true);

        AnimationSet aset = new AnimationSet(false);
        //添加动画没有先后顺序,便于同时播放
        aset.addAnimation(aa);
        aset.addAnimation(sa);
        aset.addAnimation(ra);
        aset.setDuration(2000);//覆盖上面设置的500ms

        rl_splash_root.startAnimation(aset);

        aset.setAnimationListener(new MyAnimationListener());
    }

    class MyAnimationListener implements Animation.AnimationListener{

        //开始播放动画
        @Override
        public void onAnimationStart(Animation animation) {

        }

        //结束播放
        @Override
        public void onAnimationEnd(Animation animation) {

            //判断是否进入过主页面
            boolean isStartMain = CacheUtils.getBoolean(SplashActivity.this, START_MAIN);
            if(isStartMain) {
                // 如果进入过主页面
                intent = new Intent(SplashActivity.this, MainActivity.class);


            }else {
                //没有进入过，进入引导界面
                 intent = new Intent(SplashActivity.this, GuideActivity.class);
            }
            startActivity(intent);
            //关闭页面
            finish();


        }

        //重复播放动画
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}

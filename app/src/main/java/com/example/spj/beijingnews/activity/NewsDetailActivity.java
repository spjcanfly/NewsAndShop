package com.example.spj.beijingnews.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spj.beijingnews.R;

public class NewsDetailActivity extends Activity implements View.OnClickListener{

    private TextView tvTitle;
    private ImageButton ibMenu;
    private ImageButton ibBack;
    private ImageButton ibTextsize;
    private ImageButton ibShare;

    private WebView webview;
    private ProgressBar pbLoading;
    private String url;
    private WebSettings webSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initView();

        initData();
    }

    private void initData() {
        url = getIntent().getStringExtra("url");

        //设置支持javascript
        webSettings=webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //设置双击变大变小
        webSettings.setUseWideViewPort(true);
        //增加缩放按钮
        webSettings.setBuiltInZoomControls(true);
        //设置文字的大小
        webSettings.setTextZoom(100);
        //不让从当前网页跳到系统的浏览器
        webview.setWebViewClient(new WebViewClient(){
            //当加载页面完成的时候回调,将进度条消失
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });
        webview.loadUrl(url);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ibMenu = (ImageButton) findViewById(R.id.ib_menu);
        ibBack = (ImageButton) findViewById(R.id.ib_back);
        ibTextsize = (ImageButton) findViewById(R.id.ib_textsize);
        ibShare = (ImageButton) findViewById(R.id.ib_share);
        webview = (WebView) findViewById(R.id.webview);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        tvTitle.setVisibility(View.GONE);
        ibMenu.setVisibility(View.GONE);
        ibBack.setVisibility(View.VISIBLE);
        ibTextsize.setVisibility(View.VISIBLE);
        ibShare.setVisibility(View.VISIBLE);

        ibBack.setOnClickListener(this);
        ibTextsize.setOnClickListener(this);
        ibShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ibBack) {
            // Handle clicks for ibBack
            finish();
        } else if (v == ibTextsize) {
            // Handle clicks for ibTextsize
//            Toast.makeText(NewsDetailActivity.this, "设置文字大小", Toast.LENGTH_SHORT).show();
            showChangeTextSizeDialog();
        } else if (v == ibShare) {
            // Handle clicks for ibShare
            Toast.makeText(NewsDetailActivity.this, "分享", Toast.LENGTH_SHORT).show();
        }
    }


//    private int tempSize = 2;
    private int realSize = 2;
    private void showChangeTextSizeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("设置字体");
        String[] item = new String[]{"超大字体","大字体","正常字体","小字体","超小字体"};
        alertDialog.setSingleChoiceItems(item, realSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                realSize=which;
            }
        });
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                changeTextSize(realSize);
            }
        });
        alertDialog.setNegativeButton("取消", null);
        alertDialog.show();
    }

    private void changeTextSize(int realSize) {
        switch (realSize) {
            case 0 :
                webSettings.setTextZoom(200);
                break;
            case 1 :
                webSettings.setTextZoom(150);
                break;
            case 2 :
                webSettings.setTextZoom(100);
                break;
            case 3 :
                webSettings.setTextZoom(75);
                break;
            case 4 :
                webSettings.setTextZoom(50);
                break;
        }
    }
}

package com.example.spj.beijingnews.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.spj.beijingnews.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImageActivity extends Activity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        url = getIntent().getStringExtra("url");
        PhotoView iv_photo = (PhotoView) findViewById(R.id.iv_photo);
        final PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(iv_photo);
        //使用picasso加载图片
        Picasso.with(this)
             .load(url)
             .into(iv_photo, new Callback() {
                 @Override
                 public void onSuccess() {
                     photoViewAttacher.update();
                 }

                 @Override
                 public void onError() {

                 }
             });

    }
}

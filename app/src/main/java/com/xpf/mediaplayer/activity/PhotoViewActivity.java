package com.xpf.mediaplayer.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.xpf.mediaplayer.R;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;


/**
 * Created by xinpengfei on 2019/02/22.
 * Function:PhotoView 图片显示页面
 */
public class PhotoViewActivity extends AppCompatActivity {

    private static final String TAG = "PhotoViewActivity";
    private String url;
    private ImageOptions imageOptions;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        url = getIntent().getStringExtra("url");
        Log.e(TAG, url);

        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // 设置是否有返回箭头
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
            actionBar.setHomeButtonEnabled(true); //设置返回键可用

            mToolbar.setNavigationOnClickListener(v -> finish());
        }

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
                //设置圆角
                .setRadius(DensityUtil.dip2px(5))
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default)
                .setFailureDrawableId(R.drawable.video_default)
                .build();

        x.image().bind(photoView, url, imageOptions, new Callback.CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable result) {
                attacher.update();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }
}

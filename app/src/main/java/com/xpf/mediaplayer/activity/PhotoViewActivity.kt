package com.xpf.mediaplayer.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.xpf.mediaplayer.R
import org.xutils.common.Callback.CancelledException
import org.xutils.common.Callback.CommonCallback
import org.xutils.common.util.DensityUtil
import org.xutils.image.ImageOptions
import org.xutils.x

/**
 * Created by Vance on 2019/02/22.
 * Function:PhotoView 图片显示页面
 */
class PhotoViewActivity : AppCompatActivity() {
    private var url: String? = null
    private var imageOptions: ImageOptions? = null
    private var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        url = intent.getStringExtra("url")
        Log.e(TAG, url!!)

        val photoView = findViewById<View>(R.id.iv_photo) as PhotoView

        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        // 设置是否有返回箭头
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true) //左侧添加一个默认的返回图标
            actionBar.setHomeButtonEnabled(true) //设置返回键可用

            mToolbar!!.setNavigationOnClickListener { finish() }
        }

        val attached = PhotoViewAttacher(photoView)

        imageOptions =
            ImageOptions.Builder() //.setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(80))
                //设置圆角
                .setRadius(DensityUtil.dip2px(5f))
                .setIgnoreGif(false) //是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default)
                .setFailureDrawableId(R.drawable.video_default)
                .build()

        x.image().bind(photoView, url, imageOptions, object : CommonCallback<Drawable?> {
            override fun onSuccess(result: Drawable?) {
                attached.update()
            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {
            }

            override fun onCancelled(cex: CancelledException) {
            }

            override fun onFinished() {
            }
        })
    }

    companion object {
        private const val TAG = "PhotoViewActivity"
    }
}

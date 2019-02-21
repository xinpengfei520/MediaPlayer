package com.xpf.mediaplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by xinpengfei on 2016/10/8.
 * Function:Custom VitamioVideoView
 */
public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {

    public VitamioVideoView(Context context) {
        this(context, null);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置视频的宽和高
     *
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth, int videoHeight) {
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = videoWidth;
        l.height = videoHeight;
        setLayoutParams(l);
    }
}

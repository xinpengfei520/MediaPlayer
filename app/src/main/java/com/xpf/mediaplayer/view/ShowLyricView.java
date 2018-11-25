package com.xpf.mediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.xpf.mediaplayer.bean.Lyric;

import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;

/**
 * Created by xinpengfei on 2016/10/10.
 * Function:显示歌词的类，并且显示同步的控件
 */
public class ShowLyricView extends AppCompatTextView {

    /**
     * 存放每一句歌词的集合
     */
    private ArrayList<Lyric> lyrics;
    /**
     * 用于绘制高亮歌词的画笔
     */
    private Paint paint;
    /**
     * 用于绘制未播放歌词的白画笔
     */
    private Paint whitepaint;
    /**
     * 歌词布局的宽和高
     */
    private int width;
    private int height;

    /**
     * 歌词列表中的索引
     */
    private int index;
    /**
     * 每行歌词的高度
     */
    private float textHeight = 20;
    /**
     * 记录歌曲的播放进度
     */
    private float currentPosition;
    /**
     * 时间戳(即在适当的时间显示对应的歌词)
     */
    private float timePoint;
    /**
     * 某一句的高亮显示时间(两句歌词显示时间相减)
     */
    private float sleepTime;

    /**
     * 在布局文件中使用，将会采用该方法实例化该类，如果不存在，会崩溃!
     *
     * @param context:
     * @param attrs:
     */
    public ShowLyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    /**
     * 初始化歌词视图(初始化画笔的属性)
     */
    private void initView() {

        textHeight = DensityUtil.dip2px(20);
        //画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtil.dip2px(20));
        paint.setTextAlign(Paint.Align.CENTER);//水平居中对齐

        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setAntiAlias(true);
        whitepaint.setTextSize(DensityUtil.dip2px(16));
        whitepaint.setTextAlign(Paint.Align.CENTER);//水平居中对齐

        //制作假设(示例)歌词
        lyrics = new ArrayList<>();
        Lyric lyric = new Lyric();
        for (int i = 0; i < 1000; i++) {
            lyric.setContent(i + "aaaaaaaa" + i);
            lyric.setSleepTime(1000);
            lyric.setTimePoint(1000 * i);
            lyrics.add(lyric);
            lyric = new Lyric();
        }

    }

    /**
     * 绘制当前的歌词
     *
     * @param canvas:
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {

            float push = 0;
            if (sleepTime == 0) {
                push = 0;
            } else {
                //这一句花的时间：这一句休眠时间 = 这一句要移动的距离:总距离(行高)
                //这一句要移动的距离 = (这一句花的时间/这一句休眠的时间)*总距离(行高)
                //float delta = ((currentPosition - timePoint) / sleepTime) * textHeight;

                //在屏幕上的坐标 = 总距离(行高) + 这一句要移动的距离
                push = textHeight + ((currentPosition - timePoint) / sleepTime) * textHeight;
            }

            canvas.translate(0, -push);//X轴上不移动，Y轴向上移动，即歌词从下向上滚动，所以为负

            //有歌词--> 绘制当前歌词
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paint);

            //绘制前面部分
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i++) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitepaint);
            }

            //绘制后面部分
            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitepaint);
            }

        } else {
            //没有歌词
            canvas.drawText("没有找到歌词...", width / 2, height / 2, paint);
        }
    }

    /**
     * 当前歌曲的播放进度
     *
     * @param position:
     */
    public void setNextShowLyric(int position) {

        this.currentPosition = position;
        //根据当前歌曲的播放进度，找到歌词列表的索引
        //重新绘制
        if (lyrics == null || lyrics.size() == 0)
            return;

        //歌词不为空
        for (int i = 1; i < lyrics.size(); i++) {
            if (currentPosition < lyrics.get(i).getTimePoint()) {

                int timeIndex = i - 1;//0
                if (currentPosition >= lyrics.get(timeIndex).getTimePoint()) {

                    index = timeIndex;//0

                    //缓缓往上推移
                    //timePoint:时间戳
                    timePoint = lyrics.get(index).getTimePoint();
                    //sleepTime:高亮显示时间
                    sleepTime = lyrics.get(index).getSleepTime();
                }
            } else {
                index = i;
            }

        }
        invalidate();//重绘-->回调导致onDraw()方法执行
    }

    public void setLyric(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }
}

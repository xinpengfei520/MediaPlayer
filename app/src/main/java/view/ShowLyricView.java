package view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import domain.Lyric;

/**
 * Created by xinpengfei on 2016/10/10.
 * <p>
 * Function :显示歌词，并且显示同步的控件
 */

public class ShowLyricView extends TextView {

    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint whitepaint;
    private int width;
    private int height;

    /**
     * 歌词列表中的索引
     */
    private int index;
    

    public ShowLyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}

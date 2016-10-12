package view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mediaplayer.R;
import com.atguigu.mediaplayer.SearchActivity;

/**
 * Created by xinpengfei on 2016/10/12.
 * 微信:18091383534
 * Function :标题栏
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {

    private TextView textView;
    private RelativeLayout relativeLayout;
    private ImageView imageView;

    private Context context;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
//        View.inflate(context, R.layout.titblebar,this);
    }

    /**
     * 当布局加载完成的时候调用此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textView = (TextView) getChildAt(1);
        relativeLayout = (RelativeLayout) getChildAt(2);
        imageView = (ImageView) getChildAt(3);

        //设置点击事件
        textView.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_search:
                Intent intent = new Intent(context,SearchActivity.class);

                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_history:
                Toast.makeText(context, "历史", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

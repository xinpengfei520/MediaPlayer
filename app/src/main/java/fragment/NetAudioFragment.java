package fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by xinpengfei on 2016/9/28.
 * <p>
 * Function: 网络音乐
 */

public class NetAudioFragment extends BaseFragment {

    private TextView textView;


    /**
     * 初始化视图
     * @return
     */
    @Override
    public View initView() {

        Log.e("TAG", "网络音乐UI创建了");
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);//设置内容居中

        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络音乐数据绑定了");
        textView.setText("网络音乐的内容");
    }
}

package fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by xinpengfei on 2016/9/28.
 * <p>
 * Function :
 */

public class NetVideoFragment extends BaseFragment {

    private TextView textView;

    @Override
    public View initView() {

        Log.e("TAG", "网络视频UI创建了");

        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);

        return textView;

    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络视频数据绑定了");
        textView.setText("网络视频的内容");
    }
}

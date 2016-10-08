package fragment;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;

/**
 * Created by xinpengfei on 2016/9/28.
 * <p>
 * Function :
 */

public class NetVideoFragment extends BaseFragment {

    private TextView textView;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tv_nodata;

    @Override
    public View initView() {

        Log.e("TAG", "网络视频UI创建了");

//        textView = new TextView(context);
//        textView.setTextSize(25);
//        textView.setTextColor(Color.RED);
//        textView.setGravity(Gravity.CENTER);
//
//        return textView;

        View view = View.inflate(context, R.layout.fragment_netvideo, null);
        listview = (ListView) view.findViewById(R.id.listview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        return view;

    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络视频数据绑定了");
//        textView.setText("网络视频的内容");
    }
}

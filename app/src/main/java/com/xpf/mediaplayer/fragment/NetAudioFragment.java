package com.xpf.mediaplayer.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xpf.mediaplayer.R;
import com.xpf.mediaplayer.activity.PhotoViewActivity;
import com.xpf.mediaplayer.adapter.NetAudioAdapter;
import com.xpf.mediaplayer.bean.NetAudioBean;
import com.xpf.mediaplayer.utils.CacheUtils;
import com.xpf.mediaplayer.utils.Constants;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by Vance on 2016/9/28.
 * Function: 网络音乐
 */
public class NetAudioFragment extends BaseFragment {

    private static final String TAG = "NetAudioFragment";
    private ListView listview;
    /**
     * 数据集合
     */
    private List<NetAudioBean.ListBean> lists;

    /**
     * 初始化视图
     *
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_audio_pager, null);
        listview = (ListView) view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e(TAG, "网络音乐数据绑定了");
        String saveJson = CacheUtils.getString(context, Constants.ALL_RES_URL);
        // 如果不为空从本地取，否则从网络获取
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        } else {
            getDataFromNet();
        }
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "result===" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError===" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled===" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished===");
            }
        });
    }

    /**
     * 解析和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        NetAudioBean netAudioBean = new Gson().fromJson(json, NetAudioBean.class);
        lists = netAudioBean.getList();
        if (lists != null && lists.size() > 0) {
            listview.setAdapter(new NetAudioAdapter(context, lists));
            setListener();
        } else {
            Toast.makeText(context, "没有得到数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void setListener() {
        listview.setOnItemClickListener((parent, view, position, id) -> {
            NetAudioBean.ListBean listEntity = lists.get(position);
            if (listEntity != null) {
                //3.传递视频列表
                Intent intent = new Intent(context, PhotoViewActivity.class);
                if (listEntity.getType().equals("gif")) {
                    String url = listEntity.getGif().getImages().get(0);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                } else if (listEntity.getType().equals("image")) {
                    String url = listEntity.getImage().getBig().get(0);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                }
            }
        });
    }

}

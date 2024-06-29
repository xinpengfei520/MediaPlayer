package com.xpf.mediaplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.xpf.mediaplayer.R;
import com.xpf.mediaplayer.activity.SystemPlayerActivity;
import com.xpf.mediaplayer.adapter.NetVideoFragmentAdapter;
import com.xpf.mediaplayer.bean.MediaItem;
import com.xpf.mediaplayer.utils.CacheUtils;
import com.xpf.mediaplayer.utils.Constants;
import com.xpf.mediaplayer.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Vance on 2016/9/28.
 * Function:
 */
public class NetVideoFragment extends BaseFragment {

    private static final String TAG = "NetVideoFragment";
    private TextView textView;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tv_nodata;
    private NetVideoFragmentAdapter adapter;
    /**
     * 视频列表
     */
    private ArrayList<MediaItem> mediaItems;
    private MaterialRefreshLayout refresh;

    @Override
    public View initView() {
        Log.e(TAG, "网络视频UI创建了");
        View view = View.inflate(context, R.layout.fragment_netvideo, null);
        listview = (ListView) view.findViewById(R.id.listview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        listview.setOnItemClickListener(new MyOnItemClickListener());
        //设置下拉刷新和加载更多的监听
        refresh.setMaterialRefreshListener(new MyMaterialRefreshListener());
        return view;
    }

    class MyMaterialRefreshListener extends MaterialRefreshListener {

        /**
         * 下拉刷新
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            getDataFromNet();
        }

        /**
         * 加载更多
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 当请求成功的时候回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                //解析数据
                LogUtil.e("请求数据成功==" + result);
                processMoreData(result);
            }

            /**
             * 请求失败的时候回调
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
            }

            /**
             * 当请求取消了的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            /**
             * 请求完成的时候
             */
            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }

        });

    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 调起自己的播放器
            Intent intent = new Intent(context, SystemPlayerActivity.class);
            // intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            // 使用 Bundle 传递列表数据
            Bundle bundle = new Bundle();
            bundle.putSerializable("medialist", mediaItems);
            intent.putExtra("position", position);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "网络视频数据绑定了");
        //取缓存的数据
        String saveJson = CacheUtils.getString(context, Constants.NET_VIDEO_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            progressData(saveJson);
        } else {
            getDataFromNet();
        }
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            /**
             * 当请求成功的时候回调
             * @param result
             */
            @Override
            public void onSuccess(String result) {
                //解析数据
                Log.e(TAG, "请求数据成功=" + result);
                //数据缓存
                CacheUtils.putString(context, Constants.NET_VIDEO_URL, result);
                progressData(result);
                processMoreData(result);
            }

            /**
             * 请求失败的时候回调
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
            }

            /**
             * 当请求取消了的时候回调
             * @param cex
             */
            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            /**
             * 请求完成的时候
             */
            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }

    private void processMoreData(String json) {
        //把数据添加到原来的集合中
        mediaItems.addAll(parsedJson(json));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        //把加载更多的状态还原
        refresh.finishRefreshLoadMore();
    }

    /**
     * 手动解析json数据和显示数据
     *
     * @param json
     */
    private void progressData(String json) {
        mediaItems = parsedJson(json);

        if (mediaItems != null && mediaItems.size() > 0) {
            //有数据
            tv_nodata.setVisibility(View.GONE);

            //设置适配器
            adapter = new NetVideoFragmentAdapter(context, mediaItems);
            listview.setAdapter(adapter);
        } else {
            //没有数据
            tv_nodata.setVisibility(View.VISIBLE);
            tv_nodata.setText("请求网络失败...");
        }
        //把下拉刷新的状态还原
        refresh.finishRefresh();

        progressbar.setVisibility(View.GONE);
    }

    /**
     * 解析json数据并且返回列表
     *
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = (JSONObject) jsonArray.get(i);
                    if (item != null) {
                        //创建类
                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);//添加到集合中

                        String name = item.optString("movieName");
                        mediaItem.setName(name);
                        String desc = item.optString("videoTitle");
                        mediaItem.setDesc(desc);
                        String data = item.optString("url");
                        mediaItem.setData(data);
                        String imageUrl = item.optString("coverImg");
                        mediaItem.setImageUrl(imageUrl);

                        //得到视频的总时长
                        long duration = item.optLong("videoLength");
                        mediaItem.setDuration(duration);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

        return mediaItems;

    }
}

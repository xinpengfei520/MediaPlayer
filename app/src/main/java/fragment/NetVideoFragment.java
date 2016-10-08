package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mediaplayer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import adapter.NetVideoFragmentAdapter;
import domain.MediaItem;
import utils.Constants;
import utils.LogUtil;

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
    private NetVideoFragmentAdapter adapter;
    /**
     * 视频列表
     */
    private ArrayList<MediaItem> mediaItems;

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

        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;

    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //调起自己的播放器
            Intent intent = new Intent(context, SystemPlayerActivity.class);
            //intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");

            //使用Bundler传递列表数据
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
        getDataFromNet();
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
                LogUtil.e("请求数据成功=" + result);
                progressData(result);
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

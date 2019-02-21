package com.xpf.mediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.xpf.mediaplayer.R;
import com.xpf.mediaplayer.adapter.SearchAdapter;
import com.xpf.mediaplayer.bean.SearchBean;
import com.xpf.mediaplayer.utils.Constants;
import com.xpf.mediaplayer.utils.JsonParser;
import com.xpf.mediaplayer.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 搜索页面：实现语音的搜索功能
 */
public class SearchActivity extends Activity implements View.OnClickListener {

    /**
     * 文本搜索框
     */
    private EditText etSearch;
    /**
     * 语音搜索按钮
     */
    private ImageView ivVoice;
    /**
     * 手动搜索按钮
     */
    private TextView tvSearch;
    /**
     * 用于显示搜索结果的ListView
     */
    private ListView listview;
    /**
     * 圆形进度条
     */
    private ProgressBar progressbar;
    /**
     * 没有搜索到时的文本提示
     */
    private TextView tvNodata;

    /**
     * 搜索结果适配器
     */
    private SearchAdapter adapter;

    //用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViews();
    }

    private void findViews() {

        setContentView(R.layout.activity_search);
        etSearch = (EditText) findViewById(R.id.et_search);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listview = (ListView) findViewById(R.id.listview);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);

        //设置点击事件
        ivVoice.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_voice:
                //语音搜索
                showInputDialogI();
                break;

            case R.id.tv_search:
                gotoSeachData();
                Toast.makeText(this, "搜索", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * 手动去搜索的方法
     */
    private void gotoSeachData() {

        String word = etSearch.getText().toString().trim();
        if (word != null) {
            try {
                word = URLEncoder.encode(word, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = Constants.NET_SEARCH_URL + word;

            getDataFromNet(url);
        }
    }

    /**
     * 使用xutils解析数据
     *
     * @param url:目标地址
     */
    private void getDataFromNet(String url) {
        RequestParams paranms = new RequestParams(url);
        x.http().get(paranms, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求成功==" + result);
                //解析和显示数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求失败==" + ex.getMessage());
                progressbar.setVisibility(View.GONE);
                tvNodata.setVisibility(View.VISIBLE);
                tvNodata.setText("没有搜索到内容...");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }

    private void processData(String json) {
        SearchBean bean = parseJson(json);
        List<SearchBean.ItemsEntity> list = bean.getItems();
        if (list != null && list.size() > 0) {
            //有数据
            tvNodata.setVisibility(View.GONE);
            //设置适配器
            adapter = new SearchAdapter(this, list);
            listview.setAdapter(adapter);

        } else {
            if (adapter != null) {
                adapter = new SearchAdapter(this, list);
                listview.setAdapter(adapter);
            }
            //没有数据
            tvNodata.setVisibility(View.VISIBLE);
            tvNodata.setText("没有搜索到内容...");
        }

        //隐藏
        progressbar.setVisibility(View.GONE);

    }

    /**
     * @param json:解析数据
     * @return : 返回一个实体类
     */
    private SearchBean parseJson(String json) {
        return new Gson().fromJson(json, SearchBean.class);
    }

    /**
     * 语音去搜索的方法
     */
    private void showInputDialogI() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {

            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化出错了...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * 返回结果
         *
         * @param results
         * @param b
         */
        @Override
        public void onResult(RecognizerResult results, boolean b) {
            String result = results.getResultString();
            String text = JsonParser.parseIatResult(result);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }


            String content = resultBuffer.toString();
            //将语音识别后末尾的"。"号去掉
            content = content.replace("。", "");

            etSearch.setText(content);
            etSearch.setSelection(etSearch.length());
            Log.e("TAG", result);

        }

        /**
         * 语音输入失败
         *
         * @param speechError:
         */
        @Override
        public void onError(SpeechError speechError) {

        }
    }
}
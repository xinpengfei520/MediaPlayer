package com.atguigu.mediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements View.OnClickListener {

    private EditText etSearch;
    private ImageView ivVoice;
    private TextView tvSearch;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tvNodata;

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
                Toast.makeText(this, "语音输入", Toast.LENGTH_LONG).show();
                break;
            case R.id.tv_search:
                Toast.makeText(this, "搜索", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
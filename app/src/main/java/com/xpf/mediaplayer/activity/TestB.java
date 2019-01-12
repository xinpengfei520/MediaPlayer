package com.xpf.mediaplayer.activity;

import android.app.Activity;
import android.os.Bundle;

import com.xpf.mediaplayer.R;
import com.xpf.mediaplayer.utils.LogUtil;

public class TestB extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testb);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("onDestroy");
    }

}

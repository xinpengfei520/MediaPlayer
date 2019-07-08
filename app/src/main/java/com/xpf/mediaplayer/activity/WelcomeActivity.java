package com.xpf.mediaplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xpf.mediaplayer.R;

public class WelcomeActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private boolean isStart = false;//判断是否启动
    private RelativeLayout ll_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题和状态栏设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        ll_welcome = (RelativeLayout) findViewById(R.id.ll_welcome);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 1000);
    }

    /**
     * startMainActivity的方法
     */
    private void startMainActivity() {
        if (!isStart) {
            isStart = true;
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();//关闭欢迎页面
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除所有消息和回调(防止内存泄漏)
        handler.removeCallbacksAndMessages(null);
    }
}

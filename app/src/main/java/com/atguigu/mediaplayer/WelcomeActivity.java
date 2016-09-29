package com.atguigu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class WelcomeActivity extends Activity {

    private Handler handler = new Handler();
    private boolean isStart = false;//判断是否启动
    private RelativeLayout ll_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ll_welcome = (RelativeLayout)findViewById(R.id.ll_welcome);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startMainActivity();
            }
        }, 2000);
    }

    /**
     * startMainActivity的方法
     */
    private void startMainActivity() {
        
        if(!isStart) {
            isStart =true;

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

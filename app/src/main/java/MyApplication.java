package com.atguigu.mediaplayer;


import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.x;

/**
 * Created by xinpengfei on 2016/10/8.
 * <p>
 * Function :代表整个应用，单例
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils3
        x.Ext.init(this);
        x.Ext.setDebug(true);//上线后要设置false

        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在“ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57fe11e8");

    }
}

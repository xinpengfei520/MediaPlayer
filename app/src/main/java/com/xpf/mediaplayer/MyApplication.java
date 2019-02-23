package com.xpf.mediaplayer;


import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.x;

/**
 * Created by xinpengfei on 2016/10/8.
 * Function:代表整个应用，单例
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        //初始化xUtils3
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);//上线后要设置false

        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在“ =”与 appid 之间添加任务空字符或者转义符
        // 用户校验失败10407。原因是一个应用申请的Appid和对应下载的SDK（包括jar和本地库）具有一致性，SDK不通用。
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56f4c1dd");

        /*
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
         * 输出详细的Bugly SDK的Log；
         * 每一条Crash都会被立即上报；
         * 自定义日志将会在Logcat中输出。
         * 建议在测试阶段建议设置成true，发布时设置为false。
         */
        CrashReport.initCrashReport(getApplicationContext(), "f435b56729", BuildConfig.DEBUG);
    }
}

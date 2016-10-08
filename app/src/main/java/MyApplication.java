package com.atguigu.mediaplayer;


import android.app.Application;

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
    }
}

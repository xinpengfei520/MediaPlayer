package com.xpf.mediaplayer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.xpf.mediaplayer.service.MusicPlayerService;

/**
 * Created by Vance on 2016/10/9.
 * Function:缓存工具类-共享偏好
 */
public class CacheUtils {

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param values
     */
    @SuppressLint("ApplySharedPref")
    public static void putString(Context context, String key, String values) {
        SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sp.edit().putString(key, values).commit();
    }

    /**
     * 得到缓存文本信息
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 保存播放模式
     *
     * @param context
     * @param key
     * @param value
     */
    @SuppressLint("ApplySharedPref")
    public static void savePlaymode(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }


    public static int getPlaymode(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        return sp.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }


}

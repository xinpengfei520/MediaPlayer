package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xinpengfei on 2016/10/9.
 * <p>
 * Function :缓存工具类-共享偏好
 */

public class CacheUtils {

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param values
     */
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
}

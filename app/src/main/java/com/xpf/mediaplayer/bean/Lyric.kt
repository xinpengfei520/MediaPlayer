package com.xpf.mediaplayer.bean

/**
 * Created by Vance on 2016/10/10.
 * Function :歌词显示类：代表的是一句歌词
 */
data class Lyric(var content: String? = null, var timePoint: Long? = 0, var sleepTime: Long? = 0)

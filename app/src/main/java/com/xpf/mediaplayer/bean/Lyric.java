package com.xpf.mediaplayer.bean;

/**
 * Created by Vance on 2016/10/10.
 * Function :歌词显示类：代表的是一句歌词
 */
public class Lyric {

    private String content;
    private long timePoint;
    private long sleepTime;

    public Lyric() {
    }

    public Lyric(String content, long timePoint, long sleepTime) {
        this.content = content;
        this.timePoint = timePoint;
        this.sleepTime = sleepTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}

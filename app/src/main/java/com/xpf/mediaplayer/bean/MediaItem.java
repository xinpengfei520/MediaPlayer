package com.xpf.mediaplayer.bean;

import java.io.Serializable;

/**
 * Created by xinpengfei on 2016/9/28.
 * Function : 代表一个视频或者一个音频
 */
public class MediaItem implements Serializable {

    private String name;
    private long duration;
    private long size;
    private String artist;
    private String data;
    /**
     * 描述
     */
    private String desc;
    private String imageUrl;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", artist='" + artist + '\'' +
                ", data='" + data + '\'' +
                ", desc='" + desc + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

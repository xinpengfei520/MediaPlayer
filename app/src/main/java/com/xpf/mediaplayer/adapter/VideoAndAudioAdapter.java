package com.xpf.mediaplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpf.mediaplayer.R;
import com.xpf.mediaplayer.bean.MediaItem;
import com.xpf.mediaplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by xinpengfei on 2016/10/9.
 * Function :
 */
public class VideoAndAudioAdapter extends BaseAdapter {

    private Utils utils;

    private Context context;
    private ArrayList<MediaItem> mediaItems;
    private boolean isVideo;

    public VideoAndAudioAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean isVideo) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.isVideo = isVideo;
        utils = new Utils();

    }


    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_video_fragment, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);

            //设置tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据位置得到对应的数据
        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        //设置时间
        viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));

        if (!isVideo) {
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
}

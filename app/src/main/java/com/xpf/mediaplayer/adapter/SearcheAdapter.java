package com.xpf.mediaplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xpf.mediaplayer.R;
import com.xpf.mediaplayer.bean.SearchBean;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import java.util.List;

/**
 * Created by xinpengfei on 2016/10/9.
 * Function:搜索的适配器
 */
public class SearcheAdapter extends BaseAdapter {

    private  ImageOptions imageOptions;
    private  Context context;
    private  List<SearchBean.ItemsEntity> mediaItems;

    public SearcheAdapter(Context context, List<SearchBean.ItemsEntity> list){
        this.context= context;
        this.mediaItems = list;


        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(80))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.video_default)
                .setFailureDrawableId(R.drawable.video_default)
                .build();

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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_net_video_fragment, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据位置得到对应的数据
        SearchBean.ItemsEntity mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getItemTitle());
        //在列表中使用xUtils3请求图片
//        x.image().bind(viewHolder.iv_icon, mediaItem.getImageUrl(),imageOptions);
        //使用Picasso请求图片
//        Picasso.with(context)
//                .load(mediaItem.getImageUrl())
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHolder.iv_icon);
        //使用Glide请求图片
        Glide.with(context)
                .load( mediaItem.getItemImage().getImgUrl1())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.iv_icon);


        viewHolder.tv_desc.setText(mediaItem.getKeywords());
        viewHolder.tv_duration.setText( mediaItem.getDatecheck());


        return convertView;
    }



    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        TextView tv_duration;
    }
}


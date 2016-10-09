package fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mediaplayer.R;

import java.util.ArrayList;

import domain.MediaItem;
import utils.Utils;

/**
 * Created by xinpengfei on 2016/9/28.
 * <p>
 * Function :本地视频
 */

public class VideoFragment extends BaseFragment {

    private ListView listView;
    private TextView tv_info;//资源没找到提示

    private ArrayList<MediaItem> mediaItems;

    private MyAdapter adapter;

    /**
     * 获取一个工具类对象:用于转换系统时间
     */
    private Utils utils;
//    private Context mContext;

    public VideoFragment(){
        utils = new Utils();
    }

    @Override
    public View initView() {

        Log.e("TAG", "本地视频UI创建了");

        View view = View.inflate(context, R.layout.fragment_video, null);
        listView = (ListView) view.findViewById(R.id.listview);
        tv_info = (TextView) view.findViewById(R.id.tv_nomedia);
//        mContext = context;

        //设置点击事件
        listView.setOnItemClickListener(new MyOnItemClickListener());
        return view;

    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "本地视频数据绑定了");

        isGrantExternalRW((Activity) context);
        getData();
    }

    private void getData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = new String[]{
                        MediaStore.Video.Media.DISPLAY_NAME,//在sdcard时候的名称
                        MediaStore.Video.Media.DURATION,//视频的时长，毫秒
                        MediaStore.Video.Media.SIZE,//文件大小，单位字节
                        MediaStore.Video.Media.ARTIST,//演唱者
                        MediaStore.Video.Media.DATA//在sdcard上路径
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    //循环
                    while (cursor.moveToNext()) {
                        //创建一个视频信息类
                        MediaItem mediaItem = new MediaItem();

                        //添加到集合中
                        mediaItems.add(mediaItem);

                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String artist = cursor.getString(3);
                        mediaItem.setArtist(artist);
                        String data = cursor.getString(4);
                        mediaItem.setData(data);

                    }

                    cursor.close();
                }

                handler.sendEmptyMessage(0);

            }
        }.start();

    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                tv_info.setVisibility(View.GONE);
                //设置适配器
                adapter = new MyAdapter();
                listView.setAdapter(adapter);
            }else {
                //没有数据
                tv_info.setVisibility(View.VISIBLE);
            }
        }
    };

    private class MyAdapter extends BaseAdapter {
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
            if(convertView == null) {
                convertView = View.inflate(context, R.layout.item_video_fragment,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);

                //设置tag
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            TextView textView = new TextView(context);
//            textView.setTextSize(20);
//            textView.setText(mediaItems.get(position).toString());

            //根据位置得到对应的数据
            MediaItem mediaItem = mediaItems.get(position);
            viewHolder.tv_name.setText(mediaItem.getName());
            //设置时间
            viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
            //格式化文件的存储大小
            viewHolder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
            Toast.makeText(context, "mediaItem=="+mediaItem, Toast.LENGTH_SHORT).show();
            //把系统的播放器调起来并且播放
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //调起自己的播放器
//            Intent intent = new Intent(context,SystemPlayerActivity.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //3.传递视频列表
            Intent intent = new Intent(context,SystemPlayerActivity.class);


            //使用Bundler传递列表数据
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("medialist",mediaItems);
//            intent.putExtras(bundle);

            //传递位置
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}

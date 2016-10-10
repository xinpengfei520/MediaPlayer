package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.atguigu.mediaplayer.AudioPlayerActivity;
import com.atguigu.mediaplayer.IMusicPlayerService;
import com.atguigu.mediaplayer.R;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import domain.MediaItem;
import utils.CacheUtils;
import utils.LogUtil;

//import android.support.annotation.Nullable;

/**
 * Created by xinpengfei on 2016/10/9.
 * <p>
 * Function :播放音乐的服务,里面封装音乐的逻辑
 */

public class MusicPlayerService extends Service {

    public static final String OPENAUDIO = "com.atguigu.mobilepalyer.OPENAUDIO";
    private ArrayList<MediaItem> mediaItems;
    /**
     * 这个音频的信息
     */
    private MediaItem mediaItem;
    /**
     * 当前播放列表中的位置
     */
    private int position;

    private MediaPlayer mediaPlayer;

    private NotificationManager manager;

    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 0;

    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 1;


    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;//默认为正常播放模式


    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {

        MusicPlayerService service = MusicPlayerService.this;//得到外面服务的实例

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);//调用服务的openAudio
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {
            service.setPlaymode(playmode);
        }

        @Override
        public int getPlaymode() throws RemoteException {
            return service.getPlaymode();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void notifyChange(String action) throws RemoteException {
            service.notifyChange(action);
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return mediaItem.getData();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }

    };

    /**
     * 播放上一个
     */
    private void pre() {

        setPrePosition();
        openPreByPosition();
    }

    private void openPreByPosition() {
        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position >= 0) {
                //正常范围
                openAudio(position);
            } else {
                //越界
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position >= 0) {
                //正常范围
                openAudio(position);
            } else {
                //越界
                position = 0;
            }
        }
    }

    private void setPrePosition() {

        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position--;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            position--;
            if (position < 0) {
                position = mediaItems.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position--;
            if (position < 0) {
                position = mediaItems.size() - 1;
            }
        } else {
            position--;
        }
    }

    /**
     * 根据不同的播放模式播放下一个
     */
    private void next() {

        //设置播放的位置
        setNextPosition();
        //根据位置播放对应的音频
        openNextByPosition();
    }

    private void openNextByPosition() {

        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if (position < mediaItems.size()) {
                //正常范围
                openAudio(position);
            } else {
                //越界
                position = mediaItems.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position < mediaItems.size()) {
                //正常范围
                openAudio(position);
            } else {
                //越界
                position = mediaItems.size() - 1;
            }
        }
    }

    private void setNextPosition() {

        int playmode = getPlaymode();
        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {

            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else {
            position++;
        }
    }

    /**
     * 音频的拖动
     *
     * @param position
     */
    private void seekTo(int position) {

    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到音频的总时长
     *
     * @return
     */
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    private String getAudioName() {
        if (mediaItem != null) {
            return mediaItem.getName();
        }
        return "";
    }

    private String getArtist() {
        if (mediaItem != null) {
            return mediaItem.getArtist();
        }
        return "";
    }

    private int getPlaymode() {
        return playmode;
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    private void setPlaymode(int playmode) {
        this.playmode = playmode;
        //保存模式
        CacheUtils.savePlaymode(this, "playmode", playmode);
    }

    private void pause() {
        mediaPlayer.pause();
        //隐藏通知栏
        manager.cancel(1);
    }

    /**
     * 播放音乐
     */
    private void start() {
        mediaPlayer.start();
        //弹出通知栏
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //延期的意图
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification", true);//从状态栏进入音乐播放页面的
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321影音")
                .setContentText("正在播放:" + getAudioName())
                .setContentIntent(pending)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;//点击的时候，不会消失
        manager.notify(1, notification);
    }

    private void openAudio(int position) {
        if (mediaItems != null && mediaItems.size() > 0) {
            this.position = position;
            if (position < mediaItems.size()) {
                mediaItem = mediaItems.get(position);
            }

            //释放MediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            try {
                //创建MediaPlayer,重新设置监听,重新准备
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                //设置播放地址
                mediaPlayer.setDataSource(mediaItem.getData());

                mediaPlayer.prepareAsync();//准备
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(MusicPlayerService.this, "音频还没有加载完成", Toast.LENGTH_SHORT).show();
        }

    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //开始播放
            start();
//            notifyChange(OPENAUDIO);
            //4.发布事件
            EventBus.getDefault().post(mediaItem);
        }
    }

    /**
     * 发广播
     *
     * @param action
     */
    private void notifyChange(String action) {

        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完成的时候播放下一个
//            next();
            autonext();
        }
    }

    /**
     * 自动播放下一首
     */
    private void autonext() {

        //设置播放的位置
        setNextAutoPosition();
        //根据位置播放对应的音频
        openNextByPosition();
    }

    private void setNextAutoPosition() {

        int playmode = getPlaymode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if (position > mediaItems.size() - 1) {
                position = 0;
            }
        } else {
            position++;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playmode = CacheUtils.getPlaymode(this, "playmode");
        LogUtil.e(this.toString() + "------------");
        getData();
    }

    private void getData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,//在sdcard时候的名称
                        MediaStore.Audio.Media.DURATION,//视频的时长，毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小，单位字节
                        MediaStore.Audio.Media.ARTIST,//演唱者
                        MediaStore.Audio.Media.DATA//在sdcard上路径
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {


                    //循环
                    while (cursor.moveToNext()) {

                        //创建了一个视频信息类
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

                    cursor.close();//关闭

                }

                //发消息
                //handler.sendEmptyMessage(0);
            }
        }.start();
    }

    //千万不能忘记
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.e("onBind");
        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.e("onUnbind");
        return super.onUnbind(intent);
    }
}
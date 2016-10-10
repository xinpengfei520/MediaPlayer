package com.atguigu.mediaplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import domain.Lyric;
import domain.MediaItem;
import service.MusicPlayerService;
import utils.LogUtil;
import utils.LyricUtils;
import utils.Utils;
import view.BaseVisualizerView;
import view.ShowLyricView;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    private ImageView iv_icon;
    private int position;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnAudioSwitchLyricCover;

    /**
     * 显示歌词视图的对象
     */
    private ShowLyricView show_lyric_view;
    /**
     * 显示频谱类的实例
     */
    private BaseVisualizerView baseVisualizerView;
    private Visualizer mVisualizer;

    private boolean notification;
    /**
     * 服务的代理类-aidl文件动态生成的类
     */
    private IMusicPlayerService service;
    /**
     * 进度更新
     */
    private static final int PROGERSS = 1;
    /**
     * 显示歌词
     */
    private static final int SHOWLYRIC = 2;

    private Utils utils;
    private MyBroadcastReceiver receiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SHOWLYRIC:
                    try {
                        //得到当前播放进度
                        int position = service.getCurrentPosition();

                        //根据当前歌曲的播放进度，找到歌词列表的索引
                        //重新绘制
                        show_lyric_view.setNextShowLyric(position);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOWLYRIC);
                    sendEmptyMessage(SHOWLYRIC);
                    break;

                case PROGERSS:
                    int currentPosition = 0;
                    try {
                        currentPosition = service.getCurrentPosition();
                        //更新时间进度
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    seekbarAudio.setProgress(currentPosition);

                    removeMessages(PROGERSS);
                    sendEmptyMessageDelayed(PROGERSS, 1000);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e(this.toString() + "------------");

        initData();
        findViews();
        getData();
        startAndBindService();
    }

    private void initData() {
        //注册广播
        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);
        utils = new Utils();

        //1.注册--this是AudioPlayerActivity
        EventBus.getDefault().register(this);
    }

    /**
     * 3.订阅函数
     *
     * @param mediaItem:
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void setData(MediaItem mediaItem) {
        //得到歌曲名称和演唱者名称并显示
        tvArtist.setText(mediaItem.getArtist());
        tvName.setText(mediaItem.getName());

        //歌曲开始更新
        showProgress();

        checkPlaymode();

        //显示歌词同步
        showLyric();

        //开启频谱
        setupVisualizerFxAndUi();
    }

    private void showLyric() {
        //1.得到音频的播放地址
        LyricUtils lyricUtils = new LyricUtils();
        try {
            String path = service.getAudioPath();//mnt/sdcard/audio/beijingbeijing.mp3
            path = path.substring(0, path.lastIndexOf("."));//mnt/sdcard/audio/beijingbeijing

            File file = new File(path + ".lrc");//mnt/sdcard/audio/beijingbeijing.lrc
            if (!file.exists()) {
                file = new File(path + ".txt");//mnt/sdcard/audio/beijingbeijing.txt
            }

            //解析歌词
            lyricUtils.readLyricFile(file);

            ArrayList<Lyric> lyrics = lyricUtils.getLyrics();
            show_lyric_view.setLyric(lyrics);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //2.变成歌词文件的地址
        if (lyricUtils.isExistsLyric()) {
            handler.sendEmptyMessage(SHOWLYRIC);
        }

    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //得到歌曲名称和演唱者名称并且显示
            showData();
            //歌曲开始更新
            showProgress();

            checkPlaymode();
        }
    }

    private void showProgress() {
        try {
            seekbarAudio.setMax(service.getDuration());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(PROGERSS);
    }

    private void startAndBindService() {

        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.atguigu.mobileplayer.OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);//防止服务多次实例化
    }

    private void getData() {

        //true，来自状态栏，false：列表
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);//列表
        }

    }

    private void findViews() {

        setContentView(R.layout.activity_audio_player);

        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnAudioSwitchLyricCover = (Button) findViewById(R.id.btn_audio_swich_lyric_cover);
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);
        show_lyric_view = (ShowLyricView) findViewById(R.id.show_lyric_view);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);

        iv_icon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getBackground();
        animationDrawable.start();

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioSwitchLyricCover.setOnClickListener(this);

        //设置音频进度的拖拽的监听
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.e("TAG", "onProgressChanged==" + progress);
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.e("TAG", "onStartTrackingTouch==");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e("TAG", "onStopTrackingTouch==");
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnAudioPlaymode) {//播放模式

            changePlaymode();
        } else if (v == btnAudioPre) {//上一首

            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == btnAudioStartPause) {//播放和暂停
            /*try {
                if (service.isPlaying()) {
                    //暂停
                    service.pause();
                    //按钮设置-播放状态
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                } else {
                    //播放
                    service.start();
                    //按钮设置-暂停
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            startAndPause();

        } else if (v == btnAudioNext) {//下一首

            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == btnAudioSwitchLyricCover) {//显示歌词

            startAndBindService();
        }
    }

    private void startAndPause() {

        try {
            if (service.isPlaying()) {
                service.pause();//暂停
                //设置按钮为播放状态
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            } else {
                //播放
                service.start();
                //设置按钮--暂停
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于改变播放模式的方法
     */
    private void changePlaymode() {

        try {
            int playmode = service.getPlaymode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保存在内存中
            service.setPlaymode(playmode);

            showPlaymode();//播放模式的显示

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi() {

        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "audioSessionid==" + audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);

        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        // 设置允许波形表示，并且捕获它
        baseVisualizerView.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }

    /**
     * 播放模式的显示
     */
    private void showPlaymode() {

        try {
            //从内存中获取播放模式
            int playmode = service.getPlaymode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }

        } catch (RemoteException e) {

            e.printStackTrace();
        }

    }

    /**
     * 校验显示播放模式
     */
    private void checkPlaymode() {
        try {
            //从内存中获取播放模式
            int playmode = service.getPlaymode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection conn = new ServiceConnection() {

        /**
         * 当和服务连接成功的时候回调
         * @param name
         * @param binder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            service = IMusicPlayerService.Stub.asInterface(binder);//得到服务代理类

            //开始播放音乐
            try {
                service.openAudio(position);
                if (notification) {
                    //状态栏
//                    showProgress();
                    service.notifyChange(MusicPlayerService.OPENAUDIO);
                } else {
                    //列表
                    service.openAudio(position);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //得到歌曲名称和演唱者名称并且显示
            showData();
        }

        /**
         * 当断开服务的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 显示歌曲名称和演唱者名称并且显示
     */
    private void showData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getAudioName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            mVisualizer.release();
        }
    }

    @Override
    protected void onDestroy() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }

        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        LogUtil.e("onDestroy" + "------------");

        //2.取消注册
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}

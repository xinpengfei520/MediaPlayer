package com.atguigu.mediaplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import service.MusicPlayerService;
import utils.Utils;

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
    private Button btnAudioSwichLyricCover;

    private boolean notification;
    /**
     * 服务的代理类-aidl文件动态生成的类
     */
    private IMusicPlayerService service;
    /**
     * 进度更新
     */
    private static final int PROGERSS = 1;

    private Utils utils;
    private MyBroadcastReceiver receiver;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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
        setContentView(R.layout.activity_audio_player);

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
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //歌曲开始更新
            showProgress();
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
//        position = getIntent().getIntExtra("position", 0);

        //true，来自状态栏，false：列表
        notification = getIntent().getBooleanExtra("notification", false);
        if(!notification) {
            position = getIntent().getIntExtra("position",0);//列表
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
        btnAudioSwichLyricCover = (Button) findViewById(R.id.btn_audio_swich_lyric_cover);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_icon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getBackground();
        animationDrawable.start();

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioSwichLyricCover.setOnClickListener(this);

        //设置音频进度的拖拽
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // Handle clicks for btnAudioPlaymode
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
        } else if (v == btnAudioStartPause) {
            try {
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
            }
            // Handle clicks for btnAudioStartPause
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
        } else if (v == btnAudioSwichLyricCover) {
            // Handle clicks for btnAudioSwichLyricCover
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

    private void showData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getAudioName());
        } catch (RemoteException e) {
            e.printStackTrace();
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

        super.onDestroy();
    }
}

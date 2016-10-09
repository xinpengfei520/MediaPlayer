package com.atguigu.mediaplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import service.MusicPlayerService;

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
    /**
     * 服务的代理类-aidl文件动态生成的类
     */
    private IMusicPlayerService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        findViews();
        getData();
        startAndBindService();
//        iv_icon = (ImageView) findViewById(R.id.iv_icon);
//        iv_icon.setBackgroundResource(R.drawable.animation_list);
//        AnimationDrawable animationDrawable = (AnimationDrawable) iv_icon.getBackground();
//        animationDrawable.start();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.atguigu.mobileplayer.OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);//防止服务多次实例化
    }

    private void getData() {
        position = getIntent().getIntExtra("position", 0);
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
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * 当断开服务的时候回调
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        super.onDestroy();
    }
}

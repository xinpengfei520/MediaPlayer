package fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mediaplayer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import domain.MediaItem;
import utils.LogUtil;
import utils.Utils;
import view.VideoView;

/**
 * Created by xinpengfei on 2016/9/28.
 * <p>
 * Function : 系统播放器
 */
public class SystemPlayerActivity extends Activity implements View.OnClickListener {

    /**
     * 常量-->视频进度更新
     */
    private static final int PROGRESS = 1;
    /**
     * 常量-->隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLL = 2;
    /**
     * 默认
     */
    private static final int SCREEN_DEFULT = 1;
    /**
     * 全屏
     */
    private static final int SCREEN_FULL = 2;

    /**
     * 记录按下这个时刻的当前音量
     */
    private int mVol;
    /**
     * 总距离
     */
    private int rangTouch;
    private float startY;

    private VideoView videoview;
    private Uri uri;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVideoVoice;
    private SeekBar seekbarVoice;
    private Button btnVideoSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrent;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private Utils utils;
    private MyBroadcastReceiver receiver;
    private LinearLayout ll_loading;

    /**
     * 视频列表数据
     */
    private ArrayList<MediaItem> mediaItems;

    /**
     * 点击视频在列表中的位置
     */
    private int position;
    /**
     * 定义手势识别器
     */
    private GestureDetector detector;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;
    /**
     * 屏幕的宽和高
     */
    private int screenWidth;
    private int screenHeight;
    /**
     * 真实视频的宽和高
     */
    private int videoWidth;
    private int videoHeight;
    /**
     * 调节声音
     */
    private AudioManager am;
    /**
     * 当前音量
     */
    private int currentVolume;
    /**
     * 最大音量
     */
    private int maxVolume;
    private boolean isShowMediaController = false;
    private boolean isMute;//是否是静音？
    /**
     * 是否网络地址
     */
    private boolean isNetUrl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("onCreate()");
/*
        setContentView(R.layout.activity_systemplayer);
        videoview = (VideoView) findViewById(R.id.videoview);
        uri = getIntent().getData();//视频播放地址
        LogUtil.e("uri==" + uri);
        //设置视频播放的监听：准备好，播放出错，播放完成
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置播放地址
        videoview.setVideoURI(uri);*/

        initData();
        findViews();

        getData();
        setListener();
        setData();
//
//        //设置控制面板
//        videoview.setMediaController(new MediaController(this));
    }

    private void setData() {

        if (mediaItems != null && mediaItems.size() > 0) {
            //有列表数据
            MediaItem mediaItem = mediaItems.get(position);
            isNetUrl = utils.isNetUrl(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            isNetUrl = utils.isNetUrl(mediaItem.getData());
            videoview.setVideoPath(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);

        }
        //文件，第三方应用
        else if (uri != null) {
            //设置播放地址
            isNetUrl = utils.isNetUrl(uri.toString());
            videoview.setVideoURI(uri);
            tvName.setText(uri.toString());
        } else {
            Toast.makeText(SystemPlayerActivity.this, "没有传递数据进入播放器", Toast.LENGTH_SHORT).show();
        }

        //设置按钮状态
        setButtonState();

    }

    private void setButtonState() {

        if (mediaItems != null && mediaItems.size() > 0) {

            //设置上一个和下一个可以点击
            setIsEnableButton(true);

            //如果是第0个，上一个不可点
            if (position == 0) {
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);
            }

            //如果是最后一个，下一个按钮不可以点
            if (position == mediaItems.size() - 1) {
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            }

        } else if (uri != null) {//只有一个播放地址
            setIsEnableButton(false);
        }

    }

    private void setIsEnableButton(boolean enable) {

        if (enable) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);

        } else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        }

        btnVideoPre.setEnabled(enable);
        btnVideoNext.setEnabled(enable);
    }

    /**
     * 设置视频播放的监听
     */
    private void setListener() {

        // 准备好，播放出错，播放完成
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置视频拖动
        seekbarVideo.setOnSeekBarChangeListener(new videoOnSeekBarChangeListener());

        //设置音量的拖动
        seekbarVoice.setOnSeekBarChangeListener(new volumeOnSeekBarChangeListener());

    }

    private void getData() {

        uri = getIntent().getData();//视频播放地址--文件-->null
        Log.e("TAG", "uri==" + uri);

        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("medialist");
        position = getIntent().getIntExtra("position", 0);

    }

    private void findViews() {

        setContentView(R.layout.activity_systemplayer);
        videoview = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVideoVoice = (Button) findViewById(R.id.btn_video_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnVideoSwitchPlayer = (Button) findViewById(R.id.btn_video_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrent = (TextView) findViewById(R.id.tv_current);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoExit = (Button) findViewById(R.id.btn_video_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwitchScreen = (Button) findViewById(R.id.btn_video_switch_screen);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        btnVideoVoice.setOnClickListener(this);
        btnVideoSwitchPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);

        //设置最大音量
        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);

    }

    private void initData() {

        utils = new Utils();
        //注册广播-电量变化的广播
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        //2.创建手势识别器
        detector = new GestureDetector(this, new MySimpleOnGestureListener());

        //得到屏幕的宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;

        //得到当前相关音量的信息
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //音量变大
            currentVolume++;
            updateProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLL);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //变量变小
            currentVolume--;
            updateProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLL);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onClick(View v) {

        if (v == btnVideoVoice) {
            isMute = !isMute;
            updateVolume(currentVolume);
            // Handle clicks for btnVideoVoice
        } else if (v == btnVideoSwitchPlayer) {
            // Handle clicks for btnVideoSwichPlayer
        } else if (v == btnVideoExit) {
            // Handle clicks for btnVideoExit
            finish();
        } else if (v == btnVideoPre) {
            setPlayPreVideo();
            // Handle clicks for btnVideoPre
        } else if (v == btnVideoStartPause) {//播放和暂停
            setStartAndPause();//设置播放和暂停
            // Handle clicks for btnVideoStartPause
        } else if (v == btnVideoNext) {
            setPlayNextVideo();
            // Handle clicks for btnVideoNext
        } else if (v == btnVideoSwitchScreen) {
            //切换视频模式
            setVideoMode();
            // Handle clicks for btnVideoSwichScreen
        }
        handler.removeMessages(HIDE_MEDIACONTROLL);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);

    }

    /**
     * 设置播放和暂停
     */
    private void setStartAndPause() {

        if (videoview.isPlaying()) {
            //暂停
            videoview.pause();
            //按钮状态--播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //播放
            videoview.start();
            //按钮-暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 设置播放上一个视频
     */
    private void setPlayPreVideo() {

        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUrl = utils.isNetUrl(mediaItem.getData());
                videoview.setVideoPath(mediaItem.getData());
                ll_loading.setVisibility(View.VISIBLE);

                setButtonState();

            } else {
                //列表的播放完成
                position = 0;
            }
        }

    }

    /**
     * 设置播放下一个视频
     */
    private void setPlayNextVideo() {

        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                isNetUrl = utils.isNetUrl(mediaItem.getData());
                tvName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());

                ll_loading.setVisibility(View.VISIBLE);
                setButtonState();
                if (position == mediaItems.size() - 1) {
                    Toast.makeText(SystemPlayerActivity.this, "播放最后一个视频", Toast.LENGTH_SHORT).show();

                }
            } else {
                position = mediaItems.size() - 1;
                //列表播放完成并退出
                finish();
            }
        } else if (uri != null) {//只有一个播放地址
            finish();//退出播放器
        }
    }

    private void updateVolume(int progress) {

        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            currentVolume = progress;
            seekbarVoice.setProgress(progress);
        }

    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

            //得到视频的宽和高
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

//            mp.setLooping(true);

            //开始播放
            videoview.start();

            //得到视频的总时长
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);

            tvDuration.setText(utils.stringForTime(duration));

            //默认隐藏控制面板
            hideMediaController();
//            mp.start();
            handler.sendEmptyMessage(PROGRESS);

            setVideoType(SCREEN_DEFULT);
            //隐藏视频加载效果
            ll_loading.setVisibility(View.GONE);
//            videoview.setVideoSize(50,50);//50*50像素
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 横竖屏切换时的声明周期方法
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop");
    }

    @Override
    protected void onDestroy() {

        //先把子类释放
        handler.removeCallbacksAndMessages(null);//把所有的消息和回调移除
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        LogUtil.e("onDestroy");

        super.onDestroy();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //1.得到当前进度
                    int currentPostion = videoview.getCurrentPosition();
                    //视频进度的更新
                    seekbarVideo.setProgress(currentPostion);

                    //设置时间更新
                    tvCurrent.setText(utils.stringForTime(currentPostion));

                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());

                    //设置视频缓存效果
                    if (isNetUrl) {
                        //网络的
                        int buffer = videoview.getBufferPercentage();//0~100

                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        //设置视频的缓冲
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //循环发消息
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
//                    hideMediaController();

                    break;
                case HIDE_MEDIACONTROLL://隐藏
                    hideMediaController();

                    break;
            }
        }
    };

    /**
     * 得到系统时间
     *
     * @return
     */
    private String getSystemTime() {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 播放完成的监听 : 退出播放器
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
//            finish();//退出播放器
//            mp.seekTo(0);
//            mp.start();
            setPlayNextVideo();
        }
    }


    /**
     * 用于接收电量广播的接收器
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            //主线程
            //显示电量
            setBattery(level);
        }
    }

    /**
     * 设置显示电量
     *
     * @param level
     */
    private void setBattery(int level) {

        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            setStartAndPause();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setVideoMode();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (isShowMediaController) {
                //隐藏
                hideMediaController();
                //把消息移除
                handler.removeMessages(HIDE_MEDIACONTROLL);
            } else {
                //显示
                showMediaController();
                //发延迟消息
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);

            }

            return super.onSingleTapConfirmed(e);

        }
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {

        isShowMediaController = true;
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {

        isShowMediaController = false;
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
    }

    private void setVideoMode() {

        if (isFullScreen) {
            //默认
            setVideoType(SCREEN_DEFULT);
//            //隐藏视频加载效果
//            ll_loading.setVisibility(View.GONE);
        } else {
            //全屏
            setVideoType(SCREEN_FULL);

        }

    }

    private void setVideoType(int videoType) {

        switch (videoType) {
            case SCREEN_FULL:
                isFullScreen = true;
                videoview.setVideoSize(screenWidth, screenHeight);
                //按钮状态
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);

                break;
            case SCREEN_DEFULT:

                isFullScreen = false;

                //真实视频的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的真实宽和高
                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoview.setVideoSize(width, height);
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);

                break;
        }
    }


    /**
     * 视频拖动监听的回调
     */
    private class videoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当SeekBar 改变的时候回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 自动false,用户操作true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (fromUser) {//只响应用户操作
                videoview.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            handler.removeMessages(HIDE_MEDIACONTROLL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
        }
    }

    /**
     * 音量拖动监听的回调
     */
    private class volumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                updateProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            handler.removeMessages(HIDE_MEDIACONTROLL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
        }
    }

    private void updateProgress(int progress) {

        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        currentVolume = progress;
        seekbarVoice.setProgress(progress);
        if (progress > 0) {
            isMute = false;
        } else {
            isMute = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);
        //3.把事件传入手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Intent intent = new Intent(this,TestB.class);
//                startActivity(intent);
//                return true;
                //1.记录当前音量，滑动的起始位置，得到总距离，移除消息
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                rangTouch = Math.min(screenHeight, screenWidth);
                startY = event.getY();
                handler.removeMessages(HIDE_MEDIACONTROLL);
                break;
            case MotionEvent.ACTION_MOVE:
                //2.记录endY，计算偏移量，计算改变声音，最终音量，修改声音
                float endY = event.getY();
                float distanceY = startY - endY;
                //滑动的距离：总距离 = 改变声音 ： 最大音量
                //改变声音 = (滑动的距离/总距离)*最大音量
                float delta = (distanceY / rangTouch) * maxVolume;

                //最终的音量 = 原来的音量 + 改变的音量
                int volume = (int) Math.min(Math.max(mVol + delta, 0), maxVolume);
                if (delta != 0) {
                    updateProgress(volume);
                }
                //重新赋值
                startY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000);
                break;
        }

//        return super.onTouchEvent(event);
        return true;
    }
}

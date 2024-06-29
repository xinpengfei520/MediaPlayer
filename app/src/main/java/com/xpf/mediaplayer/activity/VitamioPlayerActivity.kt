package com.xpf.mediaplayer.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.activity.SystemPlayerActivity
import com.xpf.mediaplayer.bean.MediaItem
import com.xpf.mediaplayer.utils.LogUtil
import com.xpf.mediaplayer.utils.Utils
import com.xpf.mediaplayer.view.VitamioVideoView
import io.vov.vitamio.MediaPlayer
import io.vov.vitamio.Vitamio
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.max
import kotlin.math.min

/**
 * 作者：created by Vance on 2016/9/28 16:20
 * 作用：系统播放器
 */
class VitamioPlayerActivity : Activity(), View.OnClickListener {
    private var videoView: VitamioVideoView? = null
    private var uri: Uri? = null

    private var llTop: LinearLayout? = null
    private var ll_loading: LinearLayout? = null
    private var ll_buffer: LinearLayout? = null
    private var tvName: TextView? = null
    private var ivBattery: ImageView? = null
    private var tvSystemTime: TextView? = null
    private var btnVideoVoice: Button? = null
    private var seekbarVoice: SeekBar? = null
    private var btnVideoSwichPlayer: Button? = null
    private var llBottom: LinearLayout? = null
    private var tvCurrent: TextView? = null
    private var seekbarVideo: SeekBar? = null
    private var tvDuration: TextView? = null
    private var btnVideoExit: Button? = null
    private var btnVideoPre: Button? = null
    private var btnVideoStartPause: Button? = null
    private var btnVideoNext: Button? = null
    private var btnVideoSwichScreen: Button? = null
    private var utils: Utils? = null
    private var receiver: MyBroadcastReceiver? = null
    private var tv_loading_speed: TextView? = null
    private var tv_buffer_speed: TextView? = null

    /**
     * 视频列表数据
     */
    private var mediaItems: ArrayList<MediaItem>? = null

    /**
     * 点击视频在列表中的位置
     */
    private var position = 0

    //1.定义手势识别器
    private var detector: GestureDetector? = null

    /**
     * 是否是全屏
     */
    private var isFullScreen = false

    /**
     * 屏幕的宽和高
     */
    private var screenWidth = 0
    private var screenHeight = 0

    /**
     * 真实视频的宽和高
     */
    private var videoWidht = 0
    private var videoHeight = 0

    /**
     * 调节声音
     */
    private var am: AudioManager? = null

    /**
     * 当前音量
     */
    private var currentVolume = 0

    //最大音量
    private var maxVolume = 0

    /**
     * 是否网络地址
     */
    private var isNetUrl = false

    /**
     * 上一次播放的位置
     */
    private var preCurrentPosition = 0

    private fun findViews() {
        //初始化Vitamio的库
        Vitamio.isInitialized(applicationContext)
        setContentView(R.layout.activity_vitamioplayer)
        videoView = findViewById<View>(R.id.videoView) as VitamioVideoView
        llTop = findViewById<View>(R.id.ll_top) as LinearLayout
        tvName = findViewById<View>(R.id.tv_name) as TextView
        ivBattery = findViewById<View>(R.id.iv_battery) as ImageView
        tvSystemTime = findViewById<View>(R.id.tv_system_time) as TextView
        btnVideoVoice = findViewById<View>(R.id.btn_video_voice) as Button
        seekbarVoice = findViewById<View>(R.id.seekbar_voice) as SeekBar
        btnVideoSwichPlayer = findViewById<View>(R.id.btn_video_switch_player) as Button
        llBottom = findViewById<View>(R.id.ll_bottom) as LinearLayout
        tvCurrent = findViewById<View>(R.id.tv_current) as TextView
        seekbarVideo = findViewById<View>(R.id.seekbar_video) as SeekBar
        tvDuration = findViewById<View>(R.id.tv_duration) as TextView
        btnVideoExit = findViewById<View>(R.id.btn_video_exit) as Button
        btnVideoPre = findViewById<View>(R.id.btn_video_pre) as Button
        btnVideoStartPause = findViewById<View>(R.id.btn_video_start_pause) as Button
        btnVideoNext = findViewById<View>(R.id.btn_video_next) as Button
        btnVideoSwichScreen = findViewById<View>(R.id.btn_video_switch_screen) as Button
        ll_loading = findViewById<View>(R.id.ll_loading) as LinearLayout
        ll_buffer = findViewById<View>(R.id.ll_buffer) as LinearLayout
        tv_loading_speed = findViewById<View>(R.id.tv_loading_speed) as TextView
        tv_buffer_speed = findViewById<View>(R.id.tv_buffer_speed) as TextView

        btnVideoVoice!!.setOnClickListener(this)
        btnVideoSwichPlayer!!.setOnClickListener(this)
        btnVideoExit!!.setOnClickListener(this)
        btnVideoPre!!.setOnClickListener(this)
        btnVideoStartPause!!.setOnClickListener(this)
        btnVideoNext!!.setOnClickListener(this)
        btnVideoSwichScreen!!.setOnClickListener(this)

        //设置最大音量
        seekbarVoice!!.max = maxVolume
        seekbarVoice!!.progress = currentVolume

        //发消息更新网络速度
        handler.sendEmptyMessage(SHOW_NET_SPEED)
    }

    private var isMute = false

    /**
     * Handle button click events<br></br>
     * <br></br>
     * Auto-created on 2016-09-29 11:31:54 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    override fun onClick(v: View) {
        if (v === btnVideoVoice) {
            isMute = !isMute
            updataVolume(currentVolume)
            // Handle clicks for btnVideoVoice
        } else if (v === btnVideoSwichPlayer) {
            // Handle clicks for btnVideoSwichPlayer
            showSwichPlayerDialog()
        } else if (v === btnVideoExit) {
            // Handle clicks for btnVideoExit
            finish()
        } else if (v === btnVideoPre) {
            setPlayPreVideo()
            // Handle clicks for btnVideoPre
        } else if (v === btnVideoStartPause) { //播放和暂停
            setStartAndPause()
            // Handle clicks for btnVideoStartPause
        } else if (v === btnVideoNext) {
            setPlayNextVideo()
            // Handle clicks for btnVideoNext
        } else if (v === btnVideoSwichScreen) {
            //切换视频模式
            setVideoMode()
            // Handle clicks for btnVideoSwichScreen
        }
        handler.removeMessages(HIDE_MEDIACONTROLL)
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
    }


    private fun showSwichPlayerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
        builder.setMessage("当前使用万能播放器播放视频，如果播放效果达不到您的要求，请切换到系统播放器播放试试")
        builder.setNegativeButton("取消", null)
        builder.setPositiveButton("确定") { dialog, which -> startSystemPlayer() }

        builder.show()
    }

    private fun startSystemPlayer() {
        if (videoView != null) {
            videoView!!.stopPlayback()
        }

        //传递列表和单个视频
        //调起自己的播放器
        val intent = Intent(this, SystemPlayerActivity::class.java)
        //            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
        //使用Bundler传递列表数据
        if (mediaItems != null && mediaItems!!.size > 0) {
            val bundle = Bundle()
            bundle.putSerializable("medialist", mediaItems)
            intent.putExtra("position", position)
            intent.putExtras(bundle)
        } else if (uri != null) {
            intent.setData(uri)
        }
        startActivity(intent)

        //把当前页面关闭
        finish()
    }

    private fun setStartAndPause() {
        if (videoView!!.isPlaying) {
            //暂停
            videoView!!.pause()
            //按钮状态--播放
            btnVideoStartPause!!.setBackgroundResource(R.drawable.btn_video_start_selector)
        } else {
            //播放
            videoView!!.start()
            //按钮-暂停
            btnVideoStartPause!!.setBackgroundResource(R.drawable.btn_video_pause_selector)
        }
    }


    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SHOW_NET_SPEED -> {
                    val netSpeed = utils!!.showNetSpeed(this@VitamioPlayerActivity)
                    tv_buffer_speed!!.text = netSpeed
                    tv_loading_speed!!.text = netSpeed
                    //循环发消息
                    removeMessages(SHOW_NET_SPEED)
                    sendEmptyMessageDelayed(SHOW_NET_SPEED, 1000)
                }

                PROGRESS -> {
                    //1.得到当前进度
                    val currentPosition = videoView!!.currentPosition.toInt() //0-0-0
                    //视频进度的更新
                    seekbarVideo!!.progress = currentPosition
                    //设置时间跟新
                    tvCurrent!!.text = utils!!.stringForTime(currentPosition)
                    //得到系统时间
                    val format = SimpleDateFormat("HH:mm:ss")
                    val systemTime = format.format(Date())
                    tvSystemTime?.setText(systemTime)

                    //设置视频缓存效果
                    if (isNetUrl) {
                        //网络的
                        val buffer = videoView!!.bufferPercentage //0~100
                        val totalBuffer = buffer * seekbarVideo!!.max
                        val secondaryProgress = totalBuffer / 100
                        //设置视频的缓冲
                        seekbarVideo!!.secondaryProgress = secondaryProgress
                    } else {
                        seekbarVideo!!.secondaryProgress = 0
                    }

                    //自定义监听卡
                    if (isNetUrl) {
                        if (videoView!!.isPlaying) {
                            //当前进度减掉上一次播放进度小于500就是卡
                            val buffer = currentPosition - preCurrentPosition
                            if (buffer < 500) { //视频卡了
                                ll_buffer!!.visibility = View.VISIBLE
                            } else {
                                ll_buffer!!.visibility = View.GONE
                            }
                        } else {
                            ll_buffer!!.visibility = View.GONE
                        }
                    }


                    preCurrentPosition = currentPosition

                    //循环发消息
                    removeMessages(PROGRESS)
                    sendEmptyMessageDelayed(PROGRESS, 1000)
                }

                HIDE_MEDIACONTROLL -> hideMediaController()

                else -> {

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.e("onCreate")

        initData()
        findViews()
        data
        setListener()
        setData()

        //设置控制面板
//        videoView.setMediaController(new MediaController(this));
    }

    private fun setData() {
        if (mediaItems != null && mediaItems!!.size > 0) {
            //有列表数据
            val mediaItem = mediaItems!![position]
            isNetUrl = utils!!.isNetUrl(mediaItem.data)
            tvName!!.text = mediaItem.name
            videoView!!.setVideoPath(mediaItem.data)
        } else if (uri != null) {
            //设置播放地址
            isNetUrl = utils!!.isNetUrl(uri.toString())
            videoView!!.setVideoURI(uri)
            tvName!!.text = uri.toString()
        } else {
            Toast.makeText(this@VitamioPlayerActivity, "没有传递数据进入播放器", Toast.LENGTH_SHORT)
                .show()
        }

        //设置按钮状态
        setButtonState()
    }

    private val data: Unit
        get() {
            uri = intent.data //视频播放地址-文件-->null
            LogUtil.e("uri==$uri")
            mediaItems = intent.getSerializableExtra("medialist") as ArrayList<MediaItem>?
            position = intent.getIntExtra("position", 0)
        }

    private fun initData() {
        utils = Utils()
        //注册广播--电量变化的广播
        receiver = MyBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(receiver, filter)

        //2.创建手势识别器
        detector = GestureDetector(this, MySimpleOnGestureListener())

        //得到屏幕的宽和高
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        screenWidth = outMetrics.widthPixels
        screenHeight = outMetrics.heightPixels


        //得到当前相关音量信息
        am = getSystemService(AUDIO_SERVICE) as AudioManager
        currentVolume = am!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        maxVolume = am!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    internal inner class MySimpleOnGestureListener : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
//            Toast.makeText(SystemPlayerActivity.this, "我被长按了", Toast.LENGTH_SHORT).show();
            setStartAndPause()
            super.onLongPress(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
//            Toast.makeText(SystemPlayerActivity.this, "我被双击了", Toast.LENGTH_SHORT).show();
            setVideoMode()
            return super.onDoubleTap(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
//            Toast.makeText(SystemPlayerActivity.this, "我被单击了", Toast.LENGTH_SHORT).show();
            if (isShowMediaController) {
                //隐藏
                hideMediaController()
                //把消息移除
                handler.removeMessages(HIDE_MEDIACONTROLL)
            } else {
                //显示
                showMediaController()
                //发延迟消息
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
            }
            return super.onSingleTapConfirmed(e)
        }
    }

    private fun setVideoMode() {
        if (isFullScreen) {
            //默认
            setVideoType(SCREEN_DEFULT)
        } else {
            //全屏
            setVideoType(SCREEN_FULL)
        }
    }

    private fun setVideoType(videoType: Int) {
        when (videoType) {
            SCREEN_FULL -> {
                isFullScreen = true
                videoView!!.setVideoSize(screenWidth, screenHeight)

                //按钮状态
                btnVideoSwichScreen!!.setBackgroundResource(R.drawable.btn_video_swich_screen_defualt_selector)
            }

            SCREEN_DEFULT -> {
                isFullScreen = false


                //真实视频的宽和高
                val mVideoWidth = videoWidht
                val mVideoHeight = videoHeight

                //屏幕的真实宽和高
                var width = screenWidth
                var height = screenHeight

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth
                }

                videoView!!.setVideoSize(width, height)

                //按钮状态
                btnVideoSwichScreen!!.setBackgroundResource(R.drawable.btn_video_swich_screen_full_selector)
            }
        }
    }

    internal inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra("level", 0) //0~100
            //主线程
            //显示电量
            setBattery(level)
        }
    }

    private var isShowMediaController = false

    /**
     * 隐藏控制面板
     */
    private fun hideMediaController() {
        isShowMediaController = false
        llTop!!.visibility = View.GONE
        llBottom!!.visibility = View.GONE
    }

    /**
     * 显示控制面板
     */
    private fun showMediaController() {
        isShowMediaController = true
        llTop!!.visibility = View.VISIBLE
        llBottom!!.visibility = View.VISIBLE
    }

    /**
     * 显示电量
     *
     * @param level
     */
    private fun setBattery(level: Int) {
        if (level <= 0) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_0)
        } else if (level <= 10) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_10)
        } else if (level <= 20) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_20)
        } else if (level <= 40) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_40)
        } else if (level <= 60) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_60)
        } else if (level <= 80) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_80)
        } else if (level <= 100) {
            ivBattery!!.setImageResource(R.drawable.ic_battery_100)
        } else {
            ivBattery!!.setImageResource(R.drawable.ic_battery_100)
        }
    }

    private fun setListener() {
        //设置视频播放的监听：准备好，播放出错，播放完成
        videoView!!.setOnPreparedListener(MyOnPreparedListener())
        videoView!!.setOnErrorListener(MyOnErrorListener())
        videoView!!.setOnCompletionListener(MyOnCompletionListener())

        //设置视频拖动
        seekbarVideo!!.setOnSeekBarChangeListener(VideoOnSeekBarChangeListener())

        //设置音量的拖动
        seekbarVoice!!.setOnSeekBarChangeListener(VolumeOnSeekBarChangeListener())

        //使用系统的监听播放卡,在Android4.2.2（17）以上
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            videoView.setOnInfoListener(new MyOnInfoListener());
//        }
    }

    internal inner class MyOnInfoListener : MediaPlayer.OnInfoListener {
        override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> ll_buffer!!.visibility = View.VISIBLE
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> ll_buffer!!.visibility = View.GONE
            }
            return true
        }
    }

    internal inner class VolumeOnSeekBarChangeListener : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                updataProgree(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLL)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
        }
    }

    private fun updataVolume(progress: Int) {
        if (isMute) {
            am!!.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            seekbarVoice!!.progress = 0
        } else {
            am!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            currentVolume = progress
            seekbarVoice!!.progress = progress
        }
    }

    private fun updataProgree(progress: Int) {
        am!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
        currentVolume = progress
        seekbarVoice!!.progress = progress
        isMute = if (progress > 0) {
            false
        } else {
            true
        }
    }


    internal inner class VideoOnSeekBarChangeListener : OnSeekBarChangeListener {
        /**
         * 当SeekBar 改变的时候回调这个方法
         *
         * @param seekBar
         * @param progress
         * @param fromUser 自动false,用户操作true
         */
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) { //只响应用户操作
                videoView!!.seekTo(progress.toLong())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLL)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
        }
    }

    internal inner class MyOnCompletionListener : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
//            finish();//退出播放器
//            mp.seekTo(0);
//            mp.start();
            setPlayNextVideo()
        }
    }

    private fun setPlayPreVideo() {
        if (mediaItems != null && mediaItems!!.size > 0) {
            position--
            if (position >= 0) {
                val mediaItem = mediaItems!![position]
                tvName!!.text = mediaItem.name
                isNetUrl = utils!!.isNetUrl(mediaItem.data)
                videoView!!.setVideoPath(mediaItem.data)
                ll_loading!!.visibility = View.VISIBLE
                setButtonState()
            } else {
                //列表的播放完成
                position = 0
            }
        }
    }


    private fun setPlayNextVideo() {
        if (mediaItems != null && mediaItems!!.size > 0) {
            position++
            if (position < mediaItems!!.size) {
                val mediaItem = mediaItems!![position]
                tvName!!.text = mediaItem.name
                isNetUrl = utils!!.isNetUrl(mediaItem.data)
                videoView!!.setVideoPath(mediaItem.data)

                ll_loading!!.visibility = View.VISIBLE

                setButtonState()
                if (position == mediaItems!!.size - 1) {
                    Toast.makeText(
                        this@VitamioPlayerActivity,
                        "播放最后一个视频",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                position = mediaItems!!.size - 1
                //列表的播放完成
                finish() //退出播放器
            }
        } else if (uri != null) { //只有一个播放地址
            finish() //退出播放器
        }
    }

    private fun setButtonState() {
        if (mediaItems != null && mediaItems!!.size > 0) {
            //设置上一个和下一个可以点击

            setIsEnableButton(true)


            //如果是第0个，上一个不可以点
            if (position == 0) {
                btnVideoPre!!.setBackgroundResource(R.drawable.btn_pre_gray)
                btnVideoPre!!.isEnabled = false
            }

            //如果是最后一个，下一个按钮不可以点
            if (position == mediaItems!!.size - 1) {
                btnVideoNext!!.setBackgroundResource(R.drawable.btn_next_gray)
                btnVideoNext!!.isEnabled = false
            }
        } else if (uri != null) { //只有一个播放地址
            setIsEnableButton(false)
        }
    }

    private fun setIsEnableButton(enable: Boolean) {
        if (enable) {
            btnVideoPre!!.setBackgroundResource(R.drawable.btn_video_pre_selector)
            btnVideoNext!!.setBackgroundResource(R.drawable.btn_video_next_selector)
        } else {
            btnVideoPre!!.setBackgroundResource(R.drawable.btn_pre_gray)
            btnVideoNext!!.setBackgroundResource(R.drawable.btn_next_gray)
        }

        btnVideoPre!!.isEnabled = enable
        btnVideoNext!!.isEnabled = enable
    }

    internal inner class MyOnErrorListener : MediaPlayer.OnErrorListener {
        override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
//            Toast.makeText(SystemPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
            //1.播放的视频格式不支持，播放出错--提示出错(使用万能播放器)
            showErrorDialog()
            //2.播放过程中网络中断，播放出错--重试
            //3.播放视频文件有缺损，播放出错--把缺损文件过滤掉
            return true
        }
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
        builder.setMessage("播放当前视频失败，请检查网络或者视频文件是否有损坏")
        builder.setNegativeButton("确定") { dialog, which ->
            finish() //退出播放器
        }

        builder.show()
    }

    internal inner class MyOnPreparedListener : MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            //            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemPlayerActivity.this, "视频拖动完成了", Toast.LENGTH_SHORT).show();
//                }
//            });

            //得到视频的宽和高

            videoWidht = mp.videoWidth
            videoHeight = mp.videoHeight

            //            mp.setLooping(true);
            //开始播放
            videoView!!.start()

            //得到视频的总时长
            val duration = videoView!!.duration.toInt()
            seekbarVideo!!.max = duration

            tvDuration!!.text = utils!!.stringForTime(duration)

            //默认隐藏控制面板
            hideMediaController()
            //            mp.start();
            handler.sendEmptyMessage(PROGRESS)

            setVideoType(SCREEN_DEFULT)

            //隐藏视频加载效果
            ll_loading!!.visibility = View.GONE
            //            videoView.setVideoSize(50,50);
        }
    }

    override fun onRestart() {
        super.onRestart()
        LogUtil.e("onRestart")
    }

    override fun onStart() {
        super.onStart()
        LogUtil.e("onStart")
    }

    override fun onResume() {
        super.onResume()
        LogUtil.e("onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtil.e("onPause")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.e("onStop")
    }

    override fun onDestroy() {
        //先把子类释放

        handler.removeCallbacksAndMessages(null) //把所有的消息和回调移除
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }

        LogUtil.e("onDestroy")

        super.onDestroy()
    }

    /**
     * 记录按下这个时刻的当前音量
     */
    private var mVol = 0

    /**
     * 总距离
     */
    private var rangTouch = 0
    private var startY = 0f


    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        //3.把事件传入手势识别器
        detector!!.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //1.记录当前音量，滑动的起始位置，得到总距离，移除消息
                mVol = am!!.getStreamVolume(AudioManager.STREAM_MUSIC)
                rangTouch =
                    min(screenHeight.toDouble(), screenWidth.toDouble()).toInt() //screenHeight
                startY = event.y
                handler.removeMessages(HIDE_MEDIACONTROLL)
            }

            MotionEvent.ACTION_MOVE -> {
                //2.记录endY,计算偏离，计算改变声音，最终音量，修改声音
                val endY = event.y
                val distanceY = startY - endY

                //滑动的距离：总距离  = 改变声音： 最大音量

                //改变声音 = （滑动的距离/总距离）*最大音量
                val delta = (distanceY / rangTouch) * maxVolume

                //最终音量 = 原理音量 + 改变声音
                val volume = min(max((mVol + delta).toDouble(), 0.0), maxVolume.toDouble())
                    .toInt()
                if (delta != 0f) {
                    updataProgree(volume)
                }
            }

            MotionEvent.ACTION_UP -> handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //音量变大
            currentVolume++
            updataProgree(currentVolume)
            handler.removeMessages(HIDE_MEDIACONTROLL)
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //音量变小
            currentVolume--
            updataProgree(currentVolume)
            handler.removeMessages(HIDE_MEDIACONTROLL)
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLL, 5000)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        /**
         * 常量-视频进度更新
         */
        private const val PROGRESS = 1

        /**
         * 常量-隐藏控制面板
         */
        private const val HIDE_MEDIACONTROLL = 2

        /**
         * 默认
         */
        private const val SCREEN_DEFULT = 1

        /**
         * 全屏
         */
        private const val SCREEN_FULL = 2

        /**
         * 显示网络速度
         */
        private const val SHOW_NET_SPEED = 3
    }
}

package com.xpf.mediaplayer.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.drawable.AnimationDrawable
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.xpf.mediaplayer.IMusicPlayerService
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.bean.MediaItem
import com.xpf.mediaplayer.service.MusicPlayerService
import com.xpf.mediaplayer.utils.LogUtil
import com.xpf.mediaplayer.utils.LyricUtils
import com.xpf.mediaplayer.utils.Utils
import com.xpf.mediaplayer.view.BaseVisualizerView
import com.xpf.mediaplayer.view.ShowLyricView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class AudioPlayerActivity : Activity(), View.OnClickListener {

    private var iv_icon: ImageView? = null
    private var position = 0
    private var tvArtist: TextView? = null
    private var tvName: TextView? = null
    private var tvTime: TextView? = null
    private var seekBarAudio: SeekBar? = null
    private var btnAudioPlayMode: Button? = null
    private var btnAudioPre: Button? = null
    private var btnAudioStartPause: Button? = null
    private var btnAudioNext: Button? = null
    private var btnAudioSwitchLyricCover: Button? = null

    /**
     * 显示歌词视图的对象
     */
    private var show_lyric_view: ShowLyricView? = null

    /**
     * 显示频谱类的实例
     */
    private var baseVisualizerView: BaseVisualizerView? = null
    private var mVisualizer: Visualizer? = null

    private var notification = false

    /**
     * 服务的代理类-aidl文件动态生成的类
     */
    private var service: IMusicPlayerService? = null
    private var utils: Utils? = null
    private var receiver: MyBroadcastReceiver? = null

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SHOW_LYRIC -> {
                    try {
                        //得到当前播放进度
                        val position = service!!.currentPosition
                        //根据当前歌曲的播放进度，找到歌词列表的索引
                        //重新绘制
                        show_lyric_view!!.setNextShowLyric(position)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                    removeMessages(SHOW_LYRIC)
                    sendEmptyMessage(SHOW_LYRIC)
                }

                PROGRESS -> {
                    var currentPosition = 0
                    try {
                        currentPosition = service!!.currentPosition
                        //更新时间进度
                        tvTime!!.text =
                            utils!!.stringForTime(currentPosition) + "/" + utils!!.stringForTime(
                                service!!.duration
                            )
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                    seekBarAudio!!.progress = currentPosition

                    removeMessages(PROGRESS)
                    sendEmptyMessageDelayed(PROGRESS, 1000)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.e("$this------------")
        initData()
        findViews()
        data
        startAndBindService()
    }

    private fun initData() {
        //注册广播
        receiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(MusicPlayerService.OPEN_AUDIO)
        registerReceiver(receiver, intentFilter)
        utils = Utils()

        //1.注册--this是AudioPlayerActivity
        EventBus.getDefault().register(this)
    }

    /**
     * 3.订阅函数
     *
     * @param mediaItem:
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    fun setData(mediaItem: MediaItem) {
        //得到歌曲名称和演唱者名称并显示
        tvArtist!!.text = mediaItem.artist
        tvName!!.text = mediaItem.name

        // 歌曲开始更新
        showProgress()
        checkPlaymode()
        //显示歌词同步
        showLyric()
        //开启频谱
        setupVisualizerFxAndUi()
    }

    private fun showLyric() {
        //1.得到音频的播放地址
        val lyricUtils = LyricUtils()
        try {
            var path = service!!.audioPath //mnt/sdcard/audio/beijingbeijing.mp3
            path = path.substring(0, path.lastIndexOf(".")) //mnt/sdcard/audio/beijingbeijing

            var file = File("$path.lrc") //mnt/sdcard/audio/beijingbeijing.lrc
            if (!file.exists()) {
                file = File("$path.txt") //mnt/sdcard/audio/beijingbeijing.txt
            }

            //解析歌词
            lyricUtils.readLyricFile(file)

            val lyrics = lyricUtils.lyrics
            show_lyric_view!!.setLyric(lyrics)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        //2.变成歌词文件的地址
        if (lyricUtils.isExistsLyric) {
            handler.sendEmptyMessage(SHOW_LYRIC)
        }
    }

    internal inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //得到歌曲名称和演唱者名称并且显示
            showData()
            //歌曲开始更新
            showProgress()
            checkPlaymode()
        }
    }

    private fun showProgress() {
        try {
            seekBarAudio!!.max = service!!.duration
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        handler.sendEmptyMessage(PROGRESS)
    }

    private fun startAndBindService() {
        val intent = Intent(this, MusicPlayerService::class.java)
        intent.setAction(MusicPlayerService.OPEN_AUDIO)
        bindService(intent, conn!!, BIND_AUTO_CREATE)
        startService(intent) //防止服务多次实例化
    }

    private val data: Unit
        get() {
            //true，来自状态栏，false：列表
            notification = intent.getBooleanExtra("notification", false)
            if (!notification) {
                position = intent.getIntExtra("position", 0) //列表
            }
        }

    private fun findViews() {
        setContentView(R.layout.activity_audio_player)

        tvArtist = findViewById<View>(R.id.tv_artist) as TextView
        tvName = findViewById<View>(R.id.tv_name) as TextView
        tvTime = findViewById<View>(R.id.tv_time) as TextView
        seekBarAudio = findViewById<View>(R.id.seekbar_audio) as SeekBar
        btnAudioPlayMode = findViewById<View>(R.id.btn_audio_playmode) as Button
        btnAudioPre = findViewById<View>(R.id.btn_audio_pre) as Button
        btnAudioStartPause = findViewById<View>(R.id.btn_audio_start_pause) as Button
        btnAudioNext = findViewById<View>(R.id.btn_audio_next) as Button
        btnAudioSwitchLyricCover = findViewById<View>(R.id.btn_audio_swich_lyric_cover) as Button
        baseVisualizerView = findViewById<View>(R.id.baseVisualizerView) as BaseVisualizerView
        show_lyric_view = findViewById<View>(R.id.show_lyric_view) as ShowLyricView
        iv_icon = findViewById<View>(R.id.iv_icon) as ImageView

        iv_icon!!.setBackgroundResource(R.drawable.animation_list)
        val animationDrawable = iv_icon!!.background as AnimationDrawable
        animationDrawable.start()

        btnAudioPlayMode!!.setOnClickListener(this)
        btnAudioPre!!.setOnClickListener(this)
        btnAudioStartPause!!.setOnClickListener(this)
        btnAudioNext!!.setOnClickListener(this)
        btnAudioSwitchLyricCover!!.setOnClickListener(this)

        //设置音频进度的拖拽的监听
        seekBarAudio!!.setOnSeekBarChangeListener(MyOnSeekBarChangeListener())
    }

    internal inner class MyOnSeekBarChangeListener : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            Log.e("TAG", "onProgressChanged==$progress")
            if (fromUser) {
                try {
                    //service.pause();
                    service!!.seekTo(progress)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            Log.e("TAG", "onStartTrackingTouch==")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            Log.e("TAG", "onStopTrackingTouch==")
            try {
                service!!.start()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        if (v === btnAudioPlayMode) { //播放模式
            changePlaymode()
        } else if (v === btnAudioPre) { //上一首

            try {
                service!!.pre()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else if (v === btnAudioStartPause) { //播放和暂停
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
            startAndPause()
        } else if (v === btnAudioNext) { //下一首
            try {
                service!!.next()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        } else if (v === btnAudioSwitchLyricCover) { //显示歌词
            startAndBindService()
        }
    }

    private fun startAndPause() {
        try {
            if (service!!.isPlaying) {
                service!!.pause() //暂停
                //设置按钮为播放状态
                btnAudioStartPause!!.setBackgroundResource(R.drawable.btn_audio_start_selector)
            } else {
                //播放
                service!!.start()
                //设置按钮--暂停
                btnAudioStartPause!!.setBackgroundResource(R.drawable.btn_audio_pause_selector)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 用于改变播放模式的方法
     */
    private fun changePlaymode() {
        try {
            var playmode = service!!.playmode

            playmode = if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                MusicPlayerService.REPEAT_SINGLE
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                MusicPlayerService.REPEAT_ALL
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                MusicPlayerService.REPEAT_NORMAL
            } else {
                MusicPlayerService.REPEAT_NORMAL
            }
            //保存在内存中
            service!!.playmode = playmode

            showPlaymode() //播放模式的显示
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private fun setupVisualizerFxAndUi() {
        var audioSessionid = 0
        try {
            audioSessionid = service!!.audioSessionId
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        Log.e("TAG", "audioSessionid==$audioSessionid")
        mVisualizer = Visualizer(audioSessionid)

        // 参数内必须是2的位数
        mVisualizer!!.setCaptureSize(Visualizer.getCaptureSizeRange()[1])

        // 设置允许波形表示，并且捕获它
        baseVisualizerView!!.setVisualizer(mVisualizer)
        mVisualizer!!.setEnabled(true)
    }

    /**
     * 播放模式的显示
     */
    private fun showPlaymode() {
        try {
            //从内存中获取播放模式
            val playmode = service!!.playmode

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector)
                Toast.makeText(this@AudioPlayerActivity, "顺序播放", Toast.LENGTH_SHORT).show()
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector)
                Toast.makeText(this@AudioPlayerActivity, "单曲播放", Toast.LENGTH_SHORT).show()
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector)
                Toast.makeText(this@AudioPlayerActivity, "全部循环", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@AudioPlayerActivity, "顺序播放", Toast.LENGTH_SHORT).show()
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 校验显示播放模式
     */
    private fun checkPlaymode() {
        try {
            //从内存中获取播放模式
            val playMode = service!!.playmode

            if (playMode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector)
            } else if (playMode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector)
            } else if (playMode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector)
            } else {
                btnAudioPlayMode!!.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private var conn: ServiceConnection? = object : ServiceConnection {
        /**
         * 当和服务连接成功的时候回调
         * @param name
         * @param binder
         */
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = IMusicPlayerService.Stub.asInterface(binder) //得到服务代理类
            //开始播放音乐
            try {
                if (notification) {
                    // 状态栏
                    service?.notifyChange(MusicPlayerService.OPEN_AUDIO)
                } else {
                    //列表
                    service?.openAudio(position)
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

            //得到歌曲名称和演唱者名称并且显示
            showData()
        }

        /**
         * 当断开服务的时候回调
         * @param name
         */
        override fun onServiceDisconnected(name: ComponentName) {
            service = null
        }
    }

    /**
     * 显示歌曲名称和演唱者名称并且显示
     */
    private fun showData() {
        try {
            tvArtist!!.text = service!!.artist
            tvName!!.text = service!!.name
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            if (mVisualizer != null) {
                mVisualizer!!.release()
            }
        }
    }

    override fun onDestroy() {
        if (conn != null) {
            unbindService(conn!!)
            conn = null
        }

        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }

        LogUtil.e("onDestroy" + "------------")

        //2.取消注册
        EventBus.getDefault().unregister(this)

        super.onDestroy()
    }

    companion object {
        /**
         * 进度更新
         */
        private const val PROGRESS = 1

        /**
         * 显示歌词
         */
        private const val SHOW_LYRIC = 2
    }
}

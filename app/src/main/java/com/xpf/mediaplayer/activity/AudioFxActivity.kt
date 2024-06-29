package com.xpf.mediaplayer.activity

import android.Manifest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.hjq.permissions.XXPermissions
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.utils.LogUtil
import com.xpf.mediaplayer.view.BaseVisualizerView

/**
 * Created by Vance on 2019/02/21.
 * Function:Android 跳动频谱页面
 */
class AudioFxActivity : AppCompatActivity() {
    private var mMediaPlayer: MediaPlayer? = null
    private var mVisualizer: Visualizer? = null
    private var mBaseVisualizerView: BaseVisualizerView? = null
    private var mToolbar: Toolbar? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        setContentView(R.layout.activity_audio_fx)
        mBaseVisualizerView = findViewById(R.id.visualizerView)

        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        // 设置是否有返回箭头
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true) //左侧添加一个默认的返回图标
            actionBar.setHomeButtonEnabled(true) //设置返回键可用
            mToolbar!!.setNavigationOnClickListener { finish() }
        }

        val perms =
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS)
        XXPermissions.with(this).permission(perms)
            .request { _: List<String?>?, all: Boolean ->
                if (all) {
                    initMediaPlayer()
                } else {
                    Toast.makeText(this@AudioFxActivity, "您拒绝了权限！", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.small_star)
        mMediaPlayer?.setOnPreparedListener {
            mMediaPlayer?.start()
            setupVisualizerFxAndUi()
        }

        mMediaPlayer?.isLooping = true
    }

    /**
     * 生成一个 VisualizerView 对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private fun setupVisualizerFxAndUi() {
        val audioSessionId = mMediaPlayer!!.audioSessionId
        LogUtil.i("audioSessionId==$audioSessionId")
        mVisualizer = Visualizer(audioSessionId)
        // 参数内必须是2的位数
        mVisualizer!!.setCaptureSize(Visualizer.getCaptureSizeRange()[1])
        // 设置允许波形表示，并且捕获它
        mBaseVisualizerView!!.setVisualizer(mVisualizer)
        mVisualizer!!.setEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing && mMediaPlayer != null) {
            mVisualizer!!.release()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}
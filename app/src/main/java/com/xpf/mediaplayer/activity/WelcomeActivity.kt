package com.xpf.mediaplayer.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.xpf.mediaplayer.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : AppCompatActivity() {
    private var isStart = false //判断是否启动

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 去掉窗口标题和状态栏设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                delay(1000)
            }
            startMainActivity()
        }
    }

    /**
     * startMainActivity的方法
     */
    private fun startMainActivity() {
        if (!isStart) {
            isStart = true
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish() //关闭欢迎页面
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        startMainActivity()
        return super.onTouchEvent(event)
    }
}

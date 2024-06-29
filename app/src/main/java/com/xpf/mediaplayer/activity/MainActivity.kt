package com.xpf.mediaplayer.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.jzvd.Jzvd
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.fragment.AudioFragment
import com.xpf.mediaplayer.fragment.BaseFragment
import com.xpf.mediaplayer.fragment.NetAudioFragment
import com.xpf.mediaplayer.fragment.NetVideoFragment
import com.xpf.mediaplayer.fragment.VideoFragment
import com.xsir.pgyerappupdate.library.PgyerApi

/**
 * Created by Vance on 2016/9/28.
 * Function:MainActivity
 */
class MainActivity : FragmentActivity() {
    private var rg_main: RadioGroup? = null

    /**
     * 存放各个子 Fragment 页面的集合
     */
    private var fragments: ArrayList<BaseFragment>? = null

    /**
     * 列表中对应的 Fragment 的位置
     */
    private var position = 0

    /**
     * 上一个页面的 Fragment
     */
    private var context: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) //设置沉浸式状态栏，在MIUI系统中，状态栏背景透明。原生系统中，状态栏背景半透明。
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) //设置沉浸式虚拟键，在MIUI系统中，虚拟键背景透明。原生系统中，虚拟键背景半透明

        rg_main = findViewById<View>(R.id.rg_main) as RadioGroup
        initFragment()

        /**
         * 设置 RadioGroup 状态改变的监听
         */
        rg_main!!.setOnCheckedChangeListener(MyOnCheckedChangeListener())
        rg_main!!.check(R.id.rb_main_video)

        PgyerApi.checkUpdate(this)
    }

    internal inner class MyOnCheckedChangeListener : RadioGroup.OnCheckedChangeListener {
        override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
            when (checkedId) {
                R.id.rb_main_video -> position = 0
                R.id.rb_main_music -> position = 1
                R.id.rb_net_video -> position = 2
                R.id.rb_net_music -> position = 3
            }
            // TODO: 2024/6/2 convert to if else
            val toFragment = getFragment(position)
            //            switchFragment(toFragment);
            switchFragment(context, toFragment)
        }
    }

    /**
     * @param fromFragment : 在点击这个时刻正在显示
     * @param toFragment   : 在点击后这个时刻马上要显示
     */
    private fun switchFragment(fromFragment: Fragment?, toFragment: Fragment?) {
        //fromFragment 传入的content
        //fromFragment上一次显示

        if (toFragment !== context) { //才去显示
//            context = fromFragment;
            context = toFragment //videoFragment

            if (toFragment != null) {
                val transaction = supportFragmentManager.beginTransaction()

                if (!toFragment.isAdded) {
                    //隐藏之前显示的fromFragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment)
                    }
                    //添加toFragment
                    transaction.add(R.id.fl_main_container, toFragment).commit()
                    //                    transaction.commit();
                } else {
                    //隐藏之前显示fromFragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment)
                    }
                    //显示toFragment
                    transaction.show(toFragment).commit()
                }
            }
        }


        /* Fragment fragment = getFragment(0);
        //1.得到FragmentManager
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();

        //3.替换
        transaction.replace(R.id.fl_main_container,toFragment);

        //4.提交事务
        transaction.commit();*/
    }

    /**
     * 得到Fragment: 从集合中取出相应位置的fragment
     *
     * @param position
     * @return
     */
    private fun getFragment(position: Int): Fragment {
        return fragments!![position]
    }

    /**
     * 初始化Fragment
     */
    private fun initFragment() {
        fragments = ArrayList()
        fragments!!.add(VideoFragment()) //本地视频
        fragments!!.add(AudioFragment()) //本地音频
        fragments!!.add(NetVideoFragment()) //网络视频
        fragments!!.add(NetAudioFragment()) //网络音频
    }

    private var isExit = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (0 != position) {
                //把主页选中
                position = 0
                rg_main!!.check(R.id.rb_main_video)
                return true
            } else if (!isExit) {
                isExit = true
                Toast.makeText(this@MainActivity, "再点击一次退出~", Toast.LENGTH_SHORT).show()
                handler.postDelayed({ isExit = false }, 2000)

                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }
}

package com.atguigu.mediaplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import java.util.ArrayList;

import fragment.AudioFragment;
import fragment.BaseFragment;
import fragment.NetAudioFragment;
import fragment.NetVideoFragment;
import fragment.VideoFragment;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;

    /**
     * 存放各个子Fragemnt页面的集合
     */
    private ArrayList<BaseFragment> fragments;

    /**
     * 列表中对应的Fragment的位置
     */
    private int position;

    /**
     * 上一个页面的Fragment
     */
    private Fragment context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rg_main = (RadioGroup) findViewById(R.id.rg_main);

        initFragment();

        /**
         * 设置RadioGroup状态改变的监听
         */
        rg_main.setOnCheckedChangeListener(new MyOncheckedChangeListener());
    }

    class MyOncheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                case R.id.rb_main_video:
                    position = 0;
                    break;
                case R.id.rb_main_music:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_music:
                    position = 3;
                    break;
            }

            Fragment toFragment = getFragment(position);
//            switchFragment(toFragment);
            switchFragment(context, toFragment);
        }
    }

    /**
     * @param fromFragment : 在点击这个时刻正在显示
     * @param toFragment   : 在点击后这个时刻马上要显示
     */
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {

        //fromFragment 传入的content
        //fromFragment上一次显示

        if (toFragment != context) {//才去显示
//            context = fromFragment;
            context = toFragment;//videoFragment

            if (toFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (!toFragment.isAdded()) {
                    //隐藏之前显示的fromFragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    //添加toFragment
                    transaction.add(R.id.fl_main_container, toFragment).commit();
//                    transaction.commit();
                } else {
                    //隐藏之前显示fromFragment
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                    //显示toFragment
                    transaction.show(toFragment).commit();
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
    private Fragment getFragment(int position) {
        return fragments.get(position);

    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {

        fragments = new ArrayList<>();
        fragments.add(new VideoFragment());//本地视频
        fragments.add(new AudioFragment());//本地音频
        fragments.add(new NetVideoFragment());//网络视频
        fragments.add(new NetAudioFragment());//网络音频

    }
}

package com.xpf.mediaplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by xinpengfei on 2016/9/28.
 * Function : BaseFragment,基类，公共类
 * videoFragment,AudioFragment,NetVideoFragment,NetAudioFragment要继承该类
 */
public abstract class BaseFragment extends Fragment {

    /**
     * 获取上下文
     * protected子类可以用
     */
    protected Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    /**
     * 要孩子一定要实现这个方法因为是抽象方法，意思就是说，要孩子自己实现自己的效果
     * 注：抽象方法所在的类也一定是抽象的！
     *
     * @return
     */
    public abstract View initView();

    /**
     * 当Activity创建成功的时候
     * 得到Fragment的视图，对视图进行数据的设置，联网请求
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 当需要给视图绑定数据，或者联网请求数据并且绑定数据的时候就重写该方法
     */
    public void initData() {

    }

}

package com.xpf.mediaplayer.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.cjj.MaterialRefreshLayout
import com.cjj.MaterialRefreshListener
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.activity.SystemPlayerActivity
import com.xpf.mediaplayer.adapter.NetVideoFragmentAdapter
import com.xpf.mediaplayer.bean.MediaItem
import com.xpf.mediaplayer.utils.CacheUtils
import com.xpf.mediaplayer.utils.Constants
import com.xpf.mediaplayer.utils.LogUtil
import org.json.JSONException
import org.json.JSONObject
import org.xutils.common.Callback.CancelledException
import org.xutils.common.Callback.CommonCallback
import org.xutils.http.RequestParams
import org.xutils.x

/**
 * Created by Vance on 2016/9/28.
 * Function:
 */
class NetVideoFragment : BaseFragment() {
    private val textView: TextView? = null
    private var listview: ListView? = null
    private var progressbar: ProgressBar? = null
    private var tv_nodata: TextView? = null
    private var adapter: NetVideoFragmentAdapter? = null

    /**
     * 视频列表
     */
    private var mediaItems: ArrayList<MediaItem>? = null
    private var refresh: MaterialRefreshLayout? = null

    override fun initView(): View? {
        Log.e(TAG, "网络视频UI创建了")
        val view = View.inflate(context, R.layout.fragment_netvideo, null)
        listview = view.findViewById<View>(R.id.listview) as ListView
        progressbar = view.findViewById<View>(R.id.progressbar) as ProgressBar
        tv_nodata = view.findViewById<View>(R.id.tv_nodata) as TextView
        refresh = view.findViewById<View>(R.id.refresh) as MaterialRefreshLayout
        listview!!.onItemClickListener = MyOnItemClickListener()
        //设置下拉刷新和加载更多的监听
        refresh!!.setMaterialRefreshListener(MyMaterialRefreshListener())
        return view
    }

    internal inner class MyMaterialRefreshListener : MaterialRefreshListener() {
        /**
         * 下拉刷新
         *
         * @param materialRefreshLayout
         */
        override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout) {
            dataFromNet
        }

        /**
         * 加载更多
         *
         * @param materialRefreshLayout
         */
        override fun onRefreshLoadMore(materialRefreshLayout: MaterialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout)
            moreDataFromNet
        }
    }

    private val moreDataFromNet: Unit
        get() {
            val params = RequestParams(Constants.NET_VIDEO_URL)
            x.http().get(params, object : CommonCallback<String> {
                /**
                 * 当请求成功的时候回调
                 * @param result
                 */
                override fun onSuccess(result: String) {
                    //解析数据
                    LogUtil.e("请求数据成功==$result")
                    processMoreData(result)
                }

                /**
                 * 请求失败的时候回调
                 * @param ex
                 * @param isOnCallback
                 */
                override fun onError(ex: Throwable, isOnCallback: Boolean) {
                    LogUtil.e("请求数据失败==" + ex.message)
                }

                /**
                 * 当请求取消了的时候回调
                 * @param cex
                 */
                override fun onCancelled(cex: CancelledException) {
                    LogUtil.e("onCancelled==" + cex.message)
                }

                /**
                 * 请求完成的时候
                 */
                override fun onFinished() {
                    LogUtil.e("onFinished==")
                }
            })
        }

    internal inner class MyOnItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            // 调起自己的播放器
            val intent = Intent(context, SystemPlayerActivity::class.java)
            // intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            // 使用 Bundle 传递列表数据
            val bundle = Bundle()
            bundle.putSerializable("medialist", mediaItems)
            intent.putExtra("position", position)
            intent.putExtras(bundle)
            context!!.startActivity(intent)
        }
    }

    override fun initData() {
        super.initData()
        Log.e("TAG", "网络视频数据绑定了")
        //取缓存的数据
        val saveJson = CacheUtils.getString(context, Constants.NET_VIDEO_URL)
        if (!TextUtils.isEmpty(saveJson)) {
            progressData(saveJson)
        } else {
            dataFromNet
        }
    }

    private val dataFromNet: Unit
        get() {
            val params = RequestParams(Constants.NET_VIDEO_URL)
            x.http().get(params, object : CommonCallback<String> {
                /**
                 * 当请求成功的时候回调
                 * @param result
                 */
                override fun onSuccess(result: String) {
                    //解析数据
                    Log.e(TAG, "请求数据成功=$result")
                    //数据缓存
                    CacheUtils.putString(context, Constants.NET_VIDEO_URL, result)
                    progressData(result)
                    processMoreData(result)
                }

                /**
                 * 请求失败的时候回调
                 * @param ex
                 * @param isOnCallback
                 */
                override fun onError(ex: Throwable, isOnCallback: Boolean) {
                    LogUtil.e("请求数据失败==" + ex.message)
                }

                /**
                 * 当请求取消了的时候回调
                 * @param cex
                 */
                override fun onCancelled(cex: CancelledException) {
                    LogUtil.e("onCancelled==" + cex.message)
                }

                /**
                 * 请求完成的时候
                 */
                override fun onFinished() {
                    LogUtil.e("onFinished==")
                }
            })
        }

    private fun processMoreData(json: String) {
        //把数据添加到原来的集合中
        mediaItems!!.addAll(parsedJson(json))

        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }

        //把加载更多的状态还原
        refresh!!.finishRefreshLoadMore()
    }

    /**
     * 手动解析json数据和显示数据
     *
     * @param json
     */
    private fun progressData(json: String) {
        mediaItems = parsedJson(json)

        if (mediaItems != null && mediaItems!!.size > 0) {
            //有数据
            tv_nodata!!.visibility = View.GONE

            //设置适配器
            adapter = NetVideoFragmentAdapter(context, mediaItems)
            listview!!.adapter = adapter
        } else {
            //没有数据
            tv_nodata!!.visibility = View.VISIBLE
            tv_nodata!!.text = "请求网络失败..."
        }
        //把下拉刷新的状态还原
        refresh!!.finishRefresh()

        progressbar!!.visibility = View.GONE
    }

    /**
     * 解析json数据并且返回列表
     *
     * @param json
     * @return
     */
    private fun parsedJson(json: String): ArrayList<MediaItem> {
        val mediaItems = ArrayList<MediaItem>()

        try {
            val jsonObject = JSONObject(json)
            val jsonArray = jsonObject.optJSONArray("trailers")
            if (jsonArray != null && jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray[i] as JSONObject
                    if (item != null) {
                        //创建类
                        val mediaItem = MediaItem()
                        mediaItems.add(mediaItem) //添加到集合中

                        val name = item.optString("movieName")
                        mediaItem.name = name
                        val desc = item.optString("videoTitle")
                        mediaItem.desc = desc
                        val data = item.optString("url")
                        mediaItem.data = data
                        val imageUrl = item.optString("coverImg")
                        mediaItem.imageUrl = imageUrl

                        //得到视频的总时长
                        val duration = item.optLong("videoLength")
                        mediaItem.duration = duration
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return mediaItems
    }

    companion object {
        private const val TAG = "NetVideoFragment"
    }
}

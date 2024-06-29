package com.xpf.mediaplayer.fragment

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import com.google.gson.Gson
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.activity.PhotoViewActivity
import com.xpf.mediaplayer.adapter.NetAudioAdapter
import com.xpf.mediaplayer.bean.NetAudioBean
import com.xpf.mediaplayer.bean.NetAudioBean.ListBean
import com.xpf.mediaplayer.utils.CacheUtils
import com.xpf.mediaplayer.utils.Constants
import org.xutils.common.Callback.CancelledException
import org.xutils.common.Callback.CommonCallback
import org.xutils.common.util.LogUtil
import org.xutils.http.RequestParams
import org.xutils.x

/**
 * Created by Vance on 2016/9/28.
 * Function: 网络音乐
 */
class NetAudioFragment : BaseFragment() {
    private var listview: ListView? = null

    /**
     * 数据集合
     */
    private var lists: List<ListBean>? = null

    /**
     * 初始化视图
     *
     * @return
     */
    override fun initView(): View? {
        val view = View.inflate(context, R.layout.net_audio_pager, null)
        listview = view.findViewById<View>(R.id.listView) as ListView
        return view
    }

    override fun initData() {
        super.initData()
        Log.e(TAG, "网络音乐数据绑定了")
        val saveJson = CacheUtils.getString(context, Constants.ALL_RES_URL)
        // 如果不为空从本地取，否则从网络获取
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson)
        } else {
            dataFromNet
        }
    }

    private val dataFromNet: Unit
        get() {
            val params = RequestParams(Constants.ALL_RES_URL)
            x.http().get(params, object : CommonCallback<String> {
                override fun onSuccess(result: String) {
                    Log.i(TAG, "result===$result")
                    processData(result)
                }

                override fun onError(ex: Throwable, isOnCallback: Boolean) {
                    LogUtil.e("onError===" + ex.message)
                }

                override fun onCancelled(cex: CancelledException) {
                    LogUtil.e("onCancelled===" + cex.message)
                }

                override fun onFinished() {
                    LogUtil.e("onFinished===")
                }
            })
        }

    /**
     * 解析和显示数据
     *
     * @param json
     */
    private fun processData(json: String) {
        val netAudioBean = Gson().fromJson(json, NetAudioBean::class.java)
        lists = netAudioBean.list
        if (lists != null && lists!!.size > 0) {
            listview!!.adapter = NetAudioAdapter(context, lists)
            setListener()
        } else {
            Toast.makeText(context, "没有得到数据", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListener() {
        listview!!.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val listEntity = lists!![position]
                if (listEntity != null) {
                    //3.传递视频列表
                    val intent = Intent(context, PhotoViewActivity::class.java)
                    if (listEntity.type == "gif") {
                        val url = listEntity.gif.images[0]
                        intent.putExtra("url", url)
                        context!!.startActivity(intent)
                    } else if (listEntity.type == "image") {
                        val url = listEntity.image.big[0]
                        intent.putExtra("url", url)
                        context!!.startActivity(intent)
                    }
                }
            }
    }

    companion object {
        private const val TAG = "NetAudioFragment"
    }
}

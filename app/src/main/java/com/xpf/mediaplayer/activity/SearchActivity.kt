package com.xpf.mediaplayer.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.iflytek.cloud.ErrorCode
import com.iflytek.cloud.InitListener
import com.iflytek.cloud.RecognizerResult
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.adapter.SearchAdapter
import com.xpf.mediaplayer.bean.SearchBean
import com.xpf.mediaplayer.utils.Constants
import com.xpf.mediaplayer.utils.JsonParser
import com.xpf.mediaplayer.utils.LogUtil
import org.json.JSONException
import org.json.JSONObject
import org.xutils.common.Callback.CancelledException
import org.xutils.common.Callback.CommonCallback
import org.xutils.http.RequestParams
import org.xutils.x
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Created by Vance on 2016/9/28.
 * Function:搜索页面：实现语音的搜索功能
 */
class SearchActivity : Activity(), View.OnClickListener {
    /**
     * 文本搜索框
     */
    private var etSearch: EditText? = null

    /**
     * 语音搜索按钮
     */
    private var ivVoice: ImageView? = null

    /**
     * 手动搜索按钮
     */
    private var tvSearch: TextView? = null

    /**
     * 用于显示搜索结果的ListView
     */
    private var listview: ListView? = null

    /**
     * 圆形进度条
     */
    private var progressbar: ProgressBar? = null

    /**
     * 没有搜索到时的文本提示
     */
    private var tvNodata: TextView? = null
    private var tvTalk: TextView? = null

    /**
     * 搜索结果适配器
     */
    private var adapter: SearchAdapter? = null

    //用HashMap存储听写结果
    private val mIatResults: HashMap<String?, String> = LinkedHashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViews()
    }

    private fun findViews() {
        setContentView(R.layout.activity_search)
        etSearch = findViewById<View>(R.id.et_search) as EditText
        ivVoice = findViewById<View>(R.id.iv_voice) as ImageView
        tvSearch = findViewById<View>(R.id.tv_search) as TextView
        listview = findViewById<View>(R.id.listview) as ListView
        progressbar = findViewById<View>(R.id.progressbar) as ProgressBar
        tvNodata = findViewById<View>(R.id.tv_nodata) as TextView
        tvTalk = findViewById(R.id.tvTalk)

        //设置点击事件
        ivVoice!!.setOnClickListener(this)
        tvSearch!!.setOnClickListener(this)
        tvTalk?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_voice ->                 //语音搜索
                showInputDialogI()

            R.id.tv_search -> {
                gotoSearchData()
                Toast.makeText(this, "搜索", Toast.LENGTH_LONG).show()
            }

            R.id.tvTalk -> startActivity(
                Intent(
                    this@SearchActivity,
                    ConversationActivity::class.java
                )
            )
        }
    }

    /**
     * 手动去搜索的方法
     */
    private fun gotoSearchData() {
        var word = etSearch!!.text.toString().trim { it <= ' ' }
        try {
            word = URLEncoder.encode(word, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        val url = Constants.NET_SEARCH_URL + word

        getDataFromNet(url)
    }

    /**
     * 使用xutils解析数据
     *
     * @param url:目标地址
     */
    private fun getDataFromNet(url: String) {
        val params = RequestParams(url)
        x.http().get(params, object : CommonCallback<String> {
            override fun onSuccess(result: String) {
                LogUtil.e("请求成功==$result")
                //解析和显示数据
                processData(result)
            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {
                LogUtil.e("请求失败==" + ex.message)
                progressbar!!.visibility = View.GONE
                tvNodata!!.visibility = View.VISIBLE
                tvNodata!!.text = "没有搜索到内容..."
            }

            override fun onCancelled(cex: CancelledException) {
                LogUtil.e("onCancelled==" + cex.message)
            }

            override fun onFinished() {
                LogUtil.e("onFinished==")
            }
        })
    }

    private fun processData(json: String) {
        val bean = parseJson(json)
        val list = bean.items
        if (list != null && list.size > 0) {
            //有数据
            tvNodata!!.visibility = View.GONE
            //设置适配器
            adapter = SearchAdapter(this, list)
            listview!!.adapter = adapter
        } else {
            if (adapter != null) {
                adapter = SearchAdapter(this, list)
                listview!!.adapter = adapter
            }
            //没有数据
            tvNodata!!.visibility = View.VISIBLE
            tvNodata!!.text = "没有搜索到内容..."
        }

        //隐藏
        progressbar!!.visibility = View.GONE
    }

    /**
     * @param json:解析数据
     * @return : 返回一个实体类
     */
    private fun parseJson(json: String): SearchBean {
        return Gson().fromJson(json, SearchBean::class.java)
    }

    /**
     * 语音去搜索的方法
     */
    private fun showInputDialogI() {
        //1.创建RecognizerDialog对象
        val mDialog = RecognizerDialog(this, MyInitListener())
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn") //中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin") //普通话
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(MyRecognizerDialogListener())
        //4.显示dialog，接收语音输入
        mDialog.show()
    }

    internal inner class MyInitListener : InitListener {
        override fun onInit(i: Int) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(this@SearchActivity, "初始化出错了...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal inner class MyRecognizerDialogListener : RecognizerDialogListener {
        /**
         * 返回结果
         *
         * @param results
         * @param b
         */
        override fun onResult(results: RecognizerResult, b: Boolean) {
            val result = results.resultString
            val text = JsonParser.parseIatResult(result)

            var sn: String? = null
            // 读取json结果中的sn字段
            try {
                val resultJson = JSONObject(results.resultString)
                sn = resultJson.optString("sn")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            mIatResults[sn] = text

            val resultBuffer = StringBuffer()
            for (key in mIatResults.keys) {
                resultBuffer.append(mIatResults[key])
            }


            var content = resultBuffer.toString()
            //将语音识别后末尾的"。"号去掉
            content = content.replace("。", "")

            etSearch!!.setText(content)
            etSearch!!.setSelection(etSearch!!.length())
            Log.e(TAG, result)
        }

        /**
         * 语音输入失败
         *
         * @param speechError:
         */
        override fun onError(speechError: SpeechError) {
        }
    }

    companion object {
        private const val TAG = "SearchActivity"
    }
}
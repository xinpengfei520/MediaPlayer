package com.xpf.mediaplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.hjq.permissions.XXPermissions
import com.iflytek.cloud.RecognizerResult
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.bean.ConversationInfo
import com.xpf.mediaplayer.bean.SpeechBean
import com.xpf.mediaplayer.utils.ResouesUtils
import com.xpf.mediaplayer.utils.SpeechUtils
import java.util.Random

/**
 * Created by Vance on 2016/9/28.
 * Function:人机对话页面
 */
class ConversationActivity : AppCompatActivity(), View.OnClickListener {
    private var mSpeechUtils: SpeechUtils? = null
    private var conversationList: MutableList<ConversationInfo>? = null
    private var mAdapter: MyAdapter? = null
    private var mListView: ListView? = null
    private var btnStartListen: Button? = null
    private var mPhoneNumber: String? = null
    private var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        mSpeechUtils = SpeechUtils.getInstance(this)
    }

    private fun initView() {
        setContentView(R.layout.activity_conversation)
        mListView = findViewById<View>(R.id.lv_conversation) as ListView
        btnStartListen = findViewById<View>(R.id.btn_start_listen) as Button

        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(mToolbar)
        // 设置是否有返回箭头
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true) //左侧添加一个默认的返回图标
            actionBar.setHomeButtonEnabled(true) //设置返回键可用

            mToolbar!!.setNavigationOnClickListener { finish() }
        }

        btnStartListen!!.setOnClickListener(this)
        conversationList = ArrayList()
        mAdapter = MyAdapter()
        mListView!!.adapter = mAdapter
    }

    override fun onClick(v: View) {
        mSpeechUtils!!.showListenVoiceDialog(this, MyRecognizerDialogListener())
    }

    internal inner class MyRecognizerDialogListener : RecognizerDialogListener {
        private var sb: StringBuffer = StringBuffer()

        override fun onResult(recognizerResult: RecognizerResult, isLast: Boolean) {
            //语音识别过来的内容
            val resultString = recognizerResult.resultString
            sb.append(getVoice(resultString)) //添加成一整句

            println("正在识别中: $sb")
            val askerText: String
            var answer: String?
            var imageID = -1
            if (isLast) { // true, 所有的数据都解析完闭. 赋值给askerText
                askerText = sb.toString()
                answer = sb.toString()
                if (askerText.contains("美女")) {
                    val random = Random()
                    val index = random.nextInt(3)
                    answer = ResouesUtils.mmTextArray[index]
                    imageID = ResouesUtils.mmImageArray[index]
                } else if (askerText.contains("精忠报国")) {
                    answer = ResouesUtils.markTextArray[0]
                    imageID = ResouesUtils.markImageArray[0]
                } else if (askerText.contains("奶茶妹")) {
                    answer = ResouesUtils.markTextArray[2]
                    imageID = ResouesUtils.mmImageArray[4]
                } else if (askerText.contains("开会")) {
                    answer = ResouesUtils.markTextArray[1]
                    imageID = ResouesUtils.markImageArray[1]
                } else if (askerText.contains("报警")) {
                    mPhoneNumber = "110"
                    requestPermission()
                    return
                } else if (askerText.contains("120")) {
                    mPhoneNumber = "120"
                    requestPermission()
                    return
                } else if (askerText.contains("打开微信")) {
                    val intent = Intent()
                    val cmp = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
                    intent.setAction(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setComponent(cmp)
                    startActivity(intent)
                    return
                } else if (askerText.contains("拍照")) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //调用android自带的照相机
                    //                   photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    startActivity(intent)
                    return
                } else if (askerText.contains("名字")) {
                    answer = ResouesUtils.mmTextArray[3]
                } else if (askerText.contains("我在哪儿")) {
                    answer = ResouesUtils.mmTextArray[4]
                } else if (askerText.contains("班长是谁")) {
                    answer = ResouesUtils.mmTextArray[5]
                } else if (askerText.contains("老师是谁")) {
                    answer = ResouesUtils.mmTextArray[6]
                    imageID = ResouesUtils.mmImageArray[3]
                } else if (askerText.contains("你会做什么")) {
                    answer = ResouesUtils.mmTextArray[7]
                } else if (askerText.contains("爱你")) {
                    answer = ResouesUtils.mmTextArray[8]
                }
                sb = StringBuffer()
            } else { // 还没有把数据解析完毕, return回去, 不去执行后面的代码, 继续下一次拼接.
                return
            }

            var info = ConversationInfo(askerText, null, -1, true)
            conversationList!!.add(info)
            mAdapter!!.notifyDataSetChanged()

            // 准备回答的数据.
            info = ConversationInfo(null, answer, imageID, false)
            conversationList!!.add(info)

            mAdapter!!.notifyDataSetChanged()
            mListView!!.setSelection(conversationList!!.size)
            // 把answer说出来
            mSpeechUtils!!.speakText(this@ConversationActivity, answer)
        }

        override fun onError(speechError: SpeechError) {
            Toast.makeText(this@ConversationActivity, "识别出错了", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun toCallPhone() {
        if (!TextUtils.isEmpty(mPhoneNumber)) {
            val intentPhone = Intent(Intent.ACTION_CALL, Uri.parse("tel:$mPhoneNumber"))
            startActivity(intentPhone)
        }
    }

    private fun requestPermission() {
        val perms = arrayOf(Manifest.permission.CALL_PHONE)
        XXPermissions.with(this).permission(perms)
            .request { _: List<String?>?, all: Boolean ->
                if (all) {
                    toCallPhone()
                } else {
                    Toast.makeText(this@ConversationActivity, "权限不够", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * 解析json
     *
     * @param resultString
     * @return
     */
    private fun getVoice(resultString: String): String {
        val gson = Gson()
        val voice = gson.fromJson(resultString, SpeechBean::class.java)
        val ws = voice.ws
        val sb = StringBuffer()
        for (wsBean in ws) {
            val str = wsBean.cw[0].w
            sb.append(str)
        }
        return sb.toString()
    }

    internal inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return conversationList!!.size
        }

        override fun getView(position: Int, view: View, parent: ViewGroup): View {
            val answerView = view.findViewById<View>(R.id.ll_answer)
            val answerText = view.findViewById<View>(R.id.tv_answer_text) as TextView
            val answerImage = view.findViewById<View>(R.id.iv_answer_image) as ImageView
            val askerText = view.findViewById<View>(R.id.tv_asker_text) as TextView

            val info = conversationList!![position]
            if (info.isAsker) {
                // 当前是提问者, 隐藏回答者布局
                answerView.visibility = View.GONE
                askerText.visibility = View.VISIBLE
                askerText.text = info.askerText
            } else {
                // 当前是回答者, 隐藏提问者布局
                answerView.visibility = View.VISIBLE
                askerText.visibility = View.GONE

                answerText.text = info.answerText
                if (info.imageID == -1) {
                    answerImage.visibility = View.GONE
                } else {
                    answerImage.visibility = View.VISIBLE
                    answerImage.setImageResource(info.imageID)
                }
            }
            return view
        }

        override fun getItem(position: Int): Any {
            return conversationList!![position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }
    }

}

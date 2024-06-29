package com.xpf.mediaplayer.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.hjq.permissions.XXPermissions
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.activity.AudioPlayerActivity
import com.xpf.mediaplayer.adapter.VideoAndAudioAdapter
import com.xpf.mediaplayer.bean.MediaItem
import com.xpf.mediaplayer.utils.Utils

/**
 * Created by Vance on 2016/9/28.
 * Function:
 */
class AudioFragment : BaseFragment() {
    private var listView: ListView? = null
    private var tv_nomedia: TextView? = null
    private var mediaItems: ArrayList<MediaItem>? = null
    private var adapter: VideoAndAudioAdapter? = null
    private val utils = Utils()

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (mediaItems != null && mediaItems!!.size > 0) {
                //有数据
                tv_nomedia!!.visibility = View.GONE
                //设置适配器
                adapter = VideoAndAudioAdapter(context, mediaItems, false)
                listView!!.adapter = adapter
            } else {
                //没有数据
                tv_nomedia!!.visibility = View.VISIBLE
            }
        }
    }

    override fun initView(): View {
        Log.e("TAG", "本地音乐UI创建了")
        val view = View.inflate(context, R.layout.fragment_video, null)
        listView = view.findViewById<View>(R.id.listview) as ListView
        tv_nomedia = view.findViewById<View>(R.id.tv_nomedia) as TextView
        //设置点击事件
        listView!!.onItemClickListener =
            MyOnItemClickListener()
        return view
    }

    internal inner class MyOnItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val mediaItem = mediaItems!![position]

            // Toast.makeText(context, "mediaItem=="+mediaItem, Toast.LENGTH_SHORT).show();
            //把系统的播放器调起来并且播放
            //Intent intent = new Intent();
            //intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
            //context.startActivity(intent);

            //调起自己的播放器
//            Intent intent = new Intent(context, SystemPlayerActivity.class);
            val intent = Intent(context, AudioPlayerActivity::class.java)

            //intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");

            //使用Bundler传递列表数据
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("medialist", mediaItems);
            intent.putExtra("position", position)
            //            intent.putExtras(bundle);
            context?.startActivity(intent)
        }
    }

    override fun initData() {
        super.initData()
        Log.e(TAG, "本地音乐数据绑定了")
        requestPermission()
    }

    fun requestPermission() {
        val perms = arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        XXPermissions.with(this).permission(perms)
            .request { permissions: List<String?>?, all: Boolean ->
                if (all) {
                    data
                } else {
                    Toast.makeText(context, "需要读取存储的权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val data: Unit
        get() {
            object : Thread() {
                override fun run() {
                    super.run()
                    mediaItems = ArrayList()
                    val resolver = context?.contentResolver
                    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    val objs = arrayOf(
                        MediaStore.Audio.Media.DISPLAY_NAME,  //在sdcard时候的名称
                        MediaStore.Audio.Media.DURATION,  //视频的时长，毫秒
                        MediaStore.Audio.Media.SIZE,  //文件大小，单位字节
                        MediaStore.Audio.Media.ARTIST,  //演唱者
                        MediaStore.Audio.Media.DATA //在sdcard上路径
                    )

                    val cursor = resolver?.query(uri, objs, null, null, null)
                    if (cursor != null) {
                        //循环
                        while (cursor.moveToNext()) {
                            //创建了一个视频信息类
                            val mediaItem = MediaItem()
                            //添加到集合中
                            mediaItems!!.add(mediaItem)

                            val name = cursor.getString(0)
                            mediaItem.name = name
                            val duration = cursor.getLong(1)
                            mediaItem.duration = duration
                            val size = cursor.getLong(2)
                            mediaItem.size = size
                            val artist = cursor.getString(3)
                            mediaItem.artist = artist
                            val data = cursor.getString(4)
                            mediaItem.data = data
                        }

                        cursor.close() //关闭
                    }

                    //发消息
                    handler.sendEmptyMessage(0)
                }
            }.start()
        }

    companion object {
        private const val TAG = "AudioFragment"
        private const val STORAGE_PERM = 0x224
    }
}

package com.xpf.mediaplayer.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.hjq.permissions.XXPermissions
import com.xpf.mediaplayer.R
import com.xpf.mediaplayer.activity.SystemPlayerActivity
import com.xpf.mediaplayer.bean.MediaItem
import com.xpf.mediaplayer.utils.Utils

/**
 * Created by Vance on 2016/9/28.
 * Function :本地视频
 */
class VideoFragment : BaseFragment() {
    private var listView: ListView? = null
    private var tv_info: TextView? = null //资源没找到提示
    private var mediaItems: ArrayList<MediaItem>? = null
    private var adapter: MyAdapter? = null

    /**
     * 获取一个工具类对象:用于转换系统时间
     */
    private val utils = Utils()

    override fun initView(): View? {
        Log.e(TAG, "本地视频UI创建了")
        val view = View.inflate(context, R.layout.fragment_video, null)
        listView = view.findViewById<View>(R.id.listview) as ListView
        tv_info = view.findViewById<View>(R.id.tv_nomedia) as TextView
        //设置点击事件
        listView!!.onItemClickListener =
            MyOnItemClickListener()
        return view
    }

    override fun initData() {
        super.initData()
        Log.e(TAG, "本地视频数据绑定了")
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
                    val resolver = context!!.contentResolver
                    val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    val objs = arrayOf(
                        MediaStore.Video.Media.DISPLAY_NAME,  //在sdcard时候的名称
                        MediaStore.Video.Media.DURATION,  //视频的时长，毫秒
                        MediaStore.Video.Media.SIZE,  //文件大小，单位字节
                        MediaStore.Video.Media.ARTIST,  //演唱者
                        MediaStore.Video.Media.DATA //在sdcard上路径
                    )

                    val cursor = resolver.query(uri, objs, null, null, null)
                    if (cursor != null) {
                        //循环
                        while (cursor.moveToNext()) {
                            //创建一个视频信息类
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

                        cursor.close()
                    }

                    handler.sendEmptyMessage(0)
                }
            }.start()
        }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (mediaItems != null && mediaItems!!.size > 0) {
                //有数据
                tv_info!!.visibility = View.GONE
                //设置适配器
                adapter = MyAdapter()
                listView!!.adapter = adapter
            } else {
                //没有数据
                tv_info!!.visibility = View.VISIBLE
            }
        }
    }

    private inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return mediaItems!!.size
        }

        override fun getItem(position: Int): Any {
            return mediaItems!![position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val viewHolder: ViewHolder
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_video_fragment, null)
                viewHolder = ViewHolder()
                viewHolder.iv_icon = convertView.findViewById<View>(R.id.iv_icon) as ImageView
                viewHolder.tv_name = convertView.findViewById<View>(R.id.tv_name) as TextView
                viewHolder.tv_duration =
                    convertView.findViewById<View>(R.id.tv_duration) as TextView
                viewHolder.tv_size = convertView.findViewById<View>(R.id.tv_size) as TextView

                //设置tag
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }

            //            TextView textView = new TextView(context);
//            textView.setTextSize(20);
//            textView.setText(mediaItems.get(position).toString());

            //根据位置得到对应的数据
            val mediaItem = mediaItems!![position]
            viewHolder.tv_name!!.text = mediaItem.name
            //设置时间
            viewHolder.tv_duration!!.text = utils.stringForTime(mediaItem.duration.toInt())
            //格式化文件的存储大小
            viewHolder.tv_size!!.text = Formatter.formatFileSize(context, mediaItem.size)

            return convertView
        }
    }

    internal class ViewHolder {
        var iv_icon: ImageView? = null
        var tv_name: TextView? = null
        var tv_duration: TextView? = null
        var tv_size: TextView? = null
    }

    private inner class MyOnItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val mediaItem = mediaItems!![position]
            Toast.makeText(context, "mediaItem==$mediaItem", Toast.LENGTH_SHORT).show()

            //把系统的播放器调起来并且播放
//            Intent intent = new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //调起自己的播放器
//            Intent intent = new Intent(context,SystemPlayerActivity.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);

            //3.传递视频列表
            val intent = Intent(context, SystemPlayerActivity::class.java)


            //使用Bundler传递列表数据
            val bundle = Bundle()
            bundle.putSerializable("medialist", mediaItems)
            intent.putExtras(bundle)

            //传递位置
            intent.putExtra("position", position)
            context!!.startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "VideoFragment"
    }
}

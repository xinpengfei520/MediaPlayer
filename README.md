# MediaPlayer

[apk下载](https://www.pgyer.com/MediaPlayer_Android)

321手机影音

## 1.项目中使用的第三方依赖库

- [xUtils3](https://github.com/wyouflf/xUtils3)
- [讯飞开放平台](https://www.xfyun.cn/)
- [easypermissions](https://github.com/googlesamples/easypermissions)
- [腾讯 Bugly](https://bugly.qq.com/v2/)
- [EventBus 3.0](https://github.com/greenrobot/EventBus)
- [leakcanary](https://github.com/square/leakcanary)
- [glide](https://github.com/bumptech/glide)
- [picasso](https://github.com/square/picasso)
- [Android-MaterialRefreshLayout](https://github.com/android-cjj/Android-MaterialRefreshLayout)
- [VitamioBundle](https://github.com/yixia/VitamioBundle)
- [Vitamio中文网](https://www.vitamio.org/)
- [一下科技](http://www.yixia.com/)注：Vitamio 现在属一下科技的产品，旗下有：一直播，小咖秀等APP。
- [声网开发者中心](https://docs.agora.io/cn)
- [XListView-Android](https://github.com/Maxwin-z/XListView-Android)
- [JiaoZiVideoPlayer](https://github.com/lipangit/JiaoZiVideoPlayer)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [Fragmentation](https://github.com/YoKeyword/Fragmentation)
- [PhotoView](https://github.com/chrisbanes/PhotoView)
- ...

## 2.部分功能及截图

![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example1.png)
![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example2.png)
![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example3.png)
![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example4.png)
![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example5.png)
![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example6.png)
![image](https://github.com/xinpengfei520/MediaPlayer/blob/master/image/example7.png)

## 3.把系统的播放器调起来并且播放

```
Intent intent = new Intent();
intent.setDataAndType(Uri.parse("http://192.168.1.165:8080/video.rmvb"),"video/*");
startActivity(intent);
```

注：**video.rmvb** 为文件的名称及格式，能播放什么格式的视频文件，取决于你手机系统播放器所支持的视频格式！

## TODO

...


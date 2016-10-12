// IMusicPlayerService.aidl
package com.atguigu.mediaplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {

   /**
        * 根据位置播放对应的音频文件
        *
        * @param position
        */
       void openAudio(int position) ;

       /**
        * 播放音乐
        */
        void start() ;

       /**
        * 暂停音乐
        */
        void pause();

       /**
        * 设置播放模式
        *
        * @param playmode
        */
        void setPlaymode(int playmode) ;

       /**
        * 得到播放模式
        *
        * @return
        */
        int getPlaymode() ;

       /**
        * 得到艺术家
        *
        * @return
        */
        String getArtist();
       /**
        * 得到歌曲名称
        *
        * @return
        */
        String getAudioName();

       /**
        * 得到音频总时长
        *
        * @return
        */
        int getDuration() ;

       /**
        * 得到当前的播放进度
        *
        * @return
        */
        int getCurrentPosition();

       /**
        * 音频的拖动
        *
        * @param position
        */
        void seekTo(int position);

       /**
        * 播放下一个
        */
        void next();

       /**
        * 播放上一个
        */
        void pre();

        /**
         * 是否正在播放
         */
        boolean isPlaying();

        /**
         * 是否显示通知栏
         */
         void notifyChange(String action);

         /**
          * 得到歌曲播放的绝对路径
          */
         String getAudioPath();

         int getAudioSessionId();

         boolean isLoadDataComplete();
}

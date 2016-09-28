package fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.mediaplayer.R;

import utils.LogUtil;

/**
 * Created by xinpengfei on 2016/9/28.
 * <p>
 * Function : 系统播放器
 */
public class SystemPlayerActivity extends Activity {

    private VideoView videoview;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_systemplayer);
        videoview = (VideoView) findViewById(R.id.videoview);
        uri = getIntent().getData();//视频播放地址
        LogUtil.e("uri==" + uri);
        //设置视频播放的监听：准备好，播放出错，播放完成
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        videoview.setOnErrorListener(new MyOnErrorListener());
        videoview.setOnCompletionListener(new MyOnCompletionListener());

        //设置播放地址
        videoview.setVideoURI(uri);

        //设置控制面板
        videoview.setMediaController(new MediaController(this));
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {

            //开始播放
            videoview.start();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemPlayerActivity.this, "播放出错了", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 播放完成的监听 : 退出播放器
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            finish();//退出播放器
        }
    }
}

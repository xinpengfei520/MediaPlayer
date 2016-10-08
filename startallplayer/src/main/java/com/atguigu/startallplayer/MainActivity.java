package com.atguigu.startallplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv_startAllPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_startAllPlayer = (TextView)findViewById(R.id.tv_startAllPlayer);
        tv_startAllPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把系统的播放器调起来并且播放
                Intent intent = new Intent();
//                intent.setDataAndType(Uri.parse("http://192.168.1.165:8080/yellow.mp4"),"video/*");
//                intent.setDataAndType(Uri.parse("http://192.168.1.165:8080/yellow.mp4"),"video/*");
                intent.setDataAndType(Uri.parse("http://192.168.1.165:8080/rmvb.rmvb"),"video/*");
                startActivity(intent);
            }
       });

    }
}

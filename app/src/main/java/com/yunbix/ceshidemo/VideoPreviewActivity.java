package com.yunbix.ceshidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/4/14.
 */

public class VideoPreviewActivity extends Activity {

    private RelativeLayout pop;
    private ImageView video_play,video_iv;
    private VideoView videoView;
    private MediaController controller;
    private String videopath;
    private LinearLayout back;
    private TextView tvcancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮

        setContentView(R.layout.activity_videopreview);
        Intent intent = getIntent();
        videopath = intent.getStringExtra("videopath");
        pop = (RelativeLayout) findViewById(R.id.pop);
        video_play = (ImageView) findViewById(R.id.video_play);
        video_iv = (ImageView) findViewById(R.id.video_iv);
        videoView = (VideoView) findViewById(R.id.videoview);
        back= (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(VideoPreviewActivity.this).load(videopath).into(video_iv);
            }
        });
        tvcancle = (TextView) findViewById(R.id.tv_cancel);
        tvcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new VideoMsg(videopath));
                finish();
            }
        });
        controller = new MediaController(this);
        videoView.setMediaController(controller);
        videoView.setVideoPath(videopath);
        video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.setVisibility(View.GONE);
                videoView.start();
            }
        });
    }
}

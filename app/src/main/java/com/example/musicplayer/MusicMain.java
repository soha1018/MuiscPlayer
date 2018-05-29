package com.example.musicplayer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.view.View;

public class MusicMain extends Activity implements View.OnClickListener, OnCompletionListener {
    public static MediaPlayer mPlayer;
    private ImageButton btn_back, btn_play, btn_pause, btn_prev, btn_next;
    private TextView current_time, finish_time, music_title, music_artist;
    private SeekBar timeBar;
    private String path = null;
    private int position;
    private Cursor cursor;
    private Timer timer = new Timer();
    private final int MSG = 0x123;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG) {
                setTime();
            }
        }
    };
    private TimerTask task = new TimerTask() {


        public void run() {
            mHandler.sendEmptyMessage(MSG);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyActivityManager.getIntance().addActivity(this);
        setContentView(R.layout.activity_play);
        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER+" asc");
            position = getIntent().getIntExtra("position", 0);
            System.out.print(position);
            cursor.moveToPosition(position);
            initWidget();
            stateJudge();
        }
    }

    /*protected void onStart(){
		if(mPlayer==null){
			mPlayer=new MediaPlayer();
			cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			position=getIntent().getIntExtra("position", 0);
			System.out.print(position);
			cursor.moveToPosition(position);
			initWidget();
			stateJudge();
		}
	}*/

    private void stateJudge() {
        // TODO Auto-generated method stub
        if (MusicList.tag) {
            musicPrepare();
            play();
        } else {
            if (!mPlayer.isPlaying()) {
                if (isPrepared()) {
                    changePlayStatus(false);
                } else {
                    musicPrepare();
                    changePlayStatus(false);
                }
            }
        }
    }

    private void changePlayStatus(boolean b) {
        // TODO Auto-generated method stub
        if (b) {
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);

        } else {
            btn_play.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
        }
    }

    private boolean isPrepared() {
        // TODO Auto-generated method stub
        if (MusicList.prepared) {
            return true;
        } else {
            return false;
        }
    }

    private void play() {
        // TODO Auto-generated method stub
        changePlayStatus(true);
        mPlayer.start();
    }

    private void musicPrepare() {
        // TODO Auto-generated method stub
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        mPlayer.reset();
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MusicList.prepared = true;
    }

    private void initWidget() {
        // TODO Auto-generated method stub
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_play = (ImageButton) findViewById(R.id.play);
        btn_pause = (ImageButton) findViewById(R.id.pause);
        btn_prev = (ImageButton) findViewById(R.id.prev);
        btn_next = (ImageButton) findViewById(R.id.next);
        current_time = (TextView) findViewById(R.id.time_current);
        finish_time = (TextView) findViewById(R.id.time);
        music_title = (TextView) findViewById(R.id.music_title);
        music_artist = (TextView) findViewById(R.id.music_artist);
        timeBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
        btn_back.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_prev.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        timeBar.setOnSeekBarChangeListener(new SeekBarListener());
        mPlayer.setOnCompletionListener(this);
        musicInfo();


    }

    private void musicInfo() {
        // TODO Auto-generated method stub
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        music_title.setText(title);
        music_artist.setText(artist);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        seriation(1);
        musicPrepare();
        play();

    }

    private void seriation(int i) {
        // TODO Auto-generated method stub
        int count = cursor.getCount();
        if (i == 0) {
            position = position - 1 > 0 ? position - 1 : count - 1;
        } else {
            position = position + 1 <= count - 1 ? position + 1 : 0;
        }
        MusicList.position = position;
        cursor.moveToPosition(position);
        musicInfo();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_back:
                backListener();
                break;
            case R.id.play:
                playListener();
                break;
            case R.id.pause:
                pauseListener();
                break;
            case R.id.prev:
                prevListener();
                break;
            case R.id.next:
                nextListener();
                break;
        }

    }

    private void nextListener() {
        // TODO Auto-generated method stub
        seriation(0);
        musicPrepare();
        setTime();
        play();
    }

    private void prevListener() {
        // TODO Auto-generated method stub
        seriation(0);
        musicPrepare();
        setTime();
        play();
    }

    private void setTime() {
        // TODO Auto-generated method stub
        if (timeBar != null) {
            timeBar.setMax(mPlayer.getDuration());
            finish_time.setText(toTime(mPlayer.getDuration()));
            timeBar.setProgress(mPlayer.getCurrentPosition());
            current_time.setText(toTime(mPlayer.getCurrentPosition()));
        }

    }

    private String toTime(int time) {
        // TODO Auto-generated method stub
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format("%01d:02d", minute, second);
    }

    private void pauseListener() {
        // TODO Auto-generated method stub
        if (mPlayer.isPlaying()) {
            changePlayStatus(false);
            mPlayer.stop();
        } else {
            play();
        }
    }

    private void pause() {
        // TODO Auto-generated method stub
        if (!mPlayer.isPlaying()) {

        } else {
            play();
        }
    }

    private void playListener() {
        // TODO Auto-generated method stub
        if (!mPlayer.isPlaying()) {
            if (isPrepared()) {
                play();
            }
        }
    }

    private void backListener() {
        // TODO Auto-generated method stub
        Intent it = new Intent(MusicMain.this, MusicList.class);
        it.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(it);
    }

    class SeekBarListener implements OnSeekBarChangeListener {


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            if (fromUser) {
                mPlayer.seekTo(progress);
                setTime();
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

    }

}


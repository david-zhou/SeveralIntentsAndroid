package com.dzt.httpposttest;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;


public class RecordAudio extends ActionBarActivity implements View.OnClickListener{

    Button record_audio, pause_audio, stop_audio, play_audio, stop_playing_audio;
    MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private MediaPlayer mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        initializeComponents();
    }

    private void initializeComponents() {
        record_audio = (Button) findViewById(R.id.record_audio);
        record_audio.setOnClickListener(this);

        pause_audio = (Button) findViewById(R.id.pause_audio);
        pause_audio.setOnClickListener(this);

        stop_audio = (Button) findViewById(R.id.stop_audio);
        stop_audio.setOnClickListener(this);

        play_audio = (Button) findViewById(R.id.play_audio);
        play_audio.setOnClickListener(this);

        stop_playing_audio = (Button) findViewById(R.id.stop_playing);
        stop_playing_audio.setOnClickListener(this);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            default:
                break;
            case R.id.record_audio:
                recordAudio();
                break;
            case R.id.pause_audio:
                break;
            case R.id.stop_audio:
                stopAudio();
                break;
            case R.id.play_audio:
                playAudio();
                break;
            case R.id.stop_playing:
                stopPlaying();
                break;
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void playAudio() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("MyLog", "prepare() failed");
        }
    }

    private void stopAudio() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void recordAudio() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            //Log.e("MyLog", "prepare() failed");
            e.printStackTrace();
        }

        mRecorder.start();
    }
}

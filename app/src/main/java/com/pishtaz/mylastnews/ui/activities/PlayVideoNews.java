package com.pishtaz.mylastnews.ui.activities;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pishtaz.mylastnews.R;
import com.pishtaz.mylastnews.utils.Utilities;

//import android.support.v7.app.AppCompatActivity;

public class PlayVideoNews extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


    ProgressBar progressbarVideo;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    public static Handler mHandler = new Handler();
    private Utilities utils;
    //ImageView fullscreeenImage;
    ImageView ivPlayCenter;
    private SeekBar songProgressBar;
    private String path;
    private Uri uri;
    public static VideoView videoView;
    public View view;
    boolean flag = true;
    LinearLayout linearAllVideo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_news);

        path = getIntent().getStringExtra("videoURL");
        Log.e("paaaath,", path + "_____");
        progressbarVideo = (ProgressBar) findViewById(R.id.progressbarVideo);
        videoView = (VideoView) findViewById(R.id.VideoView);
        linearAllVideo = (LinearLayout) findViewById(R.id.linearAllVideo);
       // fullscreeenImage = (ImageView) findViewById(R.id.fullscreeenImage);
        ivPlayCenter = (ImageView) findViewById(R.id.ivPlayCenter);
        //  videoView.setMediaController(controller);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songProgressBar.setOnSeekBarChangeListener(this); // Important

        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);

        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        hideController();

        if (videoView != null) {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        }

        utils = new Utilities();

        playMyVideo();

      /*  fullscreeenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // activity.fullScreen();
                finish();
            }
        });
*/
        ivPlayCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (videoView.isPlaying()) {
                    if (videoView != null) {
                        videoView.pause();
                        ivPlayCenter.setImageResource(R.drawable.play_empty);
                    }
                } else {
                    if (videoView != null) {
                        videoView.start();
                        ivPlayCenter.setImageResource(R.drawable.pause_empty);
                        Log.d("PPPPPPP", "PPPPP");

                    }

                }
            }
        });



        linearAllVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (flag) {
                    hideController();
                    handler.removeCallbacks(r);
                    flag = false;
                } else if (!flag) {
                    showController();
                    flag = true;
                    hide();
                }
                return false;
            }
        });



    }

    @Override
    public void onResume() {
        //videoView.resume();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        videoView.stopPlayback();


    }

    @Override
    public void onStart() {
        super.onStart();

    }


    public void playMyVideo() {


        if (path.equals("")) {
         //   Toast.makeText(getApplicationContext(), "???? ??? ???? ??? ??? ???", Toast.LENGTH_LONG).show();
            //  return;
        } else {
            try {
                uri = Uri.parse(path);
                videoView.setVideoURI(uri);

                // videoView.setMediaController(controller);
                // videoView.requestFocus();
                //  btnPlay.toggle();
                progressbarVideo.setVisibility(View.VISIBLE);
                hideController();
                handler.removeCallbacks(r);
                flag = false;

                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);
                updateProgressBar();
                videoView.requestFocus();
                videoView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
                ivPlayCenter.setImageResource(R.drawable.play_empty);
                progressbarVideo.setVisibility(View.GONE);
                linearAllVideo.setVisibility(View.VISIBLE);
                //   btnPlay.setVisibility(View.VISIBLE);
              //  fullscreeenImage.setVisibility(View.VISIBLE);

            } catch (Exception ex) {
                Log.d("EEEEEEE", ex + "");
            }
        }
    }

    public void hideController() {

        //  btnPlay.setVisibility(View.GONE);
        songProgressBar.setVisibility(View.GONE);
        ivPlayCenter.setVisibility(View.GONE);
        songCurrentDurationLabel.setVisibility(View.GONE);
        songTotalDurationLabel.setVisibility(View.GONE);
      //  fullscreeenImage.setVisibility(View.GONE);



    }

    public void showController() {
        ivPlayCenter.setVisibility(View.VISIBLE);
        //   btnPlay.setVisibility(View.VISIBLE);
        songProgressBar.setVisibility(View.VISIBLE);
        songCurrentDurationLabel.setVisibility(View.VISIBLE);
        songTotalDurationLabel.setVisibility(View.VISIBLE);
      //  fullscreeenImage.setVisibility(View.VISIBLE);


    }




    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }



    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = videoView.getDuration();
            long currentDuration = videoView.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = videoView.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        videoView.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        //if(isRepeat){
        // repeat is on play same song again
        // playSong(currentSongIndex);
         /*else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        }*//* else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);

        videoView.stopPlayback();

    }


    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
        ivPlayCenter.setImageResource(R.drawable.play_empty);


    }

    // >>> added in 13aug2015-0935
    @Override
    public void onBackPressed() {
        finish();
    }

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            if (videoView.isPlaying()) {
                hideController();
                flag = false;
            }
        }
    };

    public void hide() {
        if (videoView.isPlaying()) {
            handler.postDelayed(r, 2000);
        }
    }


}

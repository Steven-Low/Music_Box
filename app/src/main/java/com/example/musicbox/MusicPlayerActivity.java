package com.example.musicbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon,shuffleBtn,btnOption;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x=0;
    boolean shuffling;
    private PopupMenu popupMenu;
    SleepTimer sleepTimer = new SleepTimer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        shuffleBtn = findViewById(R.id.shuffle);
        btnOption = findViewById(R.id.btn_option);

        titleTv.setSelected(true);
        songsList = (ArrayList<AudioModel>)getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();



        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu = new PopupMenu(MusicPlayerActivity.this, btnOption);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_1:
                                InputDialogFragment inputDialog = new InputDialogFragment();
                                inputDialog.setListener(new InputDialogFragment.InputDialogListener() {
                                    @Override
                                    public void onInputDialogPositiveClick(DialogFragment dialog, String input) {
                                        MyMediaPlayer.delaySeconds = Integer.parseInt(input);
                                        Toast.makeText(getApplicationContext(),"Shuffle delay set to "+input+" seconds",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                inputDialog.show(getSupportFragmentManager(), "input_dialog");
                                break;

                            case R.id.item_2:
                                InputDialogFragment2 inputDialog2 = new InputDialogFragment2();
                                inputDialog2.setListener(new InputDialogFragment2.InputDialogListener() {
                                    @Override
                                    public void onInputDialogPositiveClick(DialogFragment dialog, String input) {
                                        MyMediaPlayer.sleepTime = Integer.parseInt(input);
                                        int sleep = Integer.parseInt(input);
                                        sleepTimer.setTime(sleep);
                                        Toast.makeText(getApplicationContext(),"Sleep timer to "+input+" minutes",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                inputDialog2.show(getSupportFragmentManager(), "input_dialog_2");
                                break;
                            // ...
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                        if(sleepTimer.getTime() - sleepTimer.start >= sleepTimer.sleep){
                            pausePlay();
                            Toast.makeText(getApplicationContext(),"Timer ended",Toast.LENGTH_SHORT).show();
                            sleepTimer.reset();
                        }
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                        if(shuffling){
                            try {
                                Thread.sleep(MyMediaPlayer.delaySeconds * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            shuffleSong();
                        }
                    }
                }
                new Handler().postDelayed(this,100); // repeatedly update the seekbar & currentTimeTv
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));
        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());
        shuffleBtn.setOnClickListener(v-> shuffleSong());
        playMusic();


    }

    private void playMusic(){
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void shuffleSong(){
        Random rand = new Random();
        MyMediaPlayer.currentIndex = rand.nextInt(songsList.size());
        mediaPlayer.reset();
        setResourcesWithMusic();
        shuffling = true;
    }

    private void playNextSong(){
        if(MyMediaPlayer.currentIndex == songsList.size()-1)
            return;  // already reach last song!
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex == 0)
            return;  // already reach first song
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            shuffling = false;
        }
        else
            mediaPlayer.start();
    }

    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
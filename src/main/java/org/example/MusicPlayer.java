package org.example;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    private Song currentSong; // this will store the details of the current song.

    /* Create a Player to hadle the music using Jlayer library */
    private AdvancedPlayer advancedPlayer;
    private int pausedOnFrame = 0;
    private boolean isPaused;

    /* Constructor */
    public MusicPlayer(){

    }

    public void loadSong( Song currentSong){
        this.currentSong = currentSong;

        /* Play the song if it's not null */
        if(this.currentSong != null){
            playCurrentSong();
        }
    }

    public void playCurrentSong(){

        if(currentSong == null) return;

        try{
            /* Read mp3 audio data */
            FileInputStream fileInputStream = new FileInputStream(this.currentSong.getFilepath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            /* Create a new advanced player */
            this.advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            this.advancedPlayer.setPlayBackListener(this);

            startMusicThreat();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private int getPausedOnFrame(){ return this.pausedOnFrame; }
    public void pauseMusic(){
        if(advancedPlayer != null){
            isPaused = true;

            // Stop the player
            stopSong();
        }
    }
    public void stopSong(){
        if (advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void startMusicThreat(){
        new Thread(() -> {
            try {
                if (isPaused){
                 /* Resume music */
                    advancedPlayer.play(getPausedOnFrame(),Integer.MAX_VALUE);
                }else{
                    /* Play music from the beginning */
                    advancedPlayer.play();
                }
            } catch (JavaLayerException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        /* Called at the beginning of the song */
        System.out.println("Playback started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        /* Called at the end of the song. */
        System.out.println("Playback Finished at: "+ evt.getFrame());
        if(isPaused){
           pausedOnFrame += (int) ( (double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }
    }
}


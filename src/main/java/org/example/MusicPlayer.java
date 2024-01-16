package org.example;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    /* Create a object to help synchronize the threads */
    private static final Object playSignal = new Object();
    /* this will store the details of the current song. */
    private Song currentSong;
    /* Create a Player to handle the music using Jlayer library */
    private AdvancedPlayer advancedPlayer;
    private int pausedOnFrame = 0;
    public void setCurrentFrame(int frame){
        this.pausedOnFrame = frame;
    }
    private boolean isPaused;
    private boolean wasPlayingSongBefore = false;
    /* Track how many milliseconds has passed since playing the song (used for update the slice) */
    private int currentTimeInMillisecond;
    /* we need a reference to the GUI class to update the slider each millisecond of the song */
    private Music_Player_GUI MusicPlayerGUI;
    /* Constructor */
    public MusicPlayer(Music_Player_GUI MusicPlayerGUI){
        this.MusicPlayerGUI = MusicPlayerGUI;
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

            /* Start music */
            startMusicThreat();

            /*Start playback slider thread */
            startPlaybackThread();

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
                    synchronized (playSignal){
                        /* Update Flag */
                        isPaused=false;
                        /* notify the other thread the signal has changed */
                        playSignal.notify();
                    }

                    System.out.println("isPaused: "+ isPaused);
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

    /* Start a new thread for the playback functionality */
    private void startPlaybackThread(){
        new Thread( ()-> {
            if(isPaused){
                try{
                    /* Waits until gets notified by the other thread
                    */
                    synchronized (playSignal){
                        playSignal.wait();
                    }
                }
                catch (InterruptedException e){
                    System.out.println(e);
                }

            }
            while(!isPaused){
                try{
                    /* Increse the current time in milli */
                    currentTimeInMillisecond++;

                    /* Calculate frame */
                    int calculateFrame = (int) ((double) currentTimeInMillisecond * 2.08 * currentSong.getFrameRatePerMilliseconds());

                    /* here we need a reference to the GUI class */
                    this.MusicPlayerGUI.setSliderValue(calculateFrame);

                    /* sleep the Thread 1 millisecond */
                    Thread.sleep(1);
                }
                catch (Exception e ){
                    System.out.println("Couldn't interrup the Thread");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void playbackStarted(PlaybackEvent evt) {
        /* Called at the beginning of the song */
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        /* Called at the end of the song. */
        if(isPaused){
           pausedOnFrame += (int) ( (double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }
    }

    public boolean isWasPlayingSongBefore() {return wasPlayingSongBefore;}

    public void toggleWasPlayingSongBefore(boolean wasPlayingSongBefore) {this.wasPlayingSongBefore = wasPlayingSongBefore;}

    public void setCurrentTimeInMillisecond(int currentTimeInMillisecond) {this.currentTimeInMillisecond = currentTimeInMillisecond;}

    public Song getCurrentSong() {return currentSong;}
}


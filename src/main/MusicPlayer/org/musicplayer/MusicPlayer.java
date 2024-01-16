package org.musicplayer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {

    /* Create an object to help synchronize the threads */
    private static final Object playSignal = new Object();
    /* this will store the details of the current song. */
    private Song currentSong;
    /* Create a Player to handle the music using JavaLayer library */
    private AdvancedPlayer advancedPlayer;
    private int pausedOnFrame = 0;
    /* Controlls if the song play in a infinite loop*/
    private boolean isloop;
    public boolean getIsLoop(){return this.isloop;}
    public void setCurrentFrame(int frame){
        this.pausedOnFrame = frame;
    }
    private boolean isPaused;
    private boolean wasPlayingSongBefore = false;
    /* Track how many milliseconds has passed since playing the song (used for update the slice) */
    private int currentTimeInMillisecond;
    /* we need a reference to the GUI class to update the slider each millisecond of the song */
    private Music_Player_GUI MusicPlayerGUI;

    private ArrayList<Song> playlist = new ArrayList<>();

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

    public void loadPlaylist( File playlistFile){
        this.playlist = new ArrayList<>();

        /* Store the path into the arraylist */
        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            /* read each line from the txt file */
            String songPath;
            while( (songPath = bufferedReader.readLine()) != null ){
                /* create a song object*/
                Song song = new Song(songPath);

                /* Add the song to the path */
                playlist.add(song);
            }
        }catch (Exception e){
            System.out.println(e);
        }

        if(playlist.size() > 0 ){
            /* Set the slider to value 0*/
            MusicPlayerGUI.setSliderValue(0);

            /* Update current song with the first one on the playlist */
            this.currentSong = playlist.getFirst();
            this.currentTimeInMillisecond = 0;

            /* Start from the beginning frame  */
            this.pausedOnFrame= 0;

            /*Update GUI*/
            MusicPlayerGUI.EnablePauseBtn();
            MusicPlayerGUI.updateSongInfo(this.currentSong);
            MusicPlayerGUI.updatePlaybackSlider(this.currentSong);

            /* Start song */
            playCurrentSong();

            System.out.println(playlist.indexOf(currentSong));
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
                    /* Increase the current time in milli */
                    currentTimeInMillisecond++;

                    /* Calculate frame */
                    int calculateFrame = (int) ((double) currentTimeInMillisecond * 2.08 * currentSong.getFrameRatePerMilliseconds());

                    /* here we need a reference to the GUI class */
                    this.MusicPlayerGUI.setSliderValue(calculateFrame);

                    /* sleep the Thread 1 millisecond */
                    Thread.sleep(1);
                }
                catch (Exception e ){
                    System.out.println("Couldn't interrupt the Thread");
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
        else if(isloop){
            System.out.println("La cancion termino");
            /* Stop the current song */
            this.isPaused = false;
            /* Restart the song */
            this.currentTimeInMillisecond = 0;
            this.pausedOnFrame = 0;
            MusicPlayerGUI.setSliderValue(0);
            playCurrentSong();
        }
    }

    public void toggleLoop(){
        if(currentSong != null) {
            this.isloop = !this.isloop;
            System.out.println(this.isloop);
        }else{
            this.isloop = false;
        }
    }

    public boolean isWasPlayingSongBefore() {return wasPlayingSongBefore;}

    public void toggleWasPlayingSongBefore(boolean wasPlayingSongBefore) {this.wasPlayingSongBefore = wasPlayingSongBefore;}

    public void setCurrentTimeInMillisecond(int currentTimeInMillisecond) {this.currentTimeInMillisecond = currentTimeInMillisecond;}

    public Song getCurrentSong() {return currentSong;}

    public boolean nextSong(){
        if (playlist == null) return false;

        if(playlist.size() > 0 ){
            /* stop the music */
            stopSong();

            /* Set the slider to value 0*/
            MusicPlayerGUI.setSliderValue(0);

            if(playlist.indexOf(this.currentSong)+1 >= playlist.size()){
                /* Update current song with the first one on the playlist */
                this.currentSong = playlist.getFirst();
            }else{
                /* Update current song with the first one on the playlist */
                this.currentSong = playlist.get(playlist.indexOf(currentSong)+1);
            }
            this.currentTimeInMillisecond = 0;

            /* Start from the beginning frame  */
            this.pausedOnFrame= 0;

            /*Update GUI*/
            MusicPlayerGUI.EnablePauseBtn();
            MusicPlayerGUI.updateSongInfo(this.currentSong);
            MusicPlayerGUI.updatePlaybackSlider(this.currentSong);

            /* Start song */
            playCurrentSong();

            System.out.println(playlist.indexOf(currentSong));
            return true;
        }
        return false;
    }

    public boolean prevSong(){
        if(playlist.size() > 0 ){
            /* stop the music */
            stopSong();

            /* Set the slider to value 0*/
            MusicPlayerGUI.setSliderValue(0);

            if(playlist.indexOf(this.currentSong)-1 < 0){
                /* Update current song with the first one on the playlist */
                this.currentSong = playlist.getLast();
            }else{
                /* Update current song with the first one on the playlist */
                this.currentSong = playlist.get(playlist.indexOf(currentSong)-1);
            }
            this.currentTimeInMillisecond = 0;

            /* Start from the beginning frame  */
            this.pausedOnFrame= 0;

            /*Update GUI*/
            MusicPlayerGUI.EnablePauseBtn();
            MusicPlayerGUI.updateSongInfo(this.currentSong);
            MusicPlayerGUI.updatePlaybackSlider(this.currentSong);

            /* Start song */
            playCurrentSong();
            return true;
        }
        return false;
    }
}


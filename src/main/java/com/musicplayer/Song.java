package com.musicplayer;

import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class Song{
    private String title;
    private String artist;
    private String duration;
    private final String filepath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filepath){
        this.filepath = filepath;
        try{
            /* We will calculate the frame rate per millisecond */

            mp3File = new Mp3File(filepath); // this gives us the total length of the song
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            duration = convertToSongLengthFormat();

            /* Create an Audio file from jaudiotagger library to read mp3 info */
            AudioFile audioFile = AudioFileIO.read(new File(this.filepath));
            /* Read the tags in the mp3 file */
            Tag tag = audioFile.getTag();

            // System.out.println(tag);

            if (tag != null){
                this.title = (tag.getFirst(FieldKey.TITLE) != null || tag.getFirst(FieldKey.TITLE) == "") ?  tag.getFirst(FieldKey.TITLE) : "Unknown";
                this.artist = (tag.getFirst(FieldKey.ARTIST) != null || tag.getFirst(FieldKey.ARTIST) == "") ?  tag.getFirst(FieldKey.ARTIST) : "Unknown";
            }else{
                this.title = "Unknown";
                this.artist = "Unknown";
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /* Getters */
    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getDuration() {
        return this.duration;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public Mp3File getMp3File() {return mp3File;}

    public double getFrameRatePerMilliseconds() {return frameRatePerMilliseconds;}
    private String convertToSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        return String.format("%02d:%02d",minutes,seconds);
    }
}
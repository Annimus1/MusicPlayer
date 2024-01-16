package org.musicplayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class Music_Player_GUI extends JFrame {
    public static final Color BACKGROUD_COLOR = Color.WHITE;
    private JMenuItem loadSong;
    private MusicPlayer musicPlayer;
    private JButton pause;
    private JButton play;
    private final JLabel songTitle = new JLabel("Song Title");
    private final JLabel artist = new JLabel("Artist");
    private final JSlider slider = new JSlider();

    public Music_Player_GUI(){
        super("Music Player");
        /* Setting the window icon */
        try {
            /* Load the icon from the path */
            Image icon = ImageIO.read( new File("src/main/MusicPlayer/assets/Icon.png"));
            /* assign the image to the Frame */
            setIconImage(icon);
        }
        catch(Exception e){
            System.out.println("Couldn't load the icon");
        }
        setSize(400,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        getContentPane().setBackground(BACKGROUD_COLOR);

        musicPlayer = new MusicPlayer(this);

        /* Add all components */
        setComponents();
    }

    private void setComponents(){
        /* Insert the toolbar */
        setToolBar();
        /* Inset cover image */
        setCover();
        /* Insert information of the current song*/
        setSongInfo();
        /* Insert Control section */
        setControls();
    }

    private void setToolBar(){
        /* Create Tool Bar */
        JToolBar toolBar = new JToolBar();
        toolBar.setBackground(null);
        /* Set size and location */
        toolBar.setBounds(0,0, getWidth(),20);
        /* Prevent it float */
        toolBar.setFloatable(false);

        /* Create menu  to contain the dropbox menus */
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(null);
        menuBar.setBorder(null);
        /* Create menu to upload a song */
        JMenu songMenu = new JMenu("Song");
        /* Create item*/
        loadSong = new JMenuItem("Load song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Handle the slice goes to 0 if another song was playing
                *  And close last thread before start the new one */
                if(musicPlayer.isWasPlayingSongBefore()){
                   /* Set slider value to 0*/
                   musicPlayer.setCurrentTimeInMillisecond(0);
                   /* interrupt last thread */
                   musicPlayer.stopSong();
                }


                File filename = getPathFromUser();
                if(filename != null){
                    /* Create a new song */
                    Song song = new Song(filename.getPath());
                    /* Toggle buttons */
                    togglePlayPause();
                    /* update Song info */
                    updateSongInfo(song);
                    /* Update slider */
                    updatePlaybackSlider(song);
                    /* Load the new song into the MusicPlayer */
                    musicPlayer.loadSong(song);
                    musicPlayer.toggleWasPlayingSongBefore(true);
                }
            }
        });
        songMenu.add(loadSong);

        /* Create items */
        /* Create menu to add a song */
        JMenuItem addPlaylist = new JMenuItem("Add Playlist");
        addPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Create the dialog */
                new MusicPlayerDialog(Music_Player_GUI.this).setVisible(true);
            }
        });

        /* Create menu to load a song */
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        JMenu playlistMenu = new JMenu("Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Playlist","txt"));
                int result = fileChooser.showOpenDialog(Music_Player_GUI.this);
                File selectedFile = fileChooser.getSelectedFile();

                if(selectedFile != null && JFileChooser.APPROVE_OPTION == result){
                    /* Stop the music */
                    musicPlayer.stopSong();

                    /* Load Playlist */
                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });

        /* Add items to the dropbox */
        playlistMenu.add(addPlaylist);
        playlistMenu.add(loadPlaylist);

        /* Adding drop boxes to the menu bar*/
        menuBar.add(songMenu);
        menuBar.add(playlistMenu);

        /* Add menu bar to the toolbar */
        toolBar.add(menuBar);


        /* Add bar to the window */
        add(toolBar);
    }

    private File getPathFromUser(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter( new FileNameExtensionFilter("MP3","mp3"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileName = fileChooser.getSelectedFile();
            return fileName;
        }
        return null;
    }
    private void setCover(){
        JLabel canvas = new JLabel(loadImage("src/main/MusicPlayer/assets/Cover.png"));
        canvas.setBounds(0,20,getWidth(),300);
        canvas.setBackground(null);
        add(canvas);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            /* Read the image from a given path */
            BufferedImage image = ImageIO.read( new File(imagePath));

            /* Return a ImageIcon */
            return new ImageIcon(image);
        }
        catch (Exception e){
            /* It means it can't read the file, or it doesn't exist. So print the error Stack Trace*/
            System.out.println("Couldn't load the image from path: " + imagePath);
        }
        return null;
    }

    private void setSongInfo(){
        /* Set the labels */
        /* Set Tittle of the song*/
        songTitle.setBounds(0,300,getWidth()-10, 30);
        songTitle.setFont( new Font("Dialog", Font.BOLD,24));
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        /* Set Artist Name */
        artist.setBounds(0,330,getWidth()-10, 30);
        artist.setFont( new Font("Dialog", Font.ITALIC, 20));
        artist.setHorizontalAlignment(SwingConstants.CENTER);
        add(artist);
    }

    private void setControls(){

        /* Insert the Slider with the width and height */
        slider.setBounds(0,400,getWidth(),40);
        slider.setBackground(null);
        slider.setValue(0);
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /* When the user holds the slider*/
                musicPlayer.pauseMusic();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                /* When the user drops the slider */
                JSlider source = (JSlider) e.getSource();

                /* Get the frame the user wants from the playback */
                int frame = source.getValue();

                /* Update the current frame in the music player to its frame */
                musicPlayer.setCurrentFrame(frame);

                /* update the current time in milliseconds */
                musicPlayer.setCurrentTimeInMillisecond((int) (frame/(2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                /* Resume the song */
                musicPlayer.playCurrentSong();
                if(play.isVisible()){
                    togglePlayPause();
                }
            }
        });
        add(slider);

        /* Create container for Controls */
        JPanel controls = new JPanel();
        controls.setBounds(0,440,getWidth(),100);
        controls.setBackground(null);

        /* Creating controls */
        /* Create List btn */
        JButton list = new JButton(loadImage("src/main/MusicPlayer/assets/List.png"));
        list.setToolTipText("Playlist");
        list.setBorder(null);
        list.setBackground(null);
        list.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Create rev btn */
        JButton prev = new JButton(loadImage("src/main/MusicPlayer/assets/Prev.png"));
        prev.setToolTipText("Prev. Song");
        prev.setBorder(null);
        prev.setBackground(null);
        prev.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Create pause btn */
        pause = new JButton(loadImage("src/main/MusicPlayer/assets/Pause.png"));
        pause.setToolTipText("Pause.");
        pause.setBorder(null);
        pause.setBackground(null);
        pause.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pause.setVisible(false);
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 /* Pause Music */
                musicPlayer.pauseMusic();
                /* Toggle buttons*/
                togglePlayPause();
            }
        });

        /* Create play btn */
        play = new JButton(loadImage("src/main/MusicPlayer/assets/Play.png"));
        play.setToolTipText("Play");
        play.setBorder(null);
        play.setBackground(null);
        play.setCursor(new Cursor(Cursor.HAND_CURSOR));
        play.setMargin(new Insets(0,50,0,50));
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(musicPlayer.getCurrentSong() != null ){
                /* Toggle buttons*/
                togglePlayPause();

                /* Play or resume song */
                musicPlayer.playCurrentSong();
            }}
        });

        /* Create Next btn */
        JButton next = new JButton(loadImage("src/main/MusicPlayer/assets/Next.png"));
        next.setToolTipText("Next Song");
        next.setBorder(null);
        next.setBackground(null);
        next.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Create loop btn */
        JButton loop = new JButton(loadImage("src/main/MusicPlayer/assets/Loop.png"));
        loop.setToolTipText("Play in Loop");
        loop.setBorder(null);
        loop.setBackground(null);
        loop.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Add Buttons to the container*/
        controls.add(list);
        controls.add(prev);
        controls.add(play);
        controls.add(pause);
        controls.add(next);
        controls.add(loop);


        /* Add the container to the layout */
        add(controls);
    }

    private void togglePlayPause(){
        pause.setVisible(!pause.isVisible());
        play.setVisible(!play.isVisible());
    }

    public void updateSongInfo(Song song){
        songTitle.setText((song.getTitle() != "") ? song.getTitle() : "Unknown");
        artist.setText((song.getArtist() != "") ? song.getArtist() : "Unknown");
    }

    public void updatePlaybackSlider(Song song){
        /* update max count of the slider */
        slider.setMaximum(song.getMp3File().getFrameCount());

        /* Create a song length label */
        Hashtable<Integer,JLabel> labelTable = new Hashtable<>();

        /* Begins in 00:00 */
        JLabel begin = new JLabel("00:00");
        begin.setFont(new Font("Dialog", Font.BOLD, 12));

        /* ends with song duration */
        JLabel end = new JLabel(song.getDuration());
        end.setFont(new Font("Dialog", Font.BOLD, 12));

        /* add values to the table */
        labelTable.put(0,begin);
        labelTable.put(song.getMp3File().getFrameCount(),end);

        /* Add label to Slider */
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
    }

    /* This will be used to update the slider*/
    public void setSliderValue( int frame){
        slider.setValue(frame);
    }

    public void EnablePlayBtn(){
        play.setVisible(true);
        pause.setVisible(false);
    }

    public void EnablePauseBtn(){
        play.setVisible(false);
        pause.setVisible(true);
    }

}
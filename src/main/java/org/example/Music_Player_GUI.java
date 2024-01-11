package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Music_Player_GUI extends JFrame implements ActionListener {

    private final Color BACKGROUD_COLOR = Color.WHITE;
    private JMenuItem loadSong;

    private final JLabel songTitle = new JLabel("Song Title");

    private final JLabel artist = new JLabel("Artist");

    private final JSlider slider = new JSlider();

    public Music_Player_GUI(){
        super("Music Player");
        /* Setting the window icon */
        try {
            /* Load the icon from the path */
            Image icon = ImageIO.read( new File("src/main/java/assets/Icon.png"));
            /* assign the imagen to the Frame */
            setIconImage(icon);
        }
        catch(Exception e){
            System.out.println("Coudn't load the icon");
        }

        setSize(400,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        getContentPane().setBackground(BACKGROUD_COLOR);

        /* Add all components */
        setComponets();
    }

    private void setComponets(){
        /* Insert the tool bar */
        setToolBar();
        /* Inset cover image */
        setCover();
        /* Insert information of the current song*/
        setSongInfo();
        /* Insert Control seccion */
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
        loadSong.addActionListener(this);
        songMenu.add(loadSong);

        /* Create menu to upload a song */
        JMenu playlistMenu = new JMenu("Playlist");
        /* Create items */
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        JMenuItem addPlaylist = new JMenuItem("Add Playlist");
        /* Add items to the dropbox */
        playlistMenu.add(addPlaylist);
        playlistMenu.add(loadPlaylist);

        /* Adding dropboxs to the menu bar*/
        menuBar.add(songMenu);
        menuBar.add(playlistMenu);

        /* Add menu bar to the tool bar */
        toolBar.add(menuBar);


        /* Add bar to the window */
        add(toolBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == loadSong){
            File filename = getPathFromUser();
            System.out.println(filename);
        }
    }
    private File getPathFromUser(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.CANCEL_OPTION) {
            File fileName = fileChooser.getSelectedFile();
            return fileName;
        }
        return null;
    }
    private void setCover(){
        JLabel canvas = new JLabel(loadImage("src/main/java/assets/Cover.png"));
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
            /* It menas it can't read the file or it doesn't exist. So print the error Stack Trace*/
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
        slider.setBounds(0,400,getWidth(),20);
        slider.setBackground(null);
        add(slider);

        /* Create container for Controls */
        JPanel controls = new JPanel();
        controls.setBounds(0,440,getWidth(),100);
        controls.setBackground(null);

        /* Creating controls */
        /* Create List btn */
        JButton list = new JButton(loadImage("src/main/java/assets/List.png"));
        list.setToolTipText("Playlist");
        list.setBorder(null);
        list.setBackground(null);
        list.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Create rev btn */
        JButton prev = new JButton(loadImage("src/main/java/assets/Prev.png"));
        prev.setToolTipText("Prev. Song");
        prev.setBorder(null);
        prev.setBackground(null);
        prev.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Create pause btn */
        JButton pause = new JButton(loadImage("src/main/java/assets/Pause.png"));
        pause.setToolTipText("Pause.");
        pause.setBorder(null);
        pause.setBackground(null);
        pause.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pause.setVisible(false);

        /* Create play btn */
        JButton play = new JButton(loadImage("src/main/java/assets/Play.png"));
        play.setToolTipText("Play");
        play.setBorder(null);
        play.setBackground(null);
        play.setCursor(new Cursor(Cursor.HAND_CURSOR));
        play.setMargin(new Insets(0,50,0,50));

        /* Create Next btn */
        JButton next = new JButton(loadImage("src/main/java/assets/Next.png"));
        next.setToolTipText("Next Song");
        next.setBorder(null);
        next.setBackground(null);
        next.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /* Create loop btn */
        JButton loop = new JButton(loadImage("src/main/java/assets/Loop.png"));
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

}
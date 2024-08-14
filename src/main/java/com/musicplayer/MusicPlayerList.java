package com.musicplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MusicPlayerList extends JDialog {
    private final MusicPlayer musicPlayer;
    private final Music_Player_GUI musicPlayerGui;

    private ArrayList<Song> playlist;
    public MusicPlayerList(MusicPlayer musicPlayer, Music_Player_GUI musicPlayerGui){
        this.musicPlayer = musicPlayer;
        this.musicPlayerGui = musicPlayerGui;
        this.playlist = this.musicPlayer.getPlaylist();

        setTitle("Playlist");
        setSize(400,400);
        getContentPane().setBackground(Music_Player_GUI.BACKGROUD_COLOR);
        setModal(true);
        setLocationRelativeTo(musicPlayerGui);
        /* Setting app icon */
        Music_Player_GUI.setWindowIcon(this);

        addComponents();
    }

    private void addComponents(){
        /* Create a container */
        JPanel container = new JPanel();
        container.setPreferredSize(new Dimension(400,1000));


        /* create a ScrollPane */
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0,0,400,500);

        for (int i = 0; i <playlist.size() ; i++) {
            CustomBtn filepath = new CustomBtn();
            filepath.setText(playlist.get(i).getTitle());
            filepath.setSong(playlist.get(i));
            filepath.setFont(new Font("Dialog", Font.BOLD, 12));
            filepath.setSize(380,20);
            filepath.setLocation(0,i*21);
            filepath.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            filepath.setHorizontalAlignment(JLabel.LEFT);
            filepath.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MusicPlayerList.this.musicPlayer.setSongFromList(((CustomBtn) e.getSource()).getSong());
                    /*Close the dialog*/
                    MusicPlayerList.this.dispose();
                }
        });

        container.add(filepath);
        }

        scrollPane.setViewportView(container);
        add(scrollPane);
    }


}

class CustomBtn extends JButton{
    private Song song;
    public CustomBtn(){
        super();
    }
    public void setSong(Song song){
        this.song = song;
    }

    public Song getSong(){return this.song;}
}

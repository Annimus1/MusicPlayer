package org.musicplayer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MusicPlayerDialog extends JDialog {
    /* Store all the path to be written in the txt file (to load the playlist)*/
    private ArrayList<String> songPath;

    public MusicPlayerDialog(Music_Player_GUI musicPlayerGui){
        this.songPath = new ArrayList<>();
        setTitle("Create Playlist");
        setSize(400,400);
        setResizable(false);
        getContentPane().setBackground(Music_Player_GUI.BACKGROUD_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(musicPlayerGui);
        /* Setting app icon */
        Music_Player_GUI.setWindowIcon(this);

        addComponents();
    }

    private void addComponents(){
        /* Create a container */
        JPanel container = new JPanel();
        container.setBounds(10,10,380,300);
        container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
        add(container);

        /* Add btn */
        JButton addBtn = new JButton("Add");
        addBtn.setBounds(60,(int)(getWidth()*0.80), 100,25);
        addBtn.setFont( new Font("Dialog",Font.BOLD,14));
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* Open file chooser*/
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));
                int result = fileChooser.showOpenDialog(MusicPlayerDialog.this);

                File selectedFile = fileChooser.getSelectedFile();
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    JLabel filepath = new JLabel(selectedFile.getPath());
                    filepath.setFont(new Font("Dialog", Font.BOLD, 12));
                    filepath.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    /* Add to the list */
                    songPath.add(filepath.getText());

                    /*Add the path to the container */
                    container.add(filepath);

                    /* Refresh the dialog to show newly added JLabel */
                    container.revalidate();
                }
            }
        });
        add(addBtn);

        /* save btn */
        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(215,(int)(getWidth()*0.80), 100,25);
        saveBtn.setFont( new Font("Dialog",Font.BOLD,14));
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(MusicPlayerDialog.this);

                    if(result == JFileChooser.APPROVE_OPTION ){
                        File selectedFile = fileChooser.getSelectedFile();

                        /* Convert in txt if not done so */
                        /* Check if the file do not have the .txt file extension */
                        if(!selectedFile.getName().substring(selectedFile.getName().length()-4).equalsIgnoreCase(".txt")){
                            selectedFile = new File(selectedFile.getAbsoluteFile()+".txt");
                        }

                        /* create the new file at the destination directory */
                        /* Write all the music path into the file */
                        FileWriter fileWriter = new FileWriter(selectedFile);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        /* Write each song path into the file */
                        for(String path : songPath){
                            bufferedWriter.write(path + "\n");
                        }
                        /* Close the buffer*/
                        bufferedWriter.close();

                        /* Display a message */
                        JOptionPane.showMessageDialog(MusicPlayerDialog.this,"Playlist created successfully");

                        /*Close the dialog*/
                        MusicPlayerDialog.this.dispose();
                    }
                }catch (Exception err) {
                    System.out.println(err);
                }
            }
        });
        add(saveBtn);
    }
}

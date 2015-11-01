package MP3Swing;

import MP3System.PlayerFacade;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

public class PlaylistWindow extends JFrame{

    private static PlaylistWindow playlistWindow = null;

    public static PlaylistWindow getPlaylistWindow(){
        if (playlistWindow == null){
            playlistWindow = new PlaylistWindow();
        }
        playlistWindow.pack();
        return playlistWindow;
    }

    private JScrollPane playlistScroll;
    private JList<String> playlist;
    private JPanel buttonsPanel;
    private JButton randomButton;
    private JButton upButton;
    private JButton downButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton loadButton;
    private PlayListModel currentPlayListModel;

    private boolean random;
    private boolean repeatAll;
    private Random generator;

    private void buildComponents(){
        this.setLayout(new GridLayout(2,1));
        playlist = new JList<>();
        playlist.setDragEnabled(true);
        playlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlist.setLayoutOrientation(JList.VERTICAL);
        playlistScroll = new JScrollPane(playlist);
        playlistScroll.setBorder(BorderFactory.createTitledBorder("Playlista:"));
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
        randomButton = new JButton("Odtwarzanie losowe: wyl");
        buttonsPanel.add(randomButton);
        upButton = new JButton("Wyzej");
        buttonsPanel.add(upButton);
        downButton = new JButton("Nizej");
        buttonsPanel.add(downButton);
        addButton = new JButton("Dodaj");
        buttonsPanel.add(addButton);
        deleteButton = new JButton("Usun");
        buttonsPanel.add(deleteButton);
        saveButton = new JButton("Zapisz");
        buttonsPanel.add(saveButton);
        loadButton = new JButton("Wczytaj");
        buttonsPanel.add(loadButton);
        add(playlistScroll);
        add(buttonsPanel);

        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (random){
                    random = false;
                    randomButton.setText("Odtwarzanie losowe: wyl");
                }
                else {
                    random = true;
                    randomButton.setText("Odtwarzanie losowe: wl");
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPlayListModel == null){
                    currentPlayListModel = new PlayListModel();
                    playlist.setModel(currentPlayListModel);
                }
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("music", "mp3");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(PlaylistWindow.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    currentPlayListModel.addMusic(fileChooser.getSelectedFile().toURI().toString());
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPlayListModel != null){
                    currentPlayListModel.removeMusic(playlist.getSelectedIndex());
                }
            }
        });

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> list = currentPlayListModel.getPlayList();
                int indexOfElement = playlist.getSelectedIndex();
                if (indexOfElement != 0){
                    Collections.swap(list,indexOfElement,indexOfElement-1);
                    currentPlayListModel.setPlayList(list);
                    playlist.setSelectedIndex(playlist.getSelectedIndex()-1);
                }
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> list = currentPlayListModel.getPlayList();
                int indexOfElement = playlist.getSelectedIndex();
                if (indexOfElement != currentPlayListModel.getSize()-1){
                    Collections.swap(list,indexOfElement,indexOfElement+1);
                    currentPlayListModel.setPlayList(list);
                    playlist.setSelectedIndex(playlist.getSelectedIndex()+1);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(PlaylistWindow.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    try {
                        PrintWriter file = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()));
                        file.println("/mp3list/");
                        for (String filename: currentPlayListModel.getPlayList()){
                            file.println(filename);
                        }
                        file.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(PlaylistWindow.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    try {
                        Scanner file = new Scanner(fileChooser.getSelectedFile());
                        List<String> newList = new LinkedList<String>();
                        if (file.nextLine().equals("/mp3list/")){
                            while (file.hasNextLine()){
                                newList.add(file.nextLine());
                            }
                        }
                        currentPlayListModel.setPlayList(newList);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private PlaylistWindow(){
        super();
        random = false;
        repeatAll = false;
        buildComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void fireMusicFinished(PlayerFacade player){
        if (random){
            if (generator == null){
                generator = new Random();
            }
            playlist.setSelectedIndex(generator.nextInt(playlist.getModel().getSize()));
            player.open(this);
            player.start();
        }
        else if (playlist.getSelectedIndex() == playlist.getModel().getSize()-1){
            playlist.setSelectedIndex(0);
            if (repeatAll){
                player.open(this);
                player.start();
            }
        }
        else {
            playlist.setSelectedIndex(playlist.getSelectedIndex()+1);
            player.open(this);
            player.start();
        }
    }

    public void ensureSelection(){
        if (playlistAvailable() && playlist.getSelectedIndex() < 0){
            playlist.setSelectedIndex(0);
        }
    }

    public String getSelectedMusic(){
        return playlist.getSelectedValue();
    }

    public boolean playlistAvailable(){
        return playlist.getModel() != null && playlist.getModel().getSize() > 0;
    }

    public boolean isRepeatAll() {
        return repeatAll;
    }

    public void setRepeatAll(boolean repeatAll) {
        this.repeatAll = repeatAll;
    }
}

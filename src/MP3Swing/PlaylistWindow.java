package MP3Swing;

import MP3System.PlayerFacade;

import javax.swing.*;

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

    private boolean random;
    private boolean repeatAll;

    private void buildComponents(){
        playlistScroll = new JScrollPane();
        playlist = new JList<>();
        playlistScroll.getViewport().setView(playlist);
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.X_AXIS));
        randomButton = new JButton("Odtwarzanie losowe");
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

    }

    public boolean isRepeatAll() {
        return repeatAll;
    }

    public void setRepeatAll(boolean repeatAll) {
        this.repeatAll = repeatAll;
    }
}

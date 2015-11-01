package MP3Swing;

import MP3System.PlayerFacade;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SwingMainWindow extends JFrame{

    private static SwingMainWindow window = null;

    private JLabel titleLabel;
    private JSlider timeSlider;
    private JSlider volumeSlider;
    private JButton repeatButton;
    private JButton playButton;
    private JButton stopButton;
    private JButton openButton;
    private JButton playlistButton;
    private JLabel timeLabel;
    private JPanel volumeRepeatPanel;
    private JPanel buttonsPanel;
    private JPanel timePlaylistPanel;
    PlaylistWindow playlistWindow;

    private PlayerFacade player;
    private repeatOptions repeatOption;
    private AtomicBoolean refresherRunning;
    private AtomicBoolean sliderChangedManually;
    private Thread refresher;

    private enum repeatOptions{
        none, file, playlist
    }
    private final static short MINUTES = 0;
    private final static int SECONDS = 1;

    public static SwingMainWindow getWindow(){
        if (window == null){
            window = new SwingMainWindow();
        }
        return window;
    }

    private SwingMainWindow(){
        super("Mp3 Player");
        player = PlayerFacade.getPlayer();
        repeatOption = repeatOptions.none;
        refresherRunning = new AtomicBoolean(false);
        sliderChangedManually = new AtomicBoolean(false);
        setLayout(new GridBagLayout());
        buildComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setPreferredSize(new Dimension(500,300));
    }

    private int[] getTimeParsed(double time){
        int[] timeParsed = new int[2];
        timeParsed[MINUTES] = (int) (time/60);
        timeParsed[SECONDS] = ((int) time) % 60;
        return timeParsed;
    }

    private void setSliderView(double time){
        timeSlider.setValue((int) time);
    }

    private void setTimeView(double time){
        int[] parsedTime = getTimeParsed(time);
        if (player.isOpened()){
            int[] stopTime = getTimeParsed(player.getStopTime());
            String timeLabelText = String.format("%02d:%02d/%02d:%02d",parsedTime[MINUTES],parsedTime[SECONDS],stopTime[MINUTES],stopTime[SECONDS]);
            timeLabel.setText(timeLabelText);
        }

    }

    private void initRefresher(){
        refresher = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (!refresherRunning.get()){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(200);
                        double time = player.getCurrentTime();
                        sliderChangedManually.set(false);
                        timeSlider.setValue((int) time);
                        sliderChangedManually.set(true);
                        setTimeView(time);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void buildComponents(){
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        titleLabel = new JLabel("Brak pliku.");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        add(titleLabel, gridBagConstraints);
        timeSlider = new JSlider(0,100,0);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        add(timeSlider, gridBagConstraints);
        volumeRepeatPanel = new JPanel();
        volumeRepeatPanel.setLayout(new BoxLayout(volumeRepeatPanel,BoxLayout.Y_AXIS));
        repeatButton = new JButton("Powtarzaj: nie");//TODO IKONY
        volumeSlider = new JSlider(0,100,50);
        volumeSlider.setBorder(BorderFactory.createTitledBorder("G³oœnoœæ"));
        volumeRepeatPanel.add(repeatButton);
        volumeRepeatPanel.add(volumeSlider);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        add(volumeRepeatPanel,gridBagConstraints);
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel,BoxLayout.Y_AXIS));
        playButton = new JButton("Odtwórz");//TODO BUTTONY
        stopButton = new JButton("Stop");
        openButton = new JButton("Otwórz");
        buttonsPanel.add(playButton);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(openButton);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 1;
        add(buttonsPanel,gridBagConstraints);
        timePlaylistPanel = new JPanel();
        timePlaylistPanel.setLayout(new BoxLayout(timePlaylistPanel,BoxLayout.Y_AXIS));
        playlistButton = new JButton("Playlista");
        playlistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlistWindow == null){
                    playlistWindow = PlaylistWindow.getPlaylistWindow();
                    playlistWindow.setLocation(SwingMainWindow.this.getX()+SwingMainWindow.this.getWidth(),SwingMainWindow.this.getY());
                }
                else {
                    if (playlistWindow.isVisible()){
                        playlistWindow.setVisible(false);
                    }
                    else {
                        playlistWindow.setVisible(true);
                    }
                }
            }
        });
        timeLabel = new JLabel("00:00/00:00");
        timePlaylistPanel.add(playlistButton);
        timePlaylistPanel.add(timeLabel);
        gridBagConstraints.gridx = 2;
        add(timePlaylistPanel,gridBagConstraints);

        repeatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (repeatOption == repeatOptions.none){
                    repeatOption = repeatOptions.file;
                    repeatButton.setText("Powtarzaj: plik");
                    player.setReplaying(true);
                }
                else if (repeatOption == repeatOptions.file){
                    repeatOption = repeatOptions.playlist;
                    repeatButton.setText("Powtarzaj: playlista");
                    playlistWindow.setRepeatAll(true);
                    player.setReplaying(false);
                }
                else {
                    repeatOption = repeatOptions.none;
                    repeatButton.setText("Powtarzaj: nie");
                    playlistWindow.setRepeatAll(false);
                }
            }
        });
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                player.setVolume(((double)volumeSlider.getValue())/volumeSlider.getMaximum());
            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playlistWindow != null && playlistWindow.isVisible()){
                    playlistWindow.setVisible(false);
                }
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("music", "mp3");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(SwingMainWindow.this);
                if (returnValue == JFileChooser.APPROVE_OPTION){
                    player.open(fileChooser.getSelectedFile().toURI().toString());
                    player.start();
                    titleLabel.setText(fileChooser.getSelectedFile().getName());
                    timeSlider.setMinimum(0);
                    timeSlider.setMaximum((int) player.getStopTime());
                    if (refresher == null){
                        initRefresher();
                    }
                    refresherRunning.set(true);
                    refresher.start();
                }
                else {
                    titleLabel.setText("Brak pliku.");
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.stop();
                refresherRunning.set(false);
            }
        });
        timeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (sliderChangedManually.get()){
                    refresherRunning.set(false);
                    player.setSecond(timeSlider.getValue());
                    refresherRunning.set(true);
                }
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.isOpened() && !playlistWindow.isVisible()){
                    refresherRunning.set(true);
                    player.start();
                }
                else if (player.isOpened() && playlistWindow.isVisible()){
                    refresherRunning.set(true);
                    player.start();
                }
                else if (playlistWindow.isVisible()){
                    playlistWindow.ensureSelection();
                    player.open(playlistWindow);
                    player.start();
                    titleLabel.setText(playlistWindow.getSelectedMusic());
                    timeSlider.setMinimum(0);
                    timeSlider.setMaximum((int) player.getStopTime());
                    if (refresher == null){
                        initRefresher();
                    }
                    refresher.start();
                    refresherRunning.set(true);
                }
            }
        });
    }
}

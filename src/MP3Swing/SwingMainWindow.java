package MP3Swing;

import javax.swing.*;
import java.awt.*;

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

    public static SwingMainWindow getWindow(){
        if (window == null){
            window = new SwingMainWindow();
        }
        return window;
    }

    private SwingMainWindow(){
        super("Mp3 Player");
        setLayout(new GridBagLayout());
        buildComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
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
        repeatButton = new JButton("Powtarzaj");//TODO IKONY
        volumeSlider = new JSlider(0,100,50);
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
        timeLabel = new JLabel("00:00/00:00");
        timePlaylistPanel.add(playlistButton);
        timePlaylistPanel.add(timeLabel);
        gridBagConstraints.gridx = 2;
        add(timePlaylistPanel,gridBagConstraints);
    }
}

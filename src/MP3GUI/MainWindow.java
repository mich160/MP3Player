package MP3GUI;

import MP3System.PlayerFacade;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.ProgressBar;
import com.googlecode.lanterna.gui.dialog.FileDialog;
import com.googlecode.lanterna.gui.layout.HorisontalLayout;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.io.File;


public class MainWindow extends Window {

    class TimeBar extends ProgressBar implements Interactable{

        public TimeBar(int preferredWidth) {
            super(preferredWidth);
        }

        @Override
        public Result keyboardInteraction(Key key) {
            if (key.getKind() == Key.Kind.ArrowLeft){
                if (isFileOpened() && player.getCurrentTime() - 5 > 0){
                    player.setSecond(player.getCurrentTime() - 5);
                    if (isPlaying()){
                        refresherRunning = true;
                    }
                    else {
                        setTimeViews(player.getCurrentTime());
                    }
                }
            }
            else if (key.getKind() == Key.Kind.ArrowRight){
                if (isFileOpened() && player.getCurrentTime() + 5 < player.getStopTime()){
                    player.setSecond(player.getCurrentTime()+5);
                    if (isPlaying()){
                        refresherRunning = true;
                    }
                    else {
                        setTimeViews(player.getCurrentTime());
                    }
                }
            }
            else if (key.getKind() == Key.Kind.ArrowDown){
                return Result.NEXT_INTERACTABLE_DOWN;
            }
            else if (key.getKind() == Key.Kind.ArrowUp){
                return Result.PREVIOUS_INTERACTABLE_UP;
            }
            return Result.EVENT_NOT_HANDLED;
        }

        @Override
        public void onEnterFocus(FocusChangeDirection focusChangeDirection) {
            timeBarPanel.setTitle(" - Czas + ");
        }

        @Override
        public void onLeaveFocus(FocusChangeDirection focusChangeDirection) {
            timeBarPanel.setTitle("Czas");
        }

        @Override
        public TerminalPosition getHotspot() {
            return null;
        }
    }

    class timeRefresher implements Runnable{

        @Override
        public void run() {
            while (true){
                while (!refresherRunning){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setTimeViews(player.getCurrentTime());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Panel mainPanel;
    private Panel titlePanel;
    private Panel settingsPanel;
    private Panel volumePanel;
    private Panel buttonsPanel;
    private Panel timeLabelPanel;
    private Panel timeBarPanel;
    private Button maximizeButton;
    private Button exitButton;
    private Label titleLabel;
    private TimeBar timeBar;
    private ProgressBar volumeBar;
    private Button minusVolumeButton;
    private Button plusVolumeButton;
    private Label timeLabel;
    private Button playButton;
    private Button stopButton;
    private Button openButton;
    private boolean maximized = false;
    private boolean refresherRunning = false;

    private PlayerFacade player;

    private final static int MINUTES = 0;// used in getTimeParsed
    private final static int SECONDS = 1;

    public MainWindow(String title) {
        super(title);
        player = PlayerFacade.getPlayer();
        buildComponents();
    }

    private synchronized boolean isFileOpened(){
        return player.isOpened();
    }

    private int[] getTimeParsed(double time){
        int[] timeParsed = new int[2];
        timeParsed[MINUTES] = (int) (time/60);
        timeParsed[SECONDS] = ((int) time) % 60;
        return timeParsed;
    }

    private synchronized void setTimeViews(double time){
        int[] parsedTime = getTimeParsed(time);
        if (isFileOpened()){
            this.timeBar.setProgress(time/player.getStopTime());
            int[] stopTime = getTimeParsed(player.getStopTime());
            String timeLabelText = String.format("%02d:%02d/%02d:%02d",parsedTime[MINUTES],parsedTime[SECONDS],stopTime[MINUTES],stopTime[SECONDS]);
            this.timeLabel.setText(timeLabelText);
        }
    }

    private void setVolumeViews(double volume){
        volumeBar.setProgress(volume);
    }

    private void setTitleView(String title){
        titleLabel.setText(title);
    }

    private boolean validFile(File file){
        int dotIndex = file.getName().lastIndexOf(".");
        if (file.getName().substring(dotIndex).equals(".mp3")){
            return true;
        }
        return false;
    }

    private synchronized boolean isPlaying(){
        return player.isPlaying();
    }

    private void buildComponents(){
        titlePanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        titleLabel = new Label("Brak pliku.");
        maximizeButton = new Button("[_]", new Action() {
            @Override
            public void doAction() {
                if (!MainWindow.this.maximized){
                    MainWindow.this.setWindowSizeOverride(new TerminalSize(MainWindow.this.getOwner().getScreen().getTerminalSize().getColumns()-1,MainWindow.this.getOwner().getScreen().getTerminalSize().getRows()-1));
                    maximized = true;//mozna zwiekszac komponenty
                }
                else {
                    MainWindow.this.setWindowSizeOverride(null);
                    maximized = false;
                }
            }
        });
        exitButton = new Button("X", new Action() {
            @Override
            public void doAction() {
                MainWindow.this.close();
            }
        });
        titlePanel.addComponent(maximizeButton);
        titlePanel.addComponent(exitButton);
        titlePanel.addComponent(titleLabel,HorisontalLayout.MAXIMIZES_HORIZONTALLY);

        timeBarPanel = new Panel("Czas", new Border.Standard(), Panel.Orientation.HORISONTAL);
        timeBar = new TimeBar(5);
        timeBarPanel.addComponent(timeBar);

        minusVolumeButton = new Button("-", new Action() {
            @Override
            public void doAction() {
                double currentVolume = volumeBar.getProgress();
                if (currentVolume >= 0.1){
                    currentVolume -= 0.1;
                    setVolumeViews(currentVolume);
                    if (isFileOpened()){
                        player.setVolume(currentVolume);
                    }
                }
            }
        });
        plusVolumeButton = new Button("+", new Action() {
            @Override
            public void doAction() {
                double currentVolume = volumeBar.getProgress();
                if (currentVolume <= 0.9){
                    currentVolume += 0.1;
                    setVolumeViews(currentVolume);
                    if (isFileOpened()){
                        player.setVolume(currentVolume);
                    }
                }
            }
        });
        volumeBar = new ProgressBar(5);
        volumePanel = new Panel("Glosnosc",new Border.Standard(), Panel.Orientation.HORISONTAL);
        volumePanel.addComponent(minusVolumeButton);
        volumePanel.addComponent(volumeBar);
        volumePanel.addComponent(plusVolumeButton);

        playButton = new Button("|>/||", new Action() {
            @Override
            public void doAction() {
                if (isFileOpened()){
                    refresherRunning = true;
                    player.start();
                }
            }
        });
        stopButton = new Button("[ ]", new Action() {
            @Override
            public void doAction() {
                player.stop();
                refresherRunning = false;
                setTimeViews(0.0);
            }
        });
        openButton = new Button("..", new Action() {
            @Override
            public void doAction() {
                File file = FileDialog.showOpenFileDialog(MainWindow.this.getOwner(),new File(System.getProperty("user.dir")),"Wybierz plik: ");
                if (file == null){
                    return;
                }
                else if (validFile(file)){
                    player.open(file.toURI().toString());
                    setTitleView(file.getName());
                    player.start();
                    player.setVolume(volumeBar.getProgress());
                    Thread refresher = new Thread(new timeRefresher());
                    refresher.start();
                    refresherRunning = true;
                }
                else {
                    setTitleView("Nieprawidlowy plik!");
                }
            }
        });
        buttonsPanel = new Panel(new Border.Standard(), Panel.Orientation.VERTICAL);
        buttonsPanel.addComponent(playButton);
        buttonsPanel.addComponent(stopButton);
        buttonsPanel.addComponent(openButton);

        timeLabelPanel = new Panel(new Border.Standard(), Panel.Orientation.HORISONTAL);
        timeLabel = new Label("00:00/00:00");
        timeLabelPanel.addComponent(timeLabel);

        settingsPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        settingsPanel.addComponent(volumePanel);
        settingsPanel.addComponent(buttonsPanel);
        settingsPanel.addComponent(timeLabelPanel);

        mainPanel = new Panel("MP3 Player", new Border.Invisible(), Panel.Orientation.VERTICAL);

        mainPanel.addComponent(titlePanel);
        mainPanel.addComponent(timeBarPanel);
        mainPanel.addComponent(settingsPanel);

        addComponent(mainPanel);

        setVolumeViews(1.0);
    }

}

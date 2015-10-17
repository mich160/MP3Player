package MP3System;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerFacade {
    private static PlayerFacade player;

    public static PlayerFacade getPlayer(){
        if (player == null){
            player = new PlayerFacade();
        }
        return player;
    }

    private Media media;
    private MediaPlayer mp;
    private JFXPanel fxPanel;
    private boolean pause = false;
    private boolean stop = true;

    PlayerFacade (){
        this.fxPanel = new JFXPanel();
    }

    public double getCurrentTime(){
        return mp.getCurrentTime().toSeconds();
    }
    public double getStopTime(){
        return mp.getStopTime().toSeconds();
    }
    public void setSecond(double second){
        if(pause){
            mp.setStartTime(Duration.seconds(second));
        }
        else if(stop){
            mp.setStartTime(Duration.seconds(second));
            mp.play();
            stop=false;
        }
        else {
            mp.seek(Duration.seconds(second));
        }
    }
    public void start() throws NullPointerException{
        if(media==null){
            throw new NullPointerException();
        }
        if (pause && !stop){
            pause=false;
            Duration time=mp.getCurrentTime();
            mp.setStartTime(time);
            mp.play();
        }
        if (!pause && !stop){
            pause=true;
            mp.pause();
        }
        else{
            mp.seek(Duration.ZERO);
            mp.play();
            pause=false;
            stop=false;
        }
    }
    public void stop(){
        if(!stop){
            mp.setStartTime(Duration.ZERO);
            mp.stop();
            stop=true;
            pause=false;
        }
    }
    public void pause(){
        if(stop){
            return;
        }
        else if(pause){
            pause=false;
            Duration time=mp.getCurrentTime();
            mp.setStartTime(time);//tu poprawiłem
            mp.play();
        }else{
            pause=true;
            mp.pause();
        }
    }

    public void open(String music){
        media=new Media(music);
        mp=new MediaPlayer(media);
    }
    //zakres-[0.0 , 1.0]
    public void setVolume(double value){
        mp.setVolume(value);
    }
    public double getVolume(){
        return mp.getVolume();
    }
    public boolean isPlaying(){
        return mp.getCurrentTime().lessThan(mp.getStopTime()) && !stop && !pause;
    }
    public boolean isOpened(){
        return mp != null;
    }
}

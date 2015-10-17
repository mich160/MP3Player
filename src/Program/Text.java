package Program;

import MP3System.PlayerFacade;

import java.util.Scanner;

public class Text {
    static public void main(String[] args){

        PlayerFacade player = PlayerFacade.getPlayer();
        String music="FILE:///C:/fullrandom/uczelnia/moje/kck/linkin.mp3";
        player.open(music);
        player.start();
        int temp=0;
        double volume;
        double time;
        Scanner sc=new Scanner(System.in);
        while(temp!=6){
            System.out.println(player.getCurrentTime());
            System.out.println("1-pauza, 2-glosnosc, 3-stop, 4-start, 5-czas, 6-kuniec, 7-czy gra, 8-czy otwarte");
            temp=sc.nextInt();
            System.out.println();
            if(temp==1){
                player.pause();
            }
            if(temp==2){
                System.out.println("wpisz g³oœnoœæ od 0 do 100(aktualna g³oœnoœæ to-"+player.getVolume());
                volume=sc.nextDouble();
                volume=volume/100;
                player.setVolume(volume);
            }

            if(temp==3){
                player.stop();
            }
            if(temp==4){
                player.start();
            }
            if(temp==5){
                System.out.println("wpisz czas (aktualna czas to-"+player.getCurrentTime()+", koniec- "+player.getStopTime());
                time=sc.nextDouble();
                player.setSecond(time);
            }
            if(temp==7){
                System.out.println(player.isPlaying());
            }
            if(temp==8){
                System.out.println(player.isOpened());
            }

        }
        System.exit(0);
    }

}

package MP3System;

public class PlayerFacade {
    private static PlayerFacade player;

    public static PlayerFacade getPlayer(){
        if (player == null){
            player = new PlayerFacade();
        }
        return player;
    }
}

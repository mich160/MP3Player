package MP3GUI;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;

public class PlayerGUI {
    private static PlayerGUI playerGUI = null;
    public static PlayerGUI getPlayerGUI(){
        if (playerGUI == null){
            playerGUI = new PlayerGUI();
        }
        return playerGUI;
    }

    private PlayerGUI(){
        build();
    }

    private void build(){
        GUIScreen textGUI = TerminalFacade.createGUIScreen();
        if(textGUI == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }
        textGUI.getScreen().startScreen();
        textGUI.setTitle("GUI Test");
        textGUI.showWindow(new MainWindow("MP3"), GUIScreen.Position.CENTER);
        textGUI.getScreen().stopScreen();
    }
}

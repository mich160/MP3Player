package MP3Swing;

import javax.swing.*;
import java.util.HashMap;

public class ResourcesManager {
    private static ResourcesManager manager = null;

    private HashMap<String, Icon> icons;

    public static ResourcesManager getManager(){
        if (manager == null){
            manager = new ResourcesManager();
        }
        return manager;
    }

    private void loadIcons() {
        icons = new HashMap<>();
        ImageIcon repeatIcon = new ImageIcon("res/repeat.png");
    }

    private ResourcesManager(){
        loadIcons();
    }
}

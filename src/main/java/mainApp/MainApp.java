package mainApp;


import mainApp.domain.Initializer;
import mainApp.ui.UIInitializer;

import java.awt.*;
import java.io.IOException;


public final class MainApp {

    private MainApp() {

    }

    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Initializer init = new Initializer(screenSize);
        try {
            init.createAllEntities();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UIInitializer graphicsInit = new UIInitializer(init);
        graphicsInit.initializeStartScreenGraphic();

    }

}

package mainApp.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mainApp.domain.Dice;
import mainApp.domain.Initializer;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class UIInitializer {
    private final Initializer initializer;
    private final JFrame mainFrame = new JFrame();
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    @SuppressFBWarnings
    public UIInitializer(Initializer init) {
        initializer = init;
        initializeMainFrame();
    }

    private void initializeMainFrame() {
        mainFrame.setSize(screenSize);
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public void initializeStartScreenGraphic() {
        Random random = new Random();
        Dice die = new Dice(random);
        StartScreenGraphic startScreen = new StartScreenGraphic(mainFrame, screenSize, die, initializer);
        startScreen.repaint();
    }
}

package mainApp.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mainApp.domain.Dice;
import mainApp.domain.Game;
import mainApp.domain.Initializer;
import mainApp.domain.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StartScreenGraphic extends JPanel {

    public JFrame mainFrame;
    public Dimension screenSize;
    private Image finalRiskImage;
    private JButton playerColorSubmitButton;
    private JButton submitButton;
    private JComboBox<String> playerCountDropBox;
    private JComboBox<String> modeDropBox;
    private final JComboBox[] playerColorDropDowns;
    private final JLabel[] playerColorLabels = new JLabel[6];

    private JPanel colorSelectionPanel;
    private JPanel gameSelectionPanel;
    private JPanel titlePanel;
    private JPanel rollingPanel;
    private boolean  chooseColorState = false;
    private final int xScreenCenter;
    private final int yScreenCenter;
    final Color backgroundColor = Color.BLACK;
    public int totalPlayers;
    public String gameMode;
    public ArrayList<String> playerColors;

    private final Font primaryFont = new Font("Arial", Font.BOLD, 24);
    private final Dice orderRollingDie;
    private final Initializer initializer;
    private JButton startGameButton;
    private JButton[] rollButtons;
    private ArrayList<Integer> playerRollValues;
    private BackgroundGraphic gameFrame;
    private ResourceBundle messages;
    private JPanel languageSelectionPanel;
    private JComboBox<String> languageComboBox;
    private JLabel chooseNumPlayersLabel;
    private JLabel chooseGameModeLabel;
    private JLabel[] playerLabels;

    @SuppressFBWarnings
    public StartScreenGraphic(JFrame frame, Dimension screenSizeInput, Dice die, Initializer init) {

        mainFrame = frame;
        screenSize = screenSizeInput;
        orderRollingDie = die;
        this.initializer = init;

        xScreenCenter = (screenSize.width / 2);
        yScreenCenter = (screenSize.height / 2);
        playerColorDropDowns = new JComboBox[6];
        initializeComponents();

        this.setBounds(0, 0, screenSize.width, (screenSize.height));
        setBackground(backgroundColor);
        setLayout(null);
        mainFrame.add(this);
        mainFrame.repaint();
    }

    private void initializeComponents(){
        messages = ResourceBundle.getBundle("message");
        initializeRiskImage();
        createTitleLabelOnPanel();
        createColorSelectionPanel();
        createGameSelectionPanel();
        onStartGameButtonClick();
        onSubmitButtonClick();
        createLanguageSelectionPanel();
        mainFrame.setVisible(true);
        mainFrame.repaint();
    }

    public void createLanguageSelectionPanel(){
        languageSelectionPanel = createPanel(screenSize.width, screenSize.height/15);
        languageSelectionPanel.setLocation(xScreenCenter - languageSelectionPanel.getWidth()/2, screenSize.height -180);
        languageSelectionPanel.setOpaque(false);

        String[] options = {"English", "Spanish"};
        languageComboBox = new JComboBox<>(options);
        languageComboBox.setMaximumSize(new Dimension(300, 50));

        String languageSelectionMessage = messages.getString("languageSelectionMessage");
        JLabel chooseLanguageLabel = createLabelForGameSelection(languageSelectionMessage);
        chooseLanguageLabel.setLabelFor(languageComboBox);

        String submit = messages.getString("submitOption");
        JButton languageSubmitButton = new JButton(submit);
        languageSubmitButton.setMaximumSize(new Dimension(150, 50));

        languageSelectionPanel.add(chooseLanguageLabel, BorderLayout.CENTER);
        languageSelectionPanel.add(languageComboBox, BorderLayout.CENTER);
        languageSelectionPanel.add(languageSubmitButton, BorderLayout.CENTER);
        languageSelectionPanel.setVisible(true);
        mainFrame.add(languageSelectionPanel, BorderLayout.CENTER);

        languageSelectionPanel.repaint();
        mainFrame.repaint();

        languageSubmitButton.addActionListener(e -> {
            languageSelectionPanel.setVisible(false);
            gameSelectionPanel.setVisible(true);
            String languageString  = (String) languageComboBox.getSelectedItem();
            if (languageString.equals("Spanish")){
                messages = ResourceBundle.getBundle("message_es_ES");

            }
            updateTexts();
            createBackgroundGraphics();
            initializer.setMessages(messages);
        });
    }

    private  void updateTexts(){
        String choosePlayerColor = messages.getString("choosePlayerColor");
        playerColorSubmitButton.setText(choosePlayerColor);

        String numPlayers = messages.getString("numPlayers");
        chooseNumPlayersLabel.setText(numPlayers);

        String modeOption1 = messages.getString("modeOption1");
        String modeOption2 = messages.getString("modeOption2");
        modeDropBox.removeAllItems();
        modeDropBox.addItem(modeOption1);
        modeDropBox.addItem(modeOption2);

        String chooseGameMode = messages.getString("chooseGameMode");
        chooseGameModeLabel.setText(chooseGameMode);

        String submitOption =  messages.getString("submitOption");
        submitButton.setText(submitOption);

        for (int i=0; i<totalPlayers; i++){
            String playerLabel = messages.getString("playerLabel");
            playerLabels[i].setText(playerLabel);
        }

        for (int i = 0; i < totalPlayers; i++) {
            String rollForTurnOrder = messages.getString("rollForTurnOrder");
            rollButtons[i].setText(rollForTurnOrder);
        }
    }
    public void createBackgroundGraphics(){
        gameFrame = new BackgroundGraphic(mainFrame, screenSize, messages);
    }

    public void initializeRiskImage() {
        ImageIcon riskImage = new ImageIcon("src/main/java/data/gamePeice.png");
        finalRiskImage = riskImage.getImage();
    }

    public void createTitleLabelOnPanel() {
        titlePanel =createPanel(screenSize.width / 4, screenSize.height / 5);
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("RISK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 150));
        titleLabel.setBounds(0, 0, screenSize.width / 4, (screenSize.height) / 5);
        titleLabel.setForeground(Color.RED);

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setLocation(xScreenCenter - (titlePanel.getWidth() / 2), 0);
        mainFrame.add(titlePanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    public void createGameSelectionPanel() {
        gameSelectionPanel = createPanel(screenSize.width, screenSize.height/15);
        gameSelectionPanel.setLocation(xScreenCenter - gameSelectionPanel.getWidth()/2, screenSize.height -180);
        gameSelectionPanel.setOpaque(false);
        choseNumberPlayersPanel(gameSelectionPanel);
        createChooseGamePanel(gameSelectionPanel);

        String choosePlayerColor = messages.getString("choosePlayerColor");
        playerColorSubmitButton = new JButton(choosePlayerColor);
        playerColorSubmitButton.setMaximumSize(new Dimension(500, 50));

        gameSelectionPanel.add(playerColorSubmitButton);
        gameSelectionPanel.setVisible(false);
        mainFrame.add(gameSelectionPanel, BorderLayout.CENTER);
    }

    public void choseNumberPlayersPanel(JPanel toAttachPanel){
        JPanel numberPlayersSelectionPanel = createPanel(screenSize.width, screenSize.height/20);

        String[] options = {"3", "4", "5"};
        playerCountDropBox = new JComboBox<>(options);
        playerCountDropBox.setMaximumSize(new Dimension(150, 50));
        String numPlayers = messages.getString("numPlayers");
        chooseNumPlayersLabel = createLabelForGameSelection(numPlayers);
        chooseNumPlayersLabel.setLabelFor(playerCountDropBox);

        numberPlayersSelectionPanel.add(chooseNumPlayersLabel, BorderLayout.CENTER);
        numberPlayersSelectionPanel.add(playerCountDropBox, BorderLayout.CENTER);
        toAttachPanel.add(numberPlayersSelectionPanel, BorderLayout.EAST);
    }


    public void createChooseGamePanel(JPanel toAttachPanel){
        JPanel gameModeSubSelectionPanel = createPanel(screenSize.width/2, screenSize.height/20);
        String chooseGameMode = messages.getString("chooseGameMode");
        chooseGameModeLabel = createLabelForGameSelection(chooseGameMode);

        String modeOption1 = messages.getString("modeOption1");
        String modeOption2 = messages.getString("modeOption2");
        String[] options = {modeOption1, modeOption2};
        modeDropBox = new JComboBox<>(options);
        modeDropBox.setMaximumSize(new Dimension(screenSize.width/8, 50));

        gameModeSubSelectionPanel.add(chooseGameModeLabel, BorderLayout.CENTER);
        gameModeSubSelectionPanel.add(modeDropBox, BorderLayout.CENTER);
        toAttachPanel.add(gameModeSubSelectionPanel, BorderLayout.CENTER);
    }
    public JLabel createLabelForGameSelection(String text){
        JLabel selectionPanel = new JLabel(text);
        selectionPanel.setFont(primaryFont);
        selectionPanel.setBounds(0, 0, screenSize.width/4, (screenSize.height) / 5);
        selectionPanel.setForeground(Color.WHITE);
        this.repaint();
        return selectionPanel;
    }

    public JPanel createPanel(int width, int height){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBounds(0, 0, width, height);
        panel.setOpaque(false);
        return panel;
    }

    public void onStartGameButtonClick() {
        playerColorSubmitButton.addActionListener(e -> {
            gameMode = (String) modeDropBox.getSelectedItem();
            String numberPlayerValue = (String) playerCountDropBox.getSelectedItem();
            if (numberPlayerValue != null) {
                chooseColorState = true;
                totalPlayers = Integer.parseInt(numberPlayerValue);
                showColorSelectionItems();
            }
            SwingUtilities.updateComponentTreeUI(mainFrame);
        });

    }

    public void onSubmitButtonClick() {
        submitButton.addActionListener(e -> {
            playerColors=  new ArrayList<>();
            for (int i=1; i<=totalPlayers; i++){
                String currentPlayerColor = (String) playerColorDropDowns[i].getSelectedItem();
                if (playerColors.contains(currentPlayerColor)){
                    String needUniqueColorError = messages.getString("needUniqueColorError");
                    JOptionPane.showMessageDialog(null, needUniqueColorError);
                    break;
                }else {
                   playerColors.add(currentPlayerColor);
                }
            }
            if (playerColors.size() == totalPlayers){
                removeAll();
                initializeRollingForOrder();
            }
        });

    }

    public void createColorSelectionPanel(){

        colorSelectionPanel  = createPanel(screenSize.width, screenSize.height/4);
        colorSelectionPanel.setLocation(xScreenCenter - colorSelectionPanel.getWidth()/2, screenSize.height/2-150);
        colorSelectionPanel.setOpaque(false);
        for (int i=1; i<=5; i++){
            createSinglePlayerColorPanel(i, colorSelectionPanel);
        }
        String submitOption =  messages.getString("submitOption");
        submitButton = new JButton(submitOption);
        submitButton.setVisible(false);
        colorSelectionPanel.add(submitButton);
        mainFrame.add(colorSelectionPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private void createSinglePlayerColorPanel(int playerNumber, JPanel colorSelectionSinglePanel) {
        JComboBox<String> playerColorDropBox= createColorComboBox();
        playerColorDropDowns[playerNumber] = playerColorDropBox;
        playerColorDropBox.setVisible(false);

        String playerLabel = messages.getString("playerLabel");
        JLabel singlePlayerChooseColorLabel = createLabelForGameSelection(playerLabel + " " +  playerNumber + ":  ");
        singlePlayerChooseColorLabel.setLabelFor(playerColorDropBox);
        singlePlayerChooseColorLabel.setMaximumSize(new Dimension(150, 50));
        playerColorLabels[playerNumber] = singlePlayerChooseColorLabel;
        singlePlayerChooseColorLabel.setVisible(false);
        playerColorLabels[playerNumber] = singlePlayerChooseColorLabel;

        colorSelectionSinglePanel.add(singlePlayerChooseColorLabel);
        colorSelectionSinglePanel.add(playerColorDropBox);
    }

    public JComboBox<String> createColorComboBox(){
        String[] options = getAllColors();
        JComboBox<String> playerColorDropBox = new JComboBox<>(options);
        playerColorDropBox.setMaximumSize(new Dimension(screenSize.width/12, 50));
        return playerColorDropBox;
    }

    public void showColorSelectionItems(){
        for (int i=totalPlayers+1; i<=5; i++){
            playerColorLabels[i].setVisible(false);
            playerColorDropDowns[i].setVisible(false);
        }

        for (int i=1; i<=totalPlayers; i++){
            String playerLabelText = messages.getString("playerLabel");
            playerColorLabels[i].setText(playerLabelText + " " + i);

            playerColorDropDowns[i].removeAllItems();
            for (String color: getAllColors()){
                playerColorDropDowns[i].addItem(color);
            }
            playerColorLabels[i].setVisible(true);
            playerColorDropDowns[i].setVisible(true);
        }

        submitButton.setVisible(true);
    }

    private String[] getAllColors(){
        String colorOption1 = messages.getString("colorOption1");
        String colorOption2 = messages.getString("colorOption2");
        String colorOption3 = messages.getString("colorOption3");
        String colorOption4 = messages.getString("colorOption4");
        String colorOption5 = messages.getString("colorOption5");
        return new String[]{colorOption1, colorOption2, colorOption3, colorOption4, colorOption5};
    }

    public void removeAll(){

        mainFrame.remove(playerColorSubmitButton);
        mainFrame.remove(submitButton);
        mainFrame.remove(playerCountDropBox);
        mainFrame.remove(playerCountDropBox);
        mainFrame.remove(modeDropBox);
        mainFrame.remove(colorSelectionPanel);
        mainFrame.remove(gameSelectionPanel);
        mainFrame.repaint();
    }

    private void initializeRollingForOrder() {
        initializeStartGameButton();
        initializePlayerRollValues();
        initializeRollButtons();
        addRollButtonActionListeners();
        initializeRollingComponent();
        addRollButtonsToRollingPanel();
        initializeAndAddPlayerText();
    }

    private void initializeStartGameButton() {
        String startGame = messages.getString("startGame");
        startGameButton = new JButton(startGame);
        startGameButton.addActionListener(e -> {
            initializeGameProcess();
        });
    }

    private void initializeGameProcess() {
        sortColors();
        ArrayList<Player> players = this.initializer.makePlayers(totalPlayers, playerColors);
        String gameModeString = messages.getString("modeOption2");
        boolean secretMissionMode = gameMode.equals(gameModeString);
        Game game = initializer.makeGame(players, secretMissionMode, messages);
        gameFrame.setGame(game);

        mainFrame.remove(this);
        mainFrame.remove(titlePanel);
        mainFrame.add(gameFrame);
        gameFrame.setBounds(0, 0, screenSize.width, screenSize.height);
        gameFrame.drawInitialBackground();
        gameFrame.repaint();
        gameFrame.setVisible(true);
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    private void sortColors() {
        ArrayList<String> colors = new ArrayList<>();
        for (int i = 6; i > 0; i--){
            int findValue = playerRollValues.indexOf(i);
            if (findValue != -1){
                colors.add(playerColors.get(findValue));
            }
        }
        playerColors = colors;
    }

    private void initializePlayerRollValues() {
        playerRollValues = new ArrayList<>(totalPlayers);
        for (int i = 0; i < totalPlayers; i++) {
            playerRollValues.add(0);
        }
    }

    private void initializeRollButtons() {
        rollButtons = new JButton[totalPlayers];
        Font rollButtonFont =new Font("Arial", Font.BOLD, 16);
        for (int i = 0; i < totalPlayers; i++) {
            String rollForTurnOrder = messages.getString("rollForTurnOrder");
            rollButtons[i] = new JButton(rollForTurnOrder);
            rollButtons[i].setFont(rollButtonFont);
            rollButtons[i].setMaximumSize(new Dimension(screenSize.width/5, 50));
        }
    }

    private void addRollButtonActionListeners() {
        for (int i = 0; i < totalPlayers; i++) {
            rollButtons[i].addActionListener(e -> {
                int buttonIndex = determineButtonPressedIndex(e);
                playerPressedRollButton(buttonIndex);
            });
        }
    }

    private int determineButtonPressedIndex(ActionEvent e) {
        for (int i = 0; i < totalPlayers; i++) {
            if (e.getSource() == rollButtons[i]){
                return i;
            }
        }
        return 0;
    }

    private void playerPressedRollButton(int buttonIndex) {
        rollingPanel.remove(rollButtons[buttonIndex]);
        int rollValue = orderRollingDie.rollUnique(playerRollValues);
        playerRollValues.set(buttonIndex, rollValue);
        initializeAndAddPlayerRollText(buttonIndex, rollValue);
//        System.out.println(playerRollValues);
        if (!playerRollValues.contains(0)) {
            addStartGameButton();
        }
        mainFrame.repaint();
    }

    private void initializeAndAddPlayerRollText(int playerIndex, int rollValue) {
        JLabel playerRollText = new JLabel(Integer.toString(rollValue));
        playerRollText.setFont(primaryFont);
        playerRollText.setForeground(Color.WHITE);
        int playerRollTextWidth = 20;
        int playerRollTextHeight = 30;
        int centeringOffset = (screenSize.width / (2 * totalPlayers)) - (playerRollTextWidth / 2);
        int playerRollTextXPosition = ((screenSize.width / totalPlayers) * playerIndex) + centeringOffset;
        int playerRollTextYPosition = (screenSize.height / 6) - (playerRollTextHeight / 2);
        playerRollText.setBounds(playerRollTextXPosition, playerRollTextYPosition, playerRollTextWidth, playerRollTextHeight);
        rollingPanel.add(playerRollText);
    }

    private void addStartGameButton() {
        int startGameButtonWidth = 300;
        int startGameButtonHeight = 50;
        int centeringOffset = (startGameButtonWidth / 2);
        int startGameButtonXPosition = xScreenCenter - centeringOffset;
        int startGameButtonYPosition = screenSize.height - (3 * startGameButtonHeight);
        startGameButton.setBounds(startGameButtonXPosition, startGameButtonYPosition, startGameButtonWidth, startGameButtonHeight);
        startGameButton.setFont(primaryFont);
        startGameButton.setVisible(true);
        this.add(startGameButton);
    }

    private void initializeRollingComponent() {
        rollingPanel = new JPanel();
        rollingPanel.setBounds(0, screenSize.height / 3, screenSize.width, screenSize.height / 3);
        rollingPanel.setBackground(Color.BLACK);
        rollingPanel.setLayout(null);
        rollingPanel.setVisible(true);
        this.add(rollingPanel);
    }

    private void addRollButtonsToRollingPanel() {
        for (int i = 0; i < totalPlayers; i++) {
            rollingPanel.add(rollButtons[i]);
            int rollButtonHeight = 50;
            int rollButtonWidth = 240;
            int centeringOffset = (screenSize.width / (2 * totalPlayers)) - (rollButtonWidth / 2);
            int rollButtonXPosition = (screenSize.width / totalPlayers) * i + centeringOffset;
            int rollButtonYPosition = (screenSize.height / 3) - rollButtonHeight;
            rollButtons[i].setBounds(rollButtonXPosition, rollButtonYPosition, rollButtonWidth, rollButtonHeight);
        }
    }

    private void initializeAndAddPlayerText() {
        Font playerFont =new Font("Arial", Font.BOLD, 16);
        playerLabels = new JLabel[totalPlayers];
        for (int i = 0; i < totalPlayers; i++) {
            String playerLabel = messages.getString("playerLabel");
            JLabel playerRollingText = new JLabel(playerLabel + " " + (i + 1));
            playerRollingText.setFont(playerFont);
            playerRollingText.setForeground(Color.WHITE);
            int playerRollingTextWidth = 95;
            int playerRollingTextHeight = 50;
            int centeringOffset = (screenSize.width / (2 * totalPlayers)) - (playerRollingTextWidth / 2);
            int playerRollingTextXPosition = (screenSize.width / totalPlayers) * i + centeringOffset;
            playerRollingText.setBounds(playerRollingTextXPosition, 0, playerRollingTextWidth, playerRollingTextHeight);
            playerLabels[i] = playerRollingText;
            rollingPanel.add(playerRollingText);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!chooseColorState){
            int xLoc = xScreenCenter - (finalRiskImage.getWidth(null) / 2);
            int yLoc = yScreenCenter - (finalRiskImage.getHeight(null) / 2);
            g.drawImage(finalRiskImage, xLoc, yLoc, null);

        }
    }
}

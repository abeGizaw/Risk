package mainApp.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import mainApp.domain.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class BackgroundGraphic extends JPanel implements MouseListener, ChangeListener{
    private JFrame mainFrame = new JFrame();
    private final ResourceBundle messages;
    private final Dimension screenSize;
    private Image finalBackgroundImage;
    private HashMap<String, JLabel> troopCountLabels;
    private ArrayList<JLabel> cardsOnPanel = new ArrayList<>();

    private JButton turnInCards;
    private JButton skipManeuverButton;
    private JButton sliderSubmitButton;
    private JLabel sliderJLabel;
    private JSlider troopSlider;
    private JScrollPane cardsPane;

    private JPanel sliderPanel;
    private JPanel optionPanel;
    private JPanel troopCountOverlay;
    private JPanel gameInfoPanel;
    private JPanel cardOptionPanel;

    private JLabel gameStateText;
    private JLabel currentPlayerLabel;

    private GameState gameState;
    private AttackPhase attackState;
    private ManeuverPhase maneuverState;
    private GameState formerGameState;
    private Game riskGame;
    private int deployableTroops;
    private Territory currentTerritory;
    private JButton newAttack;

    @SuppressFBWarnings
    public BackgroundGraphic(JFrame frame, Dimension screenSizeInput, ResourceBundle messagesInput){
        this.messages = messagesInput;
        screenSize = new Dimension(screenSizeInput.width, (int) (screenSizeInput.height * .8));
        mainFrame = frame;
        this.addMouseListener(this);
    }

    public void drawInitialBackground(){
        initializeBackgroundImage();
        initializeTroopCountOverlay();
        initializeTroopCountLabels();
        initializeTurnInCardButton();
        initializeCurrentPlayerLabel();
        initializeTroopSlider();
        initializeOptionDisplay();
        initializeGameStateText();
        initializeTurnInCardSlider();
        initializeSkipDeployButton();

        this.setBounds(0, 0, screenSize.width, (int) (screenSize.height));
        mainFrame.add(this);
        mainFrame.repaint();
    }

    private void initializeSkipDeployButton() {
        String skipManeuveringText = messages.getString("skipManeuveringText");
        skipManeuverButton = new JButton(skipManeuveringText);
        skipManeuverButton.addActionListener(generateSkipManeuveringActionListener());
    }

    private ActionListener generateSkipManeuveringActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                riskGame.transitionFromManeuverToDeploy();
                gameInfoPanel.remove(skipManeuverButton);
                resetSliderPanel();
                updateDisplay();
            }
        };
    }

    private void initializeTurnInCardSlider() {
        cardOptionPanel = new JPanel();
        cardOptionPanel.setLayout(new BoxLayout(cardOptionPanel, BoxLayout.Y_AXIS));
        JLabel cardLabel = new JLabel("Which set of cards do you want to turn in");
        cardOptionPanel.add(cardLabel);

        cardsPane = new JScrollPane(cardOptionPanel);
        cardsPane.setSize(new Dimension(450, 150));

        cardsPane.setVisible(false);
        this.add(cardsPane);
    }

    private void initializeGameStateText() {
        gameStateText = new JLabel();
        gameStateText.setFont(new Font("MV Boli", Font.BOLD, 20));
        gameStateText.setVisible(true);
        this.add(gameStateText);
    }

    private void initializeOptionDisplay() {
        optionPanel = new JPanel();
        optionPanel.setMaximumSize(new Dimension(800, 300));
        String newAttackMessage = messages.getString("newAttackMessage");
        newAttack = new JButton(newAttackMessage);

        String moveTroopsMessage = messages.getString("moveTroopsMessage");
        JButton move = new JButton(moveTroopsMessage);
        optionPanel.setBackground(new Color(0, 0, 0, 65));

        move.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                riskGame.updateGameState(GameState.MANEUVER, AttackPhase.ATTACKFROM);
                updateDisplay();
                optionPanel.setVisible(false);
                gameStateText.setVisible(true);

            }
        });

        newAttack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                riskGame.updateGameState(GameState.ATTACK, AttackPhase.ATTACKFROM);
                updateGameStateLabel(GameState.ATTACK, AttackPhase.ATTACKFROM, 0);
                optionPanel.setVisible(false);
                gameStateText.setVisible(true);

            }
        });

        optionPanel.add(newAttack);
        optionPanel.add(move);

        optionPanel.setVisible(false);
        this.add(optionPanel);
    }

    private void initializeTroopSlider() {
        sliderPanel = new JPanel();
        sliderPanel.setMaximumSize(new Dimension(800, 300));
        troopSlider = new JSlider();
        sliderPanel.add(troopSlider);
        troopSlider.setPaintTrack(true);
        troopSlider.setPaintTicks(true);
        troopSlider.setPaintLabels(true);
        troopSlider.setVisible(true);
        troopSlider.setPreferredSize(new Dimension(200, 200));
    }

    private void initializeTurnInCardButton() {
        String turnInCardsMessage = messages.getString("turnInCardsMessage");
        turnInCards = new JButton(turnInCardsMessage);
        turnInCards.setSize(50, 20);

        turnInCards.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<ArrayList<Card>> setOfValidCards = riskGame.allSetsOfValidCards();
                if (setOfValidCards.size() > 1){
                    riskGame.updateGameState(GameState.CHOOSE, AttackPhase.ATTACKFROM);
                    displayTurnInOptions(setOfValidCards);
                } else {
                    int newTroops = riskGame.turnCardsIn(setOfValidCards.get(0));
                    updateGameStateLabel(GameState.DEPLOY, attackState, newTroops);
                    updateDisplay();
                }
            }
        });
        turnInCards.setVisible(riskGame.canTurnInCards());
    }

    private void displayTurnInOptions(ArrayList<ArrayList<Card>> setOfValidCards) {
        removeLastCards();
        for (ArrayList<Card> setOfValidCard : setOfValidCards) {
            StringBuilder buttonName = new StringBuilder();
            for (Card card : setOfValidCard) {
                buttonName.append(card.value()).append(",");
            }
            JButton setDisplayButton = new JButton(String.valueOf(buttonName));
            setDisplayButton.setSize(1000, 300);
            cardOptionPanel.add(setDisplayButton);
            setDisplayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    riskGame.updateGameState(GameState.DEPLOY, AttackPhase.ATTACKFROM);
                    int newTroops = turnInChosenCards(setDisplayButton.getText());

                    updateGameStateLabel(GameState.DEPLOY, attackState, newTroops);
                    gameStateText.setVisible(true);

                    cardsPane.setVisible(false);
                    updateDisplay();
                }
            });
        }
        gameStateText.setVisible(false);
        cardsPane.setVisible(true);
    }

    private void removeLastCards() {
        Component[] components = cardOptionPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                cardOptionPanel.remove(component);
            }
        }
    }

    private int turnInChosenCards(String text) {
        String[] territoryNames = text.split(",");
        return riskGame.convertTerritoryNameToCard(territoryNames);
    }

    private void initializeBackgroundImage() {
        ImageIcon backGroundImage = new ImageIcon("src/main/java/data/mapImage.png");
        Image image = backGroundImage.getImage();
        image = image.getScaledInstance(screenSize.width, (int) (screenSize.getHeight()), Image.SCALE_SMOOTH);
        backGroundImage = new ImageIcon(image);
        finalBackgroundImage = backGroundImage.getImage();
    }

    private void initializeTroopCountOverlay() {
        troopCountOverlay = new JPanel();
        troopCountOverlay.setBounds(0, 0, screenSize.width, screenSize.height);
        troopCountOverlay.setOpaque(false);
        troopCountOverlay.setLayout(null);
        mainFrame.add(troopCountOverlay);
    }

    /**
     * Ensures: Draws a hitbox around each of the territories as specified in the territoryHitbox.txt file.
     * Note: This is just for testing and development purposes and will be removed from the final project.
     * @param g - The graphics object being drawn on.
     */
    private void drawAllBorders(Graphics g) {
        String line = "";
        String hitboxFilePath = "src/main/java/data/territoryHitbox.txt";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(hitboxFilePath), Charset.defaultCharset()));
            while ((line = br.readLine()) != null) {
                String[] territoryHitbox = line.split(",");
                drawRectangle(Double.parseDouble(territoryHitbox[1]), Double.parseDouble(territoryHitbox[2]),
                        Double.parseDouble(territoryHitbox[3]), Double.parseDouble(territoryHitbox[4]), g);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures: Draws the troop number labels with random values as specified by drawTextLabel
     * Note: This is just for testing and development purposes and will be removed from the final project.
     */
    private void initializeTroopCountLabels() {
        troopCountLabels = new HashMap<>();
        String line;
        String hitboxFilePath = "src/main/java/data/territoryTroopCountHitbox.txt";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(hitboxFilePath), Charset.defaultCharset()));
            while ((line = br.readLine()) != null) {
                String[] territoryHitbox = line.split(",");
                JLabel territoryLabel = drawTextLabel(Double.parseDouble(territoryHitbox[1]), Double.parseDouble(territoryHitbox[2]) / .9,
                        Double.parseDouble(territoryHitbox[3]), Double.parseDouble(territoryHitbox[4]) / .9);
                troopCountLabels.put(territoryHitbox[0], territoryLabel);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeCurrentPlayerLabel(){
        String currentPlayer = riskGame.getCurrentPlayerColor();
        String currentPlayerMessage = messages.getString("currentPlayerMessage");
        currentPlayerLabel = new JLabel(currentPlayerMessage + " " + currentPlayer);
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        currentPlayerLabel.setFont(new Font(null, Font.PLAIN, 25));

        gameInfoPanel = new JPanel();
        gameInfoPanel.setLocation(0, (int) screenHeight);
        gameInfoPanel.setBounds(0, (int) screenHeight, (int) screenWidth, (int) (screenHeight*.25));
        gameInfoPanel.setBackground(Color.GRAY);
        gameInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        gameInfoPanel.add(turnInCards);
        gameInfoPanel.add(currentPlayerLabel);
        mainFrame.add(gameInfoPanel);
    }

    private void addRiskCard(ArrayList<String> cardToDisplay){
        for (JLabel card: cardsOnPanel){
            gameInfoPanel.remove(card);
        }
        gameInfoPanel.remove(turnInCards);
        cardsOnPanel.clear();

        for (String cardDrawnPath: cardToDisplay) {
            ImageIcon cardImageIcon = new ImageIcon(cardDrawnPath);
            Image cardToDraw = cardImageIcon.getImage();

            double screenWidth = screenSize.getWidth();
            double screenHeight = screenSize.getHeight();
            cardToDraw = cardToDraw.getScaledInstance((int) (screenWidth / 15), (int) (screenHeight * .15), Image.SCALE_SMOOTH);

            JLabel picLabel = new JLabel(new ImageIcon(cardToDraw));
            cardsOnPanel.add(picLabel);
            gameInfoPanel.add(picLabel);
            gameInfoPanel.repaint();
        }

        turnInCards.setVisible(riskGame.canTurnInCards());
        gameInfoPanel.add(turnInCards);
        gameInfoPanel.repaint();

        mainFrame.repaint();
    }


    private void updateTroopCountLabels(DisplayTerritoryData[] boardState) {
        for (DisplayTerritoryData territoryState: boardState) {
            JLabel territoryLabel = troopCountLabels.get(territoryState.territoryName);
            territoryLabel.setText(territoryState.troopCount);
            territoryLabel.setForeground(territoryState.playerColor);
        }
    }

    private void updateCurrentPlayerLabel(ArrayList<String> cardToDisplay){
        String currentPlayer = riskGame.getCurrentPlayerColor();
        String currentPlayerMessage = messages.getString("currentPlayerMessage");
        currentPlayerLabel.setText(currentPlayerMessage + " " + currentPlayer);
        addRiskCard(cardToDisplay);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(finalBackgroundImage, 0, 0, (int) screenSize.getWidth(), (int) (screenSize.getHeight()), null);
        drawAllBorders(g);
        troopCountOverlay.setBounds(0, 0, screenSize.width, screenSize.height);
    }


    /**
     * Ensures: Outlines a green rectangle around the given upper left corner hitbox.
     * Note: all values are specified as percentages of the overall screen size.
     * @param x - The left most point of the hitbox
     * @param y - The highest point of the hitbox
     * @param width - The width of the hitbox
     * @param height - The height of the hitbox
     * @param g - The graphics object being drawn on
     */
    private void drawRectangle(double x, double y, double width, double height, Graphics g) {
        g.setColor(Color.green);
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        g.drawRect((int) (screenWidth * x), (int) (screenHeight * y /.93), (int) (screenWidth * width), (int) (screenHeight * height));
    }

    /**
     * Ensures: Draws a Text label onto the screen with a randomly generated number.
     * The location of this text label is specified by an upper left corner hitbox using the parameters given.
     * Note: all values are specified as percentages of the overall screen size.
     * @param x - The left most point of the hitbox
     * @param y - The highest point of the hitbox
     * @param width - The width of the hitbox
     * @param height - The height of the hitbox
     */
    private JLabel drawTextLabel(double x, double y, double width, double height) {
        JLabel label = new JLabel();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        label.setBounds((int) (screenWidth * x), (int) (screenHeight * y), (int) (screenWidth * width), (int) (screenHeight * height));
        label.setFont(new Font(null, Font.BOLD, 30));
        troopCountOverlay.add(label);
        return label;
    }

    @SuppressFBWarnings
    public void setGame(Game game) {
        this.riskGame = game;
        riskGame.setResourceBundle(messages);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        try{
            riskGame.clickedOnPoint(e.getPoint());

        }catch(IllegalStateException | IllegalArgumentException exc){
            JOptionPane.showMessageDialog(null, exc.getMessage());
        } finally {
            updateDisplay();
        }
    }

    private void updateDisplay() {
        updateGameData();
        displaySlidersIfNeeded();
        updateScreen();

        mainFrame.setVisible(true);
        mainFrame.repaint();
    }

    private void updateGameData() {
        formerGameState = gameState;
        gameState = riskGame.getGameState();
        attackState = riskGame.getAttackState();
        maneuverState = riskGame.getManeuverState();
        deployableTroops = riskGame.getDeployableTroops();
        currentTerritory = riskGame.getCurrentTerritory();
        if (gameState == GameState.WIN){
            handleWin();
        }
    }

    private void handleWin() {
        this.removeMouseListener(this);
        String winMessage = generateWinMessage();
        mainFrame.dispose();
        mainFrame.removeAll();
        JOptionPane.showMessageDialog(null, winMessage);
    }

    private String generateWinMessage() {
        String currentPlayer = riskGame.getCurrentPlayerColor();
        String firstHalfWinMessage = messages.getString("firstHalfWinMessage");
        String secondHalfWinMessage = messages.getString("secondHalfWinMessage");
        return firstHalfWinMessage + " " + currentPlayer + " " + secondHalfWinMessage;
    }

    private void displaySlidersIfNeeded() {
        if (gameState == GameState.DEPLOY && currentTerritory != null){
            displayTroopSlider(deployableTroops, currentTerritory);
            turnInCards.setVisible(riskGame.canTurnInCards());
        } else if (gameState == GameState.ATTACK && attackState == AttackPhase.CHOOSETROOPS  && currentTerritory != null) {
            displayAttackSlider(riskGame.attackMax(), currentTerritory);
        } else if (gameState == GameState.MANEUVER && maneuverState == ManeuverPhase.CHOOSETROOPSTOMANEUVER) {
            displayManeuverSlider();
        }
    }

    private void displayManeuverSlider() {
        setupSlider();
        int maneuverableTroops = riskGame.getManeuverableTroops();
        setSliderBounds(1, maneuverableTroops);
        String maneuverTroopsMessage = messages.getString("maneuverTroopsMessage");
        sliderSubmitButton = new JButton(maneuverTroopsMessage);

        String[] sliderLabelText = generateManeuverSliderLabelText();
        sliderJLabel = new JLabel(sliderLabelText[0] + troopSlider.getValue() + sliderLabelText[1]);

        troopSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sliderJLabel.setText(sliderLabelText[0] + troopSlider.getValue() + sliderLabelText[1]);
            }
        });

        sliderSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtomicInteger maneuverableTroopAmount = new AtomicInteger(troopSlider.getValue());
                riskGame.maneuverTroops(maneuverableTroopAmount);
                resetSliderPanel();
                updateDisplay();
            }
        });

        sliderPanel.add(sliderJLabel);
        sliderJLabel.setVisible(true);
        sliderPanel.add(sliderSubmitButton);
        sliderSubmitButton.setVisible(true);
        sliderPanel.repaint();
        this.add(sliderPanel);
        this.repaint();
        this.setVisible(true);
    }

    private void resetSliderPanel() {
        gameInfoPanel.remove(skipManeuverButton);
        Container parent = sliderPanel.getParent();
        if (parent != null) {
            sliderPanel.remove(sliderSubmitButton);
            sliderPanel.remove(sliderJLabel);
            parent.remove(sliderPanel);
            parent.repaint();
            parent.validate();
            parent.setVisible(true);
        }
    }

    private String[] generateManeuverSliderLabelText() {
        String[] sliderLabelHalves = new String[2];
        String fromTerritoryName = riskGame.getManeuverFromName();
        String toTerritoryName = riskGame.getManeuverToName();
        sliderLabelHalves[0] = messages.getString("maneuverSliderLabelOne") + " ";
        String sliderLabelPart2 = messages.getString("maneuverSliderLabelTwo");
        String sliderLabelPart3 = messages.getString("maneuverSliderLabelThree");
        sliderLabelHalves[1] = " " + sliderLabelPart2 + " " + fromTerritoryName + " " + sliderLabelPart3 + toTerritoryName;
        return sliderLabelHalves;
    }

    private void setSliderBounds(int lowerBound, int upperBound) {
        troopSlider.setMaximum(upperBound);
        troopSlider.setMinimum(lowerBound);
        if (upperBound / 2 < lowerBound) {
            troopSlider.setValue(lowerBound);
        } else {
            troopSlider.setValue(upperBound / 2);
        }
        troopSlider.setMajorTickSpacing(3);
        troopSlider.setMinorTickSpacing(1);
    }

    private void setupSlider() {
        cleanPanel(sliderPanel);
        this.remove(sliderPanel);
        gameStateText.setVisible(false);
    }


    private void updateScreen() {
        updateGameStateLabel(gameState, attackState, deployableTroops);
        DisplayTerritoryData[] boardState = riskGame.getBoardState();
        ArrayList<String> cardToDisplay = riskGame.getCurrentPlayerCards();
        if (cardToDisplay.size() >= 5 && gameState == GameState.DEPLOY){
            forceTurnIn();
        }
        updateCurrentPlayerLabel(cardToDisplay);
        updateTroopCountLabels(boardState);
        if (gameState.equals(GameState.MANEUVER)) {
            gameInfoPanel.add(skipManeuverButton);
        }
    }

    private void forceTurnIn() {
        ActionEvent event = new ActionEvent(turnInCards, ActionEvent.ACTION_PERFORMED, "Force turnInCard button to be pressed");
        ActionListener[] listeners = turnInCards.getActionListeners();
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }


    private void updateGameStateLabel(GameState gameStateInput, AttackPhase attackStateInput, int troops) {
        String original = gameStateText.getText();
        if (gameStateInput == GameState.SETUP){
            String deploy1TroopMessage = messages.getString("deploy1TroopMessage");
            gameStateText.setText(deploy1TroopMessage);
        }else if (gameStateInput == GameState.DEPLOY){
            String youHaveMessage = messages.getString("youHaveMessage");
            String troopsDeployMessage = messages.getString("troopsDeployMessage");
            gameStateText.setText(youHaveMessage + " " + troops + " " + troopsDeployMessage);
        }else if (gameStateInput == GameState.ATTACK){
            if (attackStateInput == AttackPhase.ATTACKFROM){
                String attackWithMessage = messages.getString("attackWithMessage");
                gameStateText.setText(attackWithMessage);
            }else if (attackStateInput == AttackPhase.DEFENDWITH){
                String chooseDefendTerritory = messages.getString("chooseDefendTerritory");
                gameStateText.setText(chooseDefendTerritory);
            }
        } else if (gameStateInput == GameState.MANEUVER){
            if (maneuverState == ManeuverPhase.MANEUVERFROM) {
                String maneuverFromMessage = messages.getString("maneuverFromMessage");
                gameStateText.setText(maneuverFromMessage);
            } else if (maneuverState == ManeuverPhase.MANEUVERTO) {
                String maneuverToMessage = messages.getString("maneuverToMessage");
                gameStateText.setText(maneuverToMessage);
            }
        } else if (gameStateInput == GameState.NEWATTACK) {
            gameStateText.setText("");
            optionPanel.setVisible(true);
            newAttack.setVisible(riskGame.canAttack());
        }
        if (!original.equals(gameStateText.getText())){
            gameStateText.setVisible(true);
        }

    }


    private void displayTroopSlider(int troops, Territory territory) {
        cleanPanel(sliderPanel);
        this.remove(sliderPanel);
        gameStateText.setVisible(false);

        troopSlider.setMaximum(troops);
        troopSlider.setMinimum(0);
        troopSlider.setValue(troops / 2);

        troopSlider.setMajorTickSpacing(3);
        troopSlider.setMinorTickSpacing(1);

        String submitOption = messages.getString("submitOption");
        JButton sliderSubmit = new JButton(submitOption);


        JLabel sliderLabel = new JLabel();
        sliderLabel.setFont(new Font("MV Boli", Font.PLAIN, 15));

        String deployMessage = messages.getString("deployMessage");
        String troopsTo = messages.getString("troopsTo");
        sliderLabel.setText(deployMessage + " " + troopSlider.getValue() + " " + troopsTo + " " + territory.getTerritoryName());
        //update the label
        troopSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String deployMessage = messages.getString("deployMessage");
                String troopsTo = messages.getString("troopsTo");
                sliderLabel.setText(deployMessage + " " + troopSlider.getValue() + " " + troopsTo + " " + territory.getTerritoryName());
            }
        });

        sliderSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtomicInteger troopsDeployed = new AtomicInteger();
                troopsDeployed.set(troopSlider.getValue());
                riskGame.updateDeployableTroops(troopsDeployed.intValue(), territory);
                mainFrame.repaint();
                mainFrame.setVisible(true);

                Container parent = sliderPanel.getParent();
                sliderPanel.remove(sliderSubmit);
                sliderPanel.remove(sliderLabel);
                updateDisplay();
                if (parent != null) {
                    parent.remove(sliderPanel);
                    gameStateText.setVisible(true);
                    parent.repaint();
                    parent.validate();
                    parent.setVisible(true);
                }

            }
        });
        sliderPanel.add(sliderLabel);
        sliderPanel.add(sliderSubmit);

        sliderPanel.repaint();

        this.add(sliderPanel);
        this.repaint();
        this.setVisible(true);
    }

    private void cleanPanel(JPanel panelToClean) {
        Component[] componentList = panelToClean.getComponents();
        for (Component c : componentList){
            if (c instanceof JButton || c instanceof  JLabel){
                panelToClean.remove(c);
            }
        }
    }

    private void displayAttackSlider(int attackingTroops, Territory territory) {
        cleanPanel(sliderPanel);
        this.remove(sliderPanel);
        gameStateText.setVisible(false);


        troopSlider.setMaximum(attackingTroops);
        troopSlider.setMinimum(1);
        troopSlider.setValue(attackingTroops / 2);
        troopSlider.setMinorTickSpacing(1);

        String attackMessage = messages.getString("attackMessage");
        String withMessage = messages.getString("withMessage");
        String troopsMessage = messages.getString("troopsMessage");
        JButton sliderSubmit = new JButton(attackMessage);
        JLabel sliderLabel = new JLabel();
        sliderLabel.setFont(new Font("MV Boli", Font.PLAIN, 15));
        sliderLabel.setText(attackMessage+ " " + territory.getTerritoryName() + " " + withMessage + " "
                + troopSlider.getValue() + " " + troopsMessage);


        //update the label
        troopSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sliderLabel.setText(attackMessage+ " " + territory.getTerritoryName() + " " + withMessage + " "
                        + troopSlider.getValue() + " " +  troopsMessage);            }
        });

        sliderPanel.add(sliderLabel);
        sliderLabel.setVisible(true);
        sliderSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtomicInteger attackers = new AtomicInteger();
                attackers.set(troopSlider.getValue());

                int defendCount = riskGame.defendMax();
                ArrayList<Integer> losses = riskGame.attack(attackers.intValue(), defendCount);

                String attackerLost = messages.getString("attackerLost");
                String defenderLost = messages.getString("defenderLost");
                String attackResultMessage = attackerLost + " " + losses.get(0) + " " + defenderLost + " " + losses.get(1);
                JOptionPane.showMessageDialog(null, attackResultMessage);

                int attackerWinOutRight = losses.get(2);

                mainFrame.repaint();
                mainFrame.setVisible(true);

                Container parent = sliderPanel.getParent();
                sliderPanel.remove(sliderSubmit);
                sliderPanel.remove(sliderLabel);


                updateDisplay();

                if (parent != null) {
                    parent.remove(sliderPanel);
                    parent.repaint();
                    parent.validate();
                    parent.setVisible(true);
                }

                Territory attackerTerritory = riskGame.getAttackingTerritory();

                if (attackerWinOutRight >= 1 && attackerTerritory.getCurrentNumberOfTroops() > 1){
                    displayExtraTroopSlider(attackerTerritory, riskGame.getDefendingTerritory());
                } else {
                    optionPanel.setVisible(true);
                    newAttack.setVisible(riskGame.canAttack());
                }

            }
        });
        sliderPanel.add(sliderSubmit);
        sliderPanel.repaint();

        this.add(sliderPanel);
        this.repaint();
        this.setVisible(true);
    }


    private void displayExtraTroopSlider(Territory attackingTerritory, Territory defendingTerritory) {
        cleanPanel(sliderPanel);
        this.remove(sliderPanel);


        int attackingTroops = attackingTerritory.getCurrentNumberOfTroops()-1;
        troopSlider.setMaximum(attackingTroops);
        troopSlider.setMinimum(0);
        troopSlider.setValue(attackingTroops / 2);
        troopSlider.setMinorTickSpacing(1);

        String moveMessage = messages.getString("moveMessage");
        JButton sliderSubmit = new JButton(moveMessage);
        JLabel sliderLabel = new JLabel();
        sliderLabel.setFont(new Font("MV Boli", Font.PLAIN, 15));
        String additionalTroopMessage = messages.getString("additionalTroopMessage");
        sliderLabel.setText(additionalTroopMessage + " " + defendingTerritory.getTerritoryName());

        sliderPanel.add(sliderLabel);
        sliderLabel.setVisible(true);
        sliderSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AtomicInteger extraTroops = new AtomicInteger();
                extraTroops.set(troopSlider.getValue());

                attackingTerritory.removeFromCurrentTroops(extraTroops.intValue());
                defendingTerritory.addAdditionalTroops(extraTroops.intValue());

                if (checkForForceTurnIn()) {
                    riskGame.forceTurnInSetup();
                } else {
                    optionPanel.setVisible(true);
                    newAttack.setVisible(riskGame.canAttack());
                }

                mainFrame.repaint();
                mainFrame.setVisible(true);

                Container parent = sliderPanel.getParent();
                sliderPanel.remove(sliderSubmit);
                sliderPanel.remove(sliderLabel);


                updateDisplay();
                if (parent != null) {
                    parent.remove(sliderPanel);
                    parent.repaint();
                    parent.validate();
                    parent.setVisible(true);
                }

            }
        });
        sliderPanel.add(sliderSubmit);
        sliderPanel.repaint();

        this.add(sliderPanel);
        this.repaint();
        this.setVisible(true);
    }

    private boolean checkForForceTurnIn() {
        return riskGame.getCurrentPlayerCards().size() >= 5;
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }
}

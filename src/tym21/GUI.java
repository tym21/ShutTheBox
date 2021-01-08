package tym21;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

class GUI extends JFrame {
    private final Control control;
    private GameState gamestate;

    private final GameLabel labelDice1;
    private final GameLabel labelDice2;
    private final GameLabel labelScore;
    private final GameLabel labelRoundStatus;

    private final GameButton[] flapButtons = new GameButton[9];
    private final GameButton buttonDiceRoll;
    private final GameButton buttonStartReset;
    private final GameButton buttonGiveUp;

    GUI(Control control) {
        this.control = control;

        // set the game state
        gamestate = GameState.GAME_START;

        //configure the JFrame
        setTitle("ShutTheBox");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");

        JMenuItem menuItemRules = new JMenuItem("Wikipedia rules of the game");
        menuItemRules.addActionListener(e -> openUrl("https://en.wikipedia.org/wiki/Shut_the_box#Rules"));
        JMenuItem menuItemGitHub = new JMenuItem("GitHub");
        menuItemGitHub.addActionListener(e -> openUrl("https://github.com/tym21/ShutTheBox"));
        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(e -> System.exit(0));

        menuFile.add(menuItemRules);
        menuFile.add(menuItemGitHub);
        menuFile.addSeparator();
        menuFile.add(new JMenuItem("by TK"));
        menuFile.addSeparator();
        menuFile.add(menuItemExit);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // add a font
        Font font = new Font("Tahoma", Font.PLAIN, 20);

        // create panelFlaps here because the flaps buttons need to be added directly
        JPanel panelFlaps = new JPanel();

        // JLabels
        GameLabel labelFlaps = new GameLabel("Flaps:", font);
        panelFlaps.add(labelFlaps);

        GameLabel labelLScore = new GameLabel("Score:", font);
        labelScore = new GameLabel("", font, Color.BLUE);
        GameLabel labelLDice1 = new GameLabel("Dice 1:", font);
        labelDice1 = new GameLabel("", font, Color.RED);
        GameLabel labelLDice2 = new GameLabel("Dice 2:", font);
        labelDice2 = new GameLabel("", font, Color.RED);
        labelRoundStatus = new GameLabel("    --Start--    ", font);

        // Buttons
        buttonDiceRoll = new GameButton("dice roll", false, font);
        buttonGiveUp = new GameButton("give up", false, font);
        buttonStartReset = new GameButton("Start", true, font);

        // create the flap buttons
        for (int i = 0; i < flapButtons.length; i++) {
            flapButtons[i] = new GameButton(String.valueOf(i + 1), false, font);
            int index = i;
            flapButtons[i].addActionListener(e -> closeFlap(index));
            panelFlaps.add(flapButtons[i]);
        }

        //ActionListeners
        buttonDiceRoll.addActionListener(e -> diceRoll());
        buttonGiveUp.addActionListener(e -> giveUp());
        buttonStartReset.addActionListener(e -> startReset());

        // create the control JPanel
        JPanel panelControl = new JPanel();
        panelControl.add(buttonStartReset);
        panelControl.add(buttonDiceRoll);
        panelControl.add(buttonGiveUp);

        // create the info JPanel
        JPanel panelInfo = new JPanel();
        panelInfo.add(labelLScore);
        panelInfo.add(labelScore);
        panelInfo.add(labelLDice1);
        panelInfo.add(labelDice1);
        panelInfo.add(labelLDice2);
        panelInfo.add(labelDice2);
        panelInfo.add(labelRoundStatus);

        // Layout Base: Border Layout
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(panelFlaps, BorderLayout.NORTH);
        pane.add(panelInfo, BorderLayout.CENTER);
        pane.add(panelControl, BorderLayout.SOUTH);

        // make JLabel visible
        setVisible(true);
    }

    // Methods which are called by the buttons

    private void closeFlap(int index) {
        //if the player has not yet rolled the dice and clicks on a flap button
        if (gamestate == GameState.IN_MOVE) {
            flapButtons[index].setEnabled(false);
            gameStateControl(control.closeFlap(index));
        } else showInformationDialog("Information", "Roll the dice first.");
    }

    private void giveUp() {
        if (showConfirmDialog("Give up", "Do you really want to give up?")) {
            buttonGiveUp.setEnabled(false);
            control.playerGiveUp();
            gameStateControl(1);
            if(gamestate == GameState.GAME_OVER) // When the round is lost
                return;
            startMove();
            buttonGiveUp.setEnabled(true);
        }
    }

    private void startReset() {
        if (gamestate == GameState.GAME_START)
            startGame();
        else if (showConfirmDialog("Reset Game", "Do you really want to reset?"))
            resetGame();
    }

    // GUI game logic methods

    private void startGame() {
        buttonStartReset.setEnabled(false);
        buttonStartReset.setText("Reset");
        setScore();
        startMove();
        gameStateControl(3);
        buttonGiveUp.setEnabled(true);
        buttonStartReset.setEnabled(true);
    }

    private void resetGame() {
        buttonStartReset.setEnabled(false);
        control.resetGame();
        setAllFlaps(false);
        setScore();
        buttonDiceRoll.setEnabled(false);
        buttonGiveUp.setEnabled(false);
        gamestate = GameState.GAME_START;
        labelRoundStatus.setText("    --Start--    ");
        buttonStartReset.setText("Start");
        buttonStartReset.setEnabled(true);
    }

    private void gameStateControl(int status) {
        switch (status) {
            case 0: // move not yet over
                labelRoundStatus.setText("    --close the right flaps--  ");
                break;
            case 1: // move won and over
                labelRoundStatus.setText("    --roll the dice again--    ");
                moveOver();
                break;
            case 2: // move lost and over
                labelRoundStatus.setText("    --The sum of the closed flaps was too large. Roll the dice again--    ");
                moveOver();
                startMove();
                break;
            case 3: // player info
                labelRoundStatus.setText("    --roll the dice--    ");
                break;
            case 4: // round lost
                labelRoundStatus.setText("    --GAME OVER-- score <= -45");
                break;
            case 5: // round won
                labelRoundStatus.setText("  --Round Won-- All flaps closed!");
                roundWon();
                break;
            case 6: // round lost
                labelRoundStatus.setText("    --GAME OVER-- All flaps closed");
                gameOver("GAME OVER\nAll flaps closed\n Reset Game");
                break;
        }
    }

    private void moveOver() {
        gamestate = GameState.MOVE_OVER;
        setScore();
        buttonDiceRoll.setEnabled(true);
        checkScore();
    }

    private void startMove() {
        gamestate = GameState.START_MOVE;
        setDiceValues("", "");
        setAllFlaps(true);
        buttonDiceRoll.setEnabled(true);
    }

    private boolean showConfirmDialog(String title, String message) {
        //YES = true, NO = false
        return JOptionPane.showConfirmDialog(getContentPane(), message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0;
    }

    private void showInformationDialog(String title, String message) {
        JOptionPane.showMessageDialog(getContentPane(), message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void setDiceValues(int dice1, int dice2) {
        labelDice1.setText(String.valueOf(dice1));
        labelDice2.setText(String.valueOf(dice2));
    }

    private void diceRoll() {
        buttonDiceRoll.setEnabled(false);
        int[] diceValues = control.diceRoll();
        setDiceValues(diceValues[0], diceValues[1]);
        gameStateControl(0);
        gamestate = GameState.IN_MOVE;
    }

    private void setDiceValues(String dice1, String dice2) {
        labelDice1.setText(dice1);
        labelDice2.setText(dice2);
    }

    private void setAllFlaps(boolean enabled) {
        for (GameButton flapButton : flapButtons)
            flapButton.setEnabled(enabled);
    }

    private void setScore() {
        labelScore.setText(String.valueOf(control.getScore()));
    }

    private void checkScore() {
        if (control.checkGameState()) {
            gameOver("GAME OVER\nScore <= -45\n Reset Game");
        }
    }

    private void gameOver(String message) {
        gamestate = GameState.GAME_OVER;
        buttonDiceRoll.setEnabled(false);
        buttonGiveUp.setEnabled(false);
        showInformationDialog("Info", message);
        gameStateControl(4);
    }

    private void roundWon() {
        gamestate = GameState.GAME_OVER;
        buttonDiceRoll.setEnabled(false);
        buttonGiveUp.setEnabled(false);
        showInformationDialog("Info", "WON!\nAll flaps closed.\n Reset Game");
    }

    private void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

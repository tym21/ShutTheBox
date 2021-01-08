package tym21;

public class Control {
    private final Dice dice;
    private int score = 0;
    private int currentClosedFlapSum = 0;
    private int currentDiceSum = 0;
    private final Flap[] flaps = new Flap[9];

    //creates the logic Class
    public Control() {
        // create all Flaps Objects
        for (int i = 0; i < flaps.length; i++)
            flaps[i] = new Flap(i);

        //create the dice
        dice = new Dice();
    }

    // returns the dice values to the GUI
    int[] diceRoll() {
        int[] diceValues = new int[2];
        diceValues[0] = dice.roll();
        diceValues[1] = dice.roll();
        // if the sum of the open flaps is less than 7, only one dice is rolled
        if (getSumOpenFlaps() < 7)
            diceValues[1] = 0;

        currentDiceSum = diceValues[0] + diceValues[1];
        return diceValues;
    }

    // returns the status of the game round to the gui
    int closeFlap(int index) {
        flaps[index].setOpen(false);
        return analyse(flaps[index].getValue());
    }

    // returns the status of the game round
    int analyse(int value) {
        currentClosedFlapSum += value;
        boolean isOpenFlaps = openFalps();

        if (currentClosedFlapSum == currentDiceSum) {  // If the sum of the closed flaps is equal to the sum of the dice. --> game move won and over
            if (!isOpenFlaps) // Round won and over
                return 5;

            currentClosedFlapSum = 0;
            return 1;

        }
        if (!isOpenFlaps) { // game over
            return 6;
        }
        if (currentClosedFlapSum > currentDiceSum) { // If the sum of the closed flaps is greater than the sum of the dice. --> game move lost and over
            setControlMoveEnd(currentClosedFlapSum);
            return 2;
        }
        return 0;  //move not yet finished
    }

    private int getSumOpenFlaps() {
        int sum = 0;
        for (Flap flap : flaps) {
            if (flap.isOpen())
                sum += flap.getValue();
        }
        return sum;
    }

    private boolean openFalps() {
        for (Flap flap : flaps) {
            if (flap.isOpen())
                return true;
        }
        return false;
    }

    public void playerGiveUp() {
        setControlMoveEnd(currentClosedFlapSum);
    }

    // when the player gives up or the move is over
    private void simpleReset() {
        currentClosedFlapSum = 0;
        for (Flap flap : flaps)
            flap.setOpen(true);
    }

    // resets the hole game
    void resetGame() {
        simpleReset();
        score = 0;
    }

    private void setControlMoveEnd(int add) {
        score -= getSumOpenFlaps();
        score -= add;
        simpleReset();
    }

    boolean checkGameState() {
        return score < -44;
    }

    public int getScore() {
        return score;
    }

    // creates the whole game and starts the game
    public static void main(String[] args) {
        // create the gui, the control class must not know the GUI class.
        new GUI(new Control());
    }
}

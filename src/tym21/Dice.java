package tym21;

public class Dice {

    // returns a random number between 1 and 6
    int roll() {
        return (int) ((Math.random() * 6) + 1);
    }
}

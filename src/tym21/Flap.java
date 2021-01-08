package tym21;

public class Flap {
    private final int value;
    private boolean isOpen = true;

    public Flap(int index) {
        this.value = index + 1;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getValue() {
        return value;
    }

    public boolean isOpen() {
        return isOpen;
    }
}

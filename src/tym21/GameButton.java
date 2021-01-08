package tym21;

import javax.swing.*;
import java.awt.*;

public class GameButton extends JButton {
    public GameButton(String text, Boolean isEnabled, Font font) {
        super(text);
        setEnabled(isEnabled);
        setFont(font);
    }
}

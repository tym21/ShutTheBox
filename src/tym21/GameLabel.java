package tym21;

import javax.swing.*;
import java.awt.*;

public class GameLabel extends JLabel {
    public GameLabel(String text, Font font) {
        super(text);
        setFont(font);
    }

    public GameLabel(String text, Font font, Color color) {
        super(text);
        setFont(font);
        setForeground(color);
    }
}

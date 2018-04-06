package vodka.igor.mosmetro.ui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;

public class UIUtils {
    public static void error(String message, String title)
    {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void error(String message) {
        error(message, "Ошибка!");
    }
}

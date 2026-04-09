package fundsmanager.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ThemeUtil {

    private static boolean dark = false;

    public static void toggleTheme(JFrame frame) {
        try {
            dark = !dark;
            UIManager.setLookAndFeel(
                    dark ? new FlatDarkLaf() : new FlatLightLaf()
            );
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            System.err.println("Error While changing theme." + e.getMessage());
        }
    }
}

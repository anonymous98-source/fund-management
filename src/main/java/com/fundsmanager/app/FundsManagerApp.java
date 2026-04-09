package fundsmanager.app;


import com.formdev.flatlaf.FlatLightLaf;
import fundsmanager.ui.MainFrame;

import javax.swing.*;

public class FundsManagerApp {

    public static void main(String[] args) {
            FlatLightLaf.setup();

        SwingUtilities.invokeLater(() ->
                new MainFrame().setVisible(true)
        );
    }
}


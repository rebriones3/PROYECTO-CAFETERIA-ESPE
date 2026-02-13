package com.mycompany.cafeteriaespe;

import vista.InterfazLoginMejorada;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class CafeteriaESPE {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cafetería ESPE - Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            InterfazLoginMejorada panelLogin = new InterfazLoginMejorada();
            frame.add(panelLogin);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

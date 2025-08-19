package gui;

import javax.swing.*;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("Stock Data Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Data Generator", new DataGeneratorPanel());
        tabbedPane.addTab("Stock Ranking", new StockRankingPanel());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}

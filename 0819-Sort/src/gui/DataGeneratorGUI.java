package gui;

import service.DataGenerator;

import javax.swing.*;
import java.awt.*;

public class DataGeneratorGUI extends JFrame {

    private final JTextField stockCountField = new JTextField("18000");
    private final JTextField daysField = new JTextField("2000");
    private final JButton generateButton = new JButton("Generate Data");
    private final JProgressBar progressBar = new JProgressBar();

    public DataGeneratorGUI() {
        setTitle("Data Generator");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Stock Count:"));
        add(stockCountField);
        add(new JLabel("Days:"));
        add(daysField);
        add(generateButton);
        add(progressBar);

        generateButton.addActionListener(e -> {
            generateButton.setEnabled(false);
            progressBar.setIndeterminate(true);

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    int stockCount = Integer.parseInt(stockCountField.getText());
                    int days = Integer.parseInt(daysField.getText());
                    new DataGenerator().generateData(stockCount, days, progress -> {
                        // 進度回調，更新 progressBar
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(progress);
                        });
                    });
                    return null;
                }

                @Override
                protected void done() {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    generateButton.setEnabled(true);
                    JOptionPane.showMessageDialog(DataGeneratorGUI.this, "Data generation complete!");
                }
            }.execute();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataGeneratorGUI().setVisible(true);
        });
    }
}

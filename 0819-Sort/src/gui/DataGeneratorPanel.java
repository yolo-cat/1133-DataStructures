package gui;

import service.DataGenerator;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DataGeneratorPanel extends JPanel {

    private final JTextField stockCountField = new JTextField("18000");
    private final JTextField daysField = new JTextField("2000");
    private final JButton generateButton = new JButton("Generate Data");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public DataGeneratorPanel() {
        setLayout(new GridLayout(3, 2));
        progressBar.setStringPainted(true);

        add(new JLabel("Stock Count:"));
        add(stockCountField);
        add(new JLabel("Days:"));
        add(daysField);
        add(generateButton);
        add(progressBar);

        generateButton.addActionListener(e -> {
            generateButton.setEnabled(false);
            progressBar.setValue(0);

            // Ensure directory exists
            new File(Constants.DATA_DIRECTORY).mkdirs();

            new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    int stockCount = Integer.parseInt(stockCountField.getText());
                    int days = Integer.parseInt(daysField.getText());
                    new DataGenerator().generateData(stockCount, days, this::publish);
                    return null;
                }

                @Override
                protected void process(List<Integer> chunks) {
                    progressBar.setValue(chunks.get(chunks.size() - 1));
                }

                @Override
                protected void done() {
                    try {
                        get(); // This will re-throw any exception from doInBackground
                        JOptionPane.showMessageDialog(DataGeneratorPanel.this, "Data generation complete!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DataGeneratorPanel.this, "Error generating data: " + ex.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        generateButton.setEnabled(true);
                    }
                }
            }.execute();
        });
    }
}

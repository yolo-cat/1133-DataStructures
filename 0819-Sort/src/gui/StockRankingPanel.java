package gui;

import model.StockRecord;
import service.ExternalMergeSort;
import util.SortField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

import util.Constants;

public class StockRankingPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JButton sortButton = new JButton("Load and Sort");
    private final JComboBox<SortField> sortFieldComboBox = new JComboBox<>(SortField.values());
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public StockRankingPanel() {
        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortFieldComboBox);
        topPanel.add(sortButton);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(progressBar, BorderLayout.SOUTH);
        progressBar.setStringPainted(true);

        // Table
        String[] columnNames = {"Stock Code", "Name", "Date", "Volume", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sortButton.addActionListener(e -> {
            String dataFile = Constants.DATA_DIRECTORY + "stock_data.csv";
            if (!new File(dataFile).exists()) {
                JOptionPane.showMessageDialog(this, "Data file not found. Please generate it first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            sort(dataFile);
        });
    }

    private void sort(String filePath) {
        sortButton.setEnabled(false);
        tableModel.setRowCount(0); // Clear table
        progressBar.setValue(0);

        new SwingWorker<List<StockRecord>, Integer>() {
            @Override
            protected List<StockRecord> doInBackground() throws Exception {
                ExternalMergeSort sorter = new ExternalMergeSort();
                SortField selectedField = (SortField) sortFieldComboBox.getSelectedItem();
                String sortedFile = sorter.sort(filePath, selectedField, this::publish);
                return sorter.getTopN(sortedFile, 15);
            }

            @Override
            protected void process(List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    List<StockRecord> top15 = get();
                    for (StockRecord record : top15) {
                        tableModel.addRow(new Object[]{
                                record.getStockCode(),
                                record.getStockName(),
                                record.getTradeDate(),
                                record.getVolume(),
                                record.getAmount()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StockRankingPanel.this, "Error during sorting: " + e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                sortButton.setEnabled(true);
            }
        }.execute();
    }
}

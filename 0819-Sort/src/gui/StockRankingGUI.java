package gui;

import model.StockRecord;
import service.ExternalMergeSort;
import util.SortField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StockRankingGUI extends JFrame {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JFileChooser fileChooser = new JFileChooser();
    private final JButton sortButton = new JButton("Sort");
    private final JComboBox<SortField> sortFieldComboBox = new JComboBox<>(SortField.values());

    public StockRankingGUI() {
        setTitle("Stock Ranking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortFieldComboBox);
        topPanel.add(sortButton);

        // Table
        String[] columnNames = {"Stock Code", "Name", "Date", "Volume", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Layout
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        sortButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sort(selectedFile.getAbsolutePath());
            }
        });
    }

    private void sort(String filePath) {
        sortButton.setEnabled(false);
        tableModel.setRowCount(0); // Clear table

        new SwingWorker<List<StockRecord>, Void>() {
            @Override
            protected List<StockRecord> doInBackground() throws Exception {
                ExternalMergeSort sorter = new ExternalMergeSort();
                SortField selectedField = (SortField) sortFieldComboBox.getSelectedItem();

                // 添加進度回調參數
                String sortedFile = sorter.sort(filePath, selectedField, progress -> {
                    // 更新進度 (0-100%)
                    setProgress(progress);
                });

                return sorter.getTopN(sortedFile, 15);
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
                    JOptionPane.showMessageDialog(StockRankingGUI.this, "Error during sorting: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                sortButton.setEnabled(true);
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StockRankingGUI().setVisible(true);
        });
    }
}

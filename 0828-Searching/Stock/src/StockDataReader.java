import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockDataReader extends JFrame {

  private final JComboBox<File> csvFileComboBox;
  private final JButton selectDirButton;
  private final JTable dataTable;
  private final DefaultTableModel tableModel;
  private final JLabel selectedDirLabel;
  private File selectedDirectory;
  private final JTextField dateInputField;
  private final JButton searchDateButton;
  private Map<String, List<String[]>> dateDataMap = new HashMap<>();
  private String[] lastHeader = null;

  public StockDataReader() {
    // --- 視窗設定 ---
    setTitle("股票資料讀取器");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // --- GUI 元件初始化 ---
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    selectDirButton = new JButton("選擇資料夾");
    selectedDirLabel = new JLabel("尚未選擇資料夾");
    csvFileComboBox = new JComboBox<>();

    tableModel = new DefaultTableModel();
    dataTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(dataTable);

    dateInputField = new JTextField(10);
    searchDateButton = new JButton("查詢日期");

    // --- 將元件加入面板 ---
    topPanel.add(selectDirButton);
    topPanel.add(new JLabel("選擇CSV檔案:"));
    topPanel.add(csvFileComboBox);
    topPanel.add(selectedDirLabel);
    topPanel.add(new JLabel("輸入日期(yyyy/mm/dd):"));
    topPanel.add(dateInputField);
    topPanel.add(searchDateButton);

    // 將面板加入視窗主體
    getContentPane().add(topPanel, BorderLayout.NORTH);
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    // --- 事件監聽器設定 ---

    // 1. "選擇資料夾" 按鈕的行為
    selectDirButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooseDirectory();
      }
    });

    // 2. 下拉式選單的行為
    csvFileComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File selectedFile = (File) csvFileComboBox.getSelectedItem();
        if (selectedFile != null) {
          loadCsvData(selectedFile);
        }
      }
    });

    // 3. 日期查詢按鈕事件
    searchDateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String date = dateInputField.getText().trim();
        showDataByDate(date);
      }
    });

    // 讓下拉式選單只顯示檔名
    csvFileComboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File) {
          value = ((File) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });

    // 預設載入 ./data 資料夾
    File defaultDir = new File("./data");
    if (defaultDir.exists() && defaultDir.isDirectory()) {
        selectedDirectory = defaultDir;
        selectedDirLabel.setText("目前路徑: " + defaultDir.getAbsolutePath());
        findCsvFiles(defaultDir);
    }
  }

  /**
   * 開啟檔案選擇器讓使用者選擇資料夾，並找出所有 .csv 檔案
   */
  private void chooseDirectory() {
    JFileChooser fileChooser = new JFileChooser(new File("./data")); // 預設目錄改為 ./data
    fileChooser.setDialogTitle("請選擇包含CSV檔案的資料夾");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 設定為只能選資料夾

    int returnValue = fileChooser.showOpenDialog(null);
    if (returnValue == JFileChooser.APPROVE_OPTION) {
      selectedDirectory = fileChooser.getSelectedFile();
      selectedDirLabel.setText("目前路徑: " + selectedDirectory.getAbsolutePath());
      findCsvFiles(selectedDirectory);
    }
  }

  /**
   * 在指定資料夾中尋找 .csv 檔案並更新下拉式選單
   * @param directory 要搜尋的資料夾
   */
  private void findCsvFiles(File directory) {
    csvFileComboBox.removeAllItems(); // 清空舊的列表
    File[] files = directory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".csv");
      }
    });

    if (files != null && files.length > 0) {
      for (File file : files) {
        csvFileComboBox.addItem(file);
      }
    } else {
      JOptionPane.showMessageDialog(this, "在指定資料夾中找不到任何 .csv 檔案", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * 讀取指定的CSV檔案，並將其內容依日期分組存入HashMap
   */
  private void loadCsvData(File csvFile) {
    dateDataMap.clear();
    List<String[]> allData = new ArrayList<>();
    String[] header = null;

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
      String line;
      if ((line = reader.readLine()) != null) {
        header = line.split(",", -1);
        lastHeader = header;
      }
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          String[] rowData = line.split(",", -1);
          allData.add(rowData);
          // 假設日期在第0欄
          String dateKey = rowData[0];
          dateDataMap.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(rowData);
        }
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "讀取檔案時發生錯誤: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
      return;
    }

    // 預設顯示全部資料
    if (header != null) {
      tableModel.setRowCount(0);
      tableModel.setColumnCount(0);
      for(String colName : header) {
        tableModel.addColumn(colName);
      }
      for (String[] row : allData) {
        tableModel.addRow(row);
      }
    }
  }

  /**
   * 根據日期顯示該日所有資料
   */
  private void showDataByDate(String date) {
    if (lastHeader == null) return;
    List<String[]> rows = dateDataMap.get(date);
    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);
    for(String colName : lastHeader) {
      tableModel.addColumn(colName);
    }
    if (rows != null) {
      for (String[] row : rows) {
        tableModel.addRow(row);
      }
    } else {
      JOptionPane.showMessageDialog(this, "查無此日期資料", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * 程式主入口點
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new StockDataReader().setVisible(true);
      }
    });
  }
}
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
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
  private final JComboBox<String> dateComboBox; // 起始日期下拉選單
  private final JComboBox<String> rangeComboBox; // 區間結束日期下拉選單
  private Map<String, List<String[]>> dateDataMap = new Hashtable<>();
  private String[] lastHeader = null;
  private final JTextField startTimeField = new JTextField(5);
  private final JTextField endTimeField = new JTextField(5);
  private final JButton timeFilterButton = new JButton("時間篩選");

  // 新增：複數欄位篩選元件（只勾選，不提供填寫框）
  private final JCheckBox[] filterCheckBoxes = new JCheckBox[5];

  public StockDataReader() {
    setTitle("股票資料讀取器");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // --- GUI 元件初始化 ---
    selectDirButton = new JButton("選擇資料夾");
    selectedDirLabel = new JLabel("尚未選擇資料夾");
    csvFileComboBox = new JComboBox<>();
    tableModel = new DefaultTableModel();
    dataTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(dataTable);
    dateInputField = new JTextField(10);
    searchDateButton = new JButton("查詢日期");
    dateComboBox = new JComboBox<>();
    rangeComboBox = new JComboBox<>();

    // 垂直排列
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    dirPanel.add(selectDirButton);
    dirPanel.add(selectedDirLabel);

    JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    filePanel.add(new JLabel("選擇CSV檔案:"));
    filePanel.add(csvFileComboBox);

    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    datePanel.add(new JLabel("輸入日期(yyyy/mm/dd):"));
    datePanel.add(dateInputField);
    datePanel.add(searchDateButton);

    JPanel dateComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    dateComboPanel.add(new JLabel("選擇日期區間:"));
    dateComboPanel.add(dateComboBox);
    dateComboPanel.add(new JLabel("~"));
    dateComboPanel.add(rangeComboBox);

    // 時間篩選區塊
    JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    timePanel.add(new JLabel("開始時間(hh:mm):"));
    timePanel.add(startTimeField);
    timePanel.add(new JLabel("結束時間(hh:mm):"));
    timePanel.add(endTimeField);
    timePanel.add(timeFilterButton);

    // 欄位篩選區塊（動態顯示CSV欄位名稱，對應csv資料欄位）
    JPanel multiFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    multiFilterPanel.add(new JLabel("勾選欄位進行篩選:"));
    for (int i = 0; i < 5; i++) {
      filterCheckBoxes[i] = new JCheckBox("欄" + (i + 4));
      multiFilterPanel.add(filterCheckBoxes[i]);
      filterCheckBoxes[i].addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          triggerInstantMultiFilter();
        }
      });
    }

    // --- stack 顯示區塊 ---
    JPanel stackPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 設置y軸高度比例為 80%
        int panelHeight = getHeight();
        int axisHeight = (int)(panelHeight * 0.8);

        // 假設座標軸起點與終點 X/Y
        int axisX0 = 40;
        int axisY0 = 20;
        int axisX1 = getWidth() - 40;
        int axisY1 = axisY0 + axisHeight;

        // --- 座標軸（橫向）---
        // Y軸（左側）
        g2.drawLine(axisX0, axisY0, axisX0, axisY1);

        // ...可在此繪製X軸、刻度等...

        // 樣例圖例
        int legendX = axisX1 - 150, legendY = axisY0;
        g2.drawString("存在的鍵", legendX + 20, legendY + 10);
        g2.drawString("不存在的鍵", legendX + 20, legendY + 35);

        // ...stack 顯示相關繪圖...
      }
    };
    stackPanel.setPreferredSize(new Dimension(800, 60));

    mainPanel.add(dirPanel);
    mainPanel.add(filePanel);
    mainPanel.add(datePanel);
    mainPanel.add(dateComboPanel);
    mainPanel.add(timePanel);
    mainPanel.add(multiFilterPanel);
    mainPanel.add(stackPanel); // stackPanel 排列在最下方

    getContentPane().add(mainPanel, BorderLayout.NORTH);
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    // --- 事件監聽器設定 ---
    selectDirButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooseDirectory();
      }
    });

    csvFileComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File selectedFile = (File) csvFileComboBox.getSelectedItem();
        if (selectedFile != null) {
          loadCsvData(selectedFile);
        }
      }
    });

    searchDateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String date = dateInputField.getText().trim();
        showDataByDate(date);
        dateComboBox.setSelectedItem(date); // 同步下拉選單
      }
    });

    dateComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedDate = (String) dateComboBox.getSelectedItem();
        if (selectedDate != null) {
          dateInputField.setText(selectedDate); // 同步文字框
          // 更新區間下拉選單
          updateRangeComboBox(selectedDate);
          // 預設顯示單一天資料
          showDataByDate(selectedDate);
        }
      }
    });

    rangeComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String startDate = (String) dateComboBox.getSelectedItem();
        String endDate = (String) rangeComboBox.getSelectedItem();
        if (startDate != null && endDate != null) {
          showDataByDateRange(startDate, endDate);
        }
      }
    });

    timeFilterButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String startDate = (String) dateComboBox.getSelectedItem();
        String endDate = (String) rangeComboBox.getSelectedItem();
        String startTime = startTimeField.getText().trim();
        String endTime = endTimeField.getText().trim();
        if (startDate != null && endDate != null && !startTime.isEmpty() && !endTime.isEmpty()) {
          showDataByDateTimeRange(startDate, endDate, startTime, endTime);
        }
      }
    });

    csvFileComboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof File) {
          value = ((File) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });

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
   * 讀取指定的CSV檔案，並將其內容依日期分組存入Hashtable
   */
  private void loadCsvData(File csvFile) {
    dateDataMap.clear();
    List<String[]> allData = new ArrayList<>();
    String[] header = null;
    dateComboBox.removeAllItems();
    rangeComboBox.removeAllItems();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"))) {
      String line;
      if ((line = reader.readLine()) != null) {
        header = line.split(",", -1);
        lastHeader = header;
        // 動態更新欄位篩選checkbox文字，對應csv資料欄位名稱（第4~8欄）
        for (int i = 0; i < 5; i++) {
          if (header.length > i + 3) {
            filterCheckBoxes[i].setText(header[i + 3]);
          } else {
            filterCheckBoxes[i].setText("欄" + (i + 4));
          }
        }
      }
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          String[] rowData = line.split(",", -1);
          // 日期補齊格式 yyyy/mm/dd，來源為資料的 Date 欄位（CSV第2欄）
          String[] dateParts = rowData[1].split("/");
          if (dateParts.length == 3) {
            String yyyy = dateParts[0];
            String mm = dateParts[1].length() == 1 ? "0" + dateParts[1] : dateParts[1];
            String dd = dateParts[2].length() == 1 ? "0" + dateParts[2] : dateParts[2];
            rowData[1] = yyyy + "/" + mm + "/" + dd;
          }
          allData.add(rowData);
          String dateKey = rowData[1];
          if (!dateDataMap.containsKey(dateKey)) {
            dateComboBox.addItem(dateKey);
          }
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
    // 區間選單初始化
    updateRangeComboBox((String) dateComboBox.getSelectedItem());
  }

  // 更新區間下拉選單，只顯示起始日期之後的所有日期
  private void updateRangeComboBox(String startDate) {
    rangeComboBox.removeAllItems();
    List<String> sortedDates = new ArrayList<>(dateDataMap.keySet());
    sortedDates.sort(String::compareTo); // 日期字串排序
    boolean add = false;
    for (String date : sortedDates) {
      if (date.equals(startDate)) add = true;
      if (add) rangeComboBox.addItem(date);
    }
    // 預設選擇起始日
    if (rangeComboBox.getItemCount() > 0) {
      rangeComboBox.setSelectedIndex(0);
    }
  }

  // 顯示日期區間資料（含起始與結束日），依日期順序
  private void showDataByDateRange(String startDate, String endDate) {
    if (lastHeader == null) return;
    List<String> sortedDates = new ArrayList<>(dateDataMap.keySet());
    sortedDates.sort(String::compareTo);
    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);
    for(String colName : lastHeader) {
      tableModel.addColumn(colName);
    }
    boolean inRange = false;
    for (String date : sortedDates) {
      if (date.equals(startDate)) inRange = true;
      if (inRange) {
        List<String[]> rows = dateDataMap.get(date);
        if (rows != null) {
          for (String[] row : rows) {
            tableModel.addRow(row);
          }
        }
      }
      if (date.equals(endDate)) break;
    }
    if (tableModel.getRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "查無此區間資料", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // 顯示指定日期的所有資料（依 CSV 第2欄）
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
    }
//    else {
//      JOptionPane.showMessageDialog(this, "查無此日期資料", "提示", JOptionPane.INFORMATION_MESSAGE);
//    }
  }

  // 日期+時間區間篩選（時間格式精確到秒 hh:mm:ss）
  private void showDataByDateTimeRange(String startDate, String endDate, String startTime, String endTime) {
    if (lastHeader == null) return;
    List<String> sortedDates = new ArrayList<>(dateDataMap.keySet());
    sortedDates.sort(String::compareTo);
    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);
    for(String colName : lastHeader) {
      tableModel.addColumn(colName);
    }
    boolean inRange = false;
    for (String date : sortedDates) {
      if (date.compareTo(startDate) >= 0) inRange = true;
      if (inRange) {
        List<String[]> rows = dateDataMap.get(date);
        if (rows != null) {
          for (String[] row : rows) {
            // 時間在第3欄（rowData[2]），格式 hh:mm:ss 或 hh:mm
            String time = row[2];
            // 若時間格式為 hh:mm，補 :00
            if (time.matches("\\d{2}:\\d{2}")) {
              time = time + ":00";
            }
            // 同理，若輸入時間為 hh:mm，也補 :00
            String sTime = startTime.matches("\\d{2}:\\d{2}") ? startTime + ":00" : startTime;
            String eTime = endTime.matches("\\d{2}:\\d{2}") ? endTime + ":00" : endTime;
            if (time.compareTo(sTime) >= 0 && time.compareTo(eTime) <= 0) {
              tableModel.addRow(row);
            }
          }
        }
      }
      if (date.equals(endDate)) break;
    }
    if (tableModel.getRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "查無此日期與時間區間資料", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // 日期+時間+多欄位內容篩選
  private void showDataByDateTimeMultiFilter(
      String startDate, String endDate, String startTime, String endTime,
      boolean[] useCol, String[] colValue
  ) {
    if (lastHeader == null) return;
    List<String> sortedDates = new ArrayList<>(dateDataMap.keySet());
    sortedDates.sort(String::compareTo);
    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);
    for(String colName : lastHeader) {
      tableModel.addColumn(colName);
    }
    boolean inRange = false;
    for (String date : sortedDates) {
      if (date.compareTo(startDate) >= 0) inRange = true;
      if (inRange) {
        List<String[]> rows = dateDataMap.get(date);
        if (rows != null) {
          for (String[] row : rows) {
            String time = row[2];
            if (time.matches("\\d{2}:\\d{2}")) time = time + ":00";
            String sTime = startTime.matches("\\d{2}:\\d{2}") ? startTime + ":00" : startTime;
            String eTime = endTime.matches("\\d{2}:\\d{2}") ? endTime + ":00" : endTime;
            if (time.compareTo(sTime) >= 0 && time.compareTo(eTime) <= 0) {
              boolean match = true;
              for (int i = 0; i < 5; i++) {
                // 第4~8欄分別是 row[3]~row[7]
                if (useCol[i] && !colValue[i].isEmpty()) {
                  if (!row[i + 3].equals(colValue[i])) {
                    match = false;
                    break;
                  }
                }
              }
              if (match) {
                tableModel.addRow(row);
              }
            }
          }
        }
      }
      if (date.equals(endDate)) break;
    }
    if (tableModel.getRowCount() == 0) {
      JOptionPane.showMessageDialog(this, "查無此條件資料", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // 即時多欄位篩選（只依目前日期區間與勾選欄位，不考慮時間欄位與內容）
  private void triggerInstantMultiFilter() {
    String startDate = (String) dateComboBox.getSelectedItem();
    String endDate = (String) rangeComboBox.getSelectedItem();
    boolean[] useCol = new boolean[5];
    for (int i = 0; i < 5; i++) {
      useCol[i] = filterCheckBoxes[i].isSelected();
    }
    showDataByDateMultiColCheckOnly(startDate, endDate, useCol);
  }

  // 日期區間+多欄位即時篩選（只依勾選，不考慮內容與時間）
  private void showDataByDateMultiColCheckOnly(
      String startDate, String endDate, boolean[] useCol
  ) {
    if (lastHeader == null) return;
    List<String> sortedDates = new ArrayList<>(dateDataMap.keySet());
    sortedDates.sort(String::compareTo);

    // 判斷是否有勾選欄位
    boolean anyChecked = false;
    for (boolean b : useCol) {
      if (b) {
        anyChecked = true;
        break;
      }
    }

    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);

    if (!anyChecked) {
      // 沒有勾選，顯示所有欄位
      for (String colName : lastHeader) {
        tableModel.addColumn(colName);
      }
    } else {
      // 有勾選，保留第1,2,3欄位，並只顯示勾選的欄位（第4~8欄）
      for (int i = 0; i < 3; i++) {
        tableModel.addColumn(lastHeader[i]);
      }
      for (int i = 0; i < 5; i++) {
        if (useCol[i]) {
          tableModel.addColumn(lastHeader[i + 3]);
        }
      }
    }

    boolean inRange = false;
    for (String date : sortedDates) {
      if (date.compareTo(startDate) >= 0) inRange = true;
      if (inRange) {
        List<String[]> rows = dateDataMap.get(date);
        if (rows != null) {
          for (String[] row : rows) {
            boolean match = true;
            if (anyChecked) {
              // 只顯示勾選欄位且該欄位有值
              for (int i = 0; i < 5; i++) {
                if (useCol[i] && (row[i + 3] == null || row[i + 3].trim().isEmpty())) {
                  match = false;
                  break;
                }
              }
            }
            if (match) {
              if (!anyChecked) {
                // 沒有勾選，顯示所有欄位
                tableModel.addRow(row);
              } else {
                // 有勾選，保留第1,2,3欄位，並只顯示勾選的欄位
                List<String> filteredRow = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                  filteredRow.add(row[i]);
                }
                for (int i = 0; i < 5; i++) {
                  if (useCol[i]) {
                    filteredRow.add(row[i + 3]);
                  }
                }
                tableModel.addRow(filteredRow.toArray(new String[0]));
              }
            }
          }
        }
      }
      if (date.equals(endDate)) break;
    }
    // 不顯示提示，保持即時互動
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

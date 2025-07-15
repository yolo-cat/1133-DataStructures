import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ArrayCourseTable2 extends JFrame {
  // 課程名稱與編號對照表
  private static final Map<String, Integer> COURSE_MAP = new HashMap<>();
  private static final Map<Integer, String> REVERSE_COURSE_MAP = new HashMap<>();

  static {
    COURSE_MAP.put("計算機概論", 1);
    COURSE_MAP.put("離散數學", 2);
    COURSE_MAP.put("資料結構", 3);
    COURSE_MAP.put("資料庫理論", 4);
    COURSE_MAP.put("上機實習", 5);

    // 反向對照表
    REVERSE_COURSE_MAP.put(1, "計算機概論");
    REVERSE_COURSE_MAP.put(2, "離散數學");
    REVERSE_COURSE_MAP.put(3, "資料結構");
    REVERSE_COURSE_MAP.put(4, "資料庫理論");
    REVERSE_COURSE_MAP.put(5, "上機實習");
  }

  private JTable timetable;
  private DefaultTableModel tableModel;
  private int[][] numberArray; // 儲存轉換後的數字陣列
  private JTextArea referenceArea;

  public ArrayCourseTable2() {
    initializeGUI();
    numberArray = new int[6][5]; // 6節課 x 5天
  }

  private void initializeGUI() {
    setTitle("課表程式");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // 建立課表
    createTimetable();

    // 建立課程列表
    JPanel coursePanel = createCoursePanel();

    // 建立按鈕面板
    JPanel buttonPanel = createButtonPanel();

    // 建立參考資訊面板
    JPanel referencePanel = createReferencePanel();

    // 組合介面
    add(new JScrollPane(timetable), BorderLayout.CENTER);
    add(coursePanel, BorderLayout.WEST);
    add(buttonPanel, BorderLayout.SOUTH);
    add(referencePanel, BorderLayout.EAST);

    pack();
    setLocationRelativeTo(null);
  }

  private void createTimetable() {
    String[] columns = {"節次", "一", "二", "三", "四", "五"};
    Object[][] data = {
        {"1", "", "", "", "", ""},
        {"2", "", "", "", "", ""},
        {"3", "", "", "", "", ""},
        {"4", "", "", "", "", ""},
        {"5", "", "", "", "", ""},
        {"6", "", "", "", "", ""}
    };

    tableModel = new DefaultTableModel(data, columns) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column != 0; // 第一列不可編輯
      }
    };

    timetable = new JTable(tableModel);
    timetable.setRowHeight(40);
    timetable.setFont(new Font("微軟正黑體", Font.PLAIN, 12));

    // 設定拖放功能
    timetable.setDropTarget(new DropTarget() {
      @Override
      public synchronized void drop(DropTargetDropEvent evt) {
        try {
          evt.acceptDrop(DnDConstants.ACTION_COPY);
          String courseName = (String) evt.getTransferable().getTransferData(DataFlavor.stringFlavor);

          Point point = evt.getLocation();
          int row = timetable.rowAtPoint(point);
          int col = timetable.columnAtPoint(point);

          if (row >= 0 && col > 0) { // 確保不是第一列
            tableModel.setValueAt(courseName, row, col);
          }

          evt.dropComplete(true);
        } catch (Exception e) {
          e.printStackTrace();
          evt.dropComplete(false);
        }
      }
    });
  }

  private JPanel createCoursePanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder("課程列表"));

    String[] courses = {"計算機概論", "離散數學", "資料結構", "資料庫理論", "上機實習"};

    for (String course : courses) {
      JLabel label = new JLabel(course);
      label.setOpaque(true);
      label.setBackground(Color.LIGHT_GRAY);
      label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      label.setHorizontalAlignment(SwingConstants.CENTER);

      // 設定拖曳功能
      label.setTransferHandler(new TransferHandler() {
        @Override
        protected Transferable createTransferable(JComponent c) {
          return new StringSelection(((JLabel) c).getText());
        }

        @Override
        public int getSourceActions(JComponent c) {
          return COPY;
        }
      });

      label.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
          JLabel source = (JLabel) e.getSource();
          source.getTransferHandler().exportAsDrag(source, e, TransferHandler.COPY);
        }
      });

      panel.add(label);
      panel.add(Box.createVerticalStrut(5));
    }

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout());

    JButton convertButton = new JButton("轉換");
    JButton saveButton = new JButton("保存");

    convertButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        convertTableToNumbers();
      }
    });

    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveToConsole();
      }
    });

    panel.add(convertButton);
    panel.add(saveButton);

    return panel;
  }

  private JPanel createReferencePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("課程編號對照"));

    referenceArea = new JTextArea(10, 15);
    referenceArea.setEditable(false);
    referenceArea.setFont(new Font("微軟正黑體", Font.PLAIN, 12));

    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Integer, String> entry : REVERSE_COURSE_MAP.entrySet()) {
      sb.append(entry.getKey()).append(". ").append(entry.getValue()).append("\n");
    }
    referenceArea.setText(sb.toString());

    panel.add(new JScrollPane(referenceArea), BorderLayout.CENTER);

    return panel;
  }

  private void convertTableToNumbers() {
    System.out.println("=== 轉換開始 ===");

    for (int row = 0; row < 6; row++) {
      for (int col = 1; col <= 5; col++) {
        Object cellValue = tableModel.getValueAt(row, col);
        String cellText = cellValue != null ? cellValue.toString().trim() : "";

        if (!cellText.isEmpty()) {
          // 檢查是否為課程名稱
          if (COURSE_MAP.containsKey(cellText)) {
            numberArray[row][col-1] = COURSE_MAP.get(cellText);
            System.out.println("轉換: " + cellText + " -> " + COURSE_MAP.get(cellText) +
                " (位置: " + (row+1) + "節, 星期" + getWeekday(col-1) + ")");
          } else {
            // 檢查是否為數字且在有效範圍內
            try {
              int num = Integer.parseInt(cellText);
              if (REVERSE_COURSE_MAP.containsKey(num)) {
                numberArray[row][col-1] = num;
                System.out.println("保留數字: " + num +
                    " (位置: " + (row+1) + "節, 星期" + getWeekday(col-1) + ")");
              } else {
                numberArray[row][col-1] = 0; // 無效數字設為0
                System.out.println("忽略無效數字: " + cellText +
                    " (位置: " + (row+1) + "節, 星期" + getWeekday(col-1) + ")");
              }
            } catch (NumberFormatException e) {
              numberArray[row][col-1] = 0; // 非數字設為0
              System.out.println("忽略非數字內容: " + cellText +
                  " (位置: " + (row+1) + "節, 星期" + getWeekday(col-1) + ")");
            }
          }
        } else {
          numberArray[row][col-1] = 0; // 空白設為0
        }
      }
    }

    System.out.println("=== 轉換完成 ===");
    JOptionPane.showMessageDialog(this, "轉換完成！請查看控制台輸出。");
  }

  private void saveToConsole() {
    System.out.println("\n=== 課表保存 ===");
    System.out.println("課表內容（編號形式）：");

    // 輸出表頭
    System.out.print("節次\t一\t二\t三\t四\t五\n");

    // 輸出課表內容
    for (int row = 0; row < 6; row++) {
      System.out.print((row + 1) + "\t");
      for (int col = 0; col < 5; col++) {
        System.out.print(numberArray[row][col] + "\t");
      }
      System.out.println();
    }

    System.out.println("=== 保存完成 ===\n");
    JOptionPane.showMessageDialog(this, "課表已保存到控制台！");
  }

  private String getWeekday(int index) {
    String[] weekdays = {"一", "二", "三", "四", "五"};
    return weekdays[index];
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
          e.printStackTrace();
        }

        new ArrayCourseTable2().setVisible(true);
      }
    });
  }
}

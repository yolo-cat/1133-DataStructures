import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SearchGUIV2 {
  // --- GUI 元件 ---
  private JFrame frame;
  private JButton startButton;
  private JButton generateButton;
  private JLabel statusLabel;
  private JProgressBar progressBar;
  private BarChartPanel chartPanel;

  // --- 資料與邏輯 ---
  private static final int NUM_SEARCHES = 10;
  private List<Transaction> transactions;

  // 現代化配色方案
  private static class ModernColors {
    static final Color PRIMARY = new Color(63, 81, 181);      // 靛藍色
    static final Color PRIMARY_DARK = new Color(48, 63, 159);
    static final Color ACCENT = new Color(255, 64, 129);      // 粉紅色
    static final Color BACKGROUND = new Color(250, 250, 250); // 淺灰背景
    static final Color CARD_BACKGROUND = Color.WHITE;
    static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    static final Color SUCCESS = new Color(76, 175, 80);      // 綠色
    static final Color WARNING = new Color(255, 152, 0);      // 橙色
    static final Color CHART_EXISTING = new Color(33, 150, 243);  // 藍色
    static final Color CHART_NON_EXISTING = new Color(244, 67, 54); // 紅色
  }

  // 狀態類型枚舉
  private enum StatusType { SUCCESS, ERROR, WARNING, INFO }

  // 定義一個內部 record 來簡化資料傳遞
  record Transaction(String key, String date, String customerId, String itemName, double price)
      implements Comparable<Transaction> {
    @Override
    public int compareTo(Transaction other) {
      return this.key.compareTo(other.key);
    }
  }

  // 自訂的長條圖面板類別
  private static class BarChartPanel extends JPanel {
    private Map<String, Long> existingKeyResults;
    private Map<String, Long> nonExistingKeyResults;
    private final Font FONT_SMALL = new Font("微軟正黑體", Font.PLAIN, 11);
    private final Font FONT_MEDIUM = new Font("微軟正黑體", Font.BOLD, 12);
    private final Font FONT_LARGE = new Font("微軟正黑體", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("微軟正黑體", Font.BOLD, 16);

    public void setResults(Map<String, Long> existing, Map<String, Long> nonExisting) {
      this.existingKeyResults = existing;
      this.nonExistingKeyResults = nonExisting;
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      // 取得 panel 的實際邊界
      Insets insets = getInsets();
      int paddingLeft = insets.left;
      int paddingRight = insets.right;
      int paddingTop = insets.top;
      int paddingBottom = insets.bottom;

      // 繪製漸變背景
      GradientPaint gradient = new GradientPaint(0, 0, new Color(245, 247, 250),
          0, getHeight(), Color.WHITE);
      g2.setPaint(gradient);
      g2.fillRect(0, 0, getWidth(), getHeight());

      // 移除主圖表區域邊框（drawRect）
      // g2.setColor(new Color(180, 180, 180));
      // g2.setStroke(new BasicStroke(2));
      // g2.drawRect(chartAreaX, chartAreaY, chartAreaWidth, chartAreaHeight);

      if (existingKeyResults == null || nonExistingKeyResults == null) {
        drawWelcomeMessage(g2);
        return;
      }

      // --- 長條圖（橫向）---
      int chartAreaX = paddingLeft + 120; // 左側預留演算法名稱空間
      int chartAreaY = paddingTop + 70;  // 上方預留標題空間
      int chartAreaWidth = getWidth() - paddingLeft - paddingRight - 220; // 右側預留空間
      int chartAreaHeight = getHeight() - paddingTop - paddingBottom - 120; // 上下預留空間

      int barHeight = 24;
      int groupGap = 32;
      int barGap = 8;
      String[] algoNames = {"線性搜尋", "二分搜尋", "雜湊搜尋"};
      int barCount = algoNames.length;
      int totalGroups = barCount;
      int totalBars = totalGroups * 2;
      int maxBarLength = chartAreaWidth - 60; // 右側預留數值空間

      // --- 座標軸（橫向）---
      g2.setColor(new Color(200, 200, 200));
      g2.setStroke(new BasicStroke(2));
      // X軸（數值軸）
      int axisX0 = chartAreaX;
      int axisX1 = chartAreaX + maxBarLength;
      // Y軸起點和終點要比長條圖高度多10%
      int chartHeight = totalGroups * (barHeight * 2 + groupGap) - groupGap;
      int axisY0 = chartAreaY - (int)(chartHeight * 0.1);
      int axisY1 = chartAreaY + chartHeight + (int)(chartHeight * 0.2);
      g2.drawLine(axisX0, axisY0, axisX1, axisY0); // 上方X軸
      g2.drawLine(axisX0, axisY1, axisX1, axisY1); // 下方X軸
      // Y軸（左側）
      g2.drawLine(axisX0, axisY0, axisX0, axisY1);

      // --- 橫向長條圖繪製 ---
      long maxTime = 1;
      for (long time : existingKeyResults.values()) {
        if (time > maxTime) maxTime = time;
      }
      for (long time : nonExistingKeyResults.values()) {
        if (time > maxTime) maxTime = time;
      }
      long visualMax = (long) Math.log10(maxTime);
      if (visualMax == 0) visualMax = 1;
      double scale = maxBarLength / (double) visualMax;

      int currentY = chartAreaY;
      for (String algo : algoNames) {
        long existingTime = existingKeyResults.get(algo);
        long nonExistingTime = nonExistingKeyResults.get(algo);
        double scaledExisting = (existingTime > 0) ? Math.log10(existingTime) : 0;
        double scaledNonExisting = (nonExistingTime > 0) ? Math.log10(nonExistingTime) : 0;
        int existingBarLength = (int) (scaledExisting * scale);
        int nonExistingBarLength = (int) (scaledNonExisting * scale);
        // 統一演算法名稱文字格式
        g2.setFont(FONT_LARGE);
        g2.setColor(ModernColors.TEXT_PRIMARY);
        g2.drawString(algo, paddingLeft + 10, currentY + barHeight + 2);
        // "存在" 長條
        g2.setColor(new Color(50, 150, 250));
        g2.fillRect(axisX0, currentY, existingBarLength, barHeight);
        g2.setColor(Color.BLACK);
        g2.setFont(FONT_SMALL);
        g2.drawString(existingTime + " ns", axisX0 + existingBarLength + 8, currentY + barHeight - 6);
        currentY += barHeight + barGap;
        // "不存在" 長條
        g2.setColor(new Color(250, 100, 100));
        g2.fillRect(axisX0, currentY, nonExistingBarLength, barHeight);
        g2.setColor(Color.BLACK);
        g2.drawString(nonExistingTime + " ns", axisX0 + nonExistingBarLength + 8, currentY + barHeight - 6);
        currentY += barHeight + groupGap;
      }

      // --- 圖例 ---
      int legendX = axisX1 + 100;
      int legendY = chartAreaY + 100;
      g2.setColor(new Color(255, 255, 255, 200));
      g2.fillRoundRect(legendX - 10, legendY - 10, 140, 60, 10, 10);
      g2.setFont(FONT_SMALL);
      g2.setColor(ModernColors.CHART_EXISTING);
      g2.fillRoundRect(legendX, legendY, 12, 12, 4, 4);
      g2.setColor(ModernColors.TEXT_PRIMARY);
      g2.drawString("存在的鍵", legendX + 20, legendY + 10);
      g2.setColor(ModernColors.CHART_NON_EXISTING);
      g2.fillRoundRect(legendX, legendY + 25, 12, 12, 4, 4);
      g2.setColor(ModernColors.TEXT_PRIMARY);
      g2.drawString("不存在的鍵", legendX + 20, legendY + 35);

      // --- 標題 ---
      g2.setFont(FONT_TITLE);
      g2.setColor(ModernColors.PRIMARY);
      String title = "搜尋演算法效能比較結果";
      FontMetrics fm = g2.getFontMetrics();
      int titleX = (getWidth() - fm.stringWidth(title)) / 2;
      g2.drawString(title, titleX, paddingTop + 20);
    }

    private void drawWelcomeMessage(Graphics2D g2) {
      g2.setColor(ModernColors.TEXT_SECONDARY);
      g2.setFont(FONT_LARGE);

      String message = "🚀 請點擊「開始比較」以執行效能測試";
      FontMetrics fm = g2.getFontMetrics();
      int x = (getWidth() - fm.stringWidth(message)) / 2;
      int y = getHeight() / 2;

      g2.drawString(message, x, y);

      // 繪製提示文字
      g2.setFont(FONT_SMALL);
      g2.setColor(ModernColors.TEXT_SECONDARY);
      String hint = "分析線性搜尋、二分搜尋與雜湊搜尋的效能差異";
      fm = g2.getFontMetrics();
      x = (getWidth() - fm.stringWidth(hint)) / 2;
      g2.drawString(hint, x, y + 30);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // 設置字體
        setUIFont(new Font("微軟正黑體", Font.PLAIN, 12));
      } catch (Exception e) {
        e.printStackTrace();
      }
      new SearchGUIV2().createAndShowGui();
    });
  }

  private static void setUIFont(Font font) {
    java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof Font) {
        UIManager.put(key, font);
      }
    }
  }

  private void createAndShowGui() {
    frame = new JFrame("🔍 搜尋演算法效能比較分析器");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setBackground(ModernColors.BACKGROUND);

    // 創建主要內容面板
    JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
    mainPanel.setBackground(ModernColors.BACKGROUND);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // 標題面板
    JPanel titlePanel = createTitlePanel();

    // 控制面板
    JPanel controlPanel = createControlPanel();

    // 圖表面板
    chartPanel = new BarChartPanel();
    chartPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(20, 20, 20, 20)
    ));
    chartPanel.setBackground(ModernColors.CARD_BACKGROUND);
    chartPanel.setPreferredSize(new Dimension(0, 400)); // 讓高度固定，寬度自適應
    chartPanel.setMinimumSize(new Dimension(0, 200));   // 最小高度
    chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 800)); // 最大高度，寬度不限

    mainPanel.add(titlePanel, BorderLayout.NORTH);
    mainPanel.add(controlPanel, BorderLayout.CENTER);
    mainPanel.add(chartPanel, BorderLayout.SOUTH);

    frame.add(mainPanel);
    frame.setVisible(true);
  }

  private JPanel createTitlePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(ModernColors.BACKGROUND);

    JLabel titleLabel = new JLabel("搜尋演算法效能分析器", SwingConstants.CENTER);
    titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 28));
    titleLabel.setForeground(ModernColors.PRIMARY);

    JLabel subtitleLabel = new JLabel("比較線性搜尋、二分搜尋與雜湊搜尋的執行效率", SwingConstants.CENTER);
    subtitleLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
    subtitleLabel.setForeground(ModernColors.TEXT_SECONDARY);

    panel.add(titleLabel, BorderLayout.CENTER);
    panel.add(subtitleLabel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createControlPanel() {
    JPanel outerPanel = new JPanel(new BorderLayout());
    outerPanel.setBackground(ModernColors.BACKGROUND);

    // 控制卡片
    JPanel cardPanel = new JPanel();
    cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
    cardPanel.setBackground(ModernColors.CARD_BACKGROUND);
    cardPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
        BorderFactory.createEmptyBorder(20, 30, 20, 30)
    ));

    // 按鈕面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    buttonPanel.setBackground(ModernColors.CARD_BACKGROUND);

    generateButton = createStyledButton("📊 產生測試數據", ModernColors.WARNING);
    startButton = createStyledButton("🚀 開始效能測試", ModernColors.PRIMARY);

    buttonPanel.add(generateButton);
    buttonPanel.add(startButton);

    // 狀態面板
    JPanel statusPanel = new JPanel(new BorderLayout(10, 10));
    statusPanel.setBackground(ModernColors.CARD_BACKGROUND);

    statusLabel = new JLabel("ℹ️ 請先產生測試數據，然後開始效能比較測試");
    statusLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 13));
    statusLabel.setForeground(ModernColors.TEXT_SECONDARY);
    statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

    progressBar = createStyledProgressBar();
    progressBar.setVisible(false);

    statusPanel.add(statusLabel, BorderLayout.CENTER);
    statusPanel.add(progressBar, BorderLayout.SOUTH);

    cardPanel.add(buttonPanel);
    cardPanel.add(Box.createVerticalStrut(15));
    cardPanel.add(statusPanel);

    outerPanel.add(cardPanel, BorderLayout.CENTER);

    // 事件監聽
    generateButton.addActionListener(e -> runGenerateData());
    startButton.addActionListener(e -> runComparison());

    return outerPanel;
  }

  private JButton createStyledButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setBackground(bgColor);
    button.setForeground(Color.BLACK); // 文字顏色改為黑色
    button.setFont(new Font("微軟正黑體", Font.BOLD, 14));
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(160, 45));

    // 添加懸停效果
    button.addMouseListener(new MouseAdapter() {
      private Color originalColor = bgColor;

      @Override
      public void mouseEntered(MouseEvent e) {
        if (button.isEnabled()) {
          button.setBackground(originalColor.darker());
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (button.isEnabled()) {
          button.setBackground(originalColor);
        }
      }
    });

    return button;
  }

  private JProgressBar createStyledProgressBar() {
    JProgressBar progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setBackground(new Color(240, 240, 240));
    progressBar.setForeground(ModernColors.PRIMARY);
    progressBar.setBorderPainted(false);
    progressBar.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
    progressBar.setPreferredSize(new Dimension(0, 20));
    return progressBar;
  }

  private void updateStatus(String message, StatusType type) {
    String icon = switch(type) {
      case SUCCESS -> "✅ ";
      case ERROR -> "❌ ";
      case WARNING -> "⚠️ ";
      case INFO -> "ℹ️ ";
    };

    statusLabel.setText(icon + message);
    statusLabel.setForeground(switch(type) {
      case SUCCESS -> ModernColors.SUCCESS;
      case ERROR -> ModernColors.ACCENT;
      case WARNING -> ModernColors.WARNING;
      default -> ModernColors.TEXT_SECONDARY;
    });
  }

  private void runGenerateData() {
    generateButton.setEnabled(false);
    startButton.setEnabled(false);
    progressBar.setVisible(true);
    progressBar.setIndeterminate(true);
    updateStatus("正在產生交易數據，請稍候...", StatusType.INFO);

    SwingWorker<Void, String> worker = new SwingWorker<>() {
      @Override
      protected Void doInBackground() {
        try {
          publish("正在執行數據產生...");
          // 呼叫產生交易數據
          GenerateTransactionData.main(new String[0]);
          publish("交易數據產生完成！");
        } catch (Exception ex) {
          publish("產生數據時發生錯誤: " + ex.getMessage());
        }
        return null;
      }

      @Override
      protected void process(List<String> chunks) {
        if (!chunks.isEmpty()) {
          String lastMessage = chunks.get(chunks.size() - 1);
          if (lastMessage.contains("完成")) {
            updateStatus(lastMessage, StatusType.SUCCESS);
          } else if (lastMessage.contains("錯誤")) {
            updateStatus(lastMessage, StatusType.ERROR);
          } else {
            updateStatus(lastMessage, StatusType.INFO);
          }
        }
      }

      @Override
      protected void done() {
        generateButton.setEnabled(true);
        startButton.setEnabled(true);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
      }
    };
    worker.execute();
  }

  private void runComparison() {
    startButton.setEnabled(false);
    generateButton.setEnabled(false);
    progressBar.setVisible(true);
    progressBar.setIndeterminate(false);
    progressBar.setValue(0);

    SwingWorker<Map<String, Map<String, Long>>, String> worker = new SwingWorker<>() {
      @Override
      protected Map<String, Map<String, Long>> doInBackground() throws Exception {
        // 1. 載入資料
        publish("0:正在從 transactions.csv 載入資料...");
        transactions = loadTransactions("transactions.csv");
        if (transactions.isEmpty()) {
          throw new IOException("無法載入資料，請確認 transactions.csv 存在且格式正確。");
        }

        // 2. 準備資料結構
        publish("20:正在準備搜尋所需的資料結構...");
        List<Transaction> binarySearchList = new ArrayList<>(transactions);
        Collections.sort(binarySearchList);

        Map<String, Transaction> hashMap = new HashMap<>();
        for (Transaction t : transactions) {
          hashMap.put(t.key(), t);
        }

        // 3. 準備搜尋用的 KEY
        publish("40:正在準備搜尋測試鍵值...");
        List<String> existingKeys = new ArrayList<>();
        List<String> nonExistingKeys = new ArrayList<>();
        prepareSearchKeys(transactions, existingKeys, nonExistingKeys);

        Map<String, Long> existingResults = new LinkedHashMap<>();
        Map<String, Long> nonExistingResults = new LinkedHashMap<>();

        // 4. 執行並計時
        publish("60:正在執行搜尋測試 (存在的鍵)...");
        existingResults.put("線性搜尋", measureSearchTime(key -> linearSearch(transactions, key), existingKeys));
        existingResults.put("二分搜尋", measureSearchTime(key -> binarySearch(binarySearchList, key), existingKeys));
        existingResults.put("雜湊搜尋", measureSearchTime(key -> hashSearch(hashMap, key), existingKeys));

        publish("80:正在執行搜尋測試 (不存在的鍵)...");
        nonExistingResults.put("線性搜尋", measureSearchTime(key -> linearSearch(transactions, key), nonExistingKeys));
        nonExistingResults.put("二分搜尋", measureSearchTime(key -> binarySearch(binarySearchList, key), nonExistingKeys));
        nonExistingResults.put("雜湊搜尋", measureSearchTime(key -> hashSearch(hashMap, key), nonExistingKeys));

        publish("100:分析完成！");

        Map<String, Map<String, Long>> finalResults = new HashMap<>();
        finalResults.put("existing", existingResults);
        finalResults.put("non_existing", nonExistingResults);
        return finalResults;
      }

      @Override
      protected void process(List<String> chunks) {
        if (!chunks.isEmpty()) {
          String lastMessage = chunks.get(chunks.size() - 1);
          String[] parts = lastMessage.split(":", 2);

          if (parts.length == 2) {
            int progress = Integer.parseInt(parts[0]);
            progressBar.setValue(progress);
            updateStatus(parts[1], StatusType.INFO);
          } else {
            updateStatus(lastMessage, lastMessage.contains("完成") ? StatusType.SUCCESS : StatusType.INFO);
          }
        }
      }

      @Override
      protected void done() {
        try {
          Map<String, Map<String, Long>> results = get();
          chartPanel.setResults(results.get("existing"), results.get("non_existing"));
          updateStatus("效能比較測試完成！請查看下方圖表結果。", StatusType.SUCCESS);
        } catch (Exception e) {
          String errorMsg = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
          updateStatus("發生錯誤: " + errorMsg, StatusType.ERROR);
          JOptionPane.showMessageDialog(frame, errorMsg, "錯誤", JOptionPane.ERROR_MESSAGE);
        } finally {
          startButton.setEnabled(true);
          generateButton.setEnabled(true);
          progressBar.setVisible(false);
        }
      }
    };
    worker.execute();
  }

  // 將計時邏輯封裝成一個方法
  private long measureSearchTime(java.util.function.Function<String, Boolean> searchFunction, List<String> keys) {
    long totalTime = 0;
    for (String key : keys) {
      long startTime = System.nanoTime();
      searchFunction.apply(key);
      totalTime += System.nanoTime() - startTime;
    }
    return totalTime / NUM_SEARCHES;
  }

  // --- 輔助方法 ---
  private static List<Transaction> loadTransactions(String filePath) throws IOException {
    List<Transaction> transactions = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      br.readLine(); // 跳過標頭
      String line;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        if (values.length == 5) {
          transactions.add(new Transaction(values[0], values[1], values[2], values[3],
              Double.parseDouble(values[4])));
        }
      }
    }
    return transactions;
  }

  private static void prepareSearchKeys(List<Transaction> data, List<String> existing, List<String> nonExisting) {
    Random rand = new Random();
    Set<String> existingKeySet = new HashSet<>();
    for(Transaction t : data) existingKeySet.add(t.key());

    for (int i = 0; i < NUM_SEARCHES; i++) {
      existing.add(data.get(rand.nextInt(data.size())).key());
    }

    while (nonExisting.size() < NUM_SEARCHES) {
      String randomKey = "TX-" + generateRandomAlphanumeric(10);
      if (!existingKeySet.contains(randomKey)) {
        nonExisting.add(randomKey);
      }
    }
  }

  private static boolean linearSearch(List<Transaction> list, String key) {
    for (Transaction t : list) if (t.key().equals(key)) return true;
    return false;
  }

  private static boolean binarySearch(List<Transaction> sortedList, String key) {
    return Collections.binarySearch(sortedList, new Transaction(key, null, null, null, 0.0)) >= 0;
  }

  private static boolean hashSearch(Map<String, Transaction> map, String key) {
    return map.containsKey(key);
  }

  private static String generateRandomAlphanumeric(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder(length);
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }
}

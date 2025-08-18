import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class StockRankGUI extends JFrame {
    private JTextField stockCodeField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField kField;
    private JButton searchButton;
    private JTextArea resultArea;
    private JLabel timeLabel;
    private JLabel marketStatusLabel;
    private javax.swing.Timer clockTimer;

    // 排序方式選擇按鈕
    private JRadioButton sortByAmountButton;
    private JRadioButton sortByVolumeButton;

    // 股票終端機色彩主題
    private static final Color TERMINAL_BLACK = new Color(0, 0, 0);
    private static final Color TERMINAL_GREEN = new Color(0, 255, 0);
    private static final Color TERMINAL_AMBER = new Color(255, 191, 0);
    private static final Color TERMINAL_RED = new Color(255, 0, 0);
    private static final Color TERMINAL_CYAN = new Color(0, 255, 255);
    private static final Color TERMINAL_GRAY = new Color(64, 64, 64);
    private static final Color TERMINAL_DARK_GREEN = new Color(0, 128, 0);

    public StockRankGUI() {
        setTitle("股票交易終端機 - Bloomberg Terminal Style v2.0");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        // 設置黑色終端機背景
        getContentPane().setBackground(TERMINAL_BLACK);
        setLayout(new BorderLayout(5, 5));

        // 🔄 啟動時自動生成測試資料
        generateTestDataOnStartup();

        // 創建頂部標題和時鐘面板
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 创建输入面板
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.WEST);

        // 创建结果显示面板
        JPanel resultPanel = createResultPanel();
        add(resultPanel, BorderLayout.CENTER);

        // 创建状态栏
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        // 居中顯示窗口
        setLocationRelativeTo(null);

        // 添加事件監聽器
        searchButton.addActionListener(e -> performSearch());

        // 啟動時鐘
        startClock();

        // 自動載入並設置日期範圍
        loadDateRange();
    }

    /**
     * 啟動時自動生成測試資料
     */
    private void generateTestDataOnStartup() {
        System.out.println("🚀 正在生成測試資料...");

        // 確保 src 目錄存在
        java.io.File srcDir = new java.io.File("src");
        if (!srcDir.exists()) {
            srcDir.mkdirs();
            System.out.println("📁 創建 src 目錄");
        }

        boolean success = TestDataGenerator.generateTestData("src/testdata.csv");
        if (success) {
            System.out.println("✅ 測試資料生成完成，位於：src/testdata.csv");
        } else {
            System.err.println("❌ 測試資料生成失敗！");
        }
    }

    private void loadDateRange() {
        // 在背景執行緒中載入日期範圍，避免阻塞 UI
        SwingUtilities.invokeLater(() -> {
            String[] dateRange = getDateRangeFromCSV();
            if (dateRange != null) {
                startDateField.setText(dateRange[0]);
                endDateField.setText(dateRange[1]);
                startDateField.setToolTipText("資料最早日期: " + dateRange[0]);
                endDateField.setToolTipText("資料最晚日期: " + dateRange[1]);
            }
        });
    }

    private String[] getDateRangeFromCSV() {
        String minDate = null;
        String maxDate = null;

        try (BufferedReader br = new BufferedReader(new FileReader("src/testdata.csv"))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String date = parts[1];
                    if (minDate == null || date.compareTo(minDate) < 0) {
                        minDate = date;
                    }
                    if (maxDate == null || date.compareTo(maxDate) > 0) {
                        maxDate = date;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("無法載入日期範圍: " + e.getMessage());
            // 如果無法讀取檔案，使用預設範圍
            return new String[]{"2023-01-01", "2024-03-08"};
        }

        return new String[]{minDate, maxDate};
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(TERMINAL_BLACK);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // 左側：系統標題
        JLabel titleLabel = new JLabel("█ STOCK TRADING TERMINAL █");
        titleLabel.setFont(new Font("Courier New", Font.BOLD, 16));
        titleLabel.setForeground(TERMINAL_CYAN);

        // 中間：市場狀態
        marketStatusLabel = new JLabel("Market: OPEN");
        marketStatusLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        marketStatusLabel.setForeground(TERMINAL_GREEN);
        marketStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 右側：時間顯示
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        timeLabel.setForeground(TERMINAL_AMBER);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(marketStatusLabel, BorderLayout.CENTER);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private void startClock() {
        clockTimer = new javax.swing.Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            timeLabel.setText(timeStr);

            // 模擬市場狀態
            int hour = now.getHour();
            if (hour >= 9 && hour < 14) {
                marketStatusLabel.setText("Market: OPEN");
                marketStatusLabel.setForeground(TERMINAL_GREEN);
            } else {
                marketStatusLabel.setText("Market: CLOSED");
                marketStatusLabel.setForeground(TERMINAL_RED);
            }
        });
        clockTimer.start();

        // 立即更新一次時間
        clockTimer.getActionListeners()[0].actionPerformed(null);
    }

    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(TERMINAL_BLACK);
        mainPanel.setLayout(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // 创建分组框
        JPanel inputGroup = new JPanel();
        inputGroup.setBackground(TERMINAL_BLACK);
        Border etchedBorder = BorderFactory.createEtchedBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder(etchedBorder, "查詢條件");
        titledBorder.setTitleFont(new Font("MS Sans Serif", Font.BOLD, 11));
        titledBorder.setTitleColor(TERMINAL_GREEN);
        inputGroup.setBorder(titledBorder);

        // 使用 GridBagLayout 获得更好的控制
        inputGroup.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 5);

        // 添加输入字段
        Font labelFont = new Font("MS Sans Serif", Font.PLAIN, 11);
        Font fieldFont = new Font("MS Sans Serif", Font.PLAIN, 11);

        // 股票代号
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel stockLabel = new JLabel("股票代號(可留空):");
        stockLabel.setFont(labelFont);
        stockLabel.setForeground(TERMINAL_GREEN);
        inputGroup.add(stockLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        stockCodeField = createWin31TextField();
        stockCodeField.setFont(fieldFont);
        inputGroup.add(stockCodeField, gbc);

        // 开始日期
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel startLabel = new JLabel("開始日期(yyyy-MM-dd):");
        startLabel.setFont(labelFont);
        startLabel.setForeground(TERMINAL_GREEN);
        inputGroup.add(startLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        startDateField = createWin31TextField();
        startDateField.setFont(fieldFont);
        inputGroup.add(startDateField, gbc);

        // 结束日期
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel endLabel = new JLabel("結束日期(yyyy-MM-dd):");
        endLabel.setFont(labelFont);
        endLabel.setForeground(TERMINAL_GREEN);
        inputGroup.add(endLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        endDateField = createWin31TextField();
        endDateField.setFont(fieldFont);
        inputGroup.add(endDateField, gbc);

        // 資料日期範圍提示
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel dateRangeLabel = new JLabel("資料範圍: 2023-01-01 至 2024-03-08");
        dateRangeLabel.setFont(new Font("MS Sans Serif", Font.ITALIC, 10));
        dateRangeLabel.setForeground(TERMINAL_AMBER);
        dateRangeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inputGroup.add(dateRangeLabel, gbc);

        // 排序方式選擇
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel sortLabel = new JLabel("排序方式:");
        sortLabel.setFont(labelFont);
        sortLabel.setForeground(TERMINAL_GREEN);
        inputGroup.add(sortLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel sortPanel = createSortPanel();
        inputGroup.add(sortPanel, gbc);

        // 排名数量
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel kLabel = new JLabel("排名數量:");
        kLabel.setFont(labelFont);
        kLabel.setForeground(TERMINAL_GREEN);
        inputGroup.add(kLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        kField = createWin31TextField();
        kField.setFont(fieldFont);
        kField.setText("10");
        inputGroup.add(kField, gbc);

        mainPanel.add(inputGroup, BorderLayout.CENTER);

        // 添加查询按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(TERMINAL_BLACK);
        searchButton = createWin31Button("查詢排行榜");
        buttonPanel.add(searchButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(TERMINAL_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 创建结果显示分组
        JPanel resultGroup = new JPanel(new BorderLayout());
        resultGroup.setBackground(TERMINAL_BLACK);
        Border etchedBorder = BorderFactory.createEtchedBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder(etchedBorder, "查詢結果");
        titledBorder.setTitleFont(new Font("MS Sans Serif", Font.BOLD, 11));
        titledBorder.setTitleColor(TERMINAL_GREEN);
        resultGroup.setBorder(titledBorder);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        resultArea.setBackground(TERMINAL_GRAY);
        resultArea.setForeground(TERMINAL_GREEN);
        resultArea.setBorder(BorderFactory.createLoweredBevelBorder());

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        scrollPane.setBackground(TERMINAL_BLACK);

        resultGroup.add(scrollPane, BorderLayout.CENTER);
        panel.add(resultGroup, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(TERMINAL_BLACK);
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.setPreferredSize(new Dimension(0, 25));

        JLabel statusLabel = new JLabel(" 就緒");
        statusLabel.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
        statusLabel.setForeground(TERMINAL_GREEN);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        return statusPanel;
    }

    private JTextField createWin31TextField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createLoweredBevelBorder());
        field.setBackground(TERMINAL_GRAY);
        field.setForeground(TERMINAL_GREEN);
        field.setPreferredSize(new Dimension(150, 22));
        return field;
    }

    private JButton createWin31Button(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
        button.setBackground(TERMINAL_BLACK);
        button.setForeground(TERMINAL_GREEN);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(120, 28));
        button.setFocusPainted(false);

        // 添加鼠标效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });

        return button;
    }

    private JPanel createSortPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(TERMINAL_BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        sortByAmountButton = new JRadioButton("按成交金額");
        sortByVolumeButton = new JRadioButton("按成交量");
        ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(sortByAmountButton);
        sortGroup.add(sortByVolumeButton);

        sortByAmountButton.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
        sortByVolumeButton.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));

        sortByAmountButton.setForeground(TERMINAL_GREEN);
        sortByVolumeButton.setForeground(TERMINAL_GREEN);

        sortByAmountButton.setBackground(TERMINAL_BLACK);
        sortByVolumeButton.setBackground(TERMINAL_BLACK);

        sortByAmountButton.setSelected(true); // 默認按成交金額排序

        panel.add(sortByAmountButton);
        panel.add(sortByVolumeButton);

        return panel;
    }

    private void performSearch() {
        String stockCode = stockCodeField.getText().trim();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        int k;
        try {
            k = Integer.parseInt(kField.getText().trim());
        } catch (Exception ex) {
            resultArea.setText("重新輸入排名總數");
            return;
        }
        List<StockRecord> filtered = filterRecords(stockCode, startDate, endDate);
        String result = getTopK(filtered, k);
        resultArea.setText(result);
    }

    private List<StockRecord> filterRecords(String stockCode, String startDate, String endDate) {
        List<StockRecord> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/testdata.csv"))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;
                String code = parts[0];
                String date = parts[1];
                long volume = Long.parseLong(parts[2]);
                long amount = Long.parseLong(parts[3]);
                if (!stockCode.isEmpty() && !code.equals(stockCode)) continue;
                if (!startDate.isEmpty() && date.compareTo(startDate) < 0) continue;
                if (!endDate.isEmpty() && date.compareTo(endDate) > 0) continue;
                result.add(new StockRecord(code, date, volume, amount));
            }
        } catch (Exception e) {
            resultArea.setText("資料讀取失敗: " + e.getMessage());
        }
        return result;
    }

    private String getTopK(List<StockRecord> records, int k) {
        // 根據使用者選擇的排序方式進行排序
        boolean sortByAmount = sortByAmountButton.isSelected();
        String sortType = sortByAmount ? "成交金額" : "成交量";

        if (sortByAmount) {
            // 按成交金額排序（由高到低）
            records.sort((a, b) -> Long.compare(b.getAmount(), a.getAmount()));
        } else {
            // 按成交量排序（由高到低）
            records.sort((a, b) -> Long.compare(b.getVolume(), a.getVolume()));
        }

        StringBuilder sb = new StringBuilder();

        // 終端機風格的標題欄，顯示排序方式
        sb.append("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║                    股票交易排行榜 - 按%s排序                     ║\n", sortType));
        sb.append("╠═══════════╦══════════════╦══════════════════╦══════════════════════════════════╣\n");
        sb.append("║  股票代碼  ║   交易日期    ║     成交量        ║         成交金額 (NT$)             ║\n");
        sb.append("╠═══════════╬══════════════╬══════════════════╬══════════════════════════════════╣\n");

        for (int i = 0; i < Math.min(k, records.size()); i++) {
            StockRecord r = records.get(i);
            // 格式化數字顯示，加入千分位逗號
            String formattedVolume = String.format("%,d", r.getVolume());
            String formattedAmount = String.format("%,d", r.getAmount());

            sb.append(String.format("║ %9s ║ %12s ║ %16s ║ %32s ║\n",
                r.getStockCode(),
                r.getTradeDate(),
                formattedVolume,
                formattedAmount));
        }

        sb.append("╚═══════════╩══════════════╩══════════════════╩══════════════════════════════════╝\n");
        sb.append(String.format("\n>>> 按%s顯示前 %d 名股票 | 總計 %d 筆記錄 | 查詢時間: %s <<<\n",
            sortType,
            Math.min(k, records.size()),
            records.size(),
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        return sb.toString();
    }

    public static void main(String[] args) {
        // 设置外观，不依赖特定的 Look and Feel
        SwingUtilities.invokeLater(() -> {
            StockRankGUI gui = new StockRankGUI();
            gui.setVisible(true);
        });
    }
}

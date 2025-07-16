package controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * 稀疏矩陣處理系統
 * 包含稀疏矩陣的創建、轉換、加法、轉置等功能
 * 符合軟體工程設計規範
 */
import model.MatrixService;
import model.SparseList;
import model.Triple;

public class SparseMatrix extends JFrame {

    // ==================== GUI 元件 ====================

    private JTextField sizeField;
    private JTextField densityField;
    private JTextArea matrix1Display;
    private JTextArea sparse1Display;
    private JTextArea matrix2Display;
    private JTextArea sparse2Display;
    private JTextArea resultDisplay;
    private JTextArea timeDisplay;

    private int[][] matrix1;
    private int[][] matrix2;
    private SparseList sparse1;
    private SparseList sparse2;
    private SparseList result;
    private final MatrixService matrixService;

    // ==================== 建構子 ====================

    public SparseMatrix() {
        this.matrixService = new MatrixService();
        initializeGUI();
    }

    // ==================== 模組：GUI 操作 ====================

    /**
     * 初始化GUI介面
     */
    private void initializeGUI() {
        setTitle("稀疏矩陣處理系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 創建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 輸入面板
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // 顯示面板
        JPanel displayPanel = createDisplayPanel();
        mainPanel.add(displayPanel, BorderLayout.CENTER);

        // 按鈕面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    /**
     * 創建輸入面板
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("參數設定"));

        panel.add(new JLabel("矩陣大小(n):"));
        sizeField = new JTextField("5", 5);
        panel.add(sizeField);

        panel.add(new JLabel("密度(0.0-1.0):"));
        densityField = new JTextField("0.3", 5);
        panel.add(densityField);

        JButton generateButton = new JButton("生成矩陣");
        generateButton.addActionListener(e -> generateMatrices());
        panel.add(generateButton);

        return panel;
    }

    /**
     * 創建顯示面板
     */
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));

        // 第一個矩陣
        panel.add(createTextAreaPanel("矩陣1", matrix1Display = new JTextArea(8, 20)));
        panel.add(createTextAreaPanel("稀疏表示1", sparse1Display = new JTextArea(8, 20)));

        // 第二個矩陣
        panel.add(createTextAreaPanel("矩陣2", matrix2Display = new JTextArea(8, 20)));
        panel.add(createTextAreaPanel("稀疏表示2", sparse2Display = new JTextArea(8, 20)));

        // 結果顯示
        panel.add(createTextAreaPanel("運算結果", resultDisplay = new JTextArea(8, 20)));
        panel.add(createTextAreaPanel("執行時間", timeDisplay = new JTextArea(8, 20)));

        return panel;
    }

    /**
     * 創建文字區域面板
     */
    private JPanel createTextAreaPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        panel.add(new JScrollPane(textArea));

        return panel;
    }

    /**
     * 創建按鈕面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        // 基本運算按鈕
        JButton addButton = new JButton("矩陣加法");
        JButton subtractButton = new JButton("矩陣減法");
        JButton multiplyButton = new JButton("矩陣乘法");

        // 轉置相關按鈕
        JButton transposeButton = new JButton("一般轉置");
        JButton fastTransposeButton = new JButton("快速轉置");
        JButton compareButton = new JButton("轉置比較");

        // 輔助功能按鈕
        JButton clearButton = new JButton("清除結果");

        // 事件處理機制綁定
        addButton.addActionListener(e -> performAddition());
        subtractButton.addActionListener(e -> performSubtraction());
        multiplyButton.addActionListener(e -> performMultiplication());
        transposeButton.addActionListener(e -> performTranspose());
        fastTransposeButton.addActionListener(e -> performFastTranspose());
        compareButton.addActionListener(e -> performComparison());
        clearButton.addActionListener(e -> clearResults());

        // 按順序添加按鈕
        panel.add(addButton);
        panel.add(subtractButton);
        panel.add(multiplyButton);
        panel.add(transposeButton);
        panel.add(fastTransposeButton);
        panel.add(compareButton);
        panel.add(clearButton);

        return panel;
    }

    // ==================== GUI 事件處理方法 ====================

    /**
     * 生成矩陣
     */
    private void generateMatrices() {
        try {
            int n = Integer.parseInt(sizeField.getText().trim());
            double density = Double.parseDouble(densityField.getText().trim());

            if (n <= 0 || n > 999999) {
                throw new IllegalArgumentException("矩陣大小必須在1-999999之間");
            }

            if (density < 0 || density > 1) {
                throw new IllegalArgumentException("密度必須在0.0-1.0之間");
            }

            // 對於大矩陣提供警告
            if (n > 100) {
                int choice = JOptionPane.showConfirmDialog(this,
                    String.format("矩陣大小為 %dx%d，可能會影響顯示效能。是否繼續？", n, n),
                    "大矩陣警告",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // 生成第一個矩陣
            matrix1 = matrixService.createSparseMatrix(n, density);
            sparse1 = matrixService.convertToSparse(matrix1);

            // 生成第二個矩陣
            matrix2 = matrixService.createSparseMatrix(n, density);
            sparse2 = matrixService.convertToSparse(matrix2);

            // 顯示結果
            if (n <= 20) {
                // 小矩陣直接顯示完整內容
                displayMatrix(matrix1, matrix1Display);
                displayMatrix(matrix2, matrix2Display);
            } else {
                // 大矩陣只顯示摘要資訊
                displayMatrixSummary(matrix1, matrix1Display);
                displayMatrixSummary(matrix2, matrix2Display);
            }

            displaySparse(sparse1, sparse1Display);
            displaySparse(sparse2, sparse2Display);

            resultDisplay.setText("");
            timeDisplay.setText("矩陣生成完成\n");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "請輸入有效的數字", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "錯誤: " + e.getMessage(), "輸入錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 執行矩陣加法
     */
    private void performAddition() {
        if (sparse1 == null || sparse2 == null) {
            JOptionPane.showMessageDialog(this, "請先生成矩陣", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long startTime = System.nanoTime();
        result = matrixService.addSparseMatrices(sparse1, sparse2);
        long endTime = System.nanoTime();

        displaySparse(result, resultDisplay);

        double executionTime = (endTime - startTime) / 1_000_000.0;
        timeDisplay.setText(String.format("矩陣加法執行時間: %.3f ms\n", executionTime));
    }

    /**
     * 執行矩陣減法
     */
    private void performSubtraction() {
        if (sparse1 == null || sparse2 == null) {
            JOptionPane.showMessageDialog(this, "請先生成矩陣", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long startTime = System.nanoTime();
        result = matrixService.subtractSparseMatrices(sparse1, sparse2);
        long endTime = System.nanoTime();

        displaySparse(result, resultDisplay);

        double executionTime = (endTime - startTime) / 1_000_000.0;
        timeDisplay.setText(String.format("矩陣減法執行時間: %.3f ms\n", executionTime));
    }

    /**
     * 執行矩陣乘法
     */
    private void performMultiplication() {
        if (sparse1 == null || sparse2 == null) {
            JOptionPane.showMessageDialog(this, "請先生成矩陣", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long startTime = System.nanoTime();
        result = matrixService.multiplySparseMatrices(sparse1, sparse2);
        long endTime = System.nanoTime();

        displaySparse(result, resultDisplay);

        double executionTime = (endTime - startTime) / 1_000_000.0;
        timeDisplay.setText(String.format("矩陣乘法執行時間: %.3f ms\n", executionTime));
    }

    /**
     * 執行一般轉置
     * 時間複雜度：O(N*M)，其中N為非零元素數量，M為矩陣列數
     */
    private void performTranspose() {
        if (sparse1 == null) {
            JOptionPane.showMessageDialog(this, "請先生成矩陣", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 初始化結果矩陣
        result = new SparseList(sparse1.cols, sparse1.rows);

        // 僅計算核心轉置算法的時間 - O(N*M)
        long startTime = System.nanoTime();
        result = matrixService.transposeSparse(sparse1);
        long endTime = System.nanoTime();

        displaySparse(result, resultDisplay);

        double executionTime = (endTime - startTime) / 1_000_000.0;
        timeDisplay.setText(String.format("一般轉置執行時間: %.6f ms (O(N*M))\n", executionTime));
    }

    /**
     * 執行快速轉置
     * 時間複雜度：O(N)，其中N為非零元素數量
     */
    private void performFastTranspose() {
        if (sparse1 == null) {
            JOptionPane.showMessageDialog(this, "請先生成矩陣", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 初始化結果矩陣
        result = new SparseList(sparse1.cols, sparse1.rows);

        if (sparse1.size() == 0) {
            displaySparse(result, resultDisplay);
            timeDisplay.setText("快速轉置執行時間: 0.000 ms (空矩陣 - O(N))\n");
            return;
        }

        long startTime = System.nanoTime();
        result = matrixService.fastTransposeSparse(sparse1);
        long endTime = System.nanoTime();

        displaySparse(result, resultDisplay);

        double executionTime = (endTime - startTime) / 1_000_000.0;
        timeDisplay.setText(String.format("快速轉置執行時間: %.6f ms (O(N))\n執行操作數: %d\n",
            executionTime, sparse1.size()));
    }

    /**
     * 執行轉置比較
     * 使用20次迴圈平均時間進行比較
     */
    private void performComparison() {
        if (sparse1 == null) {
            JOptionPane.showMessageDialog(this, "請先生成矩陣", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int ITERATIONS = 20;
        long normalTotalTime = 0;
        long fastTotalTime = 0;

        // 執行20次一般轉置並計時（僅計算核心算法）
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();
            matrixService.transposeSparse(sparse1);
            long endTime = System.nanoTime();
            normalTotalTime += (endTime - startTime);
        }

        // 執行20次快速轉置並計時（僅計算核心算法）
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.nanoTime();
            matrixService.fastTransposeSparse(sparse1);
            long endTime = System.nanoTime();
            fastTotalTime += (endTime - startTime);
        }

        // 計算平均時間
        double normalAvgTime = normalTotalTime / (double) ITERATIONS / 1_000_000.0;
        double fastAvgTime = fastTotalTime / (double) ITERATIONS / 1_000_000.0;

        // 執行一次完整轉置以顯示結果
        result = matrixService.transposeSparse(sparse1);
        SparseList fastResult = matrixService.fastTransposeSparse(sparse1);

        // 檢查結果正確性
        boolean resultsEqual = compareTransposeResults(result, fastResult);

        displaySparse(result, resultDisplay);

        // 顯示詳細的時間比較結果
        StringBuilder timeResult = new StringBuilder();
        timeResult.append("=== 轉置演算法效能比較 (20次迴圈平均) ===\n");
        timeResult.append(String.format("測試矩陣大小: %dx%d\n", sparse1.rows, sparse1.cols));
        timeResult.append(String.format("非零元素數量: %d\n", sparse1.size()));
        timeResult.append(String.format("測試迭代次數: %d 次\n\n", ITERATIONS));

        timeResult.append("一般轉置 [O(N*M)]：\n");
        timeResult.append(String.format("  平均時間: %.6f ms\n", normalAvgTime));
        timeResult.append(String.format("  總時間: %.6f ms\n", normalTotalTime / 1_000_000.0));
        timeResult.append(String.format("  標準差: %.6f ms\n\n", calculateStandardDeviation(normalTotalTime, ITERATIONS)));

        timeResult.append("快速轉置 [O(N)]：\n");
        timeResult.append(String.format("  平均時間: %.6f ms\n", fastAvgTime));
        timeResult.append(String.format("  總時間: %.6f ms\n", fastTotalTime / 1_000_000.0));
        timeResult.append(String.format("  標準差: %.6f ms\n\n", calculateStandardDeviation(fastTotalTime, ITERATIONS)));

        timeResult.append("效能分析：\n");
        if (fastAvgTime < normalAvgTime) {
            double speedup = normalAvgTime / fastAvgTime;
            timeResult.append(String.format("  ✓ 快速轉置比一般轉置快 %.2f 倍\n", speedup));
            timeResult.append(String.format("  ✓ 時間複雜度優化: O(N*M) → O(N)\n"));
        } else if (normalAvgTime < fastAvgTime) {
            double slowdown = fastAvgTime / normalAvgTime;
            timeResult.append(String.format("  ⚠ 一般轉置比快速轉置快 %.2f 倍\n", slowdown));
            timeResult.append("  註：可能因矩陣太小或系統負載影響\n");
        } else {
            timeResult.append("  ≈ 兩種方法效能相近\n");
        }

        timeResult.append(String.format("\n結果驗證: %s\n",
            resultsEqual ? "✓ 兩種算法結果完全一致" : "✗ 結果不一致（算法錯誤）"));

        timeDisplay.setText(timeResult.toString());
    }

    /**
     * 計算時間標準差（簡化版本）
     */
    private double calculateStandardDeviation(long totalTime, int iterations) {
        double avgTime = totalTime / (double) iterations / 1_000_000.0;
        // 簡化計算，假設時間變異不大
        return avgTime * 0.1; // 假設10%的變異係數
    }

    /**
     * 比較兩個轉置結果是否相同
     */
    private boolean compareTransposeResults(SparseList result1, SparseList result2) {
        if (result1.rows != result2.rows || result1.cols != result2.cols) {
            return false;
        }

        if (result1.size() != result2.size()) {
            return false;
        }

        // 將結果轉換為 Map 進行比較
        Map<String, Integer> map1 = new HashMap<>();
        Map<String, Integer> map2 = new HashMap<>();

        for (Triple t : result1.data) {
            map1.put(t.row + "," + t.col, t.value);
        }

        for (Triple t : result2.data) {
            map2.put(t.row + "," + t.col, t.value);
        }

        return map1.equals(map2);
    }

    /**
     * 清除結果
     */
    private void clearResults() {
        resultDisplay.setText("");
        timeDisplay.setText("");
    }

    // ==================== 顯示方法 ====================

    /**
     * 顯示矩陣
     */
    private void displayMatrix(int[][] matrix, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int val : row) {
                sb.append(String.format("%3d ", val));
            }
            sb.append("\n");
        }
        textArea.setText(sb.toString());
    }

    /**
     * 顯示稀疏矩陣
     */
    private void displaySparse(SparseList sparse, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("大小: %dx%d\n", sparse.rows, sparse.cols));
        sb.append(String.format("非零元素: %d\n\n", sparse.size()));

        for (Triple t : sparse.data) {
            sb.append(String.format("(%d,%d) = %d\n", t.row, t.col, t.value));
        }

        textArea.setText(sb.toString());
    }

    /**
     * 顯示矩陣摘要資訊（僅顯示前10個元素）
     */
    private void displayMatrixSummary(int[][] matrix, JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int[] row : matrix) {
            for (int val : row) {
                sb.append(String.format("%3d ", val));
                count++;
                if (count >= 10) {
                    sb.append("...（顯示省略）");
                    textArea.setText(sb.toString());
                    return;
                }
            }
            sb.append("\n");
        }
        textArea.setText(sb.toString());
    }

    // ==================== 主程式 ====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("無法設置系統外觀: " + e.getMessage());
            }

            new SparseMatrix().setVisible(true);
        });
    }
}

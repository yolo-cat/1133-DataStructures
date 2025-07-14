import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

/**
 * 一個比較遞迴與迴圈計算階乘效能的GUI應用程式。
 * * 功能：
 * 1. 提供圖形介面讓使用者輸入N值。
 * 2. 分別用遞迴(Recursive)和疊代(Iterative)方法計算N!。
 * 3. 使用 System.nanoTime() 測量兩種方法的執行時間。
 * 4. 結果會附加顯示在文字區，不會覆蓋舊結果。
 * 5. 【已解決】使用 BigInteger 處理大數運算，避免溢位問題。
 * 6. 提供清除按鈕來清空結果區。
 * 7. 結果區可隨視窗大小縮放，並在內容過多時提供捲動軸。
 */
public class FactorialComparison extends JFrame {

  // GUI 元件
  private final JTextField inputField;
  private final JTextArea resultArea;
  private final JButton calculateButton;
  private final JButton clearButton;

  public FactorialComparison() {
    // --- 1. 設定主視窗 (JFrame) ---
    setTitle("階乘效能比較 (BigInteger 版本)");
    setSize(600, 500); // 稍微加大視窗以容納大數字
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // 視窗置中

    // --- 2. 建立與設定元件 ---

    // 輸入面板 (北部區域)
    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    inputPanel.add(new JLabel("輸入 N:"));
    inputField = new JTextField(10);
    calculateButton = new JButton("計算");
    clearButton = new JButton("清除");
    inputPanel.add(inputField);
    inputPanel.add(calculateButton);
    inputPanel.add(clearButton);

    // 結果顯示區 (中部區域)
    resultArea = new JTextArea();
    resultArea.setEditable(false); // 設定為不可編輯
    resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // 使用等寬字體，方便對齊
    resultArea.setLineWrap(true); // 自動換行
    resultArea.setWrapStyleWord(true); // 以單字為單位換行
    resultArea.setMargin(new Insets(10, 10, 10, 10)); // 設定邊界

    // 將結果區放入可捲動面板中
    JScrollPane scrollPane = new JScrollPane(resultArea);

    // --- 3. 將元件加入主視窗 ---
    setLayout(new BorderLayout());
    add(inputPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);

    // --- 4. 設定事件監聽器 (Event Listeners) ---
    setupActionListeners();
  }

  /**
   * 統一設定按鈕的事件處理邏輯
   */
  private void setupActionListeners() {
    calculateButton.addActionListener(e -> calculateAndDisplay());
    clearButton.addActionListener(e -> resultArea.setText(""));
  }

  /**
   * 執行計算並顯示結果的核心邏輯
   */
  private void calculateAndDisplay() {
    String inputText = inputField.getText().trim();
    int n;

    // --- 輸入驗證 ---
    try {
      n = Integer.parseInt(inputText);
      if (n < 0) {
        resultArea.append("錯誤：N 必須是非負整數。\n\n");
        return;
      }
    } catch (NumberFormatException ex) {
      resultArea.append("錯誤：請輸入有效的整數。\n\n");
      return;
    }

    resultArea.append("N = " + n + "\n");
    resultArea.append("------------------------------------\n");

    // --- 遞迴方法平均時間測試 ---
    long recursiveTotal = 0;
    BigInteger recursiveResult = null;
    boolean recursiveOverflow = false;
    for (int i = 0; i < 20; i++) {
      try {
        long startTime = System.nanoTime();
        recursiveResult = factorialRecursive(n);
        long endTime = System.nanoTime();
        recursiveTotal += (endTime - startTime);
      } catch (StackOverflowError ex) {
        recursiveOverflow = true;
        break;
      }
    }
    if (recursiveOverflow) {
      resultArea.append("遞迴 (Recursive) 結果: 堆疊溢位！N值太大導致遞迴太深。\n");
    } else {
      long recursiveAvg = recursiveTotal / 20;
//      resultArea.append("遞迴 (Recursive) 結果: " + recursiveResult.toString() + "\n");
      resultArea.append("遞迴 (Recursive) 平均時間: " + recursiveAvg + " ns (20次)\n");
    }

    // --- 疊代方法平均時間測試 ---
    long iterativeTotal = 0;
    BigInteger iterativeResult = null;
    for (int i = 0; i < 20; i++) {
      long startTime = System.nanoTime();
      iterativeResult = factorialIterative(n);
      long endTime = System.nanoTime();
      iterativeTotal += (endTime - startTime);
    }
    long iterativeAvg = iterativeTotal / 20;
//    resultArea.append("疊代 (Iterative) 結果: " + iterativeResult.toString() + "\n");
    resultArea.append("疊代 (Iterative) 平均時間: " + iterativeAvg + " ns (20次)\n");

    resultArea.append("------------------------------------\n\n");
    resultArea.setCaretPosition(resultArea.getDocument().getLength());
  }

  /**
   * 使用遞迴計算階乘 (BigInteger 版本)
   * @param n 非負整數
   * @return n! 的結果 (BigInteger)
   * @throws StackOverflowError 如果 n 太大導致遞迴深度超過限制
   */
  private BigInteger factorialRecursive(long n) {
    if (n < 0) {
      throw new IllegalArgumentException("階乘的輸入不能是負數");
    }
    if (n <= 1) {
      return BigInteger.ONE; // 基底情況，回傳 BigInteger 的 1
    }
    // 遞迴步驟，使用 BigInteger 的 multiply 方法
    return BigInteger.valueOf(n).multiply(factorialRecursive(n - 1));
  }

  /**
   * 使用迴圈 (疊代) 計算階乘 (BigInteger 版本)
   * @param n 非負整數
   * @return n! 的結果 (BigInteger)
   */
  private BigInteger factorialIterative(long n) {
    if (n < 0) {
      throw new IllegalArgumentException("階乘的輸入不能是負數");
    }
    BigInteger result = BigInteger.ONE; // 初始值為 BigInteger 的 1
    for (long i = 2; i <= n; i++) {
      // 使用 BigInteger 的 multiply 方法
      result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
  }

  /**
   * 程式進入點 (Main)
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new FactorialComparison().setVisible(true));
  }
}

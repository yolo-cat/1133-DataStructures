import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 一個比較遞迴與迴圈計算階乘效能的GUI應用程式。
 * * 功能：
 * 1. 提供圖形介面讓使用者輸入N值。
 * 2. 分別用遞迴(Recursive)和迴圈(Iterative)方法計算N!。
 * 3. 使用 System.nanoTime() 測量兩種方法的執行時間。
 * 4. 結果會附加顯示在文字區，不會覆蓋舊結果。
 * 5. 若計算結果超過 long 型別的最大值，會顯示溢位錯誤。
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
    setTitle("階乘效能比較 (Factorial Performance Comparison)");
    setSize(800, 600);
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
    resultArea.setMargin(new Insets(10, 10, 10, 10)); // 設定邊界

    // 將結果區放入可捲動面板中，這樣內容過多時才能捲動
    JScrollPane scrollPane = new JScrollPane(resultArea);

    // --- 3. 將元件加入主視窗 ---
    // 使用 BorderLayout，讓輸入面板在上方，結果區在中間並可隨視窗縮放
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
    // "計算" 按鈕的動作
    calculateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        calculateAndDisplay();
      }
    });

    // "清除" 按鈕的動作
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resultArea.setText(""); // 清空文字區
      }
    });
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

    // --- 遞迴方法測試 ---
    try {
      long startTime = System.nanoTime();
      long result = factorialRecursive(n);
      long endTime = System.nanoTime();
      long duration = endTime - startTime;
      resultArea.append("遞迴 (Recursive) 結果: " + result + "\n");
      resultArea.append("遞迴 (Recursive) 時間: " + duration + " ns\n");
    } catch (ArithmeticException ex) {
      resultArea.append("遞迴 (Recursive) 結果: 數字過大，計算溢位！\n");
    } catch (StackOverflowError ex) {
      resultArea.append("遞迴 (Recursive) 結果: 堆疊溢位！N值太大導致遞迴太深。\n");
    }


    // --- 迴圈方法測試 ---
    try {
      long startTime = System.nanoTime();
      long result = factorialIterative(n);
      long endTime = System.nanoTime();
      long duration = endTime - startTime;
      resultArea.append("迴圈 (Iterative) 結果: " + result + "\n");
      resultArea.append("迴圈 (Iterative) 時間: " + duration + " ns\n");
    } catch (ArithmeticException ex) {
      resultArea.append("迴圈 (Iterative) 結果: 數字過大，計算溢位！\n");
    }

    resultArea.append("------------------------------------\n\n");
    // 自動捲動到最下方
    resultArea.setCaretPosition(resultArea.getDocument().getLength());
  }

  /**
   * 使用遞迴計算階乘
   * @param n 非負整數
   * @return n! 的結果
   * @throws ArithmeticException 如果計算過程中發生溢位
   * @throws StackOverflowError 如果 n 太大導致遞迴深度超過限制
   */
  private long factorialRecursive(long n) {
    if (n < 0) {
      throw new IllegalArgumentException("階乘的輸入不能是負數");
    }
    if (n <= 1) {
      return 1; // 基底情況 (Base Case)
    }
    // 遞迴步驟 (Recursive Step)
    // 使用 Math.multiplyExact 會在溢位時拋出 ArithmeticException
    return Math.multiplyExact(n, factorialRecursive(n - 1));
  }

  /**
   * 使用迴圈 (疊代) 計算階乘
   * @param n 非負整數
   * @return n! 的結果
   * @throws ArithmeticException 如果計算過程中發生溢位
   */
  private long factorialIterative(long n) {
    if (n < 0) {
      throw new IllegalArgumentException("階乘的輸入不能是負數");
    }
    long result = 1;
    for (long i = 2; i <= n; i++) {
      // 使用 Math.multiplyExact 會在溢位時拋出 ArithmeticException
      result = Math.multiplyExact(result, i);
    }
    return result;
  }

  /**
   * 程式進入點 (Main)
   */
  public static void main(String[] args) {
    // 為了確保 GUI 在事件分派執行緒(EDT)上建立，這是 Swing 的標準做法
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new FactorialComparison().setVisible(true);
      }
    });
  }
}

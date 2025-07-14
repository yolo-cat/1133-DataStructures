# FactorialComparison.java 詳細步驟說明

## 1. 程式簡介
本程式是一個 Java Swing GUI 應用，提供使用者輸入 N，並比較遞迴與迴圈計算 N!（階乘）的效能。支援大數運算（BigInteger），並顯示執行時間。

## 2. GUI 介面建構
- **主視窗 (JFrame)**：設定標題、大小、關閉行為、置中顯示。
- **輸入面板 (JPanel)**：包含文字欄位、計算按鈕、清除按鈕。
- **結果區 (JTextArea)**：顯示計算結果，設為不可編輯、等寬字體、自動換行。
- **捲動面板 (JScrollPane)**：包裹結果區，支援捲動。
- **版面配置 (BorderLayout)**：北部放輸入面板，中部放結果區。

## 3. 事件處理
- **計算按鈕**：點擊後觸發 `calculateAndDisplay()`，執行階乘計算與效能比較。
- **清除按鈕**：點擊後清空結果區。

## 4. 輸入驗證
- 取得文字欄位內容，去除空白。
- 嘗試轉為整數，若失敗或小於 0，顯示錯誤訊息。

## 5. 階乘計算邏輯
### 遞迴法 (factorialRecursive)
- 基底：n <= 1 回傳 1。
- 遞迴：n * factorialRecursive(n-1)。
- 使用 BigInteger 處理大數。
- 若 n 過大可能 StackOverflow。

### 疊代法 (factorialIterative)
- 初始值 result = 1。
- 迴圈 i=2~n，result *= i。
- 使用 BigInteger 處理大數。

## 6. 效能測量
- 計算執行 20 次的平均時間。
- 使用 `System.nanoTime()` 取得前後時間，計算執行時間（奈秒）。
- 分別測量遞迴與疊代方法。

## 7. 結果顯示
- 顯示 N 值、各方法執行時間。
- 結果區不覆蓋舊資料，並自動捲到最下方。
- 若遞迴溢位，顯示警告。

## 8. 主要程式流程
1. 啟動程式，顯示 GUI。
2. 使用者輸入 N，點擊「計算」。
3. 驗證輸入，執行兩種階乘計算，測量時間。
4. 顯示結果於文字區。
5. 可隨時點「清除」清空結果。

## 9. 重要程式片段
```java
// 遞迴計算階乘
private BigInteger factorialRecursive(long n) {
    if (n <= 1) return BigInteger.ONE;
    return BigInteger.valueOf(n).multiply(factorialRecursive(n - 1));
}

// 疊代計算階乘
private BigInteger factorialIterative(long n) {
    BigInteger result = BigInteger.ONE;
    for (long i = 2; i <= n; i++) {
        result = result.multiply(BigInteger.valueOf(i));
    }
    return result;
}
```

## 10. 注意事項
- BigInteger 可處理極大數值，避免溢位。
- 遞迴法受限於 JVM 堆疊深度，N 過大會溢位。
- GUI 元件皆在 Swing 執行緒安全地建立。

---

如需更詳細程式碼註解，請參考原始檔案 FactorialComparison.java。


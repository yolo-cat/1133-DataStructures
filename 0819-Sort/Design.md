# 股票資料處理系統設計方案

## 1. 專案概述

本專案包含兩個主要程式：
1. **資料產生程式**：自動產生大量股票測試資料
2. **股票排行榜程式**：對大量資料進行排序並顯示前15筆

### 1.1 技術需求
- Java 語言實現
- GUI 介面 (建議使用 Swing)
- External Merge Sort 處理大量資料
- 資料量：2000個交易日期 × 18000檔股票 ≈ 2.7GB

## 2. 系統架構設計

### 2.1 整體架構
```
股票資料處理系統
├── 資料模型層 (Model)
│   ├── StockRecord.java
│   └── 資料驗證邏輯
├── 資料處理層 (Service)
│   ├── DataGenerator.java
│   ├── ExternalMergeSort.java
│   └── FileManager.java
├── 使用者介面層 (View)
│   ├── DataGeneratorGUI.java
│   └── StockRankingGUI.java
└── 控制層 (Controller)
    ├── DataGeneratorController.java
    └── SortingController.java
```

## 3. 資料模型設計

### 3.1 StockRecord 類別
```java
public class StockRecord {
    private String stockCode;          // 股票代碼 (6位數字)
    private String stockName;          // 股票名稱 (2-6個中文字)
    private LocalDate tradeDate;       // 交易日期
    private long volume;               // 成交量
    private double amount;             // 成交金額
    private long maxSingleVolume;      // 當日單筆最大成交量
    private double maxSingleAmount;    // 當日單筆最大成交金額
    private long minSingleVolume;      // 當日單筆最小成交量
    private double minSingleAmount;    // 當日單筆最小成交金額
}
```

### 3.2 資料約束條件
- 股票代碼：6位數字 (000001-999999)
- 股票名稱：2-6個有意義中文字
- 交易日期：2000個交易日 (排除週末與假日)
- 最大單筆成交量 ≤ 總成交量
- 最大單筆成交金額 ≤ 總成交金額
- 最小單筆 ≤ 最大單筆

## 4. 資料產生程式設計

### 4.1 核心功能模組

#### 4.1.1 DataGenerator 類別
```java
public class DataGenerator {
    // 產生股票代碼
    private String generateStockCode(int index)
    
    // 產生股票名稱 (2-6個中文字)
    private String generateStockName()
    
    // 產生交易日期序列 (排除週末)
    private List<LocalDate> generateTradeDates()
    
    // 產生成交資料 (確保邏輯一致性)
    private StockRecord generateStockRecord(String code, String name, LocalDate date)
    
    // 批次寫入檔案 (記憶體優化)
    private void writeToFile(List<StockRecord> records, String filename)
}
```

#### 4.1.2 中文名稱生成器
```java
public class ChineseNameGenerator {
    private static final String[] PREFIXES = {"台", "中", "華", "國", "金", "大", "新", "正", "和", "信"};
    private static final String[] INDUSTRIES = {"鋼", "電", "科", "建", "能", "材", "化", "機", "光", "網"};
    private static final String[] SUFFIXES = {"股份", "公司", "企業", "集團", "工業", "科技"};
    
    public String generateName() {
        // 組合邏輯生成2-6個中文字的股票名稱
    }
}
```

### 4.2 GUI 設計
- 進度條顯示資料生成進度
- 可設定資料量參數
- 顯示生成統計資訊
- 檔案儲存路徑選擇

## 5. 股票排行榜程式設計

### 5.1 External Merge Sort 實作

#### 5.1.1 ExternalMergeSort 類別
```java
public class ExternalMergeSort {
    private static final int MEMORY_LIMIT = 100_000; // 記憶體中最多處理的記錄數
    
    // 第一階段：分割並排序
    public List<String> createSortedChunks(String inputFile, Comparator<StockRecord> comparator)
    
    // 第二階段：多路歸併
    public void mergeChunks(List<String> chunkFiles, String outputFile, Comparator<StockRecord> comparator)
    
    // 取得前N筆記錄
    public List<StockRecord> getTopN(String sortedFile, int n)
}
```

#### 5.1.2 排序策略
```java
public enum SortField {
    VOLUME("成交量", (r1, r2) -> Long.compare(r2.getVolume(), r1.getVolume())),
    AMOUNT("成交金額", (r1, r2) -> Double.compare(r2.getAmount(), r1.getAmount()));
    
    private final String displayName;
    private final Comparator<StockRecord> comparator;
}
```

### 5.2 檔案管理
```java
public class FileManager {
    // 讀取原始資料檔案
    public Iterator<StockRecord> readStockRecords(String filename)
    
    // 寫入排序結果
    public void writeStockRecords(List<StockRecord> records, String filename)
    
    // 臨時檔案管理
    public void cleanupTempFiles(List<String> tempFiles)
}
```

### 5.3 GUI 設計
- 檔案選擇器
- 排序欄位選擇 (RadioButton)
- 結果表格顯示前15筆
- 排序進度指示器

## 6. 檔案格式設計

### 6.1 資料檔案格式 (CSV)
```
股票代碼,股票名稱,交易日期,成交量,成交金額,最大單筆成交量,最大單筆成交金額,最小單筆成交量,最小單筆成交金額
000001,台積電,2024-01-02,1000000,50000000.0,10000,500000.0,100,5000.0
```

### 6.2 檔案分割策略
- 依記憶體限制分割大檔案
- 臨時檔案命名規則：`temp_chunk_001.csv`
- 排序結果檔案：`sorted_by_volume.csv`, `sorted_by_amount.csv`

## 7. 效能優化策略

### 7.1 記憶體管理
- 使用串流處理，避免一次載入全部資料
- 設定合理的 chunk size
- 及時釋放不需要的物件

### 7.2 IO 優化
- 使用 BufferedReader/BufferedWriter
- 批次讀寫，減少 IO 次數
- 並行處理不同 chunk 的排序

### 7.3 演算法優化
- 使用 TimSort 作為內部排序演算法
- 多路歸併時使用 Priority Queue
- 只保留需要的前15筆資料

## 8. 錯誤處理與驗證

### 8.1 資料驗證
- 檔案格式驗證
- 數值範圍檢查
- 邏輯一致性驗證

### 8.2 異常處理
- 檔案 IO 異常
- 記憶體不足異常
- 資料格式錯誤異常

## 9. 測試策略

### 9.1 單元測試
- StockRecord 類別測試
- 資料生成邏輯測試
- 排序演算法正確性測試

### 9.2 整合測試
- 完整資料流程測試
- 大資料量效能測試
- GUI 功能測試

### 9.3 效能測試
- 資料生成效能基準
- 排序演算法效能基準
- 記憶體使用量監控

## 10. 實作時程規劃

### Phase 1: 基礎架構 (1-2天)
- 建立專案結構
- 實作 StockRecord 類別
- 建立基本 GUI 框架

### Phase 2: 資料生成 (2-3天)
- 實作資料生成邏輯
- 中文名稱生成器
- 資料生成 GUI

### Phase 3: 排序系統 (3-4天)
- External Merge Sort 實作
- 檔案處理邏輯
- 排序 GUI

### Phase 4: 整合測試 (1-2天)
- 系統整合
- 效能調優
- bug 修復

## 11. 檔案結構

```
0819-Sort/
├── Design.md                    # 本設計文檔
├── Data_Requirement.md         # 資料生成需求
├── src/
│   ├── Program_Requirement.md  # 程式需求
│   ├── model/
│   │   └── StockRecord.java
│   ├── service/
│   │   ├── DataGenerator.java
│   │   ├── ChineseNameGenerator.java
│   │   ├── ExternalMergeSort.java
│   │   └── FileManager.java
│   ├── gui/
│   │   ├── DataGeneratorGUI.java
│   │   └── StockRankingGUI.java
│   ├── controller/
│   │   ├── DataGeneratorController.java
│   │   └── SortingController.java
│   └── util/
│       ├── SortField.java
│       └── Constants.java
├── data/                       # 生成的資料檔案
└── temp/                      # 臨時檔案目錄
```

## 12. 技術選型說明

- **GUI框架**: Swing (穩定、輕量)
- **資料格式**: CSV (人類可讀、易處理)
- **排序演算法**: External Merge Sort (適合大資料)
- **日期處理**: Java 8 LocalDate (API友善)
- **IO處理**: NIO + BufferedReader/Writer (高效能)

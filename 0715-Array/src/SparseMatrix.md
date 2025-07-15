### 模組：創建稀疏矩陣
- 函式：createSparseMatrix(n: int, density: float) -> Matrix
- 輸入：
  - n: 矩陣大小（n x n）
  - density: 非零元素比例（0.0 ~ 1.0）
- 流程：
  - 初始化 n x n 的矩陣 matrix
  - 計算非零元素數量 = n * n * density
  - 隨機選擇位置填入非零值（可為隨機整數）
- 輸出：
  - matrix: 二維陣列表示的稀疏矩陣

### 模組：稀疏表示法轉換
- 函式：convertToSparse(matrix: Matrix) -> SparseList
- 輸入： matrix: 二維陣列表示的矩陣
- 流程：
  - 初始化空列表 sparseList
  - 對每個元素 matrix[i][j]：
  - 若不為 0：
  - 加入三元組 (i, j, matrix[i][j]) 至 sparseList
- 輸出： 
  - sparseList: 三元組列表 [(row, col, value), ...]

### 模組：建立第二個稀疏矩陣
- 函式：createSecondMatrix(n: int, density: float) -> SparseList
- 流程：
  - 呼叫 createSparseMatrix(n, density)
  - 呼叫 convertToSparse(matrix)
- 輸出： 
  - sparseList: 第二個稀疏矩陣的三元組表示

### 模組：稀疏矩陣加法
- 函式：addSparseMatrices(sparse1: SparseList, sparse2: SparseList) -> SparseList
- 輸入： A, B: 兩個稀疏矩陣的三元組表示
- 流程：
  - 檢查 A[0] 與 B[0] 的 row, col 是否一致
  - 建立結果矩陣 resultList
  - 將 A 與 B 中的三元組合併：
  - 若 (row, col) 相同，則 value 相加否則保留原值
- 輸出： 
  - resultList: 相加後的稀疏矩陣三元組表示

### 模組：稀疏矩陣減法
- 函式：subtractSparseMatrices(sparse1: SparseList, sparse2: SparseList) -> SparseList
- 輸入： A, B: 兩個稀疏矩陣的三元組表示
- 流程：
  - 檢查 A[0] 與 B[0] 的 row, col 是否一致
  - 建立結果矩陣 resultList
  - 將 A 與 B 中的三元組合併：
  - 若 (row, col) 相同，則 value 相減否則保留原值
  - 若結果為 0，則不加入結果列表
  - 注意處理負值情況
  - 確保結果矩陣不包含零元素
- 輸出：
  - resultList: 相減後的稀疏矩陣三元組表示

### 模組：稀疏矩陣乘法
- 函式：multiplySparseMatrices(sparse1: SparseList, sparse2: SparseList) -> SparseList
- 輸入： A, B: 兩個稀疏矩陣的三元組表示
- 流程：
  - 檢查 A[0] 的 col 與 B[0] 的 row 是否一致
  - 建立結果矩陣 resultList
  - 對 A 中每個三元組 (rowA, colA, valueA)：
    - 對 B 中每個三元組 (rowB, colB, valueB)：
      - 若 colA == rowB，則計算乘積 valueA * valueB
      - 將結果加入 resultList，若已存在則累加
      - 注意處理非零元素
      - 確保結果矩陣不包含零元素
      - 確保結果矩陣的大小與預期一致
- 輸出：
  - resultList: 相乘後的稀疏矩陣三元組表示
  - 注意：乘法結果可能會有多個非零元素，需確保正確累加。

### 模組：稀疏矩陣轉置（一般方法）
- 函式：transposeSparse(A: SparseList) -> SparseList
- 流程：
  - 初始化空列表 B
  - 對 A 使用 sparse2 中每個三元組 (row, col, value)
  - 使用一般轉置方法，加入 (col, row, value) 至 B
- 輸出： 
  - B: 轉置後的稀疏矩陣三元組表示

### 模組：稀疏矩陣快速轉置
- 函式：fastTransposeSparse(A: SparseList) -> SparseList
- 流程：
  - 初始化空列表 B
  - 對 A 使用 sparse2 中每個三元組 (row, col, value)
  - 使用快入轉置方法，加入 (col, row, value) 至 B
- 輸出： 
  - B: 轉置後的稀疏矩陣三元組表示

### 模組：GUI 操作
- **主視窗架構**：
  - 視窗標題：「稀疏矩陣處理系統」
  - 尺寸：1200x800 像素
  - 布局：BorderLayout

- **輸入面板 (NORTH)**：
  - 功能：參數設定
  - 元件：
    - 矩陣大小輸入欄 (JTextField) - 預設值：5
    - 密度輸入欄 (JTextField) - 預設值：0.3
    - 生成矩陣按鈕 (JButton)
  - 驗證：矩陣大小 1-999999，密度 0.0-1.0

- **顯示面板 (CENTER)**：
  - 布局：2x3 網格布局 (GridLayout)
  - 六個文字區域面板：
    1. **矩陣1** - 顯示第一個原始矩陣
    2. **稀疏表示1** - 顯示第一個矩陣的三元組格式
    3. **矩陣2** - 顯示第二個原始矩陣
    4. **稀疏表示2** - 顯示第二個矩陣的三元組格式
    5. **運算結果** - 顯示運算後的稀疏矩陣
    6. **執行時間** - 顯示各種運算的執行時間
  - 每個面板包含：
    - 帶標題的邊框 (TitledBorder)
    - 不可編輯的文字區域 (JTextArea)
    - 捲動面板 (JScrollPane)
    - 等寬字體 (Monospaced)

- **按鈕面板 (SOUTH)**：
  - 布局：流式布局 (FlowLayout)
  - 四個操作按鈕：
    1. **矩陣加法** - 執行稀疏矩陣相加運算
    2. **矩陣減法** - 執行稀疏矩陣相減運算
    3. **矩陣乘法** - 執行稀疏矩陣相乘運算
    2. **一般轉置** - 使用基本轉置演算法
    3. **快速轉置** - 使用優化轉置演算法
    4. **清除結果** - 清空結果顯示區域

- **事件處理機制**：
  - generateMatrices() - 生成兩個稀疏矩陣並顯示
  - performAddition() - 執行矩陣加法並計時
  - performSubtraction() - 執行矩陣減法並計時
  - performMultiplication() - 執行矩陣乘法並計時
  - performTranspose() - 執行一般轉置並計時
    - 時間複雜度：O(N*M) 
    - 計時方式： 轉置時間僅計算核心算法
  - performFastTranspose() - 執行快速轉置並計時
    - 時間複雜度：O(N)
    - 計時方式： 轉置時間僅計算核心算法
  - performComparison() - 比較一般轉置和快速轉置的執行時間，使用 20 次迴圈平均時間。
  - clearResults() - 清除運算結果

- **顯示功能**：
  - displayMatrix() - 格式化顯示二維矩陣
  - displaySparse() - 顯示稀疏矩陣資訊 (大小、非零元素數、三元組列表)
  - 執行時間精確到毫秒 (ms)

- **錯誤處理**：
  - 輸入參數驗證
  - 操作前狀態檢查
  - 友善的錯誤訊息對話框 (JOptionPane)

- **使用者體驗優化**：
  - 系統外觀設定
  - 視窗居中顯示
  - 即時的操作回饋
  - 清晰的資訊分類顯示

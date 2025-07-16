package model;

import java.util.*;

/**
 * 處理稀疏矩陣運算的服務層
 */
public class MatrixService {

    /**
     * 創建稀疏矩陣
     * @param n 矩陣大小（n x n）
     * @param density 非零元素比例（0.0 ~ 1.0）
     * @return 二維陣列表示的稀疏矩陣
     */
    public int[][] createSparseMatrix(int n, double density) {
        if (n <= 0 || density < 0 || density > 1) {
            throw new IllegalArgumentException("無效的參數：n必須大於0，density必須在0-1之間");
        }

        int[][] matrix = new int[n][n];
        int nonZeroCount = (int) Math.round(n * n * density);
        Random random = new Random();
        Set<String> usedPositions = new HashSet<>();

        for (int i = 0; i < nonZeroCount; i++) {
            int row, col;
            String position;

            // 確保不重複選擇位置
            do {
                row = random.nextInt(n);
                col = random.nextInt(n);
                position = row + "," + col;
            } while (usedPositions.contains(position));

            usedPositions.add(position);
            matrix[row][col] = random.nextInt(9) + 1; // 1-9的隨機數
        }

        return matrix;
    }

    /**
     * 將二維矩陣轉換為稀疏表示法
     * @param matrix 二維陣列表示的矩陣
     * @return 三元組列表表示的稀疏矩陣
     */
    public SparseList convertToSparse(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("矩陣不能為空");
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        SparseList sparseList = new SparseList(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    sparseList.addTriple(i, j, matrix[i][j]);
                }
            }
        }

        return sparseList;
    }

    /**
     * 稀疏矩陣加法
     * @param sparse1 第一個稀疏矩陣
     * @param sparse2 第二個稀疏矩陣
     * @return 相加後的稀疏矩陣
     */
    public SparseList addSparseMatrices(SparseList sparse1, SparseList sparse2) {
        if (sparse1.rows != sparse2.rows || sparse1.cols != sparse2.cols) {
            throw new IllegalArgumentException("矩陣大小不匹配，無法相加");
        }

        SparseList result = new SparseList(sparse1.rows, sparse1.cols);
        Map<String, Integer> positionMap = new HashMap<>();

        // 處理第一個矩陣
        for (Triple t : sparse1.data) {
            String key = t.row + "," + t.col;
            positionMap.put(key, t.value);
        }

        // 處理第二個矩陣
        for (Triple t : sparse2.data) {
            String key = t.row + "," + t.col;
            int value = positionMap.getOrDefault(key, 0) + t.value;
            positionMap.put(key, value);
        }

        // 建立結果矩陣
        for (Map.Entry<String, Integer> entry : positionMap.entrySet()) {
            if (entry.getValue() != 0) {
                String[] pos = entry.getKey().split(",");
                int row = Integer.parseInt(pos[0]);
                int col = Integer.parseInt(pos[1]);
                result.addTriple(row, col, entry.getValue());
            }
        }

        return result;
    }

    /**
     * 稀疏矩陣減法
     * @param sparse1 第一個稀疏矩陣
     * @param sparse2 第二個稀疏矩陣
     * @return 相減後的稀疏矩陣
     */
    public SparseList subtractSparseMatrices(SparseList sparse1, SparseList sparse2) {
        if (sparse1.rows != sparse2.rows || sparse1.cols != sparse2.cols) {
            throw new IllegalArgumentException("矩陣大小不匹配，無法相減");
        }

        SparseList result = new SparseList(sparse1.rows, sparse1.cols);
        Map<String, Integer> positionMap = new HashMap<>();

        // 處理第一個矩陣
        for (Triple t : sparse1.data) {
            String key = t.row + "," + t.col;
            positionMap.put(key, t.value);
        }

        // 處理第二個矩陣（減法）
        for (Triple t : sparse2.data) {
            String key = t.row + "," + t.col;
            int value = positionMap.getOrDefault(key, 0) - t.value;
            positionMap.put(key, value);
        }

        // 建立結果矩陣，確保不包含零元素
        for (Map.Entry<String, Integer> entry : positionMap.entrySet()) {
            if (entry.getValue() != 0) {
                String[] pos = entry.getKey().split(",");
                int row = Integer.parseInt(pos[0]);
                int col = Integer.parseInt(pos[1]);
                result.addTriple(row, col, entry.getValue());
            }
        }

        return result;
    }

    /**
     * 稀疏矩陣乘法
     * @param sparse1 第一個稀疏矩陣
     * @param sparse2 第二個稀疏矩陣
     * @return 相乘後的稀疏矩陣
     */
    public SparseList multiplySparseMatrices(SparseList sparse1, SparseList sparse2) {
        if (sparse1.cols != sparse2.rows) {
            throw new IllegalArgumentException("矩陣大小不匹配，無法相乘：A的列數必須等於B的行數");
        }

        SparseList result = new SparseList(sparse1.rows, sparse2.cols);
        Map<String, Integer> resultMap = new HashMap<>();

        // 對於每個A中的元素(rowA, colA, valueA)
        for (Triple aTriple : sparse1.data) {
            // 對於每個B中的元素(rowB, colB, valueB)
            for (Triple bTriple : sparse2.data) {
                // 如果colA == rowB，則可以相乘
                if (aTriple.col == bTriple.row) {
                    int resultRow = aTriple.row;
                    int resultCol = bTriple.col;
                    int product = aTriple.value * bTriple.value;

                    String key = resultRow + "," + resultCol;
                    int currentValue = resultMap.getOrDefault(key, 0);
                    resultMap.put(key, currentValue + product);
                }
            }
        }

        // 建立結果矩陣，確保不包含零元素
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            if (entry.getValue() != 0) {
                String[] pos = entry.getKey().split(",");
                int row = Integer.parseInt(pos[0]);
                int col = Integer.parseInt(pos[1]);
                result.addTriple(row, col, entry.getValue());
            }
        }

        return result;
    }

    /**
     * 稀疏矩陣轉置（一般方法）
     * @param A 輸入的稀疏矩陣
     * @return B 轉置後的稀疏矩陣三元組表示
     */
    public SparseList transposeSparse(SparseList A) {
        // 初始化空列表 B
        SparseList B = new SparseList(A.cols, A.rows);

        // 對 A 中每個三元組 (row, col, value)
        // 使用一般轉置方法，加入 (col, row, value) 至 B
        for (Triple t : A.data) {
            B.addTriple(t.col, t.row, t.value);
        }

        return B;
    }

    /**
     * 稀疏矩陣快速轉置
     * @param A 輸入的稀疏矩陣
     * @return B 轉置後的稀疏矩陣三元組表示
     */
    public SparseList fastTransposeSparse(SparseList A) {
        // 初始化空列表 B
        SparseList B = new SparseList(A.cols, A.rows);

        if (A.size() == 0) {
            return B;
        }

        // 對 A 中每個三元組 (row, col, value)
        // 使用快速轉置方法，加入 (col, row, value) 至 B

        // 計算每個欄位的非零元素數量
        int[] rowTerms = new int[A.cols];
        for (Triple t : A.data) {
            rowTerms[t.col]++;
        }

        // 計算每個欄位的起始位置
        int[] startingPos = new int[A.cols];
        startingPos[0] = 0;
        for (int i = 1; i < A.cols; i++) {
            startingPos[i] = startingPos[i-1] + rowTerms[i-1];
        }

        // 建立結果矩陣的資料陣列
        Triple[] resultData = new Triple[A.size()];

        // 根據欄位順序放入元素，實現快速轉置
        for (Triple t : A.data) {
            int pos = startingPos[t.col];
            resultData[pos] = new Triple(t.col, t.row, t.value);
            startingPos[t.col]++;
        }

        // 將結果加入B
        for (Triple t : resultData) {
            B.data.add(t);
        }

        return B;
    }
}

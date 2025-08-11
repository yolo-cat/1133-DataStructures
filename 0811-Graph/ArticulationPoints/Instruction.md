兩種找割點（articulation points）的方法邏輯實作：

原始暴力法：對每個節點，移除後檢查圖的連通分量是否增加。
Tarjan 演算法：一次 DFS 遍歷，利用 low 與 disc 值判斷割點。

在 Main.java 中，已經建立了測試用的無向圖，並使用兩種方法（暴力法和 Tarjan 演算法）來找出割點。以下是測試的步驟與解釋：

建立圖：

範例圖包含 5 個節點（0 到 4），並添加了以下邊：
0 - 1, 0 - 2, 1 - 2, 1 - 3, 3 - 4

這是一個無向圖，圖的結構如下：
0
/ \
1---2
|
3
|
4

測試暴力法：
使用 BruteForceArticulationPoints 類別的 findArticulationPoints() 方法。
該方法會逐一移除每個節點，檢查圖是否變得不連通，並返回所有割點。
測試 Tarjan 演算法：

使用 TarjanArticulationPoints 類別的 findArticulationPoints() 方法。
該方法基於 DFS 遍歷圖，計算 low 和 disc 值，並高效找出所有割點。
輸出結果：

兩種方法的結果會分別打印到控制台，方便比較。
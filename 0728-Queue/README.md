# 餐廳點餐系統模擬

這個專案實作了一個具有GUI的Java餐廳點餐系統模擬，展示了Queue資料結構在實際應用中的使用，結合多執行緒程式設計和GUI動畫效果。

## 系統功能

### 核心流程
```
[ 顧客產生 (隨機時間) ] → [ 訂單加入佇列 Queue ] → [ 工作人員取出訂單製作 ] → [ 餐點完成 (送到桌上) ]
```

### 主要特色
- **顧客隨機生成**: 每隔0.5~2秒隨機產生新顧客
- **多執行緒處理**: 3個工作人員並行處理訂單
- **執行緒安全**: 使用`BlockingQueue`確保佇列操作安全
- **動畫GUI**: 使用Swing實作即時顯示系統狀態
- **速度控制**: 可透過GUI調整顧客到達速度
- **手動停止**: 透過「結束」按鈕安全停止系統

## 檔案結構

### 核心類別
1. **Order.java** - 訂單類別
   - 餐點類型（漢堡、薯條、飲料）
   - 製作時間（2秒、1秒、0.5秒）
   - 桌號、訂單時間

2. **Customer.java** - 顧客類別
   - 顧客編號、到達時間
   - 隨機生成桌號和訂單

3. **RestaurantQueue.java** - 佇列管理類別
   - 使用`BlockingQueue`實作執行緒安全
   - 提供訂單統計功能

4. **Worker.java** - 工作人員執行緒類別
   - 實作`Runnable`介面
   - 模擬餐點製作時間
   - 與GUI互動更新狀態

5. **RestaurantGUI.java** - GUI介面類別
   - 四個顯示區域：顧客、佇列、工作人員、完成訂單
   - 即時動畫效果
   - 速度調整控制項

6. **RestaurantSimulator.java** - 主程式類別
   - 整合所有元件
   - 管理執行緒生命週期
   - 處理系統啟動與停止

### 測試類別
7. **RestaurantTest.java** - 功能測試類別
   - 無GUI版本的核心功能測試
   - 驗證所有類別正常運作

## 使用方法

### 執行GUI版本
```bash
cd src
javac *.java
java RestaurantSimulator
```

### 執行測試版本
```bash
cd src
javac RestaurantTest.java
java RestaurantTest
```

## 技術重點

### 多執行緒設計
- **顧客生成器**: 使用`ScheduledExecutorService`控制顧客到達
- **工作人員**: 3個`Worker`執行緒並行處理訂單
- **GUI更新**: 使用`SwingUtilities.invokeLater`確保執行緒安全

### 執行緒安全
- **BlockingQueue**: 自動處理生產者-消費者同步問題
- **CopyOnWriteArrayList**: 安全的並行存取列表
- **ConcurrentHashMap**: 執行緒安全的狀態管理

### GUI動畫
- **顧客進店**: 閃爍動畫效果
- **即時更新**: 每100ms更新顯示內容
- **狀態顯示**: 不同顏色表示工作人員狀態
- **統計資訊**: 即時顯示系統統計數據

## 系統特性

### 餐點類型
- **漢堡**: 製作時間2秒
- **薯條**: 製作時間1秒
- **飲料**: 製作時間0.5秒

### 控制功能
- **速度調整**: 滑桿控制顧客到達頻率（1-10級）
- **結束按鈕**: 安全停止所有執行緒
- **即時統計**: 顯示總顧客數、完成訂單數、佇列長度

### 視覺效果
- **四區域顯示**: 清楚展示整個流程
- **顏色編碼**: 綠色表示等待，紅色表示處理中
- **動態更新**: 所有狀態即時反映

這個專案完整展示了Queue資料結構在實際應用中的使用，並結合了現代Java程式設計的最佳實務。
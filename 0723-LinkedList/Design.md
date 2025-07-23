# MP3 播放器設計文檔 (LinkedList 版)

## 1. 設計概述

這是一個基於 JavaFX 的 MP3 播放器應用程式，主要使用 **LinkedList** 資料結構來管理音樂播放清單，展示了鏈結串列在實際應用中的使用場景。

### 1.1 核心目標
- 學習 LinkedList 資料結構的實際應用
- 理解 GUI 程式設計的基本概念
- 掌握多媒體程式設計的基礎

### 1.2 主要功能
- 音樂播放、暫停、停止
- 上一首/下一首切換
- 播放清單管理（新增、移除）
- 音樂庫瀏覽
- 檔案導入功能

## 2. 系統架構設計

### 2.1 核心資料結構
```java
private final LinkedList<String> playlist = new LinkedList<>();      // 播放清單
private final LinkedList<String> musicLibrary = new LinkedList<>();  // 音樂庫
```

**為什麼選擇 LinkedList？**
- **動態大小**：播放清單可以動態增減歌曲
- **高效插入/刪除**：在任意位置插入或刪除歌曲效率較高
- **順序訪問**：播放清單需要按順序播放，符合鏈結串列的特性

### 2.2 UI 組件架構
```
主視窗 (VBox)
├── 當前歌曲標籤 (Label)
├── 進度條 (ProgressBar)
├── 控制按鈕區 (HBox)
│   ├── 播放、暫停、停止
│   └── 上一首、下一首
├── 管理按鈕區 (HBox)
│   ├── 切換檢視
│   └── 新增音樂
└── 歌曲列表 (ListView<HBox>)
```

## 3. 核心設計模式

### 3.1 事件驅動設計
程式採用事件驅動的設計模式，每個按鈕都有對應的事件處理器：

```java
Button playBtn = new Button("播放");
playBtn.setOnAction(e -> {
    // 事件處理邏輯
});
```

### 3.2 狀態管理設計
```java
private boolean showPlaylist = true;    // 檢視狀態（播放清單/音樂庫）
private int currentIndex = 0;           // 當前播放位置
private MediaPlayer mediaPlayer;        // 媒體播放器
```

### 3.3 MVC 概念的簡化應用
雖然沒有嚴格分離 MVC，但可以看到：
- **Model**：LinkedList（播放清單、音樂庫）
- **View**：JavaFX UI 組件
- **Controller**：事件處理方法

## 4. 關鍵演算法設計

### 4.1 播放清單管理演算法

#### 新增歌曲
```java
// 時間複雜度：O(1)
playlist.add(newPath);
```

#### 移除歌曲
```java
// 時間複雜度：O(n)，需要找到指定位置
playlist.remove(index);
// 處理當前播放位置的調整
if (currentIndex >= playlist.size()) {
    currentIndex = playlist.size() - 1;
}
```

#### 順序播放
```java
// 下一首：O(1)
if (currentIndex < playlist.size() - 1) {
    currentIndex++;
    playSong(currentIndex);
}
```

### 4.2 檔案重複處理演算法
```java
// 避免檔案名稱衝突的演算法
String baseName = selectedFile.getName();
String name = baseName;
int count = 1;
File destFile = new File(musicDir, name);
while (destFile.exists()) {
    name = nameNoExt + "_" + count + ext;
    destFile = new File(musicDir, name);
    count++;
}
```

## 5. 設計考量與決策

### 5.1 資料結構選擇
| 需求 | LinkedList 優勢 | ArrayList 比較 |
|------|----------------|----------------|
| 動態調整大小 | ✓ | ✓ |
| 中間插入/刪除 | O(1) 已知節點 | O(n) 需要移動元素 |
| 隨機訪問 | O(n) | O(1) |
| 記憶體使用 | 較高（指標開銷） | 較低 |

**結論**：因為播放清單主要是順序播放，且需要頻繁新增/移除，LinkedList 是合適的選擇。

### 5.2 UI 設計考量

#### 雙檢視設計
- **播放清單檢視**：管理當前播放順序
- **音樂庫檢視**：瀏覽所有可用音樂

#### 即時更新機制
```java
private void updateListView() {
    // 根據當前狀態重新構建 ListView
    // 確保 UI 與資料結構同步
}
```

### 5.3 錯誤處理設計
```java
// 邊界檢查
if (currentIndex >= playlist.size()) {
    currentIndex = playlist.size() - 1;
}

// 空播放清單處理
if (playlist.isEmpty()) {
    if (mediaPlayer != null) mediaPlayer.stop();
}
```

## 6. 擴展性設計

### 6.1 可擴展的功能點
1. **隨機播放**：在 playSong 方法中加入隨機邏輯
2. **重複播放**：修改 setOnEndOfMedia 處理
3. **音量控制**：加入 Slider 組件
4. **播放歷史**：再增加一個 LinkedList 記錄

### 6.2 改進建議
```java
// 可以考慮使用觀察者模式
interface PlaylistListener {
    void onSongAdded(String song);
    void onSongRemoved(int index);
    void onCurrentSongChanged(int index);
}
```

## 7. 學習重點

### 7.1 資料結構學習
- **LinkedList 操作**：add()、remove()、get()、size()
- **迭代訪問**：for-each 循環的使用
- **索引管理**：currentIndex 的維護

### 7.2 JavaFX 學習
- **事件處理**：Lambda 表達式的使用
- **Layout 管理**：VBox、HBox 的嵌套使用
- **動態 UI 更新**：ObservableList 的使用

### 7.3 程式設計原則
- **單一職責**：每個方法都有明確的功能
- **錯誤處理**：邊界條件的檢查
- **用戶體驗**：即時反饋和狀態更新

## 8. 常見問題與解決方案

### Q1: 為什麼使用 LinkedList 而不是 ArrayList？
**A**: 播放清單需要頻繁的插入和刪除操作，LinkedList 在已知位置的操作效率更高。

### Q2: 如何處理大量歌曲的效能問題？
**A**: 可以考慮：
- 懶加載（Lazy Loading）
- 虛擬化列表（Virtualized ListView）
- 分頁載入

### Q3: 如何確保 UI 與資料同步？
**A**: 使用 ObservableList 和定期調用 updateListView() 方法。

## 9. 實作建議

### 9.1 開發步驟
1. **基礎架構**：建立主視窗和基本組件
2. **資料結構**：實作 LinkedList 操作
3. **核心功能**：播放控制邏輯
4. **UI 互動**：事件處理和動態更新
5. **檔案管理**：導入和重複處理
6. **美化調整**：樣式和使用者體驗

### 9.2 測試策略
- **邊界測試**：空播放清單、單首歌曲
- **功能測試**：各按鈕功能正確性
- **整合測試**：完整播放流程

## 10. 總結

這個 MP3 播放器展示了 LinkedList 在實際應用中的使用，同時整合了 GUI 程式設計和多媒體處理。通過這個專案，學生可以學到：

1. **資料結構的實際應用**
2. **事件驅動程式設計**
3. **用戶介面設計原則**
4. **錯誤處理和邊界條件**
5. **程式碼組織和模組化**

這些都是軟體開發中的重要概念，為後續更複雜的專案打下基礎。

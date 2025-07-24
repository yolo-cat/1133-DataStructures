# 簡易畫圖程式設計文檔

## 程式概述
這是一個使用 Java Swing 開發的簡易畫圖程式，主要展示 Stack 資料結構在 Undo/Redo 功能上的應用。程式允許使用者在畫布上繪圖，並提供撤銷和重做功能。

## 主要功能
1. **畫圖功能**：滑鼠拖曳繪製線條
2. **顏色選擇**：可選擇畫筆顏色
3. **粗細調整**：可調整畫筆粗細
4. **Undo 功能**：撤銷最近的繪圖動作（最多5次）
5. **Redo 功能**：重做已撤銷的繪圖動作

## 核心設計概念

### 1. 資料結構設計
- **ArrayList&lt;Line&gt; lines**：儲存畫布上所有線段，用於繪製畫面
- **Stack&lt;ArrayList&lt;Line&gt;&gt; undoStack**：記錄可撤銷的繪圖動作
- **Stack&lt;ArrayList&lt;Line&gt;&gt; redoStack**：記錄可重做的繪圖動作
- **ArrayList&lt;Line&gt; currentStroke**：暫存當前繪圖動作的所有線段

### 2. Stack 應用原理
```
繪圖動作記錄：
mousePressed -> 開始新的 currentStroke
mouseDragged -> 將線段加入 currentStroke 和 lines
mouseReleased -> 將 currentStroke 推入 undoStack，清空 redoStack

Undo 操作：
undoStack.pop() -> 取出最近一次動作
lines.removeAll() -> 從畫布移除該動作的所有線段
redoStack.push() -> 將動作存入 redoStack 以備重做

Redo 操作：
redoStack.pop() -> 取出要重做的動作
lines.addAll() -> 將線段重新加入畫布
undoStack.push() -> 將動作重新存入 undoStack
```

## 類別架構

### 1. Painting 類別（主視窗）
- **職責**：建立 GUI 介面、處理使用者互動
- **組件**：
  - DrawPanel：繪圖畫布
  - 顏色選擇按鈕
  - 粗細調整滑桿
  - Undo/Redo 按鈕

### 2. DrawPanel 類別（畫布）
- **職責**：處理滑鼠事件、管理繪圖資料、實作 Undo/Redo
- **關鍵方法**：
  - `mousePressed()`：開始新的繪圖動作
  - `mouseDragged()`：繪製線段
  - `mouseReleased()`：完成繪圖動作並記錄
  - `undo()`：撤銷最近動作
  - `redo()`：重做動作
  - `paintComponent()`：繪製畫面

### 3. Line 類別（線段資料）
- **職責**：儲存線段的座標、顏色、粗細資訊
- **屬性**：起點座標(x1,y1)、終點座標(x2,y2)、顏色、粗細

## 建構步驟

### 步驟 1：建立基本 GUI 架構
```java
public class Painting extends JFrame {
    private DrawPanel drawPanel;
    // 建立主視窗，設定 BorderLayout
    // 加入 DrawPanel 到中央區域
    // 建立控制面板到北方區域
}
```

### 步驟 2：實作基本繪圖功能
```java
class DrawPanel extends JPanel {
    private ArrayList<Line> lines = new ArrayList<>();
    // 加入滑鼠監聽器
    // 實作 mousePressed, mouseDragged, mouseReleased
    // 實作 paintComponent 繪製所有線段
}
```

### 步驟 3：加入 Stack 資料結構
```java
private Stack<ArrayList<Line>> undoStack = new Stack<>();
private Stack<ArrayList<Line>> redoStack = new Stack<>();
private ArrayList<Line> currentStroke = null;
```

### 步驟 4：實作動作記錄機制
```java
// mousePressed：開始記錄新動作
currentStroke = new ArrayList<>();

// mouseDragged：記錄線段到當前動作
currentStroke.add(line);

// mouseReleased：完成動作記錄
undoStack.push(currentStroke);
redoStack.clear();
```

### 步驟 5：實作 Undo/Redo 功能
```java
public void undo() {
    ArrayList<Line> lastStroke = undoStack.pop();
    lines.removeAll(lastStroke);
    redoStack.push(lastStroke);
    repaint();
}

public void redo() {
    ArrayList<Line> redoStroke = redoStack.pop();
    lines.addAll(redoStroke);
    undoStack.push(redoStroke);
    repaint();
}
```

### 步驟 6：加入限制機制
- 限制 undoStack 最多儲存 5 個動作
- 當有新動作時清空 redoStack
- 檢查 Stack 是否為空以避免錯誤

## 學習重點

### 1. Stack 資料結構應用
- **LIFO 特性**：後進先出，符合 Undo/Redo 的操作邏輯
- **peek()、push()、pop()** 方法的使用
- **isEmpty()** 檢查避免例外

### 2. 物件導向設計
- **封裝**：每個類別有明確職責
- **資料隱藏**：private 欄位搭配 public 方法
- **事件驅動**：使用監聽器模式處理使用者互動

### 3. GUI 程式設計
- **Swing 組件**：JFrame、JPanel、JButton、JSlider
- **佈局管理**：BorderLayout 的使用
- **事件處理**：ActionListener、MouseListener、MouseMotionListener
- **自訂繪圖**：覆寫 paintComponent() 方法

### 4. 資料結構整合應用
- **ArrayList** 用於動態儲存線段
- **Stack** 用於記錄操作歷史
- 不同資料結構的適當選擇和搭配使用

## 擴充建議
1. **儲存/載入功能**：序列化繪圖資料
2. **更多繪圖工具**：矩形、圓形、文字等
3. **圖層功能**：多圖層繪圖
4. **快捷鍵**：Ctrl+Z (Undo)、Ctrl+Y (Redo)
5. **檔案格式支援**：匯出為 PNG、JPEG 等格式

## 常見問題與解決方案

### Q1：為什麼不直接用 Stack&lt;Line&gt;？
**A**：因為一次繪圖動作（滑鼠按下到放開）會產生多個線段，使用 Stack&lt;ArrayList&lt;Line&gt;&gt; 可以將整個動作當作一個單位進行 Undo/Redo。

### Q2：為什麼需要 currentStroke？
**A**：currentStroke 用來暫時收集當前繪圖動作的所有線段，直到 mouseReleased 時才一次性推入 undoStack。

### Q3：如何避免記憶體洩漏？
**A**：透過限制 undoStack 大小（MAX_UNDO = 5）和適時清空 redoStack 來控制記憶體使用。

## 總結
這個程式成功展示了 Stack 資料結構在實際應用中的價值，特別是在需要「歷史記錄」和「回復操作」的場景。透過適當的資料結構設計和物件導向程式設計原則，我們可以建立出功能完整且易於維護的應用程式。

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private JTextField sizeField, searchField;
    private JTextArea matrixArea, sortedArea, resultArea;
    private JButton generateBtn, searchBtn, clearBtn, btnCompare, btnRandomMode;
    private JComboBox<String> searchTypeBox;

    public GUI() {
        setTitle("姓名：陶子中／學號：D1397056 - 搜尋演算法性能比較");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 建立上方面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Font uiFont = new Font("Dialog", Font.PLAIN, 16);

        // 矩陣大小輸入
        topPanel.add(new JLabel("矩陣大小N:"));
        sizeField = new JTextField(5);
        sizeField.setFont(uiFont);
        topPanel.add(sizeField);

        // 生成矩陣按鈕
        generateBtn = new JButton("生成矩陣");
        generateBtn.setFont(uiFont);
        topPanel.add(generateBtn);

        // 清除矩陣按鈕
        clearBtn = new JButton("清除矩陣");
        clearBtn.setFont(uiFont);
        topPanel.add(clearBtn);

        // 搜尋數字輸入
        JLabel searchLabel = new JLabel("搜尋數字:");
        searchLabel.setFont(uiFont);
        topPanel.add(searchLabel);
        searchField = new JTextField(5);
        searchField.setFont(uiFont);
        topPanel.add(searchField);

        // 搜尋方法選擇
        JLabel methodLabel = new JLabel("搜尋方法:");
        methodLabel.setFont(uiFont);
        topPanel.add(methodLabel);
        searchTypeBox = new JComboBox<>(new String[]{"循序搜尋", "二元搜尋", "雜湊搜尋"});
        searchTypeBox.setFont(uiFont);
        topPanel.add(searchTypeBox);

        // 搜尋按鈕
        searchBtn = new JButton("搜尋");
        searchBtn.setFont(uiFont);
        topPanel.add(searchBtn);

//        // 清除矩陣按鈕
//        clearBtn = new JButton("清除矩陣");
//        clearBtn.setFont(uiFont);
//        topPanel.add(clearBtn);

        // 比較所有搜尋方法按鈕
        btnCompare = new JButton("比較所有搜尋方法");
        btnCompare.setFont(uiFont);
        topPanel.add(btnCompare);

        // 隨機模式按鈕
        btnRandomMode = new JButton("隨機模式(20次)");
        btnRandomMode.setFont(uiFont);
        topPanel.add(btnRandomMode);

        add(topPanel, BorderLayout.NORTH);

        // 建立中央面板（矩陣顯示區域）
        matrixArea = new JTextArea(10, 40);
        matrixArea.setFont(uiFont);
        matrixArea.setEditable(false);

        sortedArea = new JTextArea(10, 40);
        sortedArea.setFont(uiFont);
        sortedArea.setEditable(false);

        JPanel matrixPanel = new JPanel(new GridLayout(2, 1));

        JLabel matrixLabel = new JLabel("原始矩陣：");
        matrixLabel.setFont(uiFont);
        matrixPanel.add(matrixLabel);
        matrixPanel.add(new JScrollPane(matrixArea));

        JLabel sortedLabel = new JLabel("排序後一維陣列：");
        sortedLabel.setFont(uiFont);
        matrixPanel.add(sortedLabel);
        matrixPanel.add(new JScrollPane(sortedArea));

        add(matrixPanel, BorderLayout.CENTER);

        // 建立右側結果顯示區域
        resultArea = new JTextArea(20, 30);
        resultArea.setFont(uiFont);
        resultArea.setEditable(false);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        add(resultScroll, BorderLayout.EAST);

        // 設定事件監聽器
        generateBtn.addActionListener(e -> onGenerate());
        searchBtn.addActionListener(e -> onSearch());
        clearBtn.addActionListener(e -> onClear());
        btnCompare.addActionListener(e -> onCompare());
        btnRandomMode.addActionListener(e -> onRandomMode());
    }

    private void onGenerate() {
        try {
            int n = Integer.parseInt(sizeField.getText());
            if (n <= 0) throw new NumberFormatException();
            Main.matrix = null;
            Main.generateMatrix(n);
            resultArea.setText("成功生成 " + n + "*" + n + " 的矩陣!\n");
            matrixArea.setText(Main.getMatrixString());
            sortedArea.setText("");
        } catch (NumberFormatException ex) {
            resultArea.setText("請輸入正確的整數N!\n");
        }
    }

    private void onSearch() {
        if (Main.matrix == null) {
            resultArea.setText("請先生成矩陣!\n");
            return;
        }
        try {
            int target = Integer.parseInt(searchField.getText());
            int type = searchTypeBox.getSelectedIndex();
            String res;
            if (type == 0) { // 循序搜尋
                res = Main.guiSequentialSearch(target);
                sortedArea.setText("");
            } else if (type == 1) { // 二元搜尋
                Main.prepareDataForSearch();
                res = Main.guiBinarySearch(target);
                sortedArea.setText(Main.getSortedArrayString());
            } else { // 雜湊搜尋
                Main.prepareDataForSearch();
                res = Main.guiHashSearch(target); // 會顯示兩種時間
                sortedArea.setText("");
            }
            resultArea.setText(res);
        } catch (NumberFormatException ex) {
            resultArea.setText("請輸入正確的搜尋數字!\n");
        }
    }

    private void onClear() {
        Main.clearMatrix();
        matrixArea.setText("");
        sortedArea.setText("");
        resultArea.setText("已清除矩陣！\n");
        sizeField.setText("");
        searchField.setText("");
    }

    private void onCompare() {
        if (Main.matrix == null) {
            resultArea.setText("請先生成矩陣!\n");
            return;
        }
        try {
            int target = Integer.parseInt(searchField.getText());
            // 取得三種搜尋法排序結果
            String res = Main.compareAllSearchMethods(target);
            resultArea.setText(res);
            sortedArea.setText(Main.getSortedArrayString());
        } catch (NumberFormatException ex) {
            resultArea.setText("請輸入正確的搜尋數字!\n");
        }
    }

    private void onRandomMode() {
        if (Main.matrix == null) {
            resultArea.setText("請先生成矩陣!\n");
            return;
        }
        int n = Main.matrixSize;
        if (n <= 0) {
            resultArea.setText("請先生成有效的矩陣!\n");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("隨機模式(20次)搜尋結果：\n");
        long totalSeq = 0, totalBin = 0, totalHash = 0;
        int foundSeq = 0, foundBin = 0, foundHash = 0;
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < 20; i++) {
            int target = Main.matrix[rand.nextInt(n)][rand.nextInt(n)];
            sb.append("第").append(i+1).append("次，搜尋數字: ").append(target).append("\n");
            // 循序搜尋
            long t1 = System.nanoTime();
            Main.Position seqResult = SequentialSearch.search(Main.matrix, n, target);
            long t2 = System.nanoTime();
            long seqTime = t2 - t1;
            totalSeq += seqTime;
            if (seqResult != null) foundSeq++;
            // 二元搜尋
            Main.prepareDataForSearch();
            t1 = System.nanoTime();
            boolean binResult = BinarySearch.search(Main.sortedArray, target);
            t2 = System.nanoTime();
            long binTime = t2 - t1;
            totalBin += binTime;
            if (binResult) foundBin++;
            // 雜湊搜尋
            t1 = System.nanoTime();
            boolean hashResult = HashSearch.containsKey(Main.hashMap, target);
            t2 = System.nanoTime();
            long hashTime = t2 - t1;
            totalHash += hashTime;
            if (hashResult) foundHash++;
        }
        sb.append("\n--- 平均搜尋時間(20次) ---\n");
        sb.append("循序搜尋: ").append(totalSeq/20).append(" 奈秒\n");
        sb.append("二元搜尋: ").append(totalBin/20).append(" 奈秒\n");
        sb.append("雜湊搜尋: ").append(totalHash/20).append(" 奈秒\n");
        // 性能排序
        long[] times = {totalSeq/20, totalBin/20, totalHash/20};
        String[] names = {"循序搜尋", "二元搜尋", "雜湊搜尋"};
        String[] levels = new String[3];
        int maxIdx = 0, minIdx = 0, midIdx = 0;
        for (int i = 1; i < 3; i++) {
            if (times[i] > times[maxIdx]) maxIdx = i;
            if (times[i] < times[minIdx]) minIdx = i;
        }
        for (int i = 0; i < 3; i++) {
            if (i != maxIdx && i != minIdx) midIdx = i;
        }
        levels[maxIdx] = "較慢";
        levels[midIdx] = "普通";
        levels[minIdx] = "快速";
        sb.append("\n--- 平均性能比較 ---\n");
        for (int i = 0; i < 3; i++) {
            sb.append(names[i]).append(": ").append(times[i]).append(" 奈秒 (等級: ").append(levels[i]).append(")\n");
        }
        sb.append("\n排序: ");
        for (String level : new String[]{"較慢", "普通", "快速"}) {
            for (int i = 0; i < 3; i++) {
                if (levels[i].equals(level)) {
                    sb.append(names[i]).append(" ");
                }
            }
        }
        sb.append("\n");
        resultArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}

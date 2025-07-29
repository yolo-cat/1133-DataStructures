import javax.swing.*;
import java.awt.*;
import java.util.*;

public class BinaryTreeArrayGUI extends JFrame {
    private BinaryTreeArray tree;
    private Integer[] arr;
    private JTextField inputField;
    private JTextField insertIdxField, insertValField;
    private JTextField deleteIdxField;
    private JTextArea outputArea;
    private JLabel nodeCountLabel;
    private TreeVisualizationPanel treePanel;
    private JRadioButton inorderBtn, preorderBtn, postorderBtn;
    private JButton visitBtn;
    private JButton stepBtn, resetBtn;
    private javax.swing.Timer walkTimer;
    private java.util.List<Integer> currentTraversalPath;

    public BinaryTreeArrayGUI() {
        setTitle("Binary Tree/Heap 教學系統");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout(10, 10));

        // 設定主要背景色
        getContentPane().setBackground(new Color(248, 249, 250));

        // 頂部輸入面板
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "建立二元樹",
            0, 0, new Font("Arial", Font.BOLD, 14)));
        topPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        inputField = new JTextField(25);
        inputField.setFont(new Font("Arial", Font.PLAIN, 12));
        JButton buildBtn = new JButton("建立/重設樹");
        buildBtn.setBackground(new Color(144, 238, 144));  // 淡綠色背景
        buildBtn.setForeground(Color.BLACK);  // 黑色文字
        buildBtn.setFont(new Font("Arial", Font.BOLD, 12));

        JButton autoBtn = new JButton("自動產生隨機數");
        autoBtn.setBackground(new Color(144, 238, 144));  // 淡綠色背景
        autoBtn.setForeground(Color.BLACK);  // 黑色文字
        autoBtn.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField autoNField = new JTextField(5);
        autoNField.setFont(new Font("Arial", Font.PLAIN, 12));

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("輸入陣列(逗號分隔, null為空):"), gbc);
        gbc.gridx = 1;
        topPanel.add(inputField, gbc);
        gbc.gridx = 2;
        topPanel.add(buildBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("或產生 N 個隨機數:"), gbc);
        gbc.gridx = 1;
        topPanel.add(autoNField, gbc);
        gbc.gridx = 2;
        topPanel.add(autoBtn, gbc);

        add(topPanel, BorderLayout.NORTH);

        // 中央操作面板
        JPanel centerPanel = new JPanel(new BorderLayout());

        // 左側操作區
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "節點操作",
            0, 0, new Font("Arial", Font.BOLD, 14)));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.insets = new Insets(10, 10, 5, 10);
        leftGbc.anchor = GridBagConstraints.WEST;
        leftGbc.fill = GridBagConstraints.HORIZONTAL;

        // 插入/修改節點區域
        JPanel insertPanel = new JPanel(new GridBagLayout());
        insertPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(144, 238, 144), 1), "插入/修改節點"));
        insertPanel.setBackground(Color.WHITE);

        GridBagConstraints insertGbc = new GridBagConstraints();
        insertGbc.insets = new Insets(8, 8, 8, 8);
        insertGbc.anchor = GridBagConstraints.WEST;

        insertIdxField = new JTextField(15);
        insertValField = new JTextField(15);
        insertIdxField.setFont(new Font("Arial", Font.PLAIN, 14));
        insertValField.setFont(new Font("Arial", Font.PLAIN, 14));
        insertIdxField.setHorizontalAlignment(JTextField.LEFT);
        insertValField.setHorizontalAlignment(JTextField.LEFT);

        // 設定較大的首選大小
        insertIdxField.setPreferredSize(new Dimension(150, 28));
        insertValField.setPreferredSize(new Dimension(150, 28));

        JButton insertBtn = new JButton("插入/修改");
        insertBtn.setBackground(new Color(144, 238, 144));
        insertBtn.setForeground(Color.BLACK);
        insertBtn.setFont(new Font("Arial", Font.BOLD, 12));
        insertBtn.setPreferredSize(new Dimension(140, 32));

        insertGbc.gridx = 0; insertGbc.gridy = 0;
        insertPanel.add(new JLabel("索引:"), insertGbc);
        insertGbc.gridx = 1; insertGbc.weightx = 1.0; insertGbc.fill = GridBagConstraints.HORIZONTAL;
        insertPanel.add(insertIdxField, insertGbc);

        insertGbc.gridx = 0; insertGbc.gridy = 1; insertGbc.weightx = 0.0; insertGbc.fill = GridBagConstraints.NONE;
        insertPanel.add(new JLabel("值:"), insertGbc);
        insertGbc.gridx = 1; insertGbc.weightx = 1.0; insertGbc.fill = GridBagConstraints.HORIZONTAL;
        insertPanel.add(insertValField, insertGbc);

        insertGbc.gridx = 0; insertGbc.gridy = 2; insertGbc.gridwidth = 2; insertGbc.weightx = 0.0;
        insertGbc.fill = GridBagConstraints.HORIZONTAL; insertGbc.insets = new Insets(12, 8, 8, 8);
        insertPanel.add(insertBtn, insertGbc);

        // 刪除節點區域
        JPanel deletePanel = new JPanel(new GridBagLayout());
        deletePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(144, 238, 144), 1), "刪除節點"));
        deletePanel.setBackground(Color.WHITE);

        GridBagConstraints deleteGbc = new GridBagConstraints();
        deleteGbc.insets = new Insets(8, 8, 8, 8);
        deleteGbc.anchor = GridBagConstraints.WEST;

        deleteIdxField = new JTextField(15);
        deleteIdxField.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteIdxField.setHorizontalAlignment(JTextField.LEFT);
        deleteIdxField.setPreferredSize(new Dimension(150, 28));

        JButton deleteBtn = new JButton("刪除節點");
        deleteBtn.setBackground(new Color(144, 238, 144));
        deleteBtn.setForeground(Color.BLACK);
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 12));
        deleteBtn.setPreferredSize(new Dimension(140, 32));

        deleteGbc.gridx = 0; deleteGbc.gridy = 0;
        deletePanel.add(new JLabel("索引:"), deleteGbc);
        deleteGbc.gridx = 1; deleteGbc.weightx = 1.0; deleteGbc.fill = GridBagConstraints.HORIZONTAL;
        deletePanel.add(deleteIdxField, deleteGbc);

        deleteGbc.gridx = 0; deleteGbc.gridy = 1; deleteGbc.gridwidth = 2; deleteGbc.weightx = 0.0;
        deleteGbc.fill = GridBagConstraints.HORIZONTAL; deleteGbc.insets = new Insets(12, 8, 8, 8);
        deletePanel.add(deleteBtn, deleteGbc);

        // 走訪選項區域
        JPanel visitPanel = new JPanel(new GridBagLayout());
        visitPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(144, 238, 144), 1), "樹走訪"));
        visitPanel.setBackground(Color.WHITE);

        GridBagConstraints visitGbc = new GridBagConstraints();
        visitGbc.insets = new Insets(5, 5, 5, 5);
        visitGbc.anchor = GridBagConstraints.WEST;
        visitGbc.fill = GridBagConstraints.HORIZONTAL;

        inorderBtn = new JRadioButton("中序 (Inorder)");
        preorderBtn = new JRadioButton("前序 (Preorder)");
        postorderBtn = new JRadioButton("後序 (Postorder)");

        inorderBtn.setBackground(Color.WHITE);
        preorderBtn.setBackground(Color.WHITE);
        postorderBtn.setBackground(Color.WHITE);

        visitBtn = new JButton("執行走訪");
        visitBtn.setBackground(new Color(144, 238, 144));
        visitBtn.setForeground(Color.BLACK);
        visitBtn.setFont(new Font("Arial", Font.BOLD, 11));

        stepBtn = new JButton("逐步顯示");
        stepBtn.setBackground(new Color(144, 238, 144));
        stepBtn.setForeground(Color.BLACK);
        stepBtn.setFont(new Font("Arial", Font.BOLD, 11));
        stepBtn.setEnabled(false);

        resetBtn = new JButton("重置");
        resetBtn.setBackground(new Color(144, 238, 144));
        resetBtn.setForeground(Color.BLACK);
        resetBtn.setFont(new Font("Arial", Font.BOLD, 11));
        resetBtn.setEnabled(false);

        visitGbc.gridx = 0; visitGbc.gridy = 0; visitGbc.gridwidth = 3;
        visitPanel.add(inorderBtn, visitGbc);
        visitGbc.gridy = 1;
        visitPanel.add(preorderBtn, visitGbc);
        visitGbc.gridy = 2;
        visitPanel.add(postorderBtn, visitGbc);

        // 控制按鈕區域
        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 3, 3));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(visitBtn);
        controlPanel.add(stepBtn);
        controlPanel.add(resetBtn);

        visitGbc.gridy = 3; visitGbc.insets = new Insets(10, 5, 5, 5);
        visitPanel.add(controlPanel, visitGbc);

        // 將各個面板添加到左側主面板
        leftGbc.gridx = 0; leftGbc.gridy = 0; leftGbc.weightx = 1.0;
        leftPanel.add(insertPanel, leftGbc);

        leftGbc.gridy = 1; leftGbc.insets = new Insets(5, 10, 5, 10);
        leftPanel.add(deletePanel, leftGbc);

        leftGbc.gridy = 2; leftGbc.weighty = 1.0;
        leftPanel.add(visitPanel, leftGbc);

        centerPanel.add(leftPanel, BorderLayout.WEST);

        // 右側樹狀顯示區
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "樹狀結構視覺化",
            0, 0, new Font("Arial", Font.BOLD, 14)));
        rightPanel.setBackground(Color.WHITE);

        treePanel = new TreeVisualizationPanel();
        rightPanel.add(treePanel, BorderLayout.CENTER);

        centerPanel.add(rightPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 底部資訊面板
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 節點數顯示
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(248, 249, 250));
        nodeCountLabel = new JLabel("節點數: 0");
        nodeCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nodeCountLabel.setForeground(new Color(0, 123, 255));
        infoPanel.add(nodeCountLabel);
        bottomPanel.add(infoPanel, BorderLayout.NORTH);

        // 輸出結果區
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "走訪結果",
            0, 0, new Font("Arial", Font.BOLD, 14)));
        outputPanel.setBackground(Color.WHITE);

        outputArea = new JTextArea(6, 80);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        outputArea.setBackground(new Color(248, 249, 250));
        outputArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        bottomPanel.add(outputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 事件處理
        buildBtn.addActionListener(e -> buildTreeFromInput());
        autoBtn.addActionListener(e -> buildTreeAuto(autoNField.getText()));
        insertBtn.addActionListener(e -> insertNode());
        deleteBtn.addActionListener(e -> deleteNode());
        visitBtn.addActionListener(e -> visitTree());
        stepBtn.addActionListener(e -> stepTraversal());
        resetBtn.addActionListener(e -> resetTraversal());

        // 初始化 Timer
        walkTimer = new javax.swing.Timer(800, e -> {
            if (treePanel.hasMoreSteps()) {
                treePanel.showNextVisitStep();
            } else {
                walkTimer.stop();
                stepBtn.setEnabled(false);
            }
        });

        // 設定視窗居中
        setLocationRelativeTo(null);
    }

    private void buildTreeFromInput() {
        String input = inputField.getText().trim();
        arr = BinaryTreeArray.parseInput(input);
        tree = new BinaryTreeArray(arr);
        outputArea.setText("建立樹的陣列參數: " + Arrays.toString(Arrays.copyOfRange(arr, 1, arr.length)));
        updateDisplay(false);
    }

    private void buildTreeAuto(String nStr) {
        int n;
        try {
            n = Integer.parseInt(nStr.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "請輸入正整數N");
            return;
        }
        if (n <= 0) {
            JOptionPane.showMessageDialog(this, "N必須大於0");
            return;
        }
        Set<Integer> nums = new LinkedHashSet<>();
        Random rand = new Random();
        while (nums.size() < n) nums.add(rand.nextInt(10000));
        arr = new Integer[n + 1];
        arr[0] = n;
        int i = 1;
        for (int num : nums) arr[i++] = num;
        tree = new BinaryTreeArray(arr);
        inputField.setText("");
        outputArea.setText("建立樹的陣列參數: " + Arrays.toString(Arrays.copyOfRange(arr, 1, arr.length)));
        updateDisplay(false);
    }

    private void insertNode() {
        if (arr == null) return;
        int idx;
        try {
            idx = Integer.parseInt(insertIdxField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "請輸入索引");
            return;
        }
        String valStr = insertValField.getText().trim();
        Integer val = valStr.equalsIgnoreCase("null") ? null : null;
        if (!valStr.equalsIgnoreCase("null")) {
            try {
                val = Integer.parseInt(valStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "請輸入整數或null");
                return;
            }
        }
        if (idx > 0 && idx < arr.length) {
            if (arr[idx] == null && val != null) arr[0]++;
            if (arr[idx] != null && val == null) arr[0]--;
            arr[idx] = val;
            tree = new BinaryTreeArray(arr);
            updateDisplay(false);
        } else {
            JOptionPane.showMessageDialog(this, "索引超出範圍");
        }
    }

    private void deleteNode() {
        if (arr == null) return;
        int idx;
        try {
            idx = Integer.parseInt(deleteIdxField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "請輸入索引");
            return;
        }
        int[] delCount = new int[]{0};
        countDelete(arr, idx, delCount);
        tree.delete(idx);
        arr[0] -= delCount[0];
        updateDisplay(false);
    }

    private void updateDisplay(boolean showTraversal) {
        if (arr == null) return;
        nodeCountLabel.setText("節點數: " + arr[0]);

        // 更新樹狀視覺化
        treePanel.setTreeArray(arr);

        if (tree != null && showTraversal) {
            StringBuilder sb = new StringBuilder();
            if (inorderBtn.isSelected()) {
                sb.append("中序: ").append(tree.inorder()).append("\n");
            }
            if (preorderBtn.isSelected()) {
                sb.append("前序: ").append(tree.preorder()).append("\n");
            }
            if (postorderBtn.isSelected()) {
                sb.append("後序: ").append(tree.postorder()).append("\n");
            }
            if (sb.length() == 0) {
                sb.append("請至少選擇一種走訪方式");
            }
            outputArea.setText(sb.toString());
        }
    }

    // 遞迴計算將被刪除的節點數
    private static void countDelete(Integer[] arr, int idx, int[] count) {
        if (idx <= 0 || idx >= arr.length || arr[idx] == null) return;
        count[0]++;
        countDelete(arr, 2 * idx, count);
        countDelete(arr, 2 * idx + 1, count);
    }

    private void visitTree() {
        if (tree == null) return;

        currentTraversalPath = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        if (inorderBtn.isSelected()) {
            java.util.List<Integer> inorderResult = tree.inorder();
            currentTraversalPath.addAll(inorderResult);
            sb.append("中序: ").append(inorderResult).append("\n");
        }
        if (preorderBtn.isSelected()) {
            java.util.List<Integer> preorderResult = tree.preorder();
            if (!inorderBtn.isSelected()) currentTraversalPath.addAll(preorderResult);
            sb.append("前序: ").append(preorderResult).append("\n");
        }
        if (postorderBtn.isSelected()) {
            java.util.List<Integer> postorderResult = tree.postorder();
            if (!inorderBtn.isSelected() && !preorderBtn.isSelected()) currentTraversalPath.addAll(postorderResult);
            sb.append("後序: ").append(postorderResult).append("\n");
        }

        if (sb.length() == 0) {
            sb.append("請至少選擇一種走訪方式");
            outputArea.setText(sb.toString());
            return;
        }

        // 設定走訪路徑到視覺化面板
        treePanel.setVisitPath(currentTraversalPath);
        outputArea.setText(sb.toString());

        // 啟用控制按鈕
        stepBtn.setEnabled(true);
        resetBtn.setEnabled(true);
    }

    private void stepTraversal() {
        if (currentTraversalPath != null && !currentTraversalPath.isEmpty()) {
            if (treePanel.hasMoreSteps()) {
                treePanel.showNextVisitStep();
                if (!treePanel.hasMoreSteps()) {
                    stepBtn.setEnabled(false);
                }
            }
        }
    }

    private void resetTraversal() {
        if (walkTimer.isRunning()) {
            walkTimer.stop();
        }
        treePanel.clearVisitPath();
        stepBtn.setEnabled(currentTraversalPath != null && !currentTraversalPath.isEmpty());
        resetBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BinaryTreeArrayGUI gui = new BinaryTreeArrayGUI();
            gui.setVisible(true);
        });
    }
}

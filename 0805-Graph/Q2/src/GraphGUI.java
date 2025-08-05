import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GraphGUI extends JFrame {
  private Graph graph = new Graph();
  private JTextField nodeNameField = new JTextField(5);
  private JTextField fromField = new JTextField(3);
  private JTextField toField = new JTextField(3);
  private JTextField nodeCountField = new JTextField(3);
  private JTextField edgeCountField = new JTextField(3);
  private JTextField startNodeField = new JTextField(3);
  private JTextField pathFromField = new JTextField(3);
  private JTextField pathToField = new JTextField(3);
  private java.util.List<String> traversalResult = new ArrayList<>();
  private int animationIndex = 0;
  private javax.swing.Timer animationTimer;

  // Windows 3.1 復古顏色
  private static final Color WIN31_GRAY = new Color(192, 192, 192);
  private static final Color WIN31_DARK_GRAY = new Color(128, 128, 128);
  private static final Color WIN31_LIGHT_GRAY = new Color(224, 224, 224);
  private static final Color WIN31_BLUE = new Color(0, 0, 128);
  private static final Color WIN31_BLACK = Color.BLACK;
  private static final Color WIN31_WHITE = Color.WHITE;

  // 高亮顯示的邊（MST或最短路徑）
  private java.util.List<Edge> highlightEdges = new ArrayList<>();
  private String highlightType = ""; // "MST" or "PATH"

  public GraphGUI() {
    setTitle("Graph Application - Windows 3.1 Style");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 700);

    // 設置整體外觀
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      setupWin31Colors();
    } catch (Exception e) {
      System.err.println("Failed to set look and feel: " + e.getMessage());
    }

    GraphPanel panel = new GraphPanel();
    panel.setBackground(WIN31_WHITE);
    panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    add(panel, BorderLayout.CENTER);

    JPanel controlPanel = createControlPanel();
    add(controlPanel, BorderLayout.EAST);

    setLocationRelativeTo(null);
  }

  private void setupWin31Colors() {
    UIManager.put("Panel.background", WIN31_GRAY);
    UIManager.put("Button.background", WIN31_GRAY);
    UIManager.put("TextField.background", WIN31_WHITE);
    UIManager.put("TextArea.background", WIN31_WHITE);
    UIManager.put("Label.foreground", WIN31_BLACK);
    UIManager.put("Button.font", new Font("MS Sans Serif", Font.PLAIN, 11));
    UIManager.put("Label.font", new Font("MS Sans Serif", Font.PLAIN, 11));
    UIManager.put("TextField.font", new Font("MS Sans Serif", Font.PLAIN, 11));
  }

  private JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    controlPanel.setBackground(WIN31_GRAY);
    controlPanel.setBorder(new CompoundBorder(
      new BevelBorder(BevelBorder.RAISED),
      new EmptyBorder(10, 10, 10, 10)
    ));
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
    controlPanel.setPreferredSize(new Dimension(280, 0));

    // 節點操作區域 (隱藏)
//    controlPanel.add(createNodeSection());
//    controlPanel.add(Box.createVerticalStrut(10));

    // 邊操作區域 (隱藏)
//    controlPanel.add(createEdgeSection());
//    controlPanel.add(Box.createVerticalStrut(10));

    // 自動生成區域
    controlPanel.add(createAutoGenSection());
    controlPanel.add(Box.createVerticalStrut(10));

    // // 遍歷區域 (隱藏)
    // controlPanel.add(createTraversalSection());
    // controlPanel.add(Box.createVerticalStrut(10));

    // MST和最短路徑區域
    controlPanel.add(createAlgorithmSection());
    controlPanel.add(Box.createVerticalStrut(10));

    // 輸出區域
    controlPanel.add(createOutputSection());

    return controlPanel;
  }

  private JPanel createNodeSection() {
    JPanel panel = createSectionPanel("Node Operations");

    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    inputPanel.setBackground(WIN31_GRAY);
    inputPanel.add(new JLabel("Name:"));
    styleTextField(nodeNameField);
    inputPanel.add(nodeNameField);
    panel.add(inputPanel);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(WIN31_GRAY);

    JButton addNodeBtn = createWin31Button("Add Node");
    JButton removeNodeBtn = createWin31Button("Remove");

    addNodeBtn.addActionListener(e -> {
      String name = nodeNameField.getText().trim();
      if (!name.isEmpty() && !graph.containsNode(name)) {
        graph.addNode(name, new Point(0, 0));
        nodeNameField.setText("");
        arrangeNodesAsPentagon();
        repaint();
      }
    });

    removeNodeBtn.addActionListener(e -> {
      String name = nodeNameField.getText().trim();
      if (graph.containsNode(name)) {
        graph.removeNode(name);
        nodeNameField.setText("");
        arrangeNodesAsPentagon();
        repaint();
      }
    });

    buttonPanel.add(addNodeBtn);
    buttonPanel.add(removeNodeBtn);
    panel.add(buttonPanel);

    return panel;
  }

  private JPanel createEdgeSection() {
    JPanel panel = createSectionPanel("Edge Operations");

    JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
    inputPanel.setBackground(WIN31_GRAY);

    inputPanel.add(new JLabel("From:"));
    styleTextField(fromField);
    inputPanel.add(fromField);
    inputPanel.add(new JLabel("To:"));
    styleTextField(toField);
    inputPanel.add(toField);

    panel.add(inputPanel);

    JButton addEdgeBtn = createWin31Button("Add Edge");
    addEdgeBtn.addActionListener(e -> {
      String from = fromField.getText().trim();
      String to = toField.getText().trim();
      if (graph.containsNode(from) && graph.containsNode(to) && !from.equals(to)) {
        graph.addEdge(from, to);
        fromField.setText("");
        toField.setText("");
        repaint();
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(WIN31_GRAY);
    buttonPanel.add(addEdgeBtn);
    panel.add(buttonPanel);

    return panel;
  }

  private JPanel createAutoGenSection() {
    JPanel panel = createSectionPanel("Auto Generate");

    JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
    inputPanel.setBackground(WIN31_GRAY);

    inputPanel.add(new JLabel("Nodes:"));
    styleTextField(nodeCountField);
    inputPanel.add(nodeCountField);
    inputPanel.add(new JLabel("Edges:"));
    styleTextField(edgeCountField);
    inputPanel.add(edgeCountField);

    panel.add(inputPanel);

    JButton autoGenBtn = createWin31Button("Generate Graph");
    autoGenBtn.addActionListener(e -> {
      autoGenerateGraph();
      arrangeNodesAsPentagon();
      repaint();
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(WIN31_GRAY);
    buttonPanel.add(autoGenBtn);
    panel.add(buttonPanel);

    return panel;
  }

  private JPanel createTraversalSection() {
    JPanel panel = createSectionPanel("Graph Traversal");

    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    inputPanel.setBackground(WIN31_GRAY);
    inputPanel.add(new JLabel("Start:"));
    styleTextField(startNodeField);
    inputPanel.add(startNodeField);
    panel.add(inputPanel);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(WIN31_GRAY);

    JButton dfsBtn = createWin31Button("DFS");
    JButton bfsBtn = createWin31Button("BFS");

    dfsBtn.addActionListener(e -> {
      String start = startNodeField.getText().trim();
      if (!graph.containsNode(start)) {
        showWin31MessageDialog("Start node does not exist.", "Error");
        return;
      }
      traversalResult = graph.dfs(start);
      updateOutputArea("DFS: " + String.join(" → ", traversalResult));
      startAnimation();
    });

    bfsBtn.addActionListener(e -> {
      String start = startNodeField.getText().trim();
      if (!graph.containsNode(start)) {
        showWin31MessageDialog("Start node does not exist.", "Error");
        return;
      }
      traversalResult = graph.bfs(start);
      updateOutputArea("BFS: " + String.join(" → ", traversalResult));
      startAnimation();
    });

    buttonPanel.add(dfsBtn);
    buttonPanel.add(bfsBtn);
    panel.add(buttonPanel);

    return panel;
  }

  private JPanel createAlgorithmSection() {
    JPanel panel = createSectionPanel("Algorithms");

    JButton mstBtn = createWin31Button("Show MST");
    mstBtn.addActionListener(e -> {
      highlightEdges = graph.getMST();
      highlightType = "MST";
      int totalCost = highlightEdges.stream().mapToInt(edge -> edge.cost).sum();
      updateOutputArea("MST Total Cost: " + totalCost);
      repaint();
    });

    JPanel mstPanel = new JPanel(new FlowLayout());
    mstPanel.setBackground(WIN31_GRAY);
    mstPanel.add(mstBtn);
    panel.add(mstPanel);

    // 最短路徑
    JPanel pathInputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
    pathInputPanel.setBackground(WIN31_GRAY);

    pathInputPanel.add(new JLabel("From:"));
    styleTextField(pathFromField);
    pathInputPanel.add(pathFromField);
    pathInputPanel.add(new JLabel("To:"));
    styleTextField(pathToField);
    pathInputPanel.add(pathToField);

    panel.add(pathInputPanel);

    JButton pathBtn = createWin31Button("Find Shortest Path");
    pathBtn.addActionListener(e -> {
      String from = pathFromField.getText().trim();
      String to = pathToField.getText().trim();
      if (!graph.containsNode(from) || !graph.containsNode(to)) {
        showWin31MessageDialog("Node not found!", "Error");
        return;
      }
      java.util.List<Edge> path = graph.getShortestPath(from, to);
      if (path == null || path.isEmpty()) {
        showWin31MessageDialog("No path found!", "Info");
        highlightEdges = new ArrayList<>();
        highlightType = "PATH";
        updateOutputArea("No path from " + from + " to " + to);
      } else {
        highlightEdges = path;
        highlightType = "PATH";
        int totalCost = path.stream().mapToInt(edge -> edge.cost).sum();
        updateOutputArea("Shortest Path Cost: " + totalCost + " | Path: " +
          from + " → " + to);
      }
      repaint();
    });

    JPanel pathButtonPanel = new JPanel(new FlowLayout());
    pathButtonPanel.setBackground(WIN31_GRAY);
    pathButtonPanel.add(pathBtn);
    panel.add(pathButtonPanel);

    return panel;
  }

  private JPanel createOutputSection() {
    JPanel panel = createSectionPanel("Output");

    JTextArea outputArea = new JTextArea(8, 20);
    outputArea.setEditable(false);
    outputArea.setLineWrap(true);
    outputArea.setWrapStyleWord(true);
    outputArea.setBackground(WIN31_WHITE);
    outputArea.setForeground(WIN31_BLACK);
    outputArea.setFont(new Font("Courier New", Font.PLAIN, 11));
    outputArea.setBorder(new BevelBorder(BevelBorder.LOWERED));

    JScrollPane scrollPane = new JScrollPane(outputArea);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    panel.add(scrollPane);

    this.outputArea = outputArea; // 儲存引用

    return panel;
  }

  private JTextArea outputArea;

  private void updateOutputArea(String text) {
    if (outputArea != null) {
      outputArea.setText(text);
    }
  }

  private JPanel createSectionPanel(String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(WIN31_GRAY);
    panel.setBorder(new CompoundBorder(
      new TitledBorder(new EtchedBorder(), title,
        TitledBorder.LEFT, TitledBorder.TOP,
        new Font("MS Sans Serif", Font.BOLD, 11), WIN31_BLACK),
      new EmptyBorder(5, 5, 5, 5)
    ));
    return panel;
  }

  private JButton createWin31Button(String text) {
    JButton button = new JButton(text);
    button.setBackground(WIN31_GRAY);
    button.setForeground(WIN31_BLACK);
    button.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
    button.setBorder(new BevelBorder(BevelBorder.RAISED));
    button.setFocusPainted(false);

    // 添加 hover 效果
    button.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        button.setBackground(WIN31_LIGHT_GRAY);
      }
      public void mouseExited(MouseEvent e) {
        button.setBackground(WIN31_GRAY);
      }
      public void mousePressed(MouseEvent e) {
        button.setBorder(new BevelBorder(BevelBorder.LOWERED));
      }
      public void mouseReleased(MouseEvent e) {
        button.setBorder(new BevelBorder(BevelBorder.RAISED));
      }
    });

    return button;
  }

  private void styleTextField(JTextField field) {
    field.setBackground(WIN31_WHITE);
    field.setForeground(WIN31_BLACK);
    field.setFont(new Font("MS Sans Serif", Font.PLAIN, 11));
    field.setBorder(new BevelBorder(BevelBorder.LOWERED));
  }

  private void showWin31MessageDialog(String message, String title) {
    JOptionPane optionPane = new JOptionPane(message,
      title.equals("Error") ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    JDialog dialog = optionPane.createDialog(this, title);
    dialog.setModal(true);
    dialog.setVisible(true);
  }

  class GraphPanel extends JPanel {
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // 先畫所有邊
      for (Edge edge : graph.getEdges()) {
        Point p1 = graph.getNodes().get(edge.from);
        Point p2 = graph.getNodes().get(edge.to);
        if (p1 != null && p2 != null) {
          // 若為高亮邊則稍後畫
          if (highlightEdges.contains(edge)) continue;
          g2d.setColor(WIN31_DARK_GRAY);
          g2d.setStroke(new BasicStroke(1));
          g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

          // 在邊的中點顯示 cost
          int mx = (p1.x + p2.x) / 2;
          int my = (p1.y + p2.y) / 2;
          g2d.setColor(WIN31_BLUE);
          g2d.setFont(new Font("MS Sans Serif", Font.BOLD, 10));

          // 添加白色背景使文字更清晰
          FontMetrics fm = g2d.getFontMetrics();
          String costStr = String.valueOf(edge.cost);
          int strWidth = fm.stringWidth(costStr);
          int strHeight = fm.getHeight();
          g2d.setColor(WIN31_WHITE);
          g2d.fillRect(mx - strWidth/2 - 2, my - strHeight/2 - 1,
                      strWidth + 4, strHeight + 2);
          g2d.setColor(WIN31_BLUE);
          g2d.drawString(costStr, mx - strWidth/2, my + fm.getAscent()/2);
        }
      }

      // 畫高亮邊
      for (Edge edge : highlightEdges) {
        Point p1 = graph.getNodes().get(edge.from);
        Point p2 = graph.getNodes().get(edge.to);
        if (p1 != null && p2 != null) {
          Color highlightColor = highlightType.equals("MST") ?
            new Color(255, 165, 0) : new Color(255, 20, 147); // 橘色 MST, 粉紅色路徑
          g2d.setColor(highlightColor);
          g2d.setStroke(new BasicStroke(3));
          g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

          // 高亮邊的 cost
          int mx = (p1.x + p2.x) / 2;
          int my = (p1.y + p2.y) / 2;
          g2d.setFont(new Font("MS Sans Serif", Font.BOLD, 12));

          FontMetrics fm = g2d.getFontMetrics();
          String costStr = String.valueOf(edge.cost);
          int strWidth = fm.stringWidth(costStr);
          int strHeight = fm.getHeight();
          g2d.setColor(Color.YELLOW);
          g2d.fillRect(mx - strWidth/2 - 3, my - strHeight/2 - 2,
                      strWidth + 6, strHeight + 4);
          g2d.setColor(Color.RED);
          g2d.drawString(costStr, mx - strWidth/2, my + fm.getAscent()/2);
        }
      }

      // 畫節點
      for (String name : graph.getNodes().keySet()) {
        Point p = graph.getNodes().get(name);
        int idx = traversalResult.indexOf(name);

        // 節點顏色
        if (!traversalResult.isEmpty() && idx >= 0 && idx < animationIndex) {
          if (idx == 0)
            g2d.setColor(new Color(255, 0, 0)); // 起始節點紅色
          else
            g2d.setColor(new Color(0, 128, 0)); // 訪問過的節點綠色
        } else {
          g2d.setColor(WIN31_GRAY); // 未訪問節點灰色
        }

        // 畫節點圓形 - 3D 效果
        g2d.fillOval(p.x - 22, p.y - 22, 44, 44);
        g2d.setColor(WIN31_DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(p.x - 22, p.y - 22, 44, 44);

        // 添加高光效果
        g2d.setColor(WIN31_WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawArc(p.x - 18, p.y - 18, 12, 12, 45, 180);

        // 節點文字
        g2d.setColor(WIN31_BLACK);
        g2d.setFont(new Font("MS Sans Serif", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(name);
        g2d.drawString(name, p.x - textWidth/2, p.y + fm.getAscent()/2 - 2);
      }
    }
  }

  // ...existing code...

  private void startAnimation() {
    animationIndex = 0;
    if (animationTimer != null && animationTimer.isRunning()) {
      animationTimer.stop();
    }
    animationTimer = new javax.swing.Timer(800, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        animationIndex++;
        if (animationIndex > traversalResult.size()) {
          animationTimer.stop();
        }
        repaint();
      }
    });
    animationTimer.start();
  }

  private void arrangeNodesAsPentagon() {
    int n = graph.getNodes().size();
    if (n == 0) return;
    int centerX = 400, centerY = 300, radius = 180;
    java.util.List<String> names = new ArrayList<>(graph.getNodes().keySet());
    for (int i = 0; i < n; i++) {
      double angle = 2 * Math.PI * i / n - Math.PI / 2;
      int x = centerX + (int)(radius * Math.cos(angle));
      int y = centerY + (int)(radius * Math.sin(angle));
      graph.getNodes().put(names.get(i), new Point(x, y));
    }
  }

  private void autoGenerateGraph() {
    int nNodes, nEdges;
    try {
      nNodes = Integer.parseInt(nodeCountField.getText().trim());
      nEdges = Integer.parseInt(edgeCountField.getText().trim());
    } catch (NumberFormatException ex) {
      showWin31MessageDialog("Please enter valid numbers!", "Error");
      return;
    }
    if (nNodes < 2 || nEdges < 1) {
      showWin31MessageDialog("At least 2 nodes and 1 edge required!", "Error");
      return;
    }
    graph.clear();

    // 生成節點
    for (int i = 0; i < nNodes; ++i) {
      String name = "N" + i;
      graph.addNode(name, new Point(0, 0));
    }

    // 生成邊
    Set<Edge> generatedEdges = new HashSet<>();
    java.util.List<String> names = new ArrayList<>(graph.getNodes().keySet());
    Random rand = new Random();
    while (generatedEdges.size() < nEdges && generatedEdges.size() < nNodes * (nNodes - 1) / 2) {
      String from = names.get(rand.nextInt(names.size()));
      String to = names.get(rand.nextInt(names.size()));
      if (!from.equals(to)) {
        Edge edge = new Edge(from, to);
        if (!generatedEdges.contains(edge)) {
          generatedEdges.add(edge);
        }
      }
    }
    for (Edge edge : generatedEdges) {
      graph.addEdge(edge.from, edge.to);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GraphGUI().setVisible(true));
  }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GraphGUI extends JFrame {
  // 節點資料(Key:名稱, Value:位置)
  private Map<String, Point> nodes = new HashMap<>();
  // 邊資料
  private java.util.List<Edge> edges = new ArrayList<>();
  // 介面元件
  private JTextField nodeNameField = new JTextField(5);
  private JTextField fromField = new JTextField(3);
  private JTextField toField = new JTextField(3);
  private JTextField nodeCountField = new JTextField(3);
  private JTextField edgeCountField = new JTextField(3);
  private JTextField startNodeField = new JTextField(3);
  private java.util.List<String> traversalResult = new ArrayList<>();


  public GraphGUI() {
    setTitle("Graph Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 550);

    GraphPanel panel = new GraphPanel();
    add(panel, BorderLayout.CENTER);

    JPanel controlPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(2, 2, 2, 2);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 0; gbc.gridx = 0;
    controlPanel.add(new JLabel("Node Name:"), gbc);
    gbc.gridx = 1;
    controlPanel.add(nodeNameField, gbc);

    JButton addNodeBtn = new JButton("Add Node");
    JButton removeNodeBtn = new JButton("Remove Node");

    addNodeBtn.addActionListener(e -> {
      String name = nodeNameField.getText().trim();
      if (!name.isEmpty() && !nodes.containsKey(name)) {
        // 隨機產生座標
        int x = new Random().nextInt(450) + 50;
        int y = new Random().nextInt(400) + 50;
        nodes.put(name, new Point(x, y));
        nodeNameField.setText(""); // 新增後清空輸入框
        panel.repaint();
      }
    });

    removeNodeBtn.addActionListener(e -> {
      String name = nodeNameField.getText().trim();
      if (nodes.containsKey(name)) {
        nodes.remove(name);
        // 移除相關的邊
        edges.removeIf(edge -> edge.from.equals(name) || edge.to.equals(name));
        nodeNameField.setText(""); // 刪除後清空輸入框
        panel.repaint();
      }
    });

    gbc.gridx = 2;
    controlPanel.add(addNodeBtn, gbc);
    gbc.gridx = 3;
    controlPanel.add(removeNodeBtn, gbc);

    gbc.gridy = 1; gbc.gridx = 0;
    controlPanel.add(new JLabel("From:"), gbc);
    gbc.gridx = 1;
    controlPanel.add(fromField, gbc);
    gbc.gridx = 2;
    controlPanel.add(new JLabel("To:"), gbc);
    gbc.gridx = 3;
    controlPanel.add(toField, gbc);

    JButton addEdgeBtn = new JButton("Add Edge");
    addEdgeBtn.addActionListener(e -> {
      String from = fromField.getText().trim();
      String to = toField.getText().trim();
      if (nodes.containsKey(from) && nodes.containsKey(to) && !from.equals(to)) {
        Edge newEdge = new Edge(from, to);
        if (!edges.contains(newEdge)) {
          edges.add(newEdge);
          fromField.setText(""); // 新增後清空
          toField.setText("");
          panel.repaint();
        }
      }
    });

    gbc.gridx = 4;
    controlPanel.add(addEdgeBtn, gbc);

    gbc.gridy = 2; gbc.gridx = 0;
    controlPanel.add(new JLabel("Number of Nodes:"), gbc);
    gbc.gridx = 1;
    controlPanel.add(nodeCountField, gbc);
    gbc.gridx = 2;
    controlPanel.add(new JLabel("Number of Edges:"), gbc);
    gbc.gridx = 3;
    controlPanel.add(edgeCountField, gbc);

    JButton autoGenBtn = new JButton("Auto Generate Graph");
    autoGenBtn.addActionListener(e -> autoGenerateGraph());
    gbc.gridx = 4;
    controlPanel.add(autoGenBtn, gbc);

    gbc.gridy = 3; gbc.gridx = 0;
    controlPanel.add(new JLabel("Start Node:"), gbc);
    gbc.gridx = 1;
    controlPanel.add(startNodeField, gbc);

    JButton dfsBtn = new JButton("DFS");
    gbc.gridx = 2; controlPanel.add(dfsBtn, gbc);
    JButton bfsBtn = new JButton("BFS");
    gbc.gridx = 3; controlPanel.add(bfsBtn, gbc);

    add(controlPanel, BorderLayout.SOUTH);

    dfsBtn.addActionListener(e -> {
      String start = startNodeField.getText().trim();
      if (!nodes.containsKey(start)) {
        JOptionPane.showMessageDialog(this, "Start node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      traversalResult = dfs(start, getAdjacencyList());
      repaint();
    });
    bfsBtn.addActionListener(e -> {
      String start = startNodeField.getText().trim();
      if (!nodes.containsKey(start)) {
        JOptionPane.showMessageDialog(this, "Start node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      traversalResult = bfs(start, getAdjacencyList());
      repaint();
    });
  }

  // 畫圖區
  class GraphPanel extends JPanel {
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      // 畫邊
      for (Edge edge : edges) {
        Point p1 = nodes.get(edge.from);
        Point p2 = nodes.get(edge.to);
        if (p1 != null && p2 != null) {
          g.setColor(Color.BLACK);
          g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
      }
      // 畫節點，根據 traversalResult 著色
      int idx = 0;
      for (String name : nodes.keySet()) {
        Point p = nodes.get(name);
        if (!traversalResult.isEmpty() && traversalResult.contains(name)) {
            if (traversalResult.indexOf(name) == 0)
                g.setColor(Color.RED);  // 起點紅
            else
                g.setColor(Color.GREEN); // 其它綠
        } else {
            g.setColor(Color.RED); // 沒被搜尋到也可以用原來顏色
        }
        g.fillOval(p.x - 20, p.y - 20, 40, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g.drawString(name, p.x - 10, p.y + 7);
      }
    }
  }

  // 記錄邊
  // Edge 類覆寫 equals 與 hashCode 以利判斷重複
  static class Edge {
    String from, to;
    Edge(String f, String t) { from = f; to = t; }
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Edge edge = (Edge) o;
      return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
    }
    @Override
    public int hashCode() {
      return Objects.hash(from, to);
    }
  }

  // autoGenerateGraph 應為 GraphGUI 的成員方法
  private void autoGenerateGraph() {
    int nNodes, nEdges;
    try {
      nNodes = Integer.parseInt(nodeCountField.getText().trim());
      nEdges = Integer.parseInt(edgeCountField.getText().trim());
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (nNodes < 2 || nEdges < 1) {
      JOptionPane.showMessageDialog(this, "At least 2 nodes and 1 edge required!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    // 清空現有節點與邊
    nodes.clear();
    edges.clear();

    Random rand = new Random();
    // 產生節點
    for (int i = 0; i < nNodes; ++i) {
      String name = "N" + i;
      int x = rand.nextInt(450) + 50;
      int y = rand.nextInt(400) + 50;
      nodes.put(name, new Point(x, y));
    }
    // 產生隨機不重複的邊
    Set<Edge> generatedEdges = new HashSet<>();
    java.util.List<String> names = new ArrayList<>(nodes.keySet());
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
    edges.addAll(generatedEdges);

    repaint();
  }

  private Map<String, List<String>> getAdjacencyList() {
    Map<String, List<String>> graph = new HashMap<>();
    for (String name : nodes.keySet()) {
        graph.put(name, new ArrayList<>());
    }
    for (Edge edge : edges) {
        // 無向圖兩邊加
        if(graph.containsKey(edge.from) && graph.containsKey(edge.to)) {
            graph.get(edge.from).add(edge.to);
            graph.get(edge.to).add(edge.from);
        }
    }
    return graph;
}

  private List<String> dfs(String start, Map<String, List<String>> graph) {
    List<String> result = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    dfsHelper(start, graph, visited, result);
    return result;
}

private void dfsHelper(String node, Map<String, List<String>> graph, Set<String> visited, List<String> result) {
    if (!visited.contains(node)) {
        visited.add(node);
        result.add(node);
        for (String neighbor : graph.get(node)) {
            dfsHelper(neighbor, graph, visited, result);
        }
    }
}

private List<String> bfs(String start, Map<String, List<String>> graph) {
    List<String> result = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Queue<String> queue = new LinkedList<>();
    queue.add(start);
    visited.add(start);
    while (!queue.isEmpty()) {
        String node = queue.poll();
        result.add(node);
        for (String neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                queue.add(neighbor);
                visited.add(neighbor);
            }
        }
    }
    return result;
}

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GraphGUI().setVisible(true));
  }
}

import javax.swing.*;
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
  private java.util.List<String> traversalResult = new ArrayList<>();
  private int animationIndex = 0;
  private javax.swing.Timer animationTimer;

  public GraphGUI() {
    setTitle("Graph Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1500, 600);

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
      if (!name.isEmpty() && !graph.containsNode(name)) {
        Point p;
        Random rand = new Random();
        int minDist = 60;
        outer: while (true) {
          int x = rand.nextInt(1000) + 100;
          int y = rand.nextInt(400) + 100;
          p = new Point(x, y);
          for (Point exist : graph.getNodes().values()) {
            if (p.distance(exist) < minDist) continue outer;
          }
          break;
        }
        graph.addNode(name, p);
        nodeNameField.setText("");
        panel.repaint();
      }
    });

    removeNodeBtn.addActionListener(e -> {
      String name = nodeNameField.getText().trim();
      if (graph.containsNode(name)) {
        graph.removeNode(name);
        nodeNameField.setText("");
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
      if (graph.containsNode(from) && graph.containsNode(to) && !from.equals(to)) {
        graph.addEdge(from, to);
        fromField.setText("");
        toField.setText("");
        panel.repaint();
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

    JTextArea outputArea = new JTextArea(6, 18);
    outputArea.setEditable(false);
    outputArea.setLineWrap(true);
    outputArea.setWrapStyleWord(true);
    JScrollPane outputScroll = new JScrollPane(outputArea);
    gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 5;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0; gbc.weighty = 1.0;
    controlPanel.add(outputScroll, gbc);
    gbc.gridwidth = 1;
    gbc.weightx = 0; gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    add(controlPanel, BorderLayout.EAST);

    dfsBtn.addActionListener(e -> {
      String start = startNodeField.getText().trim();
      if (!graph.containsNode(start)) {
        JOptionPane.showMessageDialog(this, "Start node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      traversalResult = graph.dfs(start);
      outputArea.setText("DFS: " + String.join("  ", traversalResult));
      startAnimation(panel);
    });
    bfsBtn.addActionListener(e -> {
      String start = startNodeField.getText().trim();
      if (!graph.containsNode(start)) {
        JOptionPane.showMessageDialog(this, "Start node does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      traversalResult = graph.bfs(start);
      outputArea.setText("BFS: " + String.join("  ", traversalResult));
      startAnimation(panel);
    });
  }

  class GraphPanel extends JPanel {
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Edge edge : graph.getEdges()) {
        Point p1 = graph.getNodes().get(edge.from);
        Point p2 = graph.getNodes().get(edge.to);
        if (p1 != null && p2 != null) {
          g.setColor(Color.BLACK);
          g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
      }
      for (String name : graph.getNodes().keySet()) {
        Point p = graph.getNodes().get(name);
        int idx = traversalResult.indexOf(name);
        if (!traversalResult.isEmpty() && idx >= 0 && idx < animationIndex) {
          if (idx == 0)
            g.setColor(Color.RED);
          else
            g.setColor(Color.GREEN);
        } else {
          g.setColor(Color.RED);
        }
        g.fillOval(p.x - 20, p.y - 20, 40, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g.drawString(name, p.x - 10, p.y + 7);
      }
    }
  }

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
    graph.clear();
    Random rand = new Random();
    int minDist = 60;
    for (int i = 0; i < nNodes; ++i) {
      String name = "N" + i;
      Point p;
      outer: while (true) {
        int x = rand.nextInt(450) + 50;
        int y = rand.nextInt(400) + 50;
        p = new Point(x, y);
        for (Point exist : graph.getNodes().values()) {
          if (p.distance(exist) < minDist) continue outer;
        }
        break;
      }
      graph.addNode(name, p);
    }
    Set<Edge> generatedEdges = new HashSet<>();
    java.util.List<String> names = new ArrayList<>(graph.getNodes().keySet());
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
    repaint();
  }

  private void startAnimation(GraphPanel panel) {
    animationIndex = 0;
    if (animationTimer != null && animationTimer.isRunning()) {
      animationTimer.stop();
    }
    animationTimer = new javax.swing.Timer(500, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        animationIndex++;
        if (animationIndex > traversalResult.size()) {
          animationTimer.stop();
        }
        panel.repaint();
      }
    });
    animationTimer.start();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GraphGUI().setVisible(true));
  }
}

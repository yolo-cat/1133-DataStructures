import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class TreeVisualizationPanel extends JPanel {
    private Integer[] treeArray;
    private java.util.List<Integer> visitPath;
    private int currentVisitIndex;
    private static final int NODE_RADIUS = 20;
    private static final int LEVEL_HEIGHT = 80;
    private static final int MIN_HORIZONTAL_SPACING = 50;

    public TreeVisualizationPanel() {
        setBackground(new Color(248, 249, 250));
        setPreferredSize(new Dimension(600, 400));
        visitPath = new ArrayList<>();
        currentVisitIndex = -1;
    }

    public void setTreeArray(Integer[] arr) {
        this.treeArray = arr;
        clearVisitPath();
        repaint();
    }

    public void setVisitPath(java.util.List<Integer> path) {
        this.visitPath = path;
        this.currentVisitIndex = -1;
        repaint();
    }

    public void showNextVisitStep() {
        if (currentVisitIndex < visitPath.size() - 1) {
            currentVisitIndex++;
            repaint();
        }
    }

    public void clearVisitPath() {
        this.visitPath.clear();
        this.currentVisitIndex = -1;
        repaint();
    }

    public boolean hasMoreSteps() {
        return currentVisitIndex < visitPath.size() - 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (treeArray == null || treeArray.length <= 1) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));

        // 計算樹的最大層數
        int maxLevel = (int) (Math.log(treeArray.length - 1) / Math.log(2)) + 1;

        // 先畫連接線，再畫節點（避免線條覆蓋節點）
        drawConnections(g2d, maxLevel);
        drawNodes(g2d, maxLevel);

        g2d.dispose();
    }

    private void drawConnections(Graphics2D g2d, int maxLevel) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        for (int i = 1; i < treeArray.length; i++) {
            if (treeArray[i] == null) continue;

            int leftChild = 2 * i;
            int rightChild = 2 * i + 1;

            Point parentPos = getNodePosition(i, maxLevel);

            // 畫左子節點連線
            if (leftChild < treeArray.length && treeArray[leftChild] != null) {
                Point leftPos = getNodePosition(leftChild, maxLevel);
                g2d.draw(new Line2D.Double(parentPos.x, parentPos.y, leftPos.x, leftPos.y));
            }

            // 畫右子節點連線
            if (rightChild < treeArray.length && treeArray[rightChild] != null) {
                Point rightPos = getNodePosition(rightChild, maxLevel);
                g2d.draw(new Line2D.Double(parentPos.x, parentPos.y, rightPos.x, rightPos.y));
            }
        }
    }

    private void drawNodes(Graphics2D g2d, int maxLevel) {
        for (int i = 1; i < treeArray.length; i++) {
            if (treeArray[i] == null) continue;

            Point pos = getNodePosition(i, maxLevel);
            boolean isRoot = (i == 1);
            boolean isLeaf = isLeafNode(i);
            boolean isCurrentVisit = currentVisitIndex >= 0 && currentVisitIndex < visitPath.size()
                                   && visitPath.get(currentVisitIndex).equals(treeArray[i]);
            boolean isVisited = isNodeVisited(treeArray[i]);

            // 設定節點顏色
            if (isCurrentVisit) {
                g2d.setColor(new Color(255, 215, 0));  // 當前走訪節點：金黃色
            } else if (isVisited) {
                g2d.setColor(new Color(144, 238, 144));  // 已走訪節點：淡綠色
            } else if (isRoot) {
                g2d.setColor(Color.BLACK);  // 根節點黑色背景
            } else if (isLeaf) {
                g2d.setColor(Color.WHITE);  // 葉節點白色背景
            } else {
                g2d.setColor(new Color(200, 200, 200));  // 中間節點灰色背景
            }

            // 畫節點圓圈
            Ellipse2D circle = new Ellipse2D.Double(
                pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                2 * NODE_RADIUS, 2 * NODE_RADIUS
            );
            g2d.fill(circle);

            // 畫節點邊框
            if (isCurrentVisit) {
                g2d.setColor(new Color(255, 140, 0));  // 當前節點橙色邊框
                g2d.setStroke(new BasicStroke(4));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
            }
            g2d.draw(circle);

            // 設定文字顏色
            if (isRoot && !isCurrentVisit && !isVisited) {
                g2d.setColor(Color.WHITE);  // 根節點白色文字
            } else {
                g2d.setColor(Color.BLACK);  // 其他節點黑色文字
            }

            // 畫節點值
            String text = treeArray[i].toString();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2d.drawString(text,
                pos.x - textWidth / 2,
                pos.y + textHeight / 2 - 2);

            // 畫陣列編號標示
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.setColor(new Color(100, 100, 100));  // 灰色文字
            String indexText = "[" + i + "]";
            FontMetrics indexFm = g2d.getFontMetrics();
            int indexWidth = indexFm.stringWidth(indexText);
            // 將編號放在節點右上角
            g2d.drawString(indexText,
                pos.x + NODE_RADIUS - indexWidth + 8,
                pos.y - NODE_RADIUS + 12);

            // 恢復原本字體
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
        }
    }

    private boolean isNodeVisited(Integer value) {
        for (int i = 0; i <= currentVisitIndex && i < visitPath.size(); i++) {
            if (visitPath.get(i).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private Point getNodePosition(int index, int maxLevel) {
        int level = (int) (Math.log(index) / Math.log(2));
        int nodesInLevel = 1 << level;
        int positionInLevel = index - (1 << level);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // 計算水平位置
        int totalWidth = panelWidth - 2 * NODE_RADIUS;
        int spacing = Math.max(MIN_HORIZONTAL_SPACING, totalWidth / (nodesInLevel + 1));
        int x = NODE_RADIUS + spacing * (positionInLevel + 1);

        // 確保節點在面板範圍內
        if (nodesInLevel > 1) {
            x = NODE_RADIUS + (totalWidth * (positionInLevel + 1)) / (nodesInLevel + 1);
        } else {
            x = panelWidth / 2;
        }

        // 計算垂直位置
        int y = NODE_RADIUS + 30 + level * LEVEL_HEIGHT;

        return new Point(x, y);
    }

    private boolean isLeafNode(int index) {
        int leftChild = 2 * index;
        int rightChild = 2 * index + 1;

        boolean hasLeftChild = leftChild < treeArray.length && treeArray[leftChild] != null;
        boolean hasRightChild = rightChild < treeArray.length && treeArray[rightChild] != null;

        return !hasLeftChild && !hasRightChild;
    }
}

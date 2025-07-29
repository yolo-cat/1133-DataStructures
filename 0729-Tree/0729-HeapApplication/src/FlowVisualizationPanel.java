import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlowVisualizationPanel extends JPanel {
    private final List<AnimatedItem> animatedItems = new CopyOnWriteArrayList<>();
    private final Timer animationTimer;

    // 流程中的各個區域位置 - 動態計算以平均分配空間
    private static final int CENTER_Y = 80;
    private static final int ITEM_SIZE = 20;
    private static final int MARGIN = 80; // 左右邊距
    private static final int STATION_WIDTH = 60; // 每個站點的寬度

    public FlowVisualizationPanel() {
        setPreferredSize(new Dimension(800, 160));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createTitledBorder("📊 物品流動動畫 (Item Flow Animation)"));

        // 啟動動畫定時器
        animationTimer = new Timer(50, e -> {
            updateAnimations();
            repaint();
        });
        animationTimer.start();
    }

    private void updateAnimations() {
        // 移除已完成的動畫物品
        animatedItems.removeIf(AnimatedItem::isFinished);

        // 更新所有動畫物品的位置
        for (AnimatedItem item : animatedItems) {
            item.update();
        }
    }

    // 動態計算各站點的 X 座標
    private int getProducerX() {
        return MARGIN + STATION_WIDTH / 2;
    }

    private int getBufferX() {
        int availableWidth = getWidth() - 2 * MARGIN;
        return MARGIN + availableWidth / 3 + STATION_WIDTH / 2;
    }

    private int getMachineX() {
        int availableWidth = getWidth() - 2 * MARGIN;
        return MARGIN + 2 * availableWidth / 3 + STATION_WIDTH / 2;
    }

    private int getConsumerX() {
        return getWidth() - MARGIN - STATION_WIDTH / 2;
    }

    public void addProducerToBuffer(Item item) {
        AnimatedItem animItem = new AnimatedItem(item, getProducerX(), CENTER_Y, getBufferX(), CENTER_Y, Color.GREEN);
        animatedItems.add(animItem);
    }

    public void addBufferToMachine(Item item) {
        AnimatedItem animItem = new AnimatedItem(item, getBufferX(), CENTER_Y, getMachineX(), CENTER_Y, Color.BLUE);
        animatedItems.add(animItem);
    }

    public void addMachineToConsumer(Item item) {
        AnimatedItem animItem = new AnimatedItem(item, getMachineX(), CENTER_Y, getConsumerX(), CENTER_Y, Color.RED);
        animatedItems.add(animItem);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 繪製流程圖框架
        drawFlowFramework(g2d);

        // 繪製動畫中的物品
        for (AnimatedItem item : animatedItems) {
            item.draw(g2d);
        }
    }

    private void drawFlowFramework(Graphics2D g2d) {
        // 計算各站點位置
        int producerX = getProducerX();
        int bufferX = getBufferX();
        int machineX = getMachineX();
        int consumerX = getConsumerX();

        // 繪製各個區域的框架
        drawStationBox(g2d, producerX - STATION_WIDTH/2, CENTER_Y - 25, "🏭 生產者", Color.GREEN);
        drawStationBox(g2d, bufferX - STATION_WIDTH/2, CENTER_Y - 25, "📦 緩衝區", Color.ORANGE);
        drawStationBox(g2d, machineX - STATION_WIDTH/2, CENTER_Y - 25, "⚙️ 機台", Color.BLUE);
        drawStationBox(g2d, consumerX - STATION_WIDTH/2, CENTER_Y - 25, "🚚 消費者", Color.RED);

        // 繪製箭頭連接線
        drawArrow(g2d, producerX + STATION_WIDTH/2, CENTER_Y, bufferX - STATION_WIDTH/2, CENTER_Y);
        drawArrow(g2d, bufferX + STATION_WIDTH/2, CENTER_Y, machineX - STATION_WIDTH/2, CENTER_Y);
        drawArrow(g2d, machineX + STATION_WIDTH/2, CENTER_Y, consumerX - STATION_WIDTH/2, CENTER_Y);

        // 添加說明文字
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // 計算箭頭中點位置來放置說明文字
        int arrow1MidX = (producerX + STATION_WIDTH/2 + bufferX - STATION_WIDTH/2) / 2;
        int arrow2MidX = (bufferX + STATION_WIDTH/2 + machineX - STATION_WIDTH/2) / 2;
        int arrow3MidX = (machineX + STATION_WIDTH/2 + consumerX - STATION_WIDTH/2) / 2;

        g2d.drawString("0.2s", arrow1MidX - 10, CENTER_Y - 10);
        g2d.drawString("0.24s", arrow2MidX - 15, CENTER_Y - 10);
        g2d.drawString("0.24s", arrow3MidX - 15, CENTER_Y - 10);
    }

    private void drawStationBox(Graphics2D g2d, int x, int y, String label, Color color) {
        g2d.setColor(color.brighter().brighter());
        g2d.fillRoundRect(x, y, STATION_WIDTH, 50, 10, 10);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, STATION_WIDTH, 50, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        g2d.drawString(label, x + (STATION_WIDTH - textWidth) / 2, y + 60);
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x1, y1, x2, y2);

        // 繪製箭頭頭部
        int arrowLength = 8;
        double angle = Math.atan2(y2 - y1, x2 - x1);

        int arrowX1 = (int) (x2 - arrowLength * Math.cos(angle - Math.PI / 6));
        int arrowY1 = (int) (y2 - arrowLength * Math.sin(angle - Math.PI / 6));
        int arrowX2 = (int) (x2 - arrowLength * Math.cos(angle + Math.PI / 6));
        int arrowY2 = (int) (y2 - arrowLength * Math.sin(angle + Math.PI / 6));

        g2d.drawLine(x2, y2, arrowX1, arrowY1);
        g2d.drawLine(x2, y2, arrowX2, arrowY2);
    }

    // 內部類：動畫物品
    private static class AnimatedItem {
        private final Item item;
        private double currentX, currentY;
        private final double targetX, targetY;
        private final double startX, startY;
        private final Color color;
        private double progress = 0.0;
        private static final double ANIMATION_SPEED = 0.02;

        public AnimatedItem(Item item, double startX, double startY, double targetX, double targetY, Color color) {
            this.item = item;
            this.startX = startX;
            this.startY = startY;
            this.currentX = startX;
            this.currentY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.color = color;
        }

        public void update() {
            if (progress < 1.0) {
                progress += ANIMATION_SPEED;
                if (progress > 1.0) progress = 1.0;

                // 使用緩動函數讓動畫更平滑
                double easeProgress = easeInOutQuad(progress);
                currentX = startX + (targetX - startX) * easeProgress;
                currentY = startY + (targetY - startY) * easeProgress;
            }
        }

        private double easeInOutQuad(double t) {
            return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
        }

        public void draw(Graphics2D g2d) {
            // 繪製物品圓圈
            g2d.setColor(color);
            g2d.fillOval((int) currentX - ITEM_SIZE/2, (int) currentY - ITEM_SIZE/2, ITEM_SIZE, ITEM_SIZE);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawOval((int) currentX - ITEM_SIZE/2, (int) currentY - ITEM_SIZE/2, ITEM_SIZE, ITEM_SIZE);

            // 繪製物品編號
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String idText = String.valueOf(item.getId());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(idText);
            int textHeight = fm.getHeight();
            g2d.setColor(Color.WHITE);
            g2d.drawString(idText, (int) currentX - textWidth/2, (int) currentY + textHeight/4);
        }

        public boolean isFinished() {
            return progress >= 1.0;
        }
    }
}

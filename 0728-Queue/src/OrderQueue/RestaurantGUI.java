import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 餐廳模擬系統GUI - 使用Swing實作動畫效果
 */
public class RestaurantGUI extends JFrame {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    
    // GUI組件
    private JPanel customerPanel;
    private JPanel queuePanel;
    private JPanel workerPanel;
    private JPanel completedPanel;
    private JSlider speedSlider;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel statisticsLabel;
    
    // 資料和狀態
    private final RestaurantQueue restaurantQueue;
    private final CopyOnWriteArrayList<Customer> recentCustomers;
    private final CopyOnWriteArrayList<Order> completedOrders;
    private final ConcurrentHashMap<Integer, String> workerStatuses;
    private volatile boolean running;
    
    // 統計數據
    private int totalCustomers = 0;
    private int totalOrdersCompleted = 0;
    
    public RestaurantGUI(RestaurantQueue restaurantQueue) {
        this.restaurantQueue = restaurantQueue;
        this.recentCustomers = new CopyOnWriteArrayList<>();
        this.completedOrders = new CopyOnWriteArrayList<>();
        this.workerStatuses = new ConcurrentHashMap<>();
        this.running = true;
        
        initializeGUI();
        startGUIUpdateTimer();
    }
    
    private void initializeGUI() {
        setTitle("餐廳點餐系統模擬");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 創建主要面板
        createCustomerPanel();
        createQueuePanel();
        createWorkerPanel();
        createCompletedPanel();
        createControlPanel();
        
        // 布局設計
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(customerPanel);
        mainPanel.add(queuePanel);
        mainPanel.add(workerPanel);
        mainPanel.add(completedPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private void createCustomerPanel() {
        customerPanel = new JPanel();
        customerPanel.setBorder(BorderFactory.createTitledBorder("顧客進店區"));
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
        customerPanel.setBackground(new Color(230, 250, 230));
    }
    
    private void createQueuePanel() {
        queuePanel = new JPanel();
        queuePanel.setBorder(BorderFactory.createTitledBorder("訂單佇列"));
        queuePanel.setLayout(new BoxLayout(queuePanel, BoxLayout.Y_AXIS));
        queuePanel.setBackground(new Color(250, 250, 230));
    }
    
    private void createWorkerPanel() {
        workerPanel = new JPanel();
        workerPanel.setBorder(BorderFactory.createTitledBorder("工作人員狀態"));
        workerPanel.setLayout(new BoxLayout(workerPanel, BoxLayout.Y_AXIS));
        workerPanel.setBackground(new Color(230, 230, 250));
    }
    
    private void createCompletedPanel() {
        completedPanel = new JPanel();
        completedPanel.setBorder(BorderFactory.createTitledBorder("完成訂單"));
        completedPanel.setLayout(new BoxLayout(completedPanel, BoxLayout.Y_AXIS));
        completedPanel.setBackground(new Color(250, 230, 230));
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        // 速度調整滑桿
        JLabel speedLabel = new JLabel("顧客到達速度:");
        speedSlider = new JSlider(1, 10, 5);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        
        // 停止按鈕
        stopButton = new JButton("結束模擬");
        stopButton.setBackground(new Color(255, 100, 100));
        stopButton.addActionListener(e -> stopSimulation());
        
        // 狀態標籤
        statusLabel = new JLabel("系統運行中...");
        statisticsLabel = new JLabel("統計: 顧客總數:0, 完成訂單:0");
        
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        controlPanel.add(stopButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(statisticsLabel);
        
        return controlPanel;
    }
    
    private void startGUIUpdateTimer() {
        Timer updateTimer = new Timer(100, e -> updateGUI());
        updateTimer.start();
    }
    
    private void updateGUI() {
        if (!running) return;
        
        SwingUtilities.invokeLater(() -> {
            updateQueueDisplay();
            updateWorkerDisplay();
            updateCompletedDisplay();
            updateStatistics();
        });
    }
    
    private void updateQueueDisplay() {
        queuePanel.removeAll();
        
        Order[] orders = restaurantQueue.getQueueSnapshot();
        queuePanel.add(new JLabel("等待中的訂單數量: " + orders.length));
        queuePanel.add(Box.createVerticalStrut(5));
        
        for (int i = 0; i < Math.min(orders.length, 8); i++) {
            Order order = orders[i];
            JLabel orderLabel = new JLabel((i + 1) + ". " + order.toString());
            orderLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            queuePanel.add(orderLabel);
        }
        
        if (orders.length > 8) {
            queuePanel.add(new JLabel("... 還有 " + (orders.length - 8) + " 個訂單"));
        }
        
        queuePanel.revalidate();
        queuePanel.repaint();
    }
    
    private void updateWorkerDisplay() {
        workerPanel.removeAll();
        
        workerPanel.add(new JLabel("工作人員狀態:"));
        workerPanel.add(Box.createVerticalStrut(5));
        
        for (int i = 1; i <= 3; i++) {
            String status = workerStatuses.getOrDefault(i, "等待中");
            JLabel workerLabel = new JLabel("工作人員 #" + i + ": " + status);
            workerLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            
            if (status.startsWith("處理中")) {
                workerLabel.setForeground(new Color(200, 0, 0));
            } else {
                workerLabel.setForeground(new Color(0, 150, 0));
            }
            
            workerPanel.add(workerLabel);
        }
        
        workerPanel.revalidate();
        workerPanel.repaint();
    }
    
    private void updateCompletedDisplay() {
        completedPanel.removeAll();
        
        completedPanel.add(new JLabel("最近完成的訂單:"));
        completedPanel.add(Box.createVerticalStrut(5));
        
        int startIndex = Math.max(0, completedOrders.size() - 8);
        for (int i = startIndex; i < completedOrders.size(); i++) {
            Order order = completedOrders.get(i);
            JLabel orderLabel = new JLabel("✓ " + order.toString());
            orderLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            orderLabel.setForeground(new Color(0, 120, 0));
            completedPanel.add(orderLabel);
        }
        
        completedPanel.revalidate();
        completedPanel.repaint();
    }
    
    private void updateStatistics() {
        statisticsLabel.setText(String.format("統計: 顧客總數:%d, 完成訂單:%d, 佇列長度:%d", 
                totalCustomers, totalOrdersCompleted, restaurantQueue.getQueueSize()));
    }
    
    /**
     * 顧客進店動畫
     */
    public void customerArrived(Customer customer) {
        totalCustomers++;
        recentCustomers.add(customer);
        
        SwingUtilities.invokeLater(() -> {
            customerPanel.removeAll();
            customerPanel.add(new JLabel("最近進店的顧客:"));
            customerPanel.add(Box.createVerticalStrut(5));
            
            int startIndex = Math.max(0, recentCustomers.size() - 8);
            for (int i = startIndex; i < recentCustomers.size(); i++) {
                Customer c = recentCustomers.get(i);
                JLabel customerLabel = new JLabel("→ " + c.toString());
                customerLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                customerPanel.add(customerLabel);
            }
            
            customerPanel.revalidate();
            customerPanel.repaint();
        });
        
        // 閃爍效果
        Timer flashTimer = new Timer(500, null);
        flashTimer.addActionListener(new ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                customerPanel.setBackground(count % 2 == 0 ? 
                        new Color(180, 255, 180) : new Color(230, 250, 230));
                customerPanel.repaint();
                if (++count >= 4) {
                    flashTimer.stop();
                }
            }
        });
        flashTimer.start();
    }
    
    /**
     * 更新工作人員狀態
     */
    public void updateWorkerStatus(int workerId, String status) {
        workerStatuses.put(workerId, status);
    }
    
    /**
     * 訂單完成通知
     */
    public void orderCompleted(Order order) {
        totalOrdersCompleted++;
        completedOrders.add(order);
        
        // 保持最近50個完成的訂單
        if (completedOrders.size() > 50) {
            completedOrders.remove(0);
        }
    }
    
    /**
     * 取得顧客到達速度設定
     */
    public int getCustomerArrivalSpeed() {
        return speedSlider.getValue();
    }
    
    /**
     * 停止模擬
     */
    private void stopSimulation() {
        running = false;
        statusLabel.setText("系統已停止");
        stopButton.setEnabled(false);
        
        // 通知主程式停止
        if (stopListener != null) {
            stopListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "stop"));
        }
    }
    
    /**
     * 檢查系統是否正在運行
     */
    public boolean isRunning() {
        return running;
    }
    
    // 停止監聽器
    private ActionListener stopListener;
    
    public void setStopListener(ActionListener listener) {
        this.stopListener = listener;
    }
}
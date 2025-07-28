import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main GUI interface for the restaurant ordering system
 * Uses Swing with animation effects to display the complete workflow
 */
public class RestaurantGUI extends JFrame {
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    // GUI Components
    private JPanel customerPanel;
    private JPanel queuePanel;
    private JPanel workerPanel;
    private JPanel completedPanel;
    private JSlider speedSlider;
    private JButton stopButton;
    private JLabel statusLabel;
    
    // Data components
    private DefaultListModel<String> queueListModel;
    private JList<String> queueList;
    private DefaultListModel<String> completedListModel;
    private JList<String> completedList;
    private Map<Integer, JLabel> workerStatusLabels;
    private Map<Integer, Order> processingOrders;
    
    // Animation and control
    private volatile boolean running = true;
    private int customerArrivalSpeed = 1000; // milliseconds
    
    public RestaurantGUI() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setVisible(true);
    }
    
    private void initializeComponents() {
        setTitle("餐廳點餐系統");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize data structures
        queueListModel = new DefaultListModel<>();
        completedListModel = new DefaultListModel<>();
        workerStatusLabels = new HashMap<>();
        processingOrders = new HashMap<>();
        
        // Customer appearance panel
        customerPanel = new JPanel();
        customerPanel.setBorder(new TitledBorder("顧客到達"));
        customerPanel.setPreferredSize(new Dimension(280, 150));
        customerPanel.setBackground(new Color(255, 248, 220));
        
        // Order queue panel
        queuePanel = new JPanel(new BorderLayout());
        queuePanel.setBorder(new TitledBorder("訂單佇列"));
        queuePanel.setPreferredSize(new Dimension(280, 300));
        
        queueList = new JList<>(queueListModel);
        queueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queueList.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        JScrollPane queueScrollPane = new JScrollPane(queueList);
        queuePanel.add(queueScrollPane, BorderLayout.CENTER);
        
        // Worker status panel
        workerPanel = new JPanel();
        workerPanel.setLayout(new BoxLayout(workerPanel, BoxLayout.Y_AXIS));
        workerPanel.setBorder(new TitledBorder("工作人員狀態"));
        workerPanel.setPreferredSize(new Dimension(280, 200));
        workerPanel.setBackground(new Color(240, 248, 255));
        
        // Completed orders panel
        completedPanel = new JPanel(new BorderLayout());
        completedPanel.setBorder(new TitledBorder("完成訂單"));
        completedPanel.setPreferredSize(new Dimension(280, 300));
        
        completedList = new JList<>(completedListModel);
        completedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        completedList.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        JScrollPane completedScrollPane = new JScrollPane(completedList);
        completedPanel.add(completedScrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(new TitledBorder("控制面板"));
        
        // Speed control slider
        speedSlider = new JSlider(100, 3000, 1000);
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        controlPanel.add(new JLabel("顧客到達速度(ms):"));
        controlPanel.add(speedSlider);
        
        // Stop button
        stopButton = new JButton("結束系統");
        stopButton.setBackground(new Color(255, 99, 71));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        controlPanel.add(stopButton);
        
        // Status label
        statusLabel = new JLabel("系統運行中...");
        statusLabel.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        controlPanel.add(statusLabel);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Left panel - Customer and Queue
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(customerPanel);
        leftPanel.add(queuePanel);
        
        // Right panel - Workers and Completed
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(workerPanel);
        rightPanel.add(completedPanel);
        
        // Main content
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(new TitledBorder("控制面板"));
        
        controlPanel.add(new JLabel("顧客到達速度(ms):"));
        controlPanel.add(speedSlider);
        controlPanel.add(stopButton);
        controlPanel.add(statusLabel);
        
        return controlPanel;
    }
    
    private void setupEventHandlers() {
        // Speed slider change listener
        speedSlider.addChangeListener(e -> {
            customerArrivalSpeed = speedSlider.getValue();
        });
        
        // Stop button listener
        stopButton.addActionListener(e -> {
            running = false;
            statusLabel.setText("系統已停止");
            stopButton.setEnabled(false);
            System.exit(0);
        });
    }
    
    /**
     * Add a worker to the display
     */
    public void addWorker(int workerId, String workerName) {
        SwingUtilities.invokeLater(() -> {
            JLabel workerLabel = new JLabel(workerName + " 空閒中");
            workerLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            workerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            workerLabel.setOpaque(true);
            workerLabel.setBackground(new Color(144, 238, 144));
            
            workerStatusLabels.put(workerId, workerLabel);
            workerPanel.add(workerLabel);
            workerPanel.revalidate();
            workerPanel.repaint();
        });
    }
    
    /**
     * Update worker status display
     */
    public void updateWorkerStatus(int workerId, String status) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = workerStatusLabels.get(workerId);
            if (label != null) {
                label.setText(status);
                // Change color based on status
                if (status.contains("正在製作")) {
                    label.setBackground(new Color(255, 165, 0)); // Orange for busy
                } else {
                    label.setBackground(new Color(144, 238, 144)); // Green for idle
                }
                label.repaint();
            }
        });
    }
    
    /**
     * Update processing order for a worker
     */
    public void updateProcessingOrder(int workerId, Order order) {
        processingOrders.put(workerId, order);
    }
    
    /**
     * Remove processing order for a worker
     */
    public void removeProcessingOrder(int workerId) {
        processingOrders.remove(workerId);
    }
    
    /**
     * Show customer arrival animation
     */
    public void showCustomerArrival(Customer customer) {
        SwingUtilities.invokeLater(() -> {
            // Simple customer display - could be enhanced with animations
            customerPanel.removeAll();
            JLabel customerLabel = new JLabel("<html><center>" + customer.toString() + "<br/>已到達！</center></html>");
            customerLabel.setFont(new Font("微軟正黑體", Font.BOLD, 12));
            customerLabel.setHorizontalAlignment(JLabel.CENTER);
            customerPanel.add(customerLabel);
            customerPanel.revalidate();
            customerPanel.repaint();
            
            // Flash effect
            Timer flashTimer = new Timer(100, null);
            final int[] flashCount = {0};
            flashTimer.addActionListener(e -> {
                if (flashCount[0] < 6) {
                    customerPanel.setBackground(flashCount[0] % 2 == 0 ? 
                        new Color(255, 255, 0) : new Color(255, 248, 220));
                    flashCount[0]++;
                } else {
                    customerPanel.setBackground(new Color(255, 248, 220));
                    flashTimer.stop();
                }
                customerPanel.repaint();
            });
            flashTimer.start();
        });
    }
    
    /**
     * Update order queue display
     */
    public void updateOrderQueue(List<Order> orders) {
        SwingUtilities.invokeLater(() -> {
            queueListModel.clear();
            for (Order order : orders) {
                queueListModel.addElement(order.toString());
            }
        });
    }
    
    /**
     * Add completed order to display
     */
    public void addCompletedOrder(Order order) {
        SwingUtilities.invokeLater(() -> {
            completedListModel.insertElementAt(order.toString(), 0);
            // Limit to last 20 completed orders
            while (completedListModel.size() > 20) {
                completedListModel.removeElementAt(completedListModel.size() - 1);
            }
        });
    }
    
    /**
     * Get current customer arrival speed
     */
    public int getCustomerArrivalSpeed() {
        return customerArrivalSpeed;
    }
    
    /**
     * Check if system is running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Update system status
     */
    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }
}
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * 餐廳模擬系統主程式 - 整合所有元件
 */
public class RestaurantSimulator {
    private static final int NUM_WORKERS = 3;
    
    private RestaurantQueue restaurantQueue;
    private RestaurantGUI gui;
    private ScheduledExecutorService customerGenerator;
    private ExecutorService workerExecutor;
    private Worker[] workers;
    private Thread[] workerThreads;
    private Random random;
    private volatile boolean running;
    
    public RestaurantSimulator() {
        this.random = new Random();
        this.running = true;
        initializeSystem();
    }
    
    private void initializeSystem() {
        // 初始化佇列
        restaurantQueue = new RestaurantQueue();
        
        // 初始化GUI
        gui = new RestaurantGUI(restaurantQueue);
        gui.setStopListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
            }
        });
        
        // 初始化執行緒池
        customerGenerator = Executors.newSingleThreadScheduledExecutor();
        workerExecutor = Executors.newFixedThreadPool(NUM_WORKERS);
        
        // 初始化工作人員
        workers = new Worker[NUM_WORKERS];
        workerThreads = new Thread[NUM_WORKERS];
        
        for (int i = 0; i < NUM_WORKERS; i++) {
            workers[i] = new Worker(i + 1, restaurantQueue, gui);
            workerThreads[i] = new Thread(workers[i], "Worker-" + (i + 1));
        }
    }
    
    /**
     * 啟動模擬系統
     */
    public void startSimulation() {
        System.out.println("餐廳模擬系統啟動中...");
        
        // 顯示GUI
        SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });
        
        // 啟動工作人員執行緒
        for (Thread workerThread : workerThreads) {
            workerThread.start();
        }
        
        // 啟動顧客生成器
        startCustomerGeneration();
        
        System.out.println("系統啟動完成！");
        System.out.println("- 工作人員數量: " + NUM_WORKERS);
        System.out.println("- 顧客將隨機進店點餐");
        System.out.println("- 可透過GUI調整顧客到達速度");
        System.out.println("- 按下「結束模擬」按鈕停止系統");
    }
    
    private void startCustomerGeneration() {
        scheduleNextCustomer();
    }
    
    private void scheduleNextCustomer() {
        if (!running || !gui.isRunning()) {
            return;
        }
        
        customerGenerator.schedule(() -> {
            if (!running || !gui.isRunning()) {
                return;
            }
            
            try {
                // 生成新顧客
                Customer customer = new Customer();
                
                // 將訂單加入佇列
                restaurantQueue.addOrder(customer.getOrder());
                
                // 通知GUI顧客到達
                gui.customerArrived(customer);
                
                System.out.println("新顧客到達: " + customer + " 點了 " + customer.getOrder().getFoodType().getName());
                
                // 安排下一個顧客
                scheduleNextCustomer();
                
            } catch (Exception e) {
                System.err.println("生成顧客時發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
            
        }, getCustomerArrivalInterval(), TimeUnit.MILLISECONDS);
    }
    
    private long getCustomerArrivalInterval() {
        // 根據GUI設定調整顧客到達間隔
        int speed = gui.getCustomerArrivalSpeed(); // 1-10
        
        // 速度越高，間隔越短
        // 速度1: 2000ms (2秒), 速度10: 500ms (0.5秒)
        long baseInterval = 2500 - (speed * 200);
        
        // 加入隨機變化 ±30%
        double variation = 0.7 + (random.nextDouble() * 0.6); // 0.7 to 1.3
        
        return (long) (baseInterval * variation);
    }
    
    /**
     * 停止模擬系統
     */
    public void stopSimulation() {
        if (!running) return;
        
        running = false;
        System.out.println("正在停止餐廳模擬系統...");
        
        // 停止顧客生成
        if (customerGenerator != null && !customerGenerator.isShutdown()) {
            customerGenerator.shutdown();
            try {
                if (!customerGenerator.awaitTermination(2, TimeUnit.SECONDS)) {
                    customerGenerator.shutdownNow();
                }
            } catch (InterruptedException e) {
                customerGenerator.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // 停止工作人員
        for (Worker worker : workers) {
            if (worker != null) {
                worker.stop();
            }
        }
        
        // 中斷工作人員執行緒
        for (Thread workerThread : workerThreads) {
            if (workerThread != null && workerThread.isAlive()) {
                workerThread.interrupt();
            }
        }
        
        // 停止工作人員執行緒池
        if (workerExecutor != null && !workerExecutor.isShutdown()) {
            workerExecutor.shutdown();
            try {
                if (!workerExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                    workerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                workerExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        // 顯示最終統計
        displayFinalStatistics();
        
        System.out.println("餐廳模擬系統已停止");
        
        // 在短暫延遲後退出程式
        Timer exitTimer = new Timer(2000, e -> System.exit(0));
        exitTimer.setRepeats(false);
        exitTimer.start();
    }
    
    private void displayFinalStatistics() {
        System.out.println("\n=== 模擬結束統計 ===");
        System.out.println("總共加入的訂單數: " + restaurantQueue.getTotalOrdersAdded());
        System.out.println("總共處理的訂單數: " + restaurantQueue.getTotalOrdersProcessed());
        System.out.println("剩餘等待的訂單數: " + restaurantQueue.getQueueSize());
        System.out.println("==================");
    }
    
    /**
     * 主程式入口點
     */
    public static void main(String[] args) {
        // 設定Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("無法設定Nimbus Look and Feel，使用預設");
        }
        
        // 啟動模擬系統
        SwingUtilities.invokeLater(() -> {
            RestaurantSimulator simulator = new RestaurantSimulator();
            simulator.startSimulation();
        });
    }
}
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.UIManager;

/**
 * Main simulator class that integrates all components of the restaurant ordering system
 * Manages customer generation, worker threads, and GUI coordination
 */
public class RestaurantSimulator {
    private static final int NUM_WORKERS = 3;
    private static final Random random = new Random();
    
    private RestaurantQueue orderQueue;
    private RestaurantGUI gui;
    private List<Worker> workers;
    private List<Thread> workerThreads;
    private ScheduledExecutorService customerGenerator;
    private ScheduledExecutorService guiUpdater;
    
    private volatile boolean running = true;
    
    public RestaurantSimulator() {
        initializeComponents();
        setupWorkers();
        startCustomerGeneration();
        startGUIUpdates();
    }
    
    /**
     * Initialize main components
     */
    private void initializeComponents() {
        System.out.println("初始化餐廳點餐系統...");
        
        // Create order queue
        orderQueue = new RestaurantQueue(50);
        
        // Create and show GUI
        gui = new RestaurantGUI();
        System.out.println("GUI 介面已啟動");
        
        // Initialize collections
        workers = new ArrayList<>();
        workerThreads = new ArrayList<>();
        
        System.out.println("系統組件初始化完成");
    }
    
    /**
     * Setup and start worker threads
     */
    private void setupWorkers() {
        System.out.println("啟動工作人員...");
        
        for (int i = 1; i <= NUM_WORKERS; i++) {
            String workerName = "工作人員#" + i;
            Worker worker = new Worker(i, workerName, orderQueue, gui);
            Thread workerThread = new Thread(worker);
            
            workers.add(worker);
            workerThreads.add(workerThread);
            
            // Add worker to GUI
            gui.addWorker(i, workerName);
            
            // Start worker thread
            workerThread.start();
            System.out.println(workerName + " 已啟動");
        }
        
        System.out.println("所有工作人員已就緒");
    }
    
    /**
     * Start customer generation thread
     */
    private void startCustomerGeneration() {
        customerGenerator = Executors.newSingleThreadScheduledExecutor();
        
        // Initial delay, then repeat with dynamic interval
        customerGenerator.schedule(new CustomerGenerationTask(), 1, TimeUnit.SECONDS);
        
        System.out.println("顧客生成系統已啟動");
    }
    
    /**
     * Start GUI update thread
     */
    private void startGUIUpdates() {
        guiUpdater = Executors.newSingleThreadScheduledExecutor();
        
        // Update GUI every 500ms
        guiUpdater.scheduleAtFixedRate(new GUIUpdateTask(), 0, 500, TimeUnit.MILLISECONDS);
        
        System.out.println("GUI 更新系統已啟動");
    }
    
    /**
     * Customer generation task
     */
    private class CustomerGenerationTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!running || !gui.isRunning()) {
                    return;
                }
                
                // Generate new customer
                Customer customer = new Customer();
                Order order = customer.generateOrder();
                
                // Show customer arrival in GUI
                gui.showCustomerArrival(customer);
                
                // Add order to queue
                boolean added = orderQueue.addOrder(order);
                
                if (added) {
                    System.out.println("新顧客到達: " + customer);
                    System.out.println("新訂單加入佇列: " + order);
                } else {
                    System.out.println("訂單佇列已滿，無法接受新訂單: " + order);
                }
                
                // Schedule next customer with dynamic interval
                if (running && gui.isRunning()) {
                    int baseSpeed = gui.getCustomerArrivalSpeed();
                    int variation = random.nextInt(1000) - 500; // ±500ms variation
                    int nextDelay = Math.max(500, baseSpeed + variation); // Min 500ms
                    
                    customerGenerator.schedule(new CustomerGenerationTask(), 
                                             nextDelay, TimeUnit.MILLISECONDS);
                }
                
            } catch (Exception e) {
                System.err.println("顧客生成過程發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * GUI update task
     */
    private class GUIUpdateTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!running || !gui.isRunning()) {
                    return;
                }
                
                // Update order queue display
                gui.updateOrderQueue(orderQueue.getQueueSnapshot());
                
                // Update status
                String status = String.format("系統運行中 - 佇列: %d 筆訂單", orderQueue.getQueueSize());
                gui.updateStatus(status);
                
            } catch (Exception e) {
                System.err.println("GUI 更新過程發生錯誤: " + e.getMessage());
            }
        }
    }
    
    /**
     * Stop the simulation gracefully
     */
    public void stop() {
        System.out.println("正在停止餐廳點餐系統...");
        
        running = false;
        
        // Stop customer generation
        if (customerGenerator != null) {
            customerGenerator.shutdown();
            System.out.println("顧客生成系統已停止");
        }
        
        // Stop GUI updates
        if (guiUpdater != null) {
            guiUpdater.shutdown();
            System.out.println("GUI 更新系統已停止");
        }
        
        // Stop all workers
        for (Worker worker : workers) {
            worker.stop();
        }
        
        // Interrupt worker threads
        for (Thread workerThread : workerThreads) {
            workerThread.interrupt();
        }
        
        // Wait for threads to finish
        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join(2000); // Wait up to 2 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("餐廳點餐系統已完全停止");
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        System.out.println("=== 餐廳點餐系統啟動 ===");
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("無法設定系統外觀: " + e.getMessage());
        }
        
        // Create and start simulator
        RestaurantSimulator simulator = new RestaurantSimulator();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            simulator.stop();
        }));
        
        System.out.println("=== 系統已完全啟動 ===");
        System.out.println("請使用 GUI 介面操作系統，按下「結束系統」按鈕來停止程式。");
    }
}
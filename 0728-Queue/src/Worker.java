import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Worker thread class for processing restaurant orders
 * Implements Runnable interface for concurrent order processing
 */
public class Worker implements Runnable {
    private final int workerId;
    private final String workerName;
    private final RestaurantQueue orderQueue;
    private final RestaurantGUI gui;
    private volatile boolean running = true;
    private Order currentOrder = null;
    
    public Worker(int workerId, String workerName, RestaurantQueue orderQueue, RestaurantGUI gui) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.orderQueue = orderQueue;
        this.gui = gui;
    }
    
    @Override
    public void run() {
        System.out.println(workerName + " 開始工作");
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Take order from queue (blocking operation)
                Order order = orderQueue.pollOrder(1, TimeUnit.SECONDS);
                
                if (order != null) {
                    processOrder(order);
                }
                
            } catch (Exception e) {
                System.err.println(workerName + " 處理訂單時發生錯誤: " + e.getMessage());
            }
        }
        
        System.out.println(workerName + " 結束工作");
    }
    
    /**
     * Process an order by simulating cooking time
     * @param order The order to process
     */
    private void processOrder(Order order) {
        try {
            currentOrder = order;
            
            // Notify GUI that processing started
            if (gui != null) {
                gui.updateWorkerStatus(workerId, workerName + " 正在製作: " + order.getMealType().getChineseName());
                gui.updateProcessingOrder(workerId, order);
            }
            
            System.out.println(getCurrentTimeString() + " " + workerName + " 開始製作: " + order);
            
            // Simulate cooking time
            long cookingTimeMs = (long) (order.getCookingTimeSeconds() * 1000);
            Thread.sleep(cookingTimeMs);
            
            // Order completed
            System.out.println(getCurrentTimeString() + " " + workerName + " 完成製作: " + order);
            
            // Notify GUI that order is completed
            if (gui != null) {
                gui.updateWorkerStatus(workerId, workerName + " 空閒中");
                gui.addCompletedOrder(order);
                gui.removeProcessingOrder(workerId);
            }
            
            currentOrder = null;
            
        } catch (InterruptedException e) {
            // Worker was interrupted
            Thread.currentThread().interrupt();
            System.out.println(workerName + " 被中斷");
        }
    }
    
    /**
     * Stop the worker thread gracefully
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Check if worker is currently processing an order
     * @return true if processing an order
     */
    public boolean isBusy() {
        return currentOrder != null;
    }
    
    /**
     * Get current order being processed
     * @return Current order or null if idle
     */
    public Order getCurrentOrder() {
        return currentOrder;
    }
    
    /**
     * Get worker ID
     * @return Worker ID
     */
    public int getWorkerId() {
        return workerId;
    }
    
    /**
     * Get worker name
     * @return Worker name
     */
    public String getWorkerName() {
        return workerName;
    }
    
    /**
     * Get current time as formatted string
     * @return Current time string
     */
    private String getCurrentTimeString() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    /**
     * Get worker status string
     * @return Status description
     */
    public String getStatus() {
        if (currentOrder != null) {
            return workerName + " 正在製作: " + currentOrder.getMealType().getChineseName();
        } else {
            return workerName + " 空閒中";
        }
    }
}
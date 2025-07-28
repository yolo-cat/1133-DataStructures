import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 餐廳佇列管理類別 - 提供執行緒安全的訂單佇列操作
 */
public class RestaurantQueue {
    private final BlockingQueue<Order> orderQueue;
    private final AtomicInteger totalOrdersProcessed;
    private final AtomicInteger totalOrdersAdded;
    
    public RestaurantQueue() {
        this.orderQueue = new LinkedBlockingQueue<>();
        this.totalOrdersProcessed = new AtomicInteger(0);
        this.totalOrdersAdded = new AtomicInteger(0);
    }
    
    /**
     * 將訂單加入佇列 (執行緒安全)
     * @param order 要加入的訂單
     */
    public void addOrder(Order order) {
        try {
            orderQueue.put(order);
            totalOrdersAdded.incrementAndGet();
            System.out.println("訂單已加入佇列: " + order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("加入訂單時被中斷: " + e.getMessage());
        }
    }
    
    /**
     * 從佇列取出訂單進行處理 (執行緒安全，會阻塞直到有訂單)
     * @return 下一個要處理的訂單
     * @throws InterruptedException 如果執行緒被中斷
     */
    public Order takeOrder() throws InterruptedException {
        Order order = orderQueue.take();
        totalOrdersProcessed.incrementAndGet();
        return order;
    }
    
    /**
     * 嘗試從佇列取出訂單 (不阻塞)
     * @return 訂單或null如果佇列為空
     */
    public Order pollOrder() {
        Order order = orderQueue.poll();
        if (order != null) {
            totalOrdersProcessed.incrementAndGet();
        }
        return order;
    }
    
    /**
     * 取得目前佇列大小
     * @return 佇列中等待處理的訂單數量
     */
    public int getQueueSize() {
        return orderQueue.size();
    }
    
    /**
     * 檢查佇列是否為空
     * @return true如果佇列為空
     */
    public boolean isEmpty() {
        return orderQueue.isEmpty();
    }
    
    /**
     * 取得已處理的總訂單數
     * @return 已處理的訂單數量
     */
    public int getTotalOrdersProcessed() {
        return totalOrdersProcessed.get();
    }
    
    /**
     * 取得已加入的總訂單數
     * @return 已加入的訂單數量
     */
    public int getTotalOrdersAdded() {
        return totalOrdersAdded.get();
    }
    
    /**
     * 取得佇列中所有訂單的陣列 (用於GUI顯示)
     * @return 包含所有等待訂單的陣列
     */
    public Order[] getQueueSnapshot() {
        return orderQueue.toArray(new Order[0]);
    }
    
    /**
     * 清空佇列
     */
    public void clear() {
        orderQueue.clear();
    }
}
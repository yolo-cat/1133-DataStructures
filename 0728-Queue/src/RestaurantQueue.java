import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Thread-safe restaurant order queue management class
 * Uses BlockingQueue for thread-safe operations
 */
public class RestaurantQueue {
    private final BlockingQueue<Order> orderQueue;
    private final int maxCapacity;
    
    public RestaurantQueue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.orderQueue = new LinkedBlockingQueue<>(maxCapacity);
    }
    
    public RestaurantQueue() {
        this(100); // Default capacity
    }
    
    /**
     * Add an order to the queue
     * @param order The order to add
     * @return true if added successfully, false if queue is full
     */
    public boolean addOrder(Order order) {
        try {
            return orderQueue.offer(order, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Take an order from the queue (blocking operation)
     * @return The next order in queue, or null if interrupted
     */
    public Order takeOrder() {
        try {
            return orderQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
    
    /**
     * Poll an order from the queue with timeout
     * @param timeout Timeout value
     * @param unit Time unit
     * @return The next order or null if timeout
     */
    public Order pollOrder(long timeout, TimeUnit unit) {
        try {
            return orderQueue.poll(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
    
    /**
     * Get current queue size
     * @return Number of orders in queue
     */
    public int getQueueSize() {
        return orderQueue.size();
    }
    
    /**
     * Check if queue is empty
     * @return true if empty
     */
    public boolean isEmpty() {
        return orderQueue.isEmpty();
    }
    
    /**
     * Check if queue is full
     * @return true if full
     */
    public boolean isFull() {
        return orderQueue.size() >= maxCapacity;
    }
    
    /**
     * Get remaining capacity
     * @return Number of orders that can still be added
     */
    public int getRemainingCapacity() {
        return orderQueue.remainingCapacity();
    }
    
    /**
     * Get a snapshot of current orders in queue (for display purposes)
     * @return List of orders currently in queue
     */
    public List<Order> getQueueSnapshot() {
        return new ArrayList<>(orderQueue);
    }
    
    /**
     * Clear all orders from queue
     */
    public void clear() {
        orderQueue.clear();
    }
    
    /**
     * Get queue status information
     * @return String representation of queue status
     */
    public String getQueueStatus() {
        return String.format("佇列狀態: %d/%d (剩餘容量: %d)", 
                           getQueueSize(), maxCapacity, getRemainingCapacity());
    }
}
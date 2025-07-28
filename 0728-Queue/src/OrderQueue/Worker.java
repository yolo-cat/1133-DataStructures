/**
 * 工作人員執行緒類別 - 負責從佇列取出訂單並處理
 */
public class Worker implements Runnable {
    private final int workerId;
    private final RestaurantQueue restaurantQueue;
    private final RestaurantGUI gui;
    private volatile boolean running;
    private Order currentOrder;
    
    public Worker(int workerId, RestaurantQueue restaurantQueue, RestaurantGUI gui) {
        this.workerId = workerId;
        this.restaurantQueue = restaurantQueue;
        this.gui = gui;
        this.running = true;
        this.currentOrder = null;
    }
    
    @Override
    public void run() {
        System.out.println("工作人員 #" + workerId + " 開始工作");
        
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // 從佇列取出訂單
                Order order = restaurantQueue.takeOrder();
                currentOrder = order;
                
                // 通知GUI開始處理訂單
                if (gui != null) {
                    gui.updateWorkerStatus(workerId, "處理中: " + order.getFoodType().getName());
                }
                
                System.out.println("工作人員 #" + workerId + " 開始製作: " + order);
                
                // 模擬製作時間
                Thread.sleep(order.getPreparationTime());
                
                // 訂單完成 - 佔用桌位開始用餐
                System.out.println("工作人員 #" + workerId + " 完成製作: " + order);

                // 食物完成後，顧客開始用餐並佔用桌位
                int diningTime = 5000 + new java.util.Random().nextInt(6000); // 5~10秒
                restaurantQueue.occupyTable(order.getTableNumber(), diningTime);
                System.out.println("桌號 " + order.getTableNumber() + " 用餐中，預計用餐 " + (diningTime/1000) + " 秒");
                // 註: 不在此處等待用餐結束，讓廚師可以繼續處理下一個訂單

                // 通知GUI訂單完成
                if (gui != null) {
                    gui.orderCompleted(order);
                    gui.updateWorkerStatus(workerId, "等待中");
                }
                
                currentOrder = null;
                
            } catch (InterruptedException e) {
                // 執行緒被中斷，正常退出
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("工作人員 #" + workerId + " 發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("工作人員 #" + workerId + " 結束工作");
    }
    
    /**
     * 停止工作人員
     */
    public void stop() {
        running = false;
    }
    
    /**
     * 取得工作人員ID
     * @return 工作人員ID
     */
    public int getWorkerId() {
        return workerId;
    }
    
    /**
     * 取得目前正在處理的訂單
     * @return 目前訂單，如果沒有則為null
     */
    public Order getCurrentOrder() {
        return currentOrder;
    }
    
    /**
     * 檢查工作人員是否正在工作
     * @return true如果正在處理訂單
     */
    public boolean isWorking() {
        return currentOrder != null;
    }
    
    /**
     * 取得工作人員狀態描述
     * @return 狀態字串
     */
    public String getStatus() {
        if (currentOrder != null) {
            return "處理中: " + currentOrder.getFoodType().getName();
        } else {
            return "等待中";
        }
    }
}
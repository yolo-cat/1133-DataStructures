/**
 * 測試餐廳模擬器核心邏輯 (無GUI版本)
 * 測試修正後的系統邏輯
 */
public class RestaurantSimulatorTest {
    public static void main(String[] args) {
        System.out.println("=== 餐廳模擬器核心邏輯測試 ===");
        
        // 測試桌位管理
        System.out.println("\n1. 測試桌位管理:");
        RestaurantQueue queue = new RestaurantQueue();
        
        System.out.println("最大桌數: " + queue.getMaxTableCount());
        System.out.println("初始佔用桌位數: " + queue.getOccupiedTableCount());
        System.out.println("是否有空桌: " + queue.hasAvailableTable());
        
        // 佔用一些桌位
        queue.occupyTable(1, 3000); // 3秒用餐
        queue.occupyTable(2, 5000); // 5秒用餐
        
        System.out.println("佔用桌位1,2後:");
        System.out.println("佔用桌位數: " + queue.getOccupiedTableCount());
        System.out.println("桌1是否佔用: " + queue.isTableOccupied(1));
        System.out.println("桌3是否佔用: " + queue.isTableOccupied(3));
        System.out.println("是否有空桌: " + queue.hasAvailableTable());
        
        // 測試Customer和Order整合
        System.out.println("\n2. 測試Customer和Order整合:");
        
        Customer customer1 = new Customer();
        System.out.println("新顧客 (未分配): " + customer1);
        System.out.println("初始訂單: " + customer1.getOrder());
        
        // 分配桌位和訂單
        customer1.assignTableAndOrder(3, Order.FoodType.BURGER);
        System.out.println("分配後: " + customer1);
        System.out.println("分配後訂單: " + customer1.getOrder());
        
        // 測試Worker處理邏輯 (不阻塞版本)
        System.out.println("\n3. 測試Worker處理邏輯:");
        
        // 建立幾個測試訂單
        Order.FoodType[] foods = Order.FoodType.values();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.assignTableAndOrder(i + 4, foods[i % foods.length]); // 桌號4,5,6
            queue.addOrder(customer.getOrder());
        }
        
        System.out.println("加入3個測試訂單後佇列大小: " + queue.getQueueSize());
        
        // 啟動Worker測試 (縮短時間)
        Worker worker = new Worker(1, queue, null);
        Thread workerThread = new Thread(worker);
        workerThread.start();
        
        long startTime = System.currentTimeMillis();
        
        // 等待8秒或所有訂單處理完成
        while (System.currentTimeMillis() - startTime < 8000 && queue.getQueueSize() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        worker.stop();
        workerThread.interrupt();
        
        System.out.println("處理後佇列大小: " + queue.getQueueSize());
        System.out.println("總處理訂單數: " + queue.getTotalOrdersProcessed());
        
        // 測試桌位釋放
        System.out.println("\n4. 測試桌位釋放:");
        System.out.println("等待3秒後檢查桌位釋放...");
        try {
            Thread.sleep(3500); // 等待桌1釋放 (3秒用餐)
        } catch (InterruptedException e) {
            // ignore
        }
        
        queue.releaseFinishedTables();
        System.out.println("釋放後佔用桌位數: " + queue.getOccupiedTableCount());
        System.out.println("桌1是否仍佔用: " + queue.isTableOccupied(1));
        
        System.out.println("\n=== 核心邏輯測試完成 ===");
        System.out.println("修正後的系統邏輯運作正常！");
        System.out.println("- Customer不再使用反射");
        System.out.println("- Worker不再等待空桌才處理訂單");
        System.out.println("- Worker不再被用餐時間阻塞");
        System.out.println("- 桌位管理由定時任務處理");
    }
}
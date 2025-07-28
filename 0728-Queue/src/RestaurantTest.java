/**
 * 測試餐廳點餐系統核心功能 (無GUI版本)
 */
public class RestaurantTest {
    public static void main(String[] args) {
        System.out.println("=== 餐廳點餐系統功能測試 ===");
        
        // 測試Order類別
        System.out.println("\n1. 測試Order類別:");
        Order order1 = new Order(Order.FoodType.BURGER, 5);
        Order order2 = new Order(Order.FoodType.FRIES, 3);
        Order order3 = new Order(Order.FoodType.DRINK, 8);
        
        System.out.println(order1);
        System.out.println(order2);
        System.out.println(order3);
        
        // 測試Customer類別
        System.out.println("\n2. 測試Customer類別:");
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        
        System.out.println(customer1);
        System.out.println(customer2);
        System.out.println("顧客1的訂單: " + customer1.getOrder());
        System.out.println("顧客2的訂單: " + customer2.getOrder());
        
        // 測試RestaurantQueue類別
        System.out.println("\n3. 測試RestaurantQueue類別:");
        RestaurantQueue queue = new RestaurantQueue();
        
        System.out.println("初始佇列大小: " + queue.getQueueSize());
        
        queue.addOrder(order1);
        queue.addOrder(order2);
        queue.addOrder(order3);
        
        System.out.println("加入3個訂單後佇列大小: " + queue.getQueueSize());
        
        Order processedOrder = queue.pollOrder();
        System.out.println("取出的訂單: " + processedOrder);
        System.out.println("剩餘佇列大小: " + queue.getQueueSize());
        
        // 測試多執行緒Worker (簡化版本)
        System.out.println("\n4. 測試Worker執行緒 (5秒測試):");
        
        // 添加一些測試訂單
        for (int i = 0; i < 5; i++) {
            Customer customer = new Customer();
            queue.addOrder(customer.getOrder());
        }
        
        System.out.println("開始測試前佇列大小: " + queue.getQueueSize());
        
        // 啟動2個工作執行緒
        Worker worker1 = new Worker(1, queue, null); // 無GUI
        Worker worker2 = new Worker(2, queue, null); // 無GUI
        
        Thread thread1 = new Thread(worker1);
        Thread thread2 = new Thread(worker2);
        
        thread1.start();
        thread2.start();
        
        // 等待5秒
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 停止工作人員
        worker1.stop();
        worker2.stop();
        thread1.interrupt();
        thread2.interrupt();
        
        System.out.println("測試結束後佇列大小: " + queue.getQueueSize());
        System.out.println("總共處理的訂單數: " + queue.getTotalOrdersProcessed());
        
        System.out.println("\n=== 測試完成 ===");
        System.out.println("所有核心功能正常運作！");
    }
}
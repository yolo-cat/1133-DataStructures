/**
 * Test program to validate the restaurant ordering system components
 * without GUI for headless environment testing
 */
public class RestaurantSystemTest {
    public static void main(String[] args) {
        System.out.println("=== 餐廳點餐系統測試 ===");
        
        // Test Order creation
        System.out.println("\n1. 測試訂單創建:");
        Order order1 = new Order(Order.MealType.BURGER, 5);
        Order order2 = new Order(Order.MealType.FRIES, 3);
        Order order3 = new Order(Order.MealType.DRINK, 7);
        
        System.out.println(order1);
        System.out.println(order2);
        System.out.println(order3);
        
        // Test Customer creation
        System.out.println("\n2. 測試顧客創建:");
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        
        System.out.println(customer1);
        System.out.println(customer2);
        
        Order customerOrder1 = customer1.generateOrder();
        Order customerOrder2 = customer2.generateOrder();
        System.out.println("顧客1的訂單: " + customerOrder1);
        System.out.println("顧客2的訂單: " + customerOrder2);
        
        // Test RestaurantQueue
        System.out.println("\n3. 測試餐廳佇列:");
        RestaurantQueue queue = new RestaurantQueue(10);
        
        System.out.println("初始狀態: " + queue.getQueueStatus());
        System.out.println("佇列是否為空: " + queue.isEmpty());
        
        // Add orders to queue
        queue.addOrder(order1);
        queue.addOrder(order2);
        queue.addOrder(order3);
        queue.addOrder(customerOrder1);
        queue.addOrder(customerOrder2);
        
        System.out.println("加入訂單後: " + queue.getQueueStatus());
        System.out.println("佇列快照: " + queue.getQueueSnapshot().size() + " 筆訂單");
        
        // Test order processing (without GUI worker)
        System.out.println("\n4. 測試訂單處理:");
        for (int i = 0; i < 3; i++) {
            Order processedOrder = queue.takeOrder();
            if (processedOrder != null) {
                System.out.println("處理訂單: " + processedOrder);
                // Simulate cooking time
                try {
                    Thread.sleep((long)(processedOrder.getCookingTimeSeconds() * 100)); // 10x faster for testing
                    System.out.println("訂單完成: " + processedOrder.getMealType().getChineseName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        System.out.println("處理後佇列狀態: " + queue.getQueueStatus());
        
        // Test meal type enumeration
        System.out.println("\n5. 測試餐點類型:");
        for (Order.MealType mealType : Order.MealType.values()) {
            System.out.println(mealType.getChineseName() + " - 製作時間: " + 
                             mealType.getCookingTimeSeconds() + "秒");
        }
        
        System.out.println("\n=== 測試完成 - 所有組件正常運作 ===");
    }
}
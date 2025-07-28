import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 訂單類別 - 代表餐廳的一張訂單
 */
public class Order {
    private static int orderCounter = 1;
    
    // 餐點類型枚舉
    public enum FoodType {
        BURGER("漢堡", 4000),    // 4秒
        FRIES("薯條", 2000),     // 2秒
        DRINK("飲料", 1000);      // 1秒
        
        private final String name;
        private final int preparationTime; // 毫秒
        
        FoodType(String name, int preparationTime) {
            this.name = name;
            this.preparationTime = preparationTime;
        }
        
        public String getName() { return name; }
        public int getPreparationTime() { return preparationTime; }
    }
    
    private final int orderId;
    private final FoodType foodType;
    private final int tableNumber;
    private final LocalDateTime orderTime;
    private final int preparationTime;
    
    public Order(FoodType foodType, int tableNumber) {
        this.orderId = orderCounter++;
        this.foodType = foodType;
        this.tableNumber = tableNumber;
        this.orderTime = LocalDateTime.now();
        this.preparationTime = foodType.getPreparationTime();
    }
    
    // Getter methods
    public int getOrderId() { return orderId; }
    public FoodType getFoodType() { return foodType; }
    public int getTableNumber() { return tableNumber; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public int getPreparationTime() { return preparationTime; }
    
    public String getFormattedOrderTime() {
        return orderTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("訂單#%d - %s (桌號:%d, 時間:%s)", 
                orderId, foodType.getName(), tableNumber, getFormattedOrderTime());
    }
}
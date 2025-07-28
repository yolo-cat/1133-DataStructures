import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Order class representing a customer's meal order
 * Contains meal type, table number, creation time, and cooking time
 */
public class Order {
    public enum MealType {
        BURGER(2.0, "漢堡"),
        FRIES(1.0, "薯條"), 
        DRINK(0.5, "飲料");
        
        private final double cookingTimeSeconds;
        private final String chineseName;
        
        MealType(double cookingTimeSeconds, String chineseName) {
            this.cookingTimeSeconds = cookingTimeSeconds;
            this.chineseName = chineseName;
        }
        
        public double getCookingTimeSeconds() {
            return cookingTimeSeconds;
        }
        
        public String getChineseName() {
            return chineseName;
        }
    }
    
    private static int orderCounter = 1;
    
    private final int orderId;
    private final MealType mealType;
    private final int tableNumber;
    private final LocalTime creationTime;
    private final double cookingTimeSeconds;
    
    public Order(MealType mealType, int tableNumber) {
        this.orderId = orderCounter++;
        this.mealType = mealType;
        this.tableNumber = tableNumber;
        this.creationTime = LocalTime.now();
        this.cookingTimeSeconds = mealType.getCookingTimeSeconds();
    }
    
    // Getters
    public int getOrderId() {
        return orderId;
    }
    
    public MealType getMealType() {
        return mealType;
    }
    
    public int getTableNumber() {
        return tableNumber;
    }
    
    public LocalTime getCreationTime() {
        return creationTime;
    }
    
    public double getCookingTimeSeconds() {
        return cookingTimeSeconds;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("訂單#%d - %s (桌號:%d, 時間:%s, 製作時間:%.1f秒)", 
                           orderId, mealType.getChineseName(), tableNumber, 
                           creationTime.format(formatter), cookingTimeSeconds);
    }
}
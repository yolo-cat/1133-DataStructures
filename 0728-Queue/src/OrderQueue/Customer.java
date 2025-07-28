import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 顧客類別 - 代表來餐廳的顧客
 */
public class Customer {
    private static int customerCounter = 1;
    private static Random random = new Random();
    
    private final int customerId;
    private final LocalDateTime arrivalTime;
    private final int tableNumber;
    private final Order order;
    
    public Customer() {
        this.customerId = customerCounter++;
        this.arrivalTime = LocalDateTime.now();
        this.tableNumber = generateRandomTableNumber();
        this.order = generateRandomOrder();
    }
    
    private int generateRandomTableNumber() {
        return random.nextInt(5) + 1; // 桌號 1-5
    }
    
    private Order generateRandomOrder() {
        Order.FoodType[] foods = Order.FoodType.values();
        Order.FoodType randomFood = foods[random.nextInt(foods.length)];
        return new Order(randomFood, tableNumber);
    }
    
    // Getter methods
    public int getCustomerId() { return customerId; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public int getTableNumber() { return tableNumber; }
    public Order getOrder() { return order; }
    
    public String getFormattedArrivalTime() {
        return arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("顧客#%d (桌號:%d, 到達時間:%s)", 
                customerId, tableNumber, getFormattedArrivalTime());
    }
}
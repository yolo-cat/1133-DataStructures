import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Customer class representing a restaurant customer
 * Contains customer ID, arrival time, and generates orders
 */
public class Customer {
    private static int customerCounter = 1;
    private static final Random random = new Random();
    
    private final int customerId;
    private final LocalTime arrivalTime;
    private final int tableNumber;
    
    public Customer() {
        this.customerId = customerCounter++;
        this.arrivalTime = LocalTime.now();
        this.tableNumber = random.nextInt(20) + 1; // Random table 1-20
    }
    
    /**
     * Generate a random order for this customer
     * @return A new Order with random meal type
     */
    public Order generateOrder() {
        Order.MealType[] mealTypes = Order.MealType.values();
        Order.MealType randomMealType = mealTypes[random.nextInt(mealTypes.length)];
        return new Order(randomMealType, tableNumber);
    }
    
    // Getters
    public int getCustomerId() {
        return customerId;
    }
    
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    
    public int getTableNumber() {
        return tableNumber;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("顧客#%d (桌號:%d, 到達時間:%s)", 
                           customerId, tableNumber, arrivalTime.format(formatter));
    }
}
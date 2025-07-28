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
    private int tableNumber; // 改為非final，初始為-1
    private Order order;

    public Customer() {
        this.customerId = customerCounter++;
        this.arrivalTime = LocalDateTime.now();
        this.tableNumber = -1; // 尚未分配座位
        this.order = null; // 訂單稍後產生
    }
    
    // 新增：分配座位與產生訂單
    public void assignTableAndOrder(int tableNumber, Order.FoodType foodType) {
        this.tableNumber = tableNumber;
        // 產生訂單
        Order order = new Order(foodType, tableNumber);
        // 反射設置order欄位（或改為getter動態產生）
        try {
            java.lang.reflect.Field f = Customer.class.getDeclaredField("order");
            f.setAccessible(true);
            f.set(this, order);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        return String.format("顧客#%d (桌號:%s, 到達時間:%s)",
                customerId, (tableNumber > 0 ? tableNumber : "無座位"), getFormattedArrivalTime());
    }
}
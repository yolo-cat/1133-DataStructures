import java.time.LocalDateTime;
import java.util.Random;

public class Item implements Comparable<Item> {
    private final int id;
    private final LocalDateTime producedAt;
    private static final Random random = new Random();
    private static final int MIN_ID = 100;
    private static final int MAX_ID = 999;

    // 原有的建構子，保持向後相容
    public Item(int id) {
        this.id = id;
        this.producedAt = LocalDateTime.now();
    }

    // 新增隨機編號建構子
    public Item() {
        this.id = MIN_ID + random.nextInt(MAX_ID - MIN_ID + 1);
        this.producedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getProducedAt() {
        return producedAt;
    }

    @Override
    public int compareTo(Item other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format("Item{id=%d, producedAt=%s}", id,
            producedAt.toString().substring(11, 19));
    }
}

import java.util.LinkedList;
import java.util.Queue;

public class ProcessedQueue {
    private final Queue<Item> queue = new LinkedList<>();
    private final int capacity;

    public ProcessedQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(Item item) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.offer(item);
        notifyAll();
    }

    public synchronized Item take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Item item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}


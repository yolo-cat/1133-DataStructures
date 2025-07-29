import java.util.PriorityQueue;
import java.util.Queue;

public class MinHeapBuffer {
    private final int capacity;
    private final Queue<Item> heap;

    public MinHeapBuffer(int capacity) {
        this.capacity = capacity;
        this.heap = new PriorityQueue<>();
    }

    public synchronized void put(Item item) throws InterruptedException {
        while (heap.size() >= capacity) {
            wait();
        }
        heap.offer(item);
        notifyAll();
    }

    public synchronized Item take() throws InterruptedException {
        while (heap.isEmpty()) {
            wait();
        }
        Item item = heap.poll();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return heap.size();
    }

    public synchronized boolean isFull() {
        return heap.size() >= capacity;
    }

    public synchronized boolean isEmpty() {
        return heap.isEmpty();
    }

    public synchronized String itemsInfo() {
        StringBuilder sb = new StringBuilder();
        for (Item item : heap) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}

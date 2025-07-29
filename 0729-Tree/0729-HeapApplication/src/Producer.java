public class Producer extends Thread {
    private final MinHeapBuffer buffer;
    private final int produceIntervalMs;
    private final ProducerConsumerGUI gui;
    private int nextId = 1;
    private volatile boolean running = true;

    public Producer(MinHeapBuffer buffer, int produceIntervalMs) {
        this(buffer, produceIntervalMs, null);
    }

    public Producer(MinHeapBuffer buffer, int produceIntervalMs, ProducerConsumerGUI gui) {
        this.buffer = buffer;
        this.produceIntervalMs = produceIntervalMs;
        this.gui = gui;
    }

    public void stopProducer() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Item item = new Item(); // 使用隨機編號建構子
                buffer.put(item);
                String message = "生產: " + item;
                System.out.println(message);
                if (gui != null) {
                    gui.appendToProducer(message);
                    gui.triggerProducerToBufferAnimation(item); // 觸發動畫
                }
                Thread.sleep(produceIntervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Consumer extends Thread {
    private final MinHeapBuffer buffer;
    private final int consumeIntervalMs;
    private volatile boolean running = true;

    public Consumer(MinHeapBuffer buffer, int consumeIntervalMs) {
        this.buffer = buffer;
        this.consumeIntervalMs = consumeIntervalMs;
    }

    public void stopConsumer() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Item item = buffer.take();
                // 可在此處理 item，例如列印或傳遞給 GUI
                System.out.println("消費: " + item);
                Thread.sleep(consumeIntervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


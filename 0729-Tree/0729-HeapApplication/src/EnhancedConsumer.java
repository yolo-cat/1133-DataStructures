public class EnhancedConsumer extends Thread {
    private final MinHeapBuffer buffer;
    private final int consumeIntervalMs;
    private final ProducerConsumerGUI gui;
    private volatile boolean running = true;

    public EnhancedConsumer(MinHeapBuffer buffer, int consumeIntervalMs, ProducerConsumerGUI gui) {
        this.buffer = buffer;
        this.consumeIntervalMs = consumeIntervalMs;
        this.gui = gui;
    }

    public void stopConsumer() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Item item = buffer.take();
                String message = "消費: " + item;
                System.out.println(message);
                if (gui != null) {
                    gui.appendToConsumer(message);
                }
                Thread.sleep(consumeIntervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

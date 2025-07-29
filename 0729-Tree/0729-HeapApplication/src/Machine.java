public class Machine extends Thread {
    private final MinHeapBuffer buffer;
    private final ProcessedQueue processedQueue;
    private final int processIntervalMs;
    private final ProducerConsumerGUI gui;
    private volatile boolean running = true;

    public Machine(MinHeapBuffer buffer, ProcessedQueue processedQueue, int processIntervalMs, ProducerConsumerGUI gui) {
        this.buffer = buffer;
        this.processedQueue = processedQueue;
        this.processIntervalMs = processIntervalMs;
        this.gui = gui;
    }

    public void stopMachine() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Item item = buffer.take();
                if (gui != null) {
                    gui.triggerBufferToMachineAnimation(item); // 觸發緩衝區到機台的動畫
                }
                String message = "機台處理: " + item;
                System.out.println(message);
                if (gui != null) {
                    gui.appendToMachine(message);
                }
                // 處理後放入暫存區
                processedQueue.put(item);
                if (gui != null) {
                    gui.triggerMachineToConsumerAnimation(item); // 觸發機台到消費者的動畫
                }
                Thread.sleep(processIntervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

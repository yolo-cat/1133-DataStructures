import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProducerConsumerGUI extends JFrame {
    private JTextField bufferSizeField;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea producerArea;
    private JTextArea bufferStatusArea;
    private JTextArea machineArea;
    private JTextArea consumerArea;
    private JLabel bufferSizeLabel;
    private JLabel currentBufferSizeLabel;

    private MinHeapBuffer buffer;
    private Producer producer;
    private EnhancedConsumer consumer;
    private ProcessedQueue processedQueue;
    private Machine machine;
    private Timer updateTimer;
    private FlowVisualizationPanel flowPanel;

    public ProducerConsumerGUI() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("生產者-消費者模擬 (Min-Heap Buffer) - 四區域顯示");
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // 控制面板元件
        bufferSizeField = new JTextField("10", 10);
        startButton = new JButton("開始生產");
        stopButton = new JButton("停止生產");
        stopButton.setEnabled(false);

        // 四個顯示區域
        producerArea = new JTextArea(15, 25);
        producerArea.setEditable(false);
        producerArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        producerArea.setBackground(new Color(230, 255, 230)); // 淺綠色

        bufferStatusArea = new JTextArea(15, 25);
        bufferStatusArea.setEditable(false);
        bufferStatusArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        bufferStatusArea.setBackground(new Color(255, 255, 230)); // 淺黃色

        machineArea = new JTextArea(15, 25);
        machineArea.setEditable(false);
        machineArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        machineArea.setBackground(new Color(230, 230, 255)); // 淺藍色

        consumerArea = new JTextArea(15, 25);
        consumerArea.setEditable(false);
        consumerArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        consumerArea.setBackground(new Color(255, 230, 230)); // 淺紅色

        bufferSizeLabel = new JLabel("緩衝區大小:");
        currentBufferSizeLabel = new JLabel("當前緩衝區: 0/0");

        // 初始化流程動畫面板
        flowPanel = new FlowVisualizationPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(bufferSizeLabel);
        controlPanel.add(bufferSizeField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(currentBufferSizeLabel);

        // 四區域顯示面板
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 生產者區域
        JPanel producerPanel = new JPanel(new BorderLayout());
        producerPanel.setBorder(BorderFactory.createTitledBorder("🏭 生產者 (Producer)"));
        producerPanel.add(new JScrollPane(producerArea), BorderLayout.CENTER);

        // Buffer 狀態區域
        JPanel bufferPanel = new JPanel(new BorderLayout());
        bufferPanel.setBorder(BorderFactory.createTitledBorder("📦 緩衝區狀態 (Buffer Status)"));
        bufferPanel.add(new JScrollPane(bufferStatusArea), BorderLayout.CENTER);

        // 機台區域
        JPanel machinePanel = new JPanel(new BorderLayout());
        machinePanel.setBorder(BorderFactory.createTitledBorder("⚙️ 機台處理 (Machine Processing)"));
        machinePanel.add(new JScrollPane(machineArea), BorderLayout.CENTER);

        // 消費者區域
        JPanel consumerPanel = new JPanel(new BorderLayout());
        consumerPanel.setBorder(BorderFactory.createTitledBorder("🚚 消費者 (Consumer)"));
        consumerPanel.add(new JScrollPane(consumerArea), BorderLayout.CENTER);

        // 按順序添加到主面板
        mainPanel.add(producerPanel);
        mainPanel.add(bufferPanel);
        mainPanel.add(machinePanel);
        mainPanel.add(consumerPanel);

        add(controlPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(flowPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startProduction();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopProduction();
            }
        });
    }

    private void startProduction() {
        try {
            int bufferSize = Integer.parseInt(bufferSizeField.getText());
            if (bufferSize <= 0) {
                JOptionPane.showMessageDialog(this, "緩衝區大小必須大於 0", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 清空所有顯示區域
            clearAllAreas();

            // 初始化緩衝區和執行緒
            buffer = new MinHeapBuffer(bufferSize);
            processedQueue = new ProcessedQueue(bufferSize); // 暫存區容量同 buffer
            producer = new Producer(buffer, 200, this);
            machine = new Machine(buffer, processedQueue, 240, this);
            consumer = new EnhancedConsumer(processedQueue, 240, this);

            // 啟動執行緒
            producer.start();
            machine.start();
            consumer.start();

            // 更新 UI 狀態
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            bufferSizeField.setEnabled(false);

            // 啟動定時更新
            startUpdateTimer();

            appendToMachine("=== 系統啟動 ===");
            appendToMachine("緩衝區大小: " + bufferSize);
            appendToMachine("生產週期: 200ms");
            appendToMachine("機台處理週期: 240ms");
            appendToMachine("消費週期: 240ms");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的數字", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopProduction() {
        if (producer != null) {
            producer.stopProducer();
        }
        if (machine != null) {
            machine.stopMachine();
        }
        if (consumer != null) {
            consumer.stopConsumer();
        }
        if (updateTimer != null) {
            updateTimer.stop();
        }

        // 重設 UI 狀態
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        bufferSizeField.setEnabled(true);

        appendToMachine("=== 系統停止 ===");
    }

    private void clearAllAreas() {
        producerArea.setText("");
        bufferStatusArea.setText("");
        machineArea.setText("");
        consumerArea.setText("");
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBufferStatus();
            }
        });
        updateTimer.start();
    }

    private void updateBufferStatus() {
        if (buffer != null) {
            int currentSize = buffer.size();
            int capacity = Integer.parseInt(bufferSizeField.getText());
            currentBufferSizeLabel.setText("當前緩衝區: " + currentSize + "/" + capacity);

            // 更新緩衝區狀態顯示
            StringBuilder status = new StringBuilder();
            status.append("=== 緩衝區即時狀態 ===\n");
            status.append("容量: ").append(capacity).append("\n");
            status.append("當前大小: ").append(currentSize).append("\n");
            status.append("使用率: ").append(String.format("%.1f%%", (double) currentSize / capacity * 100)).append("\n");
            status.append("狀態: ");
            if (buffer.isFull()) {
                status.append("🔴 已滿\n");
            } else if (buffer.isEmpty()) {
                status.append("⚪ 空的\n");
            } else {
                status.append("🟢 正常\n");
            }

            // 顯示進度條
            int barLength = 20;
            int filled = (int) ((double) currentSize / capacity * barLength);
            status.append("進度: [");
            for (int i = 0; i < barLength; i++) {
                if (i < filled) {
                    status.append("█");
                } else {
                    status.append("░");
                }
            }
            status.append("]\n\n");

            status.append("=== Buffer 內容 ===\n");
            status.append(buffer.itemsInfo());
            status.append("\n");

            status.append("=== 歷史統計 ===\n");
            // 這裡可以添加更多統計信息

            bufferStatusArea.setText(status.toString());
        }
    }

    // 四個區域的訊息添加方法
    public void appendToProducer(String message) {
        SwingUtilities.invokeLater(() -> {
            producerArea.append(getCurrentTime() + " " + message + "\n");
            producerArea.setCaretPosition(producerArea.getDocument().getLength());
        });
    }

    public void appendToMachine(String message) {
        SwingUtilities.invokeLater(() -> {
            machineArea.append(getCurrentTime() + " " + message + "\n");
            machineArea.setCaretPosition(machineArea.getDocument().getLength());
        });
    }

    public void appendToConsumer(String message) {
        SwingUtilities.invokeLater(() -> {
            consumerArea.append(getCurrentTime() + " " + message + "\n");
            consumerArea.setCaretPosition(consumerArea.getDocument().getLength());
        });
    }

    // 動畫觸發方法
    public void triggerProducerToBufferAnimation(Item item) {
        if (flowPanel != null) {
            flowPanel.addProducerToBuffer(item);
        }
    }

    public void triggerBufferToMachineAnimation(Item item) {
        if (flowPanel != null) {
            flowPanel.addBufferToMachine(item);
        }
    }

    public void triggerMachineToConsumerAnimation(Item item) {
        if (flowPanel != null) {
            flowPanel.addMachineToConsumer(item);
        }
    }

    private String getCurrentTime() {
        return String.format("[%02d:%02d:%02d]",
                (System.currentTimeMillis() / 1000) % 86400 / 3600,
                (System.currentTimeMillis() / 1000) % 3600 / 60,
                (System.currentTimeMillis() / 1000) % 60);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ProducerConsumerGUI().setVisible(true);
        });
    }
}

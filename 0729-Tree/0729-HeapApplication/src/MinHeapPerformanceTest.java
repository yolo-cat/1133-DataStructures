import java.util.*;

public class MinHeapPerformanceTest {

    public static void main(String[] args) {
        System.out.println("=== Min-Heap 效能測試 ===");

        // 測試不同資料量的效能
        int[] testSizes = {100, 500, 1000, 5000, 10000};

        for (int size : testSizes) {
            System.out.println("\n--- 測試資料量: " + size + " ---");
            testPerformance(size);
        }

        // 測試 MinHeapBuffer 的正確性
        System.out.println("\n=== Min-Heap 正確性驗證 ===");
        testMinHeapCorrectness();

        // 測試隨機編號分布
        System.out.println("\n=== 隨機編號分布測試 ===");
        testRandomIdDistribution();
    }

    /**
     * 比較線性搜尋與 Min-Heap 的效能
     */
    public static void testPerformance(int dataSize) {
        // 生成測試資料
        List<Item> testItems = generateRandomItems(dataSize);

        // 測試線性搜尋效能
        long linearTime = testLinearSearch(new ArrayList<>(testItems));

        // 測試 Min-Heap 效能
        long heapTime = testMinHeapSearch(new ArrayList<>(testItems));

        // 計算效能提升比例
        double improvement = (double) linearTime / heapTime;

        System.out.println("線性搜尋時間: " + linearTime + " ms");
        System.out.println("Min-Heap 時間: " + heapTime + " ms");
        System.out.println("效能提升: " + String.format("%.2f", improvement) + " 倍");
        System.out.println("理論複雜度: O(n) vs O(log n)");
    }

    /**
     * 測試線性搜尋找最小值的效能
     */
    private static long testLinearSearch(List<Item> items) {
        long startTime = System.nanoTime();

        // 模擬多次找最小值操作
        for (int i = 0; i < items.size(); i++) {
            if (!items.isEmpty()) {
                // 線性搜尋找最小值
                Item min = items.get(0);
                int minIndex = 0;
                for (int j = 1; j < items.size(); j++) {
                    if (items.get(j).compareTo(min) < 0) {
                        min = items.get(j);
                        minIndex = j;
                    }
                }
                items.remove(minIndex); // 移除最小值
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // 轉換為毫秒
    }

    /**
     * 測試 Min-Heap 找最小值的效能
     */
    private static long testMinHeapSearch(List<Item> items) {
        long startTime = System.nanoTime();

        // 使用 PriorityQueue 模擬 MinHeapBuffer
        PriorityQueue<Item> heap = new PriorityQueue<>(items);

        // 模擬多次取最小值操作
        while (!heap.isEmpty()) {
            heap.poll(); // O(log n) 操作
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // 轉換為毫秒
    }

    /**
     * 生成隨機測試資料
     */
    private static List<Item> generateRandomItems(int count) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(new Item()); // 使用隨機編號建構子
        }
        return items;
    }

    /**
     * 測試 MinHeapBuffer 的正確性
     */
    public static void testMinHeapCorrectness() {
        try {
            MinHeapBuffer buffer = new MinHeapBuffer(50);
            List<Integer> insertedIds = new ArrayList<>();

            // 插入隨機物品
            System.out.println("插入隨機物品...");
            for (int i = 0; i < 20; i++) {
                Item item = new Item();
                insertedIds.add(item.getId());
                buffer.put(item);
                System.out.println("插入: " + item);
            }

            // 按順序取出，驗證是否為最小堆
            System.out.println("\n按最小堆順序取出...");
            List<Integer> extractedIds = new ArrayList<>();
            while (!buffer.isEmpty()) {
                Item item = buffer.take();
                extractedIds.add(item.getId());
                System.out.println("取出: " + item);
            }

            // 驗證取出順序是否為遞增
            boolean isCorrect = true;
            for (int i = 1; i < extractedIds.size(); i++) {
                if (extractedIds.get(i) < extractedIds.get(i-1)) {
                    isCorrect = false;
                    break;
                }
            }

            System.out.println("\n插入的ID: " + insertedIds);
            System.out.println("取出的ID: " + extractedIds);
            System.out.println("Min-Heap 正確性: " + (isCorrect ? "✓ 通過" : "✗ 失敗"));

        } catch (InterruptedException e) {
            System.err.println("測試被中斷: " + e.getMessage());
        }
    }

    /**
     * 測試隨機編號分布
     */
    public static void testRandomIdDistribution() {
        Map<Integer, Integer> distribution = new HashMap<>();
        int testCount = 10000;

        // 生成大量隨機編號
        for (int i = 0; i < testCount; i++) {
            Item item = new Item();
            int id = item.getId();
            distribution.put(id, distribution.getOrDefault(id, 0) + 1);
        }

        // 分析分布
        int min = Collections.min(distribution.keySet());
        int max = Collections.max(distribution.keySet());
        double avg = distribution.values().stream().mapToInt(i -> i).average().orElse(0);

        System.out.println("生成數量: " + testCount);
        System.out.println("不重複編號數: " + distribution.size());
        System.out.println("編號範圍: " + min + " ~ " + max);
        System.out.println("平均出現次數: " + String.format("%.2f", avg));
        System.out.println("分布均勻性: " + (distribution.size() > testCount * 0.8 ? "良好" : "需改善"));

        // 顯示前10個最常出現的編號
        System.out.println("\n最常出現的編號:");
        distribution.entrySet().stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> System.out.println("ID " + entry.getKey() + ": " + entry.getValue() + " 次"));
    }
}

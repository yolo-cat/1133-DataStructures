import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// 使用 Java Record 來簡潔地定義一個不可變的資料物件
record Transaction(String key, String date, String customerId, String itemName, double price) implements Comparable<Transaction> {
  @Override
  public int compareTo(Transaction other) {
    return this.key.compareTo(other.key);
  }
}

public class SearchPerformanceComparison {

  private static final int NUM_SEARCHES = 10;

  public static void main(String[] args) {
    String csvFile = "transactions.csv";
    System.out.println("開始從 " + csvFile + " 讀取資料...");
    List<Transaction> transactions = loadTransactions(csvFile);
    if (transactions.isEmpty()) {
      System.out.println("讀取資料失敗或檔案為空，程式終止。");
      return;
    }
    System.out.println("成功讀取 " + transactions.size() + " 筆資料。\n");

    // --- 1. 準備不同演算法所需的資料結構 ---
    System.out.println("正在準備搜尋所需的資料結構...");
    // a. 用於線性搜尋的原始列表 (無需額外處理)
    List<Transaction> linearSearchList = transactions;

    // b. 用於二分搜尋的排序列表
    List<Transaction> binarySearchList = new ArrayList<>(transactions);
    Collections.sort(binarySearchList);

    // c. 用於雜湊搜尋的 HashMap
    Map<String, Transaction> hashMap = new HashMap<>();
    for (Transaction t : transactions) {
      hashMap.put(t.key(), t);
    }
    System.out.println("資料結構準備完成。\n");


    // --- 2. 準備搜尋用的 KEY ---
    List<String> existingKeys = new ArrayList<>();
    List<String> nonExistingKeys = new ArrayList<>();
    prepareSearchKeys(transactions, existingKeys, nonExistingKeys);

    System.out.println("--- 搜尋存在的 KEY ---");
    runPerformanceTest(linearSearchList, binarySearchList, hashMap, existingKeys);

    System.out.println("\n--- 搜尋不存在的 KEY ---");
    runPerformanceTest(linearSearchList, binarySearchList, hashMap, nonExistingKeys);
  }

  /**
   * 執行並評測三種搜尋演算法的效能
   */
  private static void runPerformanceTest(List<Transaction> linearList, List<Transaction> sortedList, Map<String, Transaction> map, List<String> keysToSearch) {
    // 線性搜尋
    long linearTotalTime = 0;
    for (String key : keysToSearch) {
      long startTime = System.nanoTime();
      linearSearch(linearList, key);
      linearTotalTime += System.nanoTime() - startTime;
    }

    // 二分搜尋
    long binaryTotalTime = 0;
    for (String key : keysToSearch) {
      long startTime = System.nanoTime();
      binarySearch(sortedList, key);
      binaryTotalTime += System.nanoTime() - startTime;
    }

    // 雜湊搜尋
    long hashTotalTime = 0;
    for (String key : keysToSearch) {
      long startTime = System.nanoTime();
      hashSearch(map, key);
      hashTotalTime += System.nanoTime() - startTime;
    }

    System.out.println("+------------------+------------------------+");
    System.out.println("| 搜尋演算法       | 平均時間 (奈秒 ns)     |");
    System.out.println("+------------------+------------------------+");
    System.out.printf("| 線性搜尋 (O(n))    | %-22d |\n", linearTotalTime / NUM_SEARCHES);
    System.out.printf("| 二分搜尋 (O(log n))| %-22d |\n", binaryTotalTime / NUM_SEARCHES);
    System.out.printf("| 雜湊搜尋 (O(1))    | %-22d |\n", hashTotalTime / NUM_SEARCHES);
    System.out.println("+------------------+------------------------+");
  }

  /**
   * 從 CSV 檔案讀取交易資料
   */
  private static List<Transaction> loadTransactions(String filePath) {
    List<Transaction> transactions = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      br.readLine(); // 跳過標頭
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        if (values.length == 5) {
          Transaction t = new Transaction(
              values[0],
              values[1],
              values[2],
              values[3],
              Double.parseDouble(values[4])
          );
          transactions.add(t);
        }
      }
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
    return transactions;
  }

  /**
   * 準備存在的和不存在的搜尋 KEY
   */
  private static void prepareSearchKeys(List<Transaction> data, List<String> existing, List<String> nonExisting) {
    Random rand = new Random();
    Set<String> existingKeySet = new HashSet<>();
    for(Transaction t : data) {
      existingKeySet.add(t.key());
    }

    // 隨機挑選 10 個存在的 KEY
    for (int i = 0; i < NUM_SEARCHES; i++) {
      int randomIndex = rand.nextInt(data.size());
      existing.add(data.get(randomIndex).key());
    }

    // 隨機產生 10 個不存在的 KEY
    while (nonExisting.size() < NUM_SEARCHES) {
      String randomKey = "TX-" + generateRandomAlphanumeric(10);
      if (!existingKeySet.contains(randomKey)) {
        nonExisting.add(randomKey);
      }
    }
  }

  // --- 搜尋演算法實作 ---

  public static boolean linearSearch(List<Transaction> list, String key) {
    for (Transaction t : list) {
      if (t.key().equals(key)) {
        return true;
      }
    }
    return false;
  }

  public static boolean binarySearch(List<Transaction> sortedList, String key) {
    int index = Collections.binarySearch(sortedList, new Transaction(key, null, null, null, 0.0));
    return index >= 0;
  }

  public static boolean hashSearch(Map<String, Transaction> map, String key) {
    return map.containsKey(key);
  }

  private static String generateRandomAlphanumeric(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder(length);
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }
}
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateTransactionData {

  public static void main(String[] args) {
    String csvFile = "transactions.csv";
    int numberOfRecords = 1_000_000;

    // 更新為大宗原物料期貨交易標的
    String[] itemNames = {
        // 能源 (Energy)
        "WTI原油期貨 (Crude Oil WTI)",
        "布蘭特原油期貨 (Brent Crude)",
        "天然氣期貨 (Natural Gas)",
        "熱燃油期貨 (Heating Oil)",

        // 貴金屬 (Precious Metals)
        "黃金期貨 (Gold)",
        "白銀期貨 (Silver)",
        "鉑金期貨 (Platinum)",
        "鈀金期貨 (Palladium)",

        // 工業金屬 (Industrial Metals)
        "銅期貨 (Copper)",
        "鋁期貨 (Aluminum)",
        "鋅期貨 (Zinc)",
        "鎳期貨 (Nickel)",
        "鉛期貨 (Lead)",
        "鐵礦石期貨 (Iron Ore)",

        // 農產品 (Agricultural)
        "玉米期貨 (Corn)",
        "小麥期貨 (Wheat)",
        "黃豆期貨 (Soybeans)",
        "黃豆油期貨 (Soybean Oil)",
        "黃豆粉期貨 (Soybean Meal)",
        "燕麥期貨 (Oats)",
        "棉花期貨 (Cotton)",
        "糖期貨 (Sugar)",
        "咖啡期貨 (Coffee)",
        "可可期貨 (Cocoa)",
        "活牛期貨 (Live Cattle)",
        "瘦肉豬期貨 (Lean Hogs)"
    };

    try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
      // 寫入 CSV 標頭
      writer.println("交易代碼KEY,交易日期,客戶代碼,物品名稱,價格");

      Random random = new Random();
      DecimalFormat df = new DecimalFormat("0.00");
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      for (int i = 0; i < numberOfRecords; i++) {
        String transactionKey = "TX-" + generateRandomAlphanumeric(10);
        LocalDate randomDate = generateRandomDate(LocalDate.of(2023, 1, 1), LocalDate.of(2025, 12, 31));
        String customerId = "CUST-" + generateRandomNumeric(8);
        String itemName = itemNames[random.nextInt(itemNames.length)];
        double price = 10.0 + (5000.0 - 10.0) * random.nextDouble();

        writer.println(
            transactionKey + "," +
                dateFormatter.format(randomDate) + "," +
                customerId + "," +
                itemName + "," +
                df.format(price)
        );
      }
      System.out.println("成功產生 " + numberOfRecords + " 筆交易資料到 " + csvFile);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 產生指定長度的隨機英數字串
   * @param length 字串長度
   * @return 隨機字串
   */
  private static String generateRandomAlphanumeric(int length) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder(length);
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  /**
   * 產生指定長度的隨機數字字串
   * @param length 字串長度
   * @return 隨機數字字串
   */
  private static String generateRandomNumeric(int length) {
    String chars = "0123456789";
    StringBuilder sb = new StringBuilder(length);
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  /**
   * 在指定的日期範圍內產生一個隨機日期
   * @param startInclusive 起始日期 (包含)
   * @param endExclusive 結束日期 (不包含)
   * @return 隨機日期
   */
  private static LocalDate generateRandomDate(LocalDate startInclusive, LocalDate endExclusive) {
    long minDay = startInclusive.toEpochDay();
    long maxDay = endExclusive.toEpochDay();
    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
    return LocalDate.ofEpochDay(randomDay);
  }
}
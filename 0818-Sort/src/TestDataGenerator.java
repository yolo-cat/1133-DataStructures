import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 測試資料生成器
 * 自動產生400個交易日期、1800檔股票的資料檔
 */
public class TestDataGenerator {
    private static final int STOCK_COUNT = 1800;     // 股票數量
    private static final int TRADING_DAYS = 400;     // 交易日期數量
    private static final Random random = new Random();

    /**
     * 生成測試資料並寫入指定路徑
     * @param filePath 檔案路徑
     * @return 是否成功生成
     */
    public static boolean generateTestData(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // 寫入標題行
            writer.write("stockCode,tradeDate,volume,amount\n");

            // 生成起始日期（從2023-01-01開始）
            LocalDate startDate = LocalDate.of(2023, 1, 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 生成交易日期（排除週末）
            LocalDate[] tradingDates = generateTradingDates(startDate, TRADING_DAYS);

            // 為每個交易日期生成所有股票的資料
            for (LocalDate date : tradingDates) {
                for (int stockNum = 1; stockNum <= STOCK_COUNT; stockNum++) {
                    String stockCode = String.format("%04d", stockNum);
                    String dateStr = date.format(formatter);
                    long volume = generateRandomVolume();
                    long amount = generateRandomAmount();

                    writer.write(String.format("%s,%s,%d,%d\n",
                        stockCode, dateStr, volume, amount));
                }
            }

            System.out.println("✅ 測試資料生成完成：" + filePath);
            System.out.println("📊 資料統計：");
            System.out.println("   - 股票數量：" + STOCK_COUNT + " 檔");
            System.out.println("   - 交易日期：" + TRADING_DAYS + " 天");
            System.out.println("   - 總記錄數：" + (STOCK_COUNT * TRADING_DAYS) + " 筆");

            return true;

        } catch (IOException e) {
            System.err.println("❌ 資料生成失敗：" + e.getMessage());
            return false;
        }
    }

    /**
     * 生成交易日期（排除週末）
     */
    private static LocalDate[] generateTradingDates(LocalDate startDate, int count) {
        LocalDate[] dates = new LocalDate[count];
        LocalDate current = startDate;
        int index = 0;

        while (index < count) {
            // 排除週六(6)和週日(7)
            if (current.getDayOfWeek().getValue() <= 5) {
                dates[index] = current;
                index++;
            }
            current = current.plusDays(1);
        }

        return dates;
    }

    /**
     * 生成隨機成交量（10,000 ~ 2,000,000）
     */
    private static long generateRandomVolume() {
        return 10000 + random.nextLong(1990000);
    }

    /**
     * 生成隨機成交金額（1,000,000 ~ 100,000,000）
     */
    private static long generateRandomAmount() {
        return 1000000 + random.nextLong(99000000);
    }

    /**
     * 測試用主方法
     */
    public static void main(String[] args) {
        generateTestData("src/testdata.csv");
    }
}

package model;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

public class StockRecord implements Comparable<StockRecord> {
    private String stockCode;          // 股票代碼 (6位數字)
    private String stockName;          // 股票名稱 (2-6個中文字)
    private LocalDate tradeDate;       // 交易日期
    private long volume;               // 成交量
    private double amount;             // 成交金額
    private long maxSingleVolume;      // 當日單筆最大成交量
    private double maxSingleAmount;    // 當日單筆最大成交金額
    private long minSingleVolume;      // 當日單筆最小成交量
    private double minSingleAmount;    // 當日單筆最小成交金額

    // Getters and Setters
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getMaxSingleVolume() {
        return maxSingleVolume;
    }

    public void setMaxSingleVolume(long maxSingleVolume) {
        this.maxSingleVolume = maxSingleVolume;
    }

    public double getMaxSingleAmount() {
        return maxSingleAmount;
    }

    public void setMaxSingleAmount(double maxSingleAmount) {
        this.maxSingleAmount = maxSingleAmount;
    }

    public long getMinSingleVolume() {
        return minSingleVolume;
    }

    public void setMinSingleVolume(long minSingleVolume) {
        this.minSingleVolume = minSingleVolume;
    }

    public double getMinSingleAmount() {
        return minSingleAmount;
    }

    public void setMinSingleAmount(double minSingleAmount) {
        this.minSingleAmount = minSingleAmount;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String toString() {
        return String.join(",",
                stockCode,
                stockName,
                tradeDate.format(DATE_FORMATTER),
                String.valueOf(volume),
                String.valueOf(amount),
                String.valueOf(maxSingleVolume),
                String.valueOf(maxSingleAmount),
                String.valueOf(minSingleVolume),
                String.valueOf(minSingleAmount)
        );
    }

    public static StockRecord fromString(String csvLine) {
        String[] fields = csvLine.split(",");
        if (fields.length != 9) {
            throw new IllegalArgumentException("CSV line does not have 9 fields");
        }

        StockRecord record = new StockRecord();
        record.setStockCode(fields[0]);
        record.setStockName(fields[1]);
        record.setTradeDate(LocalDate.parse(fields[2], DATE_FORMATTER));
        record.setVolume(Long.parseLong(fields[3]));
        record.setAmount(Double.parseDouble(fields[4]));
        record.setMaxSingleVolume(Long.parseLong(fields[5]));
        record.setMaxSingleAmount(Double.parseDouble(fields[6]));
        record.setMinSingleVolume(Long.parseLong(fields[7]));
        record.setMinSingleAmount(Double.parseDouble(fields[8]));
        
        record.validate();
        return record;
    }

    public void validate() {
        if (stockCode == null || !stockCode.matches("\\d{6}")) {
            throw new IllegalArgumentException("Invalid stock code: " + stockCode);
        }
        if (stockName == null || stockName.length() < 2 || stockName.length() > 6) {
            throw new IllegalArgumentException("Invalid stock name: " + stockName);
        }
        if (maxSingleVolume > volume) {
            throw new IllegalArgumentException("Max single volume cannot be greater than total volume.");
        }
        if (maxSingleAmount > amount) {
            throw new IllegalArgumentException("Max single amount cannot be greater than total amount.");
        }
        if (minSingleVolume < 0 || minSingleVolume > maxSingleVolume) {
            throw new IllegalArgumentException("Invalid min single volume.");
        }
        if (minSingleAmount < 0 || minSingleAmount > maxSingleAmount) {
            throw new IllegalArgumentException("Invalid min single amount.");
        }
    }

    @Override
    public int compareTo(StockRecord other) {
        // Default comparison by volume
        return Long.compare(other.volume, this.volume);
    }
}

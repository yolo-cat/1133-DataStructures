public class StockRecord {
    private String stockCode;      // 股票代碼
    private String tradeDate;      // 交易日期（可用字串或Date型別）
    private long volume;           // 成交量
    private long amount;           // 成交金額

    public StockRecord(String stockCode, String tradeDate, long volume, long amount) {
        this.stockCode = stockCode;
        this.tradeDate = tradeDate;
        this.volume = volume;
        this.amount = amount;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public long getVolume() {
        return volume;
    }

    public long getAmount() {
        return amount;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return stockCode + "," + tradeDate + "," + volume + "," + amount;
    }
}


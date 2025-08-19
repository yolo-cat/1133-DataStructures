package util;

import model.StockRecord;

import java.util.Comparator;

public enum SortField {
    VOLUME("成交量", Comparator.comparingLong(StockRecord::getVolume).reversed()),
    AMOUNT("成交金額", Comparator.comparingDouble(StockRecord::getAmount).reversed());

    private final String displayName;
    private final Comparator<StockRecord> comparator;

    SortField(String displayName, Comparator<StockRecord> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Comparator<StockRecord> getComparator() {
        return comparator;
    }
}

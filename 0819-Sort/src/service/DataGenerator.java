package service;

import model.StockRecord;
import util.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.function.Consumer;

public class DataGenerator {

    private static final Random RANDOM = new Random();
    private final ChineseNameGenerator nameGenerator = new ChineseNameGenerator();

    public void generateData(int stockCount, int days, Consumer<Integer> progressConsumer) throws IOException {
        List<LocalDate> tradeDates = generateTradeDates(days);
        List<StockRecord> records = new ArrayList<>();
        int totalRecords = stockCount * days;
        int generatedCount = 0;

        for (int i = 1; i <= stockCount; i++) {
            String stockCode = String.format("%06d", i);
            String stockName = nameGenerator.generateName();
            for (LocalDate date : tradeDates) {
                records.add(generateStockRecord(stockCode, stockName, date));
                generatedCount++;
                if (generatedCount % 1000 == 0) { // Report progress every 1000 records
                    progressConsumer.accept((int) ((double) generatedCount / totalRecords * 100));
                }

                if (records.size() >= Constants.MEMORY_LIMIT) {
                    writeToFile(records, Constants.DATA_DIRECTORY + "stock_data.csv", true);
                    records.clear();
                }
            }
        }
        if (!records.isEmpty()) {
            writeToFile(records, Constants.DATA_DIRECTORY + "stock_data.csv", true);
        }
        progressConsumer.accept(100);
    }

    private List<LocalDate> generateTradeDates(int days) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        while (dates.size() < days) {
            currentDate = currentDate.minusDays(1);
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(currentDate);
            }
        }
        return dates;
    }

    private StockRecord generateStockRecord(String code, String name, LocalDate date) {
        StockRecord record = new StockRecord();
        record.setStockCode(code);
        record.setStockName(name);
        record.setTradeDate(date);

        long volume = 1000 + RANDOM.nextInt(1_000_000);
        double amount = volume * (10 + RANDOM.nextDouble() * 1000);
        long maxSingleVolume = 1 + RANDOM.nextInt((int) (volume - 1));
        double maxSingleAmount = maxSingleVolume * (amount/volume);
        long minSingleVolume = 1 + RANDOM.nextInt((int) maxSingleVolume);
        double minSingleAmount = minSingleVolume * (amount/volume);

        record.setVolume(volume);
        record.setAmount(amount);
        record.setMaxSingleVolume(maxSingleVolume);
        record.setMaxSingleAmount(maxSingleAmount);
        record.setMinSingleVolume(minSingleVolume);
        record.setMinSingleAmount(minSingleAmount);

        return record;
    }

    private void writeToFile(List<StockRecord> records, String filename, boolean append) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {
            for (StockRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
    }
}

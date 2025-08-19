package service;

import model.StockRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class FileManager {

    public static Iterator<StockRecord> readStockRecords(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return new Iterator<StockRecord>() {
            private String nextLine;

            @Override
            public boolean hasNext() {
                try {
                    nextLine = reader.readLine();
                } catch (IOException e) {
                    nextLine = null;
                    throw new RuntimeException(e);
                }
                return nextLine != null;
            }

            @Override
            public StockRecord next() {
                return StockRecord.fromString(nextLine);
            }
        };
    }
}

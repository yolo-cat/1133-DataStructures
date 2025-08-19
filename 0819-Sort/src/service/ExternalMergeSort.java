package service;

import model.StockRecord;
import util.Constants;
import util.SortField;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import java.util.function.Consumer;

public class ExternalMergeSort {

    public String sort(String inputFile, SortField sortField, Consumer<Integer> progressConsumer) throws IOException {
        new File(Constants.TEMP_DIRECTORY).mkdirs();

        // Estimate total lines for progress calculation
        long totalLines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            while (reader.readLine() != null) totalLines++;
        }

        List<String> chunkFiles = createSortedChunks(inputFile, sortField, progressConsumer, totalLines);
        String outputFile = Constants.TEMP_DIRECTORY + "sorted_" + sortField.name() + ".csv";
        mergeChunks(chunkFiles, outputFile, sortField, progressConsumer, totalLines);
        return outputFile;
    }

    private List<String> createSortedChunks(String inputFile, SortField sortField, Consumer<Integer> progressConsumer, long totalLines) throws IOException {
        List<String> chunkFiles = new ArrayList<>();
        List<StockRecord> records = new ArrayList<>(Constants.MEMORY_LIMIT);
        Iterator<StockRecord> iterator = FileManager.readStockRecords(inputFile);

        int chunkIndex = 0;
        long processedLines = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            processedLines++;
            if (processedLines % 1000 == 0) {
                progressConsumer.accept((int) ((double) processedLines / totalLines * 50)); // Chunking is 0-50%
            }

            if (records.size() == Constants.MEMORY_LIMIT) {
                chunkFiles.add(sortAndWriteChunk(records, chunkIndex++, sortField));
                records.clear();
            }
        }

        if (!records.isEmpty()) {
            chunkFiles.add(sortAndWriteChunk(records, chunkIndex, sortField));
        }
        progressConsumer.accept(50);
        return chunkFiles;
    }

    private String sortAndWriteChunk(List<StockRecord> records, int chunkIndex, SortField sortField) throws IOException {
        records.sort(sortField.getComparator());
        String chunkFile = Constants.TEMP_DIRECTORY + "chunk_" + chunkIndex + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile))) {
            for (StockRecord record : records) {
                writer.write(record.toString());
                writer.newLine();
            }
        }
        return chunkFile;
    }

    private void mergeChunks(List<String> chunkFiles, String outputFile, SortField sortField, Consumer<Integer> progressConsumer, long totalLines) throws IOException {
        PriorityQueue<ChunkLine> pq = new PriorityQueue<>((a, b) -> sortField.getComparator().compare(a.record, b.record));

        for (String chunkFile : chunkFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(chunkFile));
            String line = reader.readLine();
            if (line != null) {
                pq.add(new ChunkLine(StockRecord.fromString(line), reader));
            }
        }

        long mergedLines = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            while (!pq.isEmpty()) {
                ChunkLine chunkLine = pq.poll();
                writer.write(chunkLine.record.toString());
                writer.newLine();
                mergedLines++;
                if (mergedLines % 1000 == 0) {
                    progressConsumer.accept(50 + (int) ((double) mergedLines / totalLines * 50)); // Merging is 50-100%
                }

                String nextLine = chunkLine.reader.readLine();
                if (nextLine != null) {
                    pq.add(new ChunkLine(StockRecord.fromString(nextLine), chunkLine.reader));
                } else {
                    chunkLine.reader.close();
                }
            }
        }
        // Clean up chunk files
        for (String chunkFile : chunkFiles) {
            new File(chunkFile).delete();
        }
        progressConsumer.accept(100);
    }

    public List<StockRecord> getTopN(String sortedFile, int n) throws IOException {
        List<StockRecord> topN = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(sortedFile))) {
            String line;
            while ((line = reader.readLine()) != null && topN.size() < n) {
                topN.add(StockRecord.fromString(line));
            }
        }
        return topN;
    }

    private static class ChunkLine {
        StockRecord record;
        BufferedReader reader;

        ChunkLine(StockRecord record, BufferedReader reader) {
            this.record = record;
            this.reader = reader;
        }
    }
}

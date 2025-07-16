package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 稀疏矩陣列表類別
 */
public class SparseList {
    public int rows;
    public int cols;
    public List<Triple> data;

    public SparseList(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new ArrayList<>();
    }

    public void addTriple(int row, int col, int value) {
        if (value != 0) {
            data.add(new Triple(row, col, value));
        }
    }

    public int size() {
        return data.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("矩陣大小: %dx%d, 非零元素: %d\n", rows, cols, size()));
        for (Triple t : data) {
            sb.append(t).append(" ");
        }
        return sb.toString();
    }
}

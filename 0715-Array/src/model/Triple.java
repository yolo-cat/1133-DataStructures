package model;

import java.util.Objects;

/**
 * 三元組類別，表示稀疏矩陣中的非零元素
 */
public class Triple {
    public int row;
    public int col;
    public int value;

    public Triple(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d,%d)", row, col, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Triple triple = (Triple) obj;
        return row == triple.row && col == triple.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

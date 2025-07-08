public class SequentialSearch {
    public static Main.Position search(int[][] matrix, int size, int target) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == target) {
                    return new Main.Position(i, j);
                }
            }
        }
        return null;
    }
}


import java.util.Random;

public class MatrixGenerator {
    public static int[][] generate(int size) {
        int[][] matrix = new int[size][size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(1000); // 生成0-999的隨機數
            }
        }

        return matrix;
    }
}

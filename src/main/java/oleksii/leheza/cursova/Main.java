package oleksii.leheza.cursova;

import oleksii.leheza.cursova.alghorithm.StripedMatrixMultiplication;
import oleksii.leheza.cursova.basic.BasicMatrixMultiply;
import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.MatrixUtil;


public class Main {
    // Чи можна множити лише квадратні матриці
// 2500 / 5 == 2.88

    public static void main(String[] args) {

        int matrixSize = 6;
        int threadAmount = 3;
        MatrixUtil matrixUtil = new MatrixUtil();
        Matrix matrix = matrixUtil.initializeRandomMatrix(matrixSize);
        printMatrix(matrix.getMatrix());
        Matrix result1 = new Matrix(matrixSize);
        Matrix result2Matrix = new Matrix(matrixSize);

        BasicMatrixMultiply basicMatrixMultiply = new BasicMatrixMultiply();
        StripedMatrixMultiplication stripedMatrixMultiplication = new StripedMatrixMultiplication(matrix, matrix, threadAmount, result2Matrix);
        long startTime1 = System.currentTimeMillis();
        basicMatrixMultiply.multiplyMatrix(matrix, matrix, result1);
        long endTime1 = System.currentTimeMillis();

        System.out.println("---------------Result Matrix 1---------------");
        printMatrix(result1.getMatrix());

        long startTime2 = System.currentTimeMillis();
        stripedMatrixMultiplication.multiply();
        long endTime2 = System.currentTimeMillis();
        double resultTime1 = endTime1 - startTime1;
        double resultTime2 = endTime2 - startTime2;


        System.out.println("---------------Result Matrix 2---------------");
        printMatrix(result2Matrix.getMatrix());

        System.out.println("---------------Result---------------" +
                "\nSimple matrix multiplication: " + resultTime1 +
                "\nTape matrix multiplication: " + resultTime2 + "; speedup = " + resultTime1 / resultTime2
        );
        System.out.println(matrixUtil.isMatricesEqual(result1, result2Matrix));
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}




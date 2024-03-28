package oleksii.leheza.cursova.matrix;

import java.util.concurrent.atomic.AtomicInteger;

public class Matrix {

    private final int[][] matrix;
    private final int matrixSize;

    public Matrix(int matrixSize) {
        matrix = new int[matrixSize][matrixSize];
        this.matrixSize = matrixSize;
    }

    public Matrix(int[][] matrix) {
        this.matrix = matrix;
        this.matrixSize = matrix.length;
    }

    public int[] getRow(int rowNumber) {
        return matrix[rowNumber];
    }

    public int[] getColumn(int columnNumber) {
        int[] column = new int[matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            column[i] = matrix[i][columnNumber];
        }
        return column;
    }

    public void setValue(int row, int column, int value) {
        matrix[row][column] = value;
    }

    public void plusValue(int row, int column, int value) {
        matrix[row][column] += value;
    }

    public int getValue(int row, int column) {
        return matrix[row][column];
    }

    public int getMatrixSize() {
        return matrixSize;
    }
    public int[][] getMatrix() {
        return matrix;
    }
}

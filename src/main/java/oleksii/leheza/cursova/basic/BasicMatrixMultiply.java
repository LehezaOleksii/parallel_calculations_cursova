package oleksii.leheza.cursova.basic;


import oleksii.leheza.cursova.matrix.Matrix;

public class BasicMatrixMultiply {

    public void multiplyMatrix(Matrix firstMatrix, Matrix secondMatrix, Matrix result) {
        if (firstMatrix.getMatrixSize() == secondMatrix.getMatrixSize()) {
            int matrixLength = firstMatrix.getMatrixSize();
            for (int i = 0; i < matrixLength; i++) {
                for (int j = 0; j < matrixLength; j++) {
                    int[] column = new int[matrixLength];
                    for (int k = 0; k < matrixLength; k++) {
                        if (j - i >= 0) {
                            column[k] = secondMatrix.matrix[k][j - i];
                        } else {
                            column[k] = secondMatrix.matrix[k][matrixLength + (j - i)];
                        }
                    }
                    int[] row = firstMatrix.getRow(j);
                    int columnNumber = j - i;
                    if (columnNumber < 0) {
                        columnNumber += matrixLength;
                    }
                    int sum = 0;
                    for (int n = 0; n < matrixLength; n++) {
                        sum += row[n] * column[n];
                    }
                    result.matrix[j][columnNumber] = sum;
                }
            }
        }
    }

    public void basicMultiply(int[][] firstMatrix, int[][] secondMatrix, Matrix result) {
        int size = firstMatrix.length;
        int[][] resultMatrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                resultMatrix[i][j] = 0;
                for (int k = 0; k < size; k++) {
                    result.matrix[i][j] = firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }
    }
}

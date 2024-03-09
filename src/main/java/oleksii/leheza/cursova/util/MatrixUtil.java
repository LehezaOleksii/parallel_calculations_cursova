package oleksii.leheza.cursova.util;


import oleksii.leheza.cursova.matrix.Matrix;

import java.util.Random;

public class MatrixUtil {

    public Matrix initializeRandomMatrix(int matrixSize) {
        Random random = new Random();
        Matrix matrix = new Matrix(matrixSize);
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
//                matrix.setValue(i, j, random.nextInt() % 10);
                matrix.setValue(i, j, 1);
            }
        }
        return matrix;
    }

    public static boolean isMatricesEqual(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getMatrixSize() != matrix2.getMatrixSize()) {
            return false;
        }
        for (int i = 0; i < matrix1.getMatrixSize(); i++) {
            for (int j = 0; j < matrix1.getMatrixSize(); j++) {
                if (matrix1.getValue(i, j) != matrix2.getValue(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
}

package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class TaskDistributer implements Runnable {

    private ExecutorService executor;
    private int matrixLength;
    private Matrix firstMatrix;
    private Matrix secondMatrix;
    private int iterations;

    private int step;

    private Matrix result;

    public TaskDistributer(ExecutorService executor, int iterations, int step, Matrix firstMatrix, Matrix secondMatrix, Matrix result) {
        this.executor = executor;
        this.iterations = iterations;
        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
        this.result = result;
        this.step = step;
        matrixLength = firstMatrix.getMatrixSize();
    }

    @Override
    public void run() {
        for (int i = iterations; i < iterations + step; i++) {
            for (int j = 0; j < matrixLength; j++) {
                int[] column = new int[secondMatrix.getColumn(1).length];
                for (int k = 0; k < matrixLength; k++) {
                    if (j - i >= 0) {
                        column[k] = secondMatrix.getValue(k, j - i);
                    } else {
                        column[k] = secondMatrix.getValue(k, matrixLength + (j - i));
                    }
                }

                executor.execute(new StripedMatrixMultiplicationThread(firstMatrix.getRow(i), column, i, j, result));
//                int columnNumber = j - i;
//                if (columnNumber < 0) {
//                    columnNumber += matrixLength;
//                }
//
//                int sum = 0;
//                for (int n = 0; n < matrixLength; n++) {
//                    sum += firstMatrix.getValue(i,n) * column[n];
//                }
//                setValueToMatrix(i, columnNumber, sum);
            }
        }
    }

    private void setValueToMatrix(int rowNumber, int columnNumber, int sum) {
        result.setValue(rowNumber, columnNumber, sum);
    }
}

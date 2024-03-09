package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StripedMatrixMultiplication {

    public void multiply(Matrix firstMatrix, Matrix secondMatrix, int threadsAmount, Matrix result) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
        if (firstMatrix.getMatrixSize() == secondMatrix.getMatrixSize()) {
            int step = firstMatrix.getMatrixSize() / threadsAmount;
            ExecutorService ne = Executors.newFixedThreadPool(threadsAmount);
            for (int i = 0; i < firstMatrix.getMatrixSize(); i += step) {
                TaskDistributer taskDistributer = new TaskDistributer(ne, i, step, firstMatrix, secondMatrix, result);
                executorService.execute(taskDistributer);
            }
        }
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(120, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void multiply(int[][] firstMatrix, int[][] secondMatrix, int threadsAmount, Result result) {
//        ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
//        if (firstMatrix.length == secondMatrix.length) {
//            int matrixLength = firstMatrix.length;
//            for (int i = 0; i < matrixLength; i++) {
//                for (int j = 0; j < matrixLength; j++) {
//                    int[] column = new int[matrixLength];
//                    for (int k = 0; k < matrixLength; k++) {
//                        if (j - i >= 0) {
//                            column[k] = secondMatrix[k][j - i];
//                        } else {
//                            column[k] = secondMatrix[k][matrixLength + (j - i)];
//                        }
//                    }
//                    int columnNumber = j - i;
//                    if (columnNumber < 0) {
//                        columnNumber += matrixLength;
//                    }
//                    executorService.execute(new StripedMatrixMultiplicationThread(firstMatrix[j], column, j, columnNumber, result));
//                }
//            }
//        }
//        executorService.shutdown();
//        try {
//            if (executorService.awaitTermination(20, TimeUnit.SECONDS)) {
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}

package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

public class EndThread extends ClassicThread {

    public EndThread(Matrix result, int iteration, int matrixLength, Synchronizer synchronizer,int threadAmount) {
        super(result, iteration, matrixLength, synchronizer,threadAmount);
    }

    @Override
    public void run() {
        int[] row;
        int[] column;
        while (iteration<matrixLength) {
            for (int i = 0; i < matrixLength; i++) {
                while (rows.isEmpty()) {
                    try {
                        synchronized (lockObj) {
                            lockObj.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    if (i == matrixLength - 1) {
                        column = lastColumn;
                    } else {
                        column = columns.take();
                    }
                    row = rows.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                int sum = 0;
                for (int n = 0; n < row.length; n++) {
                    sum += row[n] * column[n];
                }

                int columnNumber = (i + iteration) % matrixLength;
                result.matrix[i][columnNumber] = sum;
            }
            synchronizer.setCycleEnd(true);
            synchronized (synchronizer) {
                synchronizer.notifyAll();
            }
            iteration+=threadAmount;
        }
    }
}
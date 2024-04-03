package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

public class EndThread extends ClassicThread {

    public Synchronizer synchronizer;

    public EndThread(Matrix result, int iteration, int matrixLength, Synchronizer synchronizer) {
        super(result, iteration, matrixLength, synchronizer);
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {

        int[] row;
        int[] column;
        while (!synchronizer.getAlgorithmEnd()) {
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
                if (nextThread != null) {
                    if (i == 0) {
                        nextThread.setValueToLastColumn(column);
                    } else {
                        nextThread.addColumnToQueue(column);
                    }
                    nextThread.addRowToQueue(row);
                    synchronized (nextThread.lockObj) {
                        nextThread.lockObj.notify();
                    }
                }

                int sum = 0;
                for (int n = 0; n < row.length; n++) {
                    sum += row[n] * column[n];
                }

                int columnNumber = (i + iteration) % matrixLength;
                result.matrix[i][columnNumber] = sum;
            }

            if (iteration >= matrixLength - 1) {
                synchronizer.setAlgorithmEnd(true);
                synchronized (synchronizer) {
                    synchronizer.notifyAll();
                }
            }
            synchronizer.setCycleEnd(true);
            synchronized (synchronizer) {
                synchronizer.notifyAll();
            }
        }
    }
}
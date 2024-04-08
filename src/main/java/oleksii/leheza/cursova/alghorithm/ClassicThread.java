package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClassicThread extends Thread {
    protected int iteration;

    protected Matrix result;

    protected BlockingQueue<int[]> columns = new LinkedBlockingQueue<>();

    protected int[] lastColumn;

    protected BlockingQueue<int[]> rows = new LinkedBlockingQueue<>();


    protected ClassicThread nextThread;

    public final Object lockObj = new Object();
    protected int matrixLength;

    protected final Synchronizer synchronizer;

    protected int threadAmount;

    public ClassicThread(Matrix result, int iteration, int matrixLength, Synchronizer synchronizer, int threadAmount) {
        this.result = result;
        this.iteration = iteration;
        this.matrixLength = matrixLength;
        this.synchronizer = synchronizer;
        this.threadAmount = threadAmount;
        lastColumn = new int[result.getMatrixSize()];
    }

    @Override
    public void run() {
        multiply();
    }

    protected void multiply() {
        int[] row;
        int[] column;
        while (iteration < matrixLength) {
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
                if (i == 0) {
                    nextThread.setValueToLastColumn(column);
                } else {
                    nextThread.addColumnToQueue(column);
                }
                nextThread.addRowToQueue(row);
                synchronized (nextThread.lockObj) {
                    nextThread.lockObj.notify();
                }

                int sum = 0;
                for (int n = 0; n < row.length; n++) {
                    sum += row[n] * column[n];
                }

                int columnNumber = (i + iteration) % matrixLength;
                result.matrix[i][columnNumber] = sum;
            }
            synchronized (synchronizer) {
                while (synchronizer.getCycleEnd()) {
                    try {
                        synchronizer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            iteration += threadAmount;
        }
    }

    public void addColumnToQueue(int[] column) {
        columns.add(column);
    }

    public void addRowToQueue(int[] row) {
        rows.add(row);
    }

    public void setValueToLastColumn(int[] column) {
        lastColumn = column;
    }

    public void setSubThread(ClassicThread classicThread) {
        this.nextThread = classicThread;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }
}
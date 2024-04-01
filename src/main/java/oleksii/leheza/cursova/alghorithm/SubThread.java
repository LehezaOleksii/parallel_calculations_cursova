package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SubThread extends Thread {
    private int iteration;

    private Matrix result;

    private BlockingQueue<int[]> columns = new LinkedBlockingQueue<>();

    private BlockingQueue<int[]> lastColumns = new LinkedBlockingQueue<>();

    private BlockingQueue<int[]> rows = new LinkedBlockingQueue<>();


    private SubThread nextThread;

    public final Object lockObj = new Object();

//    private AtomicInteger lastColumn = new AtomicInteger();
//
//    private AtomicInteger lastRow = new AtomicInteger();

    private int matrixLength;

    public SubThread(Matrix result, int iteration, int matrixLength) {
        this.result = result;
        this.iteration = iteration;
        this.matrixLength = matrixLength;
    }

    @Override
    public void run() {
        int[] row;
        int[] column;
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
                    column = lastColumns.take();
                } else {
                    column = columns.take();
                }
                row = rows.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (nextThread != null) {
                if (i == 0) {
                    nextThread.addValueToLastColumns(column);
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
            result.matrix[i][columnNumber] = sum;//last column row index
        }
//            isEndIteration = true; // need new iteration  not ++    AND we need to wait all other threads and than set new iteration
//            lastRow.set(0);
//            lastColumn.set(iteration);
    }

    public void addColumnToQueue(int[] column) {
        columns.add(column);
    }

    public void addRowToQueue(int[] row) {
        rows.add(row);
    }

//    public int getAndIncrementLastRowIndex() {
//        return lastRow.getAndIncrement();
//    }
//
//    public int incrementAndGetLastRowIndex() {
//        return lastRow.incrementAndGet();
//    }
//
//    public int incrementAndGetLastColumnIndex() {
//        return lastColumn.incrementAndGet();
//    }
//
//    public int getAndIncrementLastColumnIndex() {
//        if (lastColumn.get() >= matrixLength - 1) {
//            lastColumn.set(0);
//        } else {
//            return lastColumn.getAndIncrement();
//        }
//        return lastColumn.get();
//    }

//    public int getLastRowIndex() {
//        return lastRow.get();
//    }

    public void addValueToLastColumns(int[] column) {
        lastColumns.add(column);
    }

    public void setSubThread(SubThread subThread) {
        this.nextThread = subThread;
    }
}
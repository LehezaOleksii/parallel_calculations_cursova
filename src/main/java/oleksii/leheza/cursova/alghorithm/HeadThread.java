package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

import java.util.concurrent.atomic.AtomicInteger;

public class HeadThread extends ClassicThread {

    private final Object lock;

    private boolean isNeedNewData;

    public AtomicInteger lastNeedColumnIndex = new AtomicInteger();

    public AtomicInteger lastNeedRowIndex = new AtomicInteger();


    public HeadThread(Matrix result, int iteration, int matrixLength, Object lock, Synchronizer synchronizer,int threadAmount) {
        super(result, iteration, matrixLength, synchronizer,threadAmount);
        this.result = result;
        this.iteration = iteration;
        this.lock = lock;
        this.matrixLength = matrixLength;
    }

    @Override
    public void run() {
        int[] row;
        int[] column;
        while (iteration<matrixLength) {
            lastNeedRowIndex.set(0);
            lastNeedColumnIndex.set(iteration);
            for (int i = 0; i < matrixLength; i++) {
                while (rows.isEmpty() && columns.isEmpty()) {
                    isNeedNewData = true;
                    try {
                        synchronized (lock) {
                            lock.notify();
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    column = columns.take();
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
            while (synchronizer.getCycleEnd()) {
                try {
                    synchronized (synchronizer) {
                        synchronizer.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            iteration+=threadAmount;
        }
        System.out.println("1");
    }

    public void addColumnToQueue(int[] column) {
        columns.add(column);
    }

    public void addRowToQueue(int[] row) {
        rows.add(row);
    }

    public void setIsNeedNewData(boolean isNeedNewRow) {
        this.isNeedNewData = isNeedNewRow;
    }

    public boolean getIsNeedNewData() {
        return isNeedNewData;
    }

    public void incrementLastColumnIndex() {
        if (lastNeedColumnIndex.get() >= matrixLength - 1) {
            lastNeedColumnIndex.set(0);
        } else {
            lastNeedColumnIndex.getAndIncrement();
        }
    }

    public void incrementLastRowIndex() {
        lastNeedRowIndex.incrementAndGet();
    }

    public int getLastRowIndex() {
        return lastNeedRowIndex.get();
    }
}
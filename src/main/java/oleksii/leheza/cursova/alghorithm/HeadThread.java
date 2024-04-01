package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class HeadThread extends Thread {
    private int iteration;

    private Matrix result;

    private BlockingQueue<int[]> columns = new LinkedBlockingQueue<>();

    private BlockingQueue<int[]> rows = new LinkedBlockingQueue<>();


    private SubThread nextThread;

    private final Object lock;

    private boolean isNeedNewData;
    private boolean isEndIteration;

    private AtomicInteger lastNeedColumnIndex = new AtomicInteger();

    private AtomicInteger lastNeewRowIndex = new AtomicInteger();

    private int matrixLength;

    private int threadAmount;

    private Synchronizer synchronizer;


    public HeadThread(Matrix result, int iteration, int matrixLength, Object lock) {
        this.result = result;
        this.iteration = iteration;
        this.lock = lock;
        this.matrixLength = matrixLength;
    }

    @Override
    public void run() {
        int[] row;
        int[] column;
        lastNeewRowIndex.set(0);
        lastNeedColumnIndex.set(iteration);
        for (int i = 0; i < matrixLength; i++) {
            while (rows.isEmpty() && columns.isEmpty()) {
                isNeedNewData = true;
                try {
                    synchronized (lock) {
                        lock.notifyAll();
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
                    nextThread.addValueToLastColumns(column);
                } else {
                    nextThread.addColumnToQueue(column);
                }
                nextThread.addRowToQueue(row);
                synchronized (nextThread.lockObj) {
                    nextThread.lockObj.notifyAll();//previous thread obj
                }
            }
            int sum = 0;
            for (int n = 0; n < row.length; n++) {
                sum += row[n] * column[n];
            }

            int columnNumber = (i + iteration) % matrixLength;
            result.matrix[i][columnNumber] = sum;//last column row index
        }
        synchronized (lock) {
            lock.notifyAll();
        }
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
        lastNeewRowIndex.incrementAndGet();
    }

    public int getLastRowIndex() {
        return lastNeewRowIndex.get();
    }

    public int getLastColumnIndex() {
        return lastNeedColumnIndex.get();
    }

    public void setSubThread(SubThread subThread) {
        this.nextThread = subThread;
    }
}
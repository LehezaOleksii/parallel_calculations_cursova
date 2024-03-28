package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessMultiply extends Thread {
    private int iteration;

    private Matrix result;

    private BlockingQueue<int[]> columns;

    private BlockingQueue<int[]> rows;


    private ProcessMultiply previousThread;

    private final Object lock;

    private boolean isNeedNewData;
    private boolean isEndIteration;

    private AtomicInteger lastColumn = new AtomicInteger();

    private AtomicInteger lastRow = new AtomicInteger();

    private int matrixLength;

    private int threadAmount;

    private Synchronizer synchronizer;

    private boolean isThreadEnd;

    public ProcessMultiply(BlockingQueue<int[]> rows, BlockingQueue<int[]> columns, ProcessMultiply previousThread, Matrix result, int iteration, int matrixLength, Object lock, int threadAmount, Synchronizer synchronizer) {
        this.rows = rows;
        this.columns = columns;
        this.previousThread = previousThread;
        this.result = result;
        this.iteration = iteration;
        this.synchronizer = synchronizer;
        this.lock = lock;
        this.matrixLength = matrixLength;
        this.threadAmount = threadAmount;
        lastRow.set(rows.size());
        lastColumn.set(columns.size());
    }

    @Override
    public void run() {
        int[] row;
        int[] column;
        while (!isThreadEnd) {
            for (int i = 0; i < matrixLength; i++) {
                while (rows.isEmpty() || columns.isEmpty()) {
                    isNeedNewData = true;
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    try {
                        synchronized (lock) {
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

                if (previousThread != null) {

                        previousThread.addColumnToQueue(column);//mistake we need to make сдвижение

                    previousThread.addRowToQueue(row); // maybe there is need to create previous column variable
                    // maybe set new iteration (and create static iteration or sync iteration)
                }
                synchronized (lock) {
                    lock.notifyAll();
                }

                int sum = 0;
                for (int n = 0; n < row.length; n++) {
                    sum += row[n] * column[n];
                }

                int columnNumber = (i + iteration)%matrixLength;
//                if (columnNumber > matrixLength - 1) {
//                    columnNumber -= matrixLength;
//                }
                result.setValue(i, columnNumber, sum);//last column row index
            }
//            isEndIteration = true; // need new iteration  not ++    AND we need to wait all other threads and than set new iteration
            iteration += threadAmount;


            if (iteration >= matrixLength) {
                if (iteration == matrixLength + threadAmount - 1) {
                    synchronizer.setIsEnd(true);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
                isThreadEnd = true;
            }


            lastRow.set(0);
            lastColumn.set(iteration);
        }
    }

    private void setValueToMatrix(int rowNumber, int columnNumber, int sum) {
        result.setValue(rowNumber, columnNumber, sum);
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

    public int getAndIncrementLastRowIndex() {
        return lastRow.getAndIncrement();
    }

    public int incrementAndGetLastRowIndex() {
        return lastRow.incrementAndGet();
    }

    public int incrementAndGetLastColumnIndex() {
        return lastColumn.incrementAndGet();
    }

    public int getAndIncrementLastColumnIndex() {
        if (lastColumn.get() >= matrixLength - 1) {
            lastColumn.set(0);
        } else {
            return lastColumn.getAndIncrement();
        }
        return lastColumn.get();
    }

    public int getLastRowIndex() {
        return lastRow.get();
    }

    public int getLastColumnIndex() {
        return lastColumn.get();
    }


    public void setPreviousThread(ProcessMultiply previousThread) {
        this.previousThread = previousThread;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public void setIsEndIteration(boolean isEndIteration) {
        this.isEndIteration = isEndIteration;
    }

    public boolean getIsEndIteration() {
        return isEndIteration;
    }
}

//            for (int j = startColumnNumber; j < result.getMatrixSize(); j++) {
//                while (column) {
//                    try {
//                        wait();
//                    } catch (InterruptedException e) {
//                        System.err.print(e.getMessage());
//                    }
//                }


//                int[] column = new int[secondMatrix.getColumn(1).length];
//                for (int k = 0; k < matrixLength; k++) {
//                    if (j - i >= 0) {
//                        column[k] = secondMatrix.getValue(k, j - i);
//                    } else {
//                        column[k] = secondMatrix.getValue(k, matrixLength + (j - i));
//                    }
//                }

//                executor.execute(new StripedMatrixMultiplicationThread(firstMatrix.getRow(i), column, i, j, result));
//                int columnNumber = j - i;
//                if (columnNumber < 0) {
//                    columnNumber += matrixLength;
//                }

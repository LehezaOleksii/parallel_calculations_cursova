package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

import java.util.LinkedList;
import java.util.List;

public class StripedMatrixMultiplication {

    private Matrix firstMatrix;
    private Matrix secondMatrix;
    private int threadsAmount;
    private Matrix result;
    private final Object lock = new Object();

    private Synchronizer synchronizer;


    public StripedMatrixMultiplication(Matrix firstMatrix, Matrix secondMatrix, int threadsAmount, Matrix result) {
        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
        this.threadsAmount = threadsAmount;
        this.result = result;
    }

    public void multiply() {
        if (firstMatrix.getRow(0).length != secondMatrix.getColumn(0).length) {
            throw new RuntimeException("Different length");
        }

        //prepare thread
        synchronizer = new Synchronizer();
        HeadThread headThread = new HeadThread(result, 0, firstMatrix.getMatrixSize(), lock, synchronizer);
        List<ClassicThread> threads = new LinkedList<>();
        threads.add(headThread);
        ClassicThread lastClassicThread = null;
        //Create matrix threads
        int iteration = 1;
        for (; iteration < threadsAmount - 1; iteration++) {
            if (lastClassicThread == null) {
                lastClassicThread = new ClassicThread(result, iteration, firstMatrix.getMatrixSize(), synchronizer);
                headThread.setSubThread(lastClassicThread);
            } else {
                ClassicThread classicThread = new ClassicThread(result, iteration, firstMatrix.getMatrixSize(), synchronizer);
                lastClassicThread.setSubThread(classicThread);
                lastClassicThread = classicThread;
            }
            threads.add(lastClassicThread);
        }
        EndThread endThread = new EndThread(result, threadsAmount - 1, firstMatrix.getMatrixSize(), synchronizer);
        lastClassicThread.setSubThread(endThread);
        threads.add(endThread);
        //start threads
        for (Thread process : threads) {
            process.start();
        }
        //maintain threads
        for (int i = 0; i < firstMatrix.getMatrix().length; ) {
            for (int n = 0; n < firstMatrix.getMatrixSize(); ) {
                synchronized (lock) {
                    while (!headThread.getIsNeedNewData()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                int addColumnAndRowAmount = 5;
                if (headThread.getLastRowIndex() + addColumnAndRowAmount < firstMatrix.getMatrixSize() - 1) {
                    for (int k = 0; k < addColumnAndRowAmount; k++) {
                        setValueAndIncrementIndexes(headThread);
                        n++;
                    }
                } else {
                    setValueAndIncrementIndexes(headThread);
                    n++;
                }
                headThread.setIsNeedNewData(false);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            synchronized (synchronizer) {
                while (!synchronizer.getCycleEnd()) {
                    try {
                        synchronizer.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            i += threadsAmount;
            iteration = i;
            for (ClassicThread thread : threads) {
                thread.setIteration(iteration++);
            }
            headThread.lastNeedRowIndex.set(0);
            headThread.lastNeedColumnIndex.set(i);
            synchronizer.setCycleEnd(false);
            synchronized (synchronizer) {
                synchronizer.notifyAll();
            }
        }
        while (!synchronizer.getAlgorithmEnd()) {
            synchronized (synchronizer) {
                try {
                    synchronizer.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setValueAndIncrementIndexes(HeadThread headThread) {
        headThread.addRowToQueue(firstMatrix.getRow(headThread.lastNeedRowIndex.get()));
        headThread.addColumnToQueue(secondMatrix.getColumn(headThread.lastNeedColumnIndex.get()));
        headThread.incrementLastColumnIndex();
        headThread.incrementLastRowIndex();
    }
}

package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;

import java.util.LinkedList;
import java.util.List;

public class StripedMatrixMultiplication {

    private Matrix firstMatrix;
    private Matrix secondMatrix;
    private int threadsAmount;
    private Matrix result;
    private final Object lock = new Object();


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
        List<SubThread> subThreads = null;
        for (int i = 0; i < firstMatrix.getMatrix().length; i += threadsAmount) {
            HeadThread headThread = new HeadThread(result, i, firstMatrix.getMatrixSize(), lock);
            subThreads = new LinkedList<>();
            SubThread lastSubThread = null;
            //Create matrix threads
            for (int iteration = i + 1; iteration < i + threadsAmount; iteration++) {
                if (lastSubThread == null) {
                    lastSubThread = new SubThread(result, iteration, firstMatrix.getMatrixSize());
                    headThread.setSubThread(lastSubThread);
                } else {
                    SubThread subThread = new SubThread(result, iteration, firstMatrix.getMatrixSize());
                    lastSubThread.setSubThread(subThread);
                    lastSubThread = subThread;
//                        process = new SubThread(queueRow, queueColumn, subThreads.get((iteration % threadsAmount) - 1), result, iteration, firstMatrix.getMatrixSize(), lock, threadsAmount);
                    //firstMatrix.getRow(0), secondMatrix.getColumn(i * threadsAmount)
                }
                subThreads.add(lastSubThread);
            }
            //start threads
            headThread.start();
            for (Thread process : subThreads) {
                process.start();
            }

            //maintain threads
            for (int n = 0; n < firstMatrix.getMatrixSize(); ) {
                synchronized (lock) {
                    while (!headThread.getIsNeedNewData()) {//////////////////////////
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                int addColumnAndRowAmount = 3;
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
        }
        //finish
        for (Thread process : subThreads) {
            try {
                process.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setValueAndIncrementIndexes(HeadThread headThread) {
        headThread.addRowToQueue(firstMatrix.getRow(headThread.lastNeewRowIndex.get()));
        headThread.addColumnToQueue(secondMatrix.getColumn(headThread.lastNeedColumnIndex.get()));
        headThread.incrementLastColumnIndex();
        headThread.incrementLastRowIndex();
    }
}

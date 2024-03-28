package oleksii.leheza.cursova.alghorithm;

import oleksii.leheza.cursova.matrix.Matrix;
import oleksii.leheza.cursova.util.Synchronizer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        //prepare thread
        //start Threads
        //maintain threads
        //finish
        multiply1();
    }

    public void multiply1() {
        if (firstMatrix.getRow(0).length != secondMatrix.getColumn(0).length) {
            throw new RuntimeException("Different length");
        }
//        AtomicInteger lastRow = new AtomicInteger(0); // i
//        AtomicInteger lastColumn = new AtomicInteger(0); // j

        //prepare thread
        List<ProcessMultiply> processes = new LinkedList<>();
        Synchronizer synchronizer = new Synchronizer();
        for (int iteration = 0; iteration < threadsAmount; iteration++) {
            ProcessMultiply process;
            BlockingQueue<int[]> queueColumn = new LinkedBlockingQueue<>(); //need sync ?
            BlockingQueue<int[]> queueRow = new LinkedBlockingQueue<>(); //need sync ?
            try {
                queueRow.put(firstMatrix.getRow(0));
                queueColumn.put(secondMatrix.getColumn(iteration));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (processes.isEmpty()) {
                process = new ProcessMultiply(queueRow, queueColumn, null, result, iteration, firstMatrix.getMatrixSize(), lock, threadsAmount, synchronizer);//TODO tru use sync instead of lock
            } else {
                process = new ProcessMultiply(queueRow, queueColumn, processes.get(iteration - 1), result, iteration, firstMatrix.getMatrixSize(), lock, threadsAmount, synchronizer);
            }
            processes.add(process);
        }
        //start threads
        for (Thread process : processes) {
            process.start();
        }

        //maintain threads
        ProcessMultiply lastProcess = processes.get(processes.size() - 1);
        ProcessMultiply firstProcess = processes.get(0);

        while (!synchronizer.getIsEnd()) {
            while (!lastProcess.getIsNeedNewData() && !synchronizer.getIsEnd()) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            int addColumnAndRowNumber = 3;
            if (lastProcess.getLastRowIndex() + addColumnAndRowNumber < firstMatrix.getMatrixSize() - 1) {
                for (int k = 0; k < addColumnAndRowNumber; k++) {
                    lastProcess.addRowToQueue(firstMatrix.getRow(lastProcess.getAndIncrementLastRowIndex())); //last column / row
                    lastProcess.addColumnToQueue(secondMatrix.getColumn(lastProcess.getAndIncrementLastColumnIndex())); //last column / row
                }
            } else {
                lastProcess.addRowToQueue(firstMatrix.getRow(lastProcess.getAndIncrementLastRowIndex())); //last column / row
                lastProcess.addColumnToQueue(secondMatrix.getColumn(lastProcess.getAndIncrementLastColumnIndex())); //last column / row
            }
            lastProcess.setIsNeedNewData(false);
            synchronized (lock) {
                lock.notifyAll();
            }
//            lastProcess.setIteration(lastProcess.getLastColumnIndex());
        }
        //finish
        for (Thread process : processes) {
            try {
                process.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

//            Synchronizer sync = new Synchronizer();
//
//        AtomicInteger lastColumn = new AtomicInteger(0);
//        AtomicInteger lastRow = new AtomicInteger(0);
//            List<ProcessMultiply> processes = new ArrayList<>();
//        List<Thread> threads = new ArrayList<>();
//        for (int iteration = 0; iteration < firstMatrix.getMatrixSize(); iteration++) {
//            for (int n = 0; n < secondMatrix.getMatrixSize(); n++) {
//
//                BlockingQueue<int[]> queue = new LinkedBlockingQueue<>(); //need sync ?
//                lastColumn.set(iteration);
//                try {
//                    queue.put(secondMatrix.getColumn(lastColumn.get()));
//                } catch (InterruptedException e) {
//                    System.err.println(e.getMessage());
//                }
//                ProcessMultiply process;
//                if (processes.isEmpty()) {
//                    process = new ProcessMultiply(firstMatrix.getRow(iteration), queue, null, result, lastColumn.get(), sync, lock);
//                } else {
//                    process = new ProcessMultiply(firstMatrix.getRow(iteration), queue, processes.get(iteration - 1), result, lastColumn.get(), sync, lock);
//                }
//                threads.add(process);
//                processes.add(process);
//                lastRow.incrementAndGet();
//                lastColumn.incrementAndGet();
//
//                for (Thread thread : threads) {
//                    executorService.execute(thread);
//                }
//
//                for (int j = lastColumn.get(); j < firstMatrix.getMatrixSize(); j++) {
//                    processes.get(iteration).addColumnToQueue(secondMatrix.getColumn(lastColumn.getAndIncrement()));//?
//                    synchronized (lock) {
//                        lock.notifyAll();
//                    }
//                }
////            for (int j2 = secondMatrix.getMatrixSize() - lastColumn.get(); j2 < secondMatrix.getMatrixSize(); j2++) {
////                processes.get(n).addColumnToQueue(secondMatrix.getColumn(lastColumn.getAndIncrement()));//problem
////            }
//                synchronized (lock) {
//                    lock.notifyAll();
//                }
//                lastRow.incrementAndGet();
//
//            }
//        }
//        executorService.shutdown();


//        try {
//            if (executorService.awaitTermination(120, TimeUnit.SECONDS)) {
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//    public void multiply(Matrix firstMatrix, Matrix secondMatrix, int threadsAmount, Matrix result) {
//        ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
//        if (firstMatrix.getMatrixSize() == secondMatrix.getMatrixSize()) {
//            int step = firstMatrix.getMatrixSize() / threadsAmount;
//            ExecutorService ne = Executors.newFixedThreadPool(threadsAmount);
//            for (int i = 0; i < firstMatrix.getMatrixSize(); i += step) {
//                TaskDistributer taskDistributer = new TaskDistributer(ne, i, step, firstMatrix, secondMatrix, result);
//                executorService.execute(taskDistributer);
//            }
//        }
//        executorService.shutdown();
//        try {
//            if (executorService.awaitTermination(120, TimeUnit.SECONDS)) {
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

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


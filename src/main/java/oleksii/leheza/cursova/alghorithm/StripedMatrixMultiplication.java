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
        //start Threads
        //maintain threads
        //finish
        multiply1();
    }

    public void multiply1() {
        //prepare thread
        List<SubThread> subThreads = null;
        for (int i = 0; i < firstMatrix.getMatrix().length; i += threadsAmount) {
            HeadThread headThread = new HeadThread(result, i, firstMatrix.getMatrixSize(), lock);
            subThreads = new LinkedList<>();
            SubThread lastSubThread = null;
            //Create matrix threads
            for (int iteration = i+1; iteration < i + threadsAmount; iteration++) {
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
                while (!headThread.getIsNeedNewData()) {
                    synchronized (lock) {
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
                if (headThread.getIsEndIteration()) {
                    break;
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
        headThread.addRowToQueue(firstMatrix.getRow(headThread.getLastRowIndex()));
        headThread.addColumnToQueue(secondMatrix.getColumn(headThread.getLastColumnIndex()));
        headThread.incrementLastColumnIndex();
        headThread.incrementLastRowIndex();
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


package oleksii.leheza.cursova.alghorithm;


import oleksii.leheza.cursova.matrix.Matrix;

public class StripedMatrixMultiplicationThread implements Runnable {

    private int iteration;
    private int j;
    private int[] row;
    private int[] column;
    private Matrix result;


    public StripedMatrixMultiplicationThread(int[] row, int[] column, int iteration, int j, Matrix result) {
        this.result = result;
        this.iteration = iteration;
        this.row = row;
        this.column = column;
        this.j = j;
    }

    @Override
    public void run() {
//        for (int j = 0; j < matrixLength; j++) {
//            int[] column = new int[matrixLength];
//            for (int k = 0; k < matrixLength; k++) {
//                if (j - iteration >= 0) {
//                    column[k] = secondMatrix.getValue(k,j - iteration);
//                } else {
//                    column[k] = secondMatrix.getValue(k,matrixLength + (j - iteration));
//                }
//        }
        int columnNumber = j - iteration;
        if (columnNumber < 0) {
            columnNumber += column.length;
        }

        int sum = 0;
        for (int n = 0; n < row.length; n++) {
            sum += row[n] * column[n];
        }
        setValueToMatrix(iteration, columnNumber, sum);
    }

    private void setValueToMatrix(int rowNumber, int columnNumber, int sum) {
        result.setValue(rowNumber, columnNumber, sum);
    }
}

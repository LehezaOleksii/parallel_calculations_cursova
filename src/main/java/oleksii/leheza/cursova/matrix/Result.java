package oleksii.leheza.cursova.matrix;

public class Result {

    private Matrix matrix;
    private boolean isComplete;

    public Result(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public boolean isComplete() {
        return isComplete;
    }
}

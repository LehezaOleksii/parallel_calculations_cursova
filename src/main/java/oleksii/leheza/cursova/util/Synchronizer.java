package oleksii.leheza.cursova.util;

public class Synchronizer {

    private boolean isCycleEnd;

    private boolean isAlgorithmEnd;

    public void setCycleEnd(boolean cycleEnd) {
        isCycleEnd = cycleEnd;
    }

    public boolean getCycleEnd() {
        return isCycleEnd;
    }

    public void setAlgorithmEnd(boolean isAlgorithmEnd) {
        this.isAlgorithmEnd = isAlgorithmEnd;
    }

    public boolean getAlgorithmEnd() {
        return isAlgorithmEnd;
    }
}

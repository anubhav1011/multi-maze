package hwMaze;

import java.util.List;

public class Result {

    private List<Direction> direction;
    private int totalCount;
    private boolean solutionFound;

    public Result(List<Direction> direction, int counter, boolean solutionFound) {
        this.direction = direction;
        this.totalCount = counter;
        this.solutionFound = solutionFound;
    }

    public List<Direction> getDirection() {
        return direction;
    }

    public void setDirection(List<Direction> direction) {
        this.direction = direction;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }
}

package hwMaze;

import java.util.List;

public class Result {

    private List<Direction> direction;
    private int totalCount;

    public Result(List<Direction> direction, int counter) {
        this.direction = direction;
        this.totalCount = counter;
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
}

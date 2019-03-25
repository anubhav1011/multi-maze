package hwMaze;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentSTMazeSolver extends SkippingMazeSolver {

    private final ExecutorService exec;

    public StudentSTMazeSolver(Maze maze) {
        super(maze);
        int numberofProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of processors available " + numberofProcessors);
        this.exec = Executors.newFixedThreadPool(numberofProcessors);

    }

    @Override
    public List<Direction> solve() {
        return null;
    }


    class ParallelTask implements Callable<Result> {


        Choice startingPoint;

        public ParallelTask(Choice startingPoint) {
            this.startingPoint = startingPoint;
        }

        @Override
        public Result call() {
            LinkedList<Choice> choiceStack = new LinkedList<Choice>();
            Choice ch;
            choiceStack.push(this.startingPoint);
            try {
                while (!choiceStack.isEmpty()) {
                    ch = choiceStack.peek();
                    if (ch.isDeadend()) {
                        choiceStack.pop();
                        if (!choiceStack.isEmpty()) {
                            choiceStack.peek().choices.pop();
                        }
                        continue;

                    }
                    choiceStack.push(follow(ch.at, ch.choices.peek()));

                }
                return null;
            } catch (SolutionFound e) {
                Iterator<Choice> iter = choiceStack.iterator();
                LinkedList<Direction> solutionPath = new LinkedList<Direction>();
                while (iter.hasNext()) {
                    ch = iter.next();
                    solutionPath.push(ch.choices.peek());
                }
                if (maze.display != null)
                    maze.display.updateDisplay();
                Result result = new Result(pathToFullPath(solutionPath), 0);
                return result;


            }


        }
    }


}

package hwMaze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

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
        List<Future<Result>> results = new ArrayList<>();
        Position startPosition = maze.getStart();
        try {
            Choice firstChoice = firstChoice(startPosition);
            while (!firstChoice.choices.isEmpty()) {
                //Create a separate task for all possible directions;
                Direction currentDirection = firstChoice.choices.peek();
                ParallelTask task = new ParallelTask(follow(firstChoice.at, firstChoice.choices.pop()), currentDirection);
                Future<Result> taskFuture = exec.submit(task);
                results.add(taskFuture);
            }
        } catch (SolutionFound solutionFound) {
            solutionFound.printStackTrace();
        }
        try {
            for (Future<Result> resultFuture : results) {
                Result result = resultFuture.get();
                if (result != null) {
                    return result.getDirection();
                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    class ParallelTask implements Callable<Result> {


        Choice startingPoint;
        Direction from;

        public ParallelTask(Choice startingPoint, Direction from) {
            this.startingPoint = startingPoint;
            this.from = from;
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
                solutionPath.push(from);
                Iterator<Direction> iterator = solutionPath.iterator();
                while (iterator.hasNext()) {
                    System.out.print(" " + iterator.next() + " ");
                }
                if (maze.display != null) {
                    markPath(solutionPath, 1);
                    maze.display.updateDisplay();
                }
                Result result = new Result(pathToFullPath(solutionPath), 0);
                return result;
            }
        }
    }


}

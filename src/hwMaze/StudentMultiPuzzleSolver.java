package hwMaze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class StudentMultiPuzzleSolver extends SkippingMazeSolver {

    private int totalProcessors;
    private ExecutorService executor;

    public StudentMultiPuzzleSolver(Maze maze)
    {

        super(maze);
        totalProcessors = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(totalProcessors);
    }


    public List<Direction> solve()
    {

        List<Callable<Result>> tasks = new LinkedList<>();
        try {
            Choice startingPoint = firstChoice(maze.getStart());

            while(!startingPoint.choices.isEmpty()) {

                Choice ch = follow(startingPoint.at, startingPoint.choices.peek());
                tasks.add(new CallableDepthFirstSearch (ch, startingPoint.choices.pop(), 1));

            }

        } catch (SolutionFound e) {}

        List<Direction> solution = null;
        List<Future<Result>> expectedSolutions = null;
        try {
            expectedSolutions = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                expectedSolutions.add(executor.submit(tasks.get(i)));
            }
            int totalChoicesMade =0;
            for (int i = 0; i < tasks.size(); i++) {
                Result result = expectedSolutions.get(i).get();
                if(result != null) {
                    totalChoicesMade += result.getTotalCount();
                }


            }
            System.out.println("Total Choices followed :" + totalChoicesMade);
            for (int i = 0; i < tasks.size(); i++) {
                Result result = expectedSolutions.get(i).get();
                if (result != null && result.getDirection()!= null) {
                    solution = result.getDirection();
                    System.out.println("Solution found!");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return solution;
    }


    private class CallableDepthFirstSearch implements Callable<Result> {
        Choice startPt;
        Direction firstDir;

        int choiceCounter;
        public CallableDepthFirstSearch (Choice startPt, Direction firstDir, int choiceCounter) {
            this.startPt = startPt;
            this.firstDir = firstDir;

            this.choiceCounter = choiceCounter;
        }

        @Override
        public Result call() {
            // DFS implementation from STPuzzleSolverDFS
            LinkedList<Choice> choiceStack = new LinkedList<>();
            Choice ch;

            try {
                choiceStack.push(this.startPt);


                while (!choiceStack.isEmpty()) {

                    ch = choiceStack.peek();

                    if (ch.isDeadend()) {

                        // backtrack.
                        choiceStack.pop();
                        if (!choiceStack.isEmpty())
                            choiceStack.peek().choices.pop();
                        continue;
                    }

                    this.choiceCounter++;
                    choiceStack.push(follow(ch.at, ch.choices.peek()));

                }
                // No solution found.
                return null;
            } catch (SolutionFound e) {
                Iterator<Choice> iter = choiceStack.iterator();
                LinkedList<Direction> solutionPath = new LinkedList<Direction>();
                while (iter.hasNext()) {
                    ch = iter.next();
                    solutionPath.push(ch.choices.peek());
                }
                solutionPath.push(this.firstDir);

                return new Result(pathToFullPath(solutionPath), choiceCounter);
            }
        }
    }
}

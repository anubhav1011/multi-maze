package hwMaze;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * This file needs to hold your solver to be tested.
 * You can alter the class to extend any class that extends MazeSolver.
 * It must have a constructor that takes in a Maze.
 * It must have the solve() method that returns the datatype List<Direction>
 * which will either be a reference to a list of steps to take or will
 * be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends SkippingMazeSolver {

    private final ExecutorService exec;

    private final ConcurrentMap<Position, Boolean> seen;
    private final Semaphore sem;

    final ValueLatch<Move> solution = new ValueLatch<>();

    public StudentMTMazeSolver(Maze maze) {
        super(maze);
        this.exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.seen = new ConcurrentHashMap<>();
        this.sem = new Semaphore(Runtime.getRuntime().availableProcessors());
    }

    public List<Direction> solve() {
        // TODO: Implement your code here
        Position p = this.maze.getStart();
        for (Direction direction : this.maze.getMoves(p)) {
            exec.execute(newTask(p.move(direction), direction, null, maze));
        }
        Move endMove;
        try {
            endMove = solution.getValue();
            LinkedList<Direction> sol = endMove == null ? null : constructSolution(endMove);
            return sol;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Runnable newTask(Position p, Direction direction, Move prev, Maze maze) {
        return new Puzzletask(p, direction, prev, maze);
    }


    private LinkedList<Direction> constructSolution(Move endMove) {
        LinkedList<Direction> sol = new LinkedList<>();
        Move current = endMove;
        while (current != null && current.to != null) {
            sol.addFirst(current.to);
            current = current.previous;

        }
        return sol;

    }


    class Puzzletask extends Move implements Runnable {

        private final Maze maze;


        Puzzletask(Position pos, Direction move, Move prev, Maze maze) {
            super(pos, move, prev);
            this.maze = maze;

        }

        public void run() {
            if (solution.isSet() || seen.containsKey(this.from)) {
                return;
            }
            seen.put(this.from, true);
            if (this.maze.getEnd().equals(this.from)) {
                //System.out.println("Solution found + " + this.from.toString());
                solution.setValue(this);
                sem.release();
                return;
            }
            for (Direction direction : this.maze.getMoves(this.from)) {
                //if (sem.tryAcquire()) {
                //exec.execute(newTask(this.from.move(direction), direction, this, maze));
                if (sem.tryAcquire()) {
                    try {
                        sem.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    exec.execute(newTask(this.from.move(direction), direction, this, maze));

                } else {
                    Puzzletask puzzletask = new Puzzletask(this.from.move(direction), direction, this, maze);
                    puzzletask.solveSequentially();
                }


            }
            sem.release();
        }

        public void solveSequentially() {
            if (solution.isSet() || seen.containsKey(this.from)) {
                return;
            }
            seen.put(this.from, true);
            if (this.maze.getEnd().equals(this.from)) {
                //System.out.println("Solution found + " + this.from.toString());
                solution.setValue(this);
                return;
            }
            for (Direction direction : this.maze.getMoves(this.from)) {
                Puzzletask puzzletask = new Puzzletask(this.from.move(direction), direction, this, maze);
                puzzletask.solveSequentially();

            }

        }


    }


}

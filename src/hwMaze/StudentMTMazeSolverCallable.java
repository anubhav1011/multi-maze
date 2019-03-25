//package hwMaze;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.*;
//
//public class StudentMTMazeSolverCallable extends SkippingMazeSolver {
//
//    private final ConcurrentMap<Position, Boolean> seen;
//
//    private final ExecutorService exec;
//
//    private final CompletionService completionService;
//
//    public StudentMTMazeSolverCallable(Maze maze) {
//        super(maze);
//        this.seen = new ConcurrentHashMap<>();
//        this.exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        this.completionService = new ExecutorCompletionService(exec);
//
//
//    }
//
//    public List<Direction> solve() {
//        Position p = this.maze.getStart();
//        solve(p);
//        return null;
//    }
//
//    public LinkedList<Direction> solve(Position p) {
//        return null;
//    }
//
//
//    class MazeSolverTask implements Callable<LinkedList> {
//
//
//        private final Position p;
//        private final Maze maze;
//
//        public MazeSolverTask(Position p, Maze maze) {
//            this.p = p;
//            this.maze = maze;
//        }
//
//        @Override
//        public LinkedList<Direction> call() throws Exception {
//            if (this.maze.getEnd().equals(this.p)) {
//                return new LinkedList<>();
//
//            }
//            List<MazeSolverTask> allTasks = new ArrayList<>();
//            seen.put(this.p, true);
//            List<Direction> ds = maze.getMoves(this.p);
//            for (Direction d : ds) {
//                Position nextP = p.move(d);
//                if (!seen.containsKey(nextP)) {
//                    MazeSolverTask newTask = new MazeSolverTask(nextP, this.maze);
//                    completionService.submit(newTask);
//                   // allTasks.add(newTask);
//
//                }
//            }
//
//
//            for(int i=0; i<ds.size();i++){
//                completionService.take()
//            }
//
//
//
//            completionService.
//            List<Future<LinkedList>> futures = exec.invokeAll(allTasks);
//            for (Future<LinkedList> future : futures) {
//            }
//            return null; // no solution
//
//
//        }
//    }
//
//
//}

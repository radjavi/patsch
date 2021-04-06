package wrappers;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import search.Search;
import models.*;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class InstanceSolver {

  private static final Logger logger = LogManager.getLogger(InstanceSolver.class);

  public static Path solve(Instance instance) throws Exception {
    SingleExecutor executor = SingleExecutor.getInstance();
    if (executor == null) {
      return solveSequential(instance);
    }
    return solveParallel(instance);
  }

  private static Path solveSequential(Instance instance) throws Exception {
    int m = instance.getM();
    if (instance.getA() > instance.getB()) {
      // Find correct d to return path from Proposition 1.
      for (int d = 0; d <= m; d++) {
        Instance critical = Search.criticalWithEmptyIntersection(m, d);
        if (critical.lessThanOrEqualTo(instance))
          return billiardBallPath(instance, d);
      }
      return null;
    }
    LinkedList<Path> paths = new LinkedList<>();

    initPathsToSolve(instance, paths);
    // logger.trace("{} - Initial number of paths: {}",
    // instance.waitingTimesToString(),
    // paths.size());

    int nrPaths = 0;
    while (!paths.isEmpty()) {
      Path p = paths.pop();
      nrPaths++;
      Path solution = extendPath(instance, paths, p);
      if (solution != null) {
        logger.trace("{} visited {} paths (feasible)", instance.waitingTimesToString(), nrPaths);
        return solution;
      }
    }
    logger.trace("{} visited {} paths (infeasible)", instance.waitingTimesToString(), nrPaths);
    return null;
  }

  private static Path extendPath(Instance instance, AbstractCollection<Path> paths, Path p)
      throws Exception {
    for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
      Path pq = new Path(p);
      pq.addPositionLast(q);
      if (redundantPath(instance, pq))
        continue;
      if (pq.valid()) {
        if (pq.isValidCycle() && pq.visitsAllProperties()) {
          return pq;
        } else {
          Path pqp = new Path(pq);
          Iterator<Position> reverseIterator = p.getPath().descendingIterator();
          while (reverseIterator.hasNext()) {
            pqp.addPositionLast(reverseIterator.next());
          }
          if (pqp.valid() && pqp.isValidCycle() && pqp.visitsAllProperties()) {
            return pqp;
          }
          paths.add(pq);
        }
      }
    }
    return null;
  }

  private static boolean redundantPath(Instance instance, Path pq) throws Exception {
    return redundantPathOfLength2(instance, pq);
  }

  private static boolean redundantPathOfLength2(Instance instance, Path pq) {
    Position antepenultimate = pq.getPath().get(pq.getPath().size() - 3);
    Position penultimate = pq.getPath().get(pq.getPath().size() - 2);
    Position q = pq.getLast();
    PositionGraph validGraph = instance.getValidGraph();
    
    // Diagonal
    if (Math.abs(antepenultimate.getX() - q.getX()) == 1
        && Math.abs(antepenultimate.getY() - q.getY()) == 1)
      return true;

    // Square Diamond
    if (antepenultimate.getX() == penultimate.getX() && penultimate.getX() == q.getX()) {
      Position right = new Position(penultimate.getX() + 1, penultimate.getY());
      Position left = new Position(penultimate.getX() - 1, penultimate.getY());
      if ((antepenultimate.getY() < penultimate.getY() && penultimate.getY() < q.getY())
          || (antepenultimate.getY() > penultimate.getY() && penultimate.getY() > q.getY())) {
        if (validGraph.hasPosition(right)
            || validGraph.hasPosition(left))
          return true;
      }
    }
    if (antepenultimate.getY() == penultimate.getY() && penultimate.getY() == q.getY()) {
      Position above = new Position(penultimate.getX(), penultimate.getY() + 1);
      Position under = new Position(penultimate.getX(), penultimate.getY() - 1);
      if ((antepenultimate.getX() < penultimate.getX() && penultimate.getX() < q.getX())
          || (antepenultimate.getX() > penultimate.getX() && penultimate.getX() > q.getX())) {
        if (validGraph.hasPosition(above)
            || validGraph.hasPosition(under))
          return true;
      }
    }

    // Parallelogram
    // if (antepenultimate.getX() < penultimate.getX() && antepenultimate.getY() == penultimate.getY()) {
    //   if (instance.getProperties()[penultimate.getY()].getWaitingTime() > 2) {
    //     if (penultimate.getX() < q.getX() && penultimate.getY() < q.getY()) {
    //       Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //     if (penultimate.getX() < q.getX() && penultimate.getY() > q.getY()) {
    //       Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //   }
    // }
    // if (antepenultimate.getX() > penultimate.getX() && antepenultimate.getY() == penultimate.getY()) {
    //   if (instance.getProperties()[penultimate.getY()].getWaitingTime() > 2) {
    //     if (penultimate.getX() > q.getX() && penultimate.getY() < q.getY()) {
    //       Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //     if (penultimate.getX() > q.getX() && penultimate.getY() > q.getY()) {
    //       Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //   }
    // }
    // if (antepenultimate.getY() < penultimate.getY() && antepenultimate.getX() == penultimate.getX()) {
    //   if (instance.getProperties()[penultimate.getX()].getWaitingTime() > 2) {
    //     if (penultimate.getX() < q.getX() && penultimate.getY() < q.getY()) {
    //       Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //     if (penultimate.getX() > q.getX() && penultimate.getY() < q.getY()) {
    //       Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //   }
    // }
    // if (antepenultimate.getY() > penultimate.getY() && antepenultimate.getX() == penultimate.getX()) {
    //   if (instance.getProperties()[penultimate.getX()].getWaitingTime() > 2) {
    //     if (penultimate.getX() < q.getX() && penultimate.getY() > q.getY()) {
    //       Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //     if (penultimate.getX() > q.getX() && penultimate.getY() > q.getY()) {
    //       Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
    //       if (validGraph.hasPosition(intermediate))
    //         return true;
    //     }
    //   }
    // }
    
    int x_ = penultimate.getX() - antepenultimate.getX();
    int y_ = penultimate.getY() - antepenultimate.getY();
    if (((x_ == 1 || x_ == -1) && (y_ == 0)) || ((y_ == 1 || y_ == -1) && (x_ == 0))) {
      int diffX = q.getX() - antepenultimate.getX();
      int diffY = q.getY() - antepenultimate.getY();
      if ((diffX == 2*x_ + y_ || diffX == 2*x_ - y_) && (diffY == 2*x_ + y_ || diffY == 2*y_ - x_)) {
        if (x_ != 0 && instance.getProperties()[penultimate.getY()].getWaitingTime() > 2) {
          Position intermediate = null;
          if (q.getY() > penultimate.getY())
            intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
          else
            intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
          if (validGraph.hasPosition(intermediate))
              return true;
        } else if (y_ != 0 && instance.getProperties()[penultimate.getX()].getWaitingTime() > 2) {
          Position intermediate = null;
          if (q.getX() > penultimate.getX())
            intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
          else
            intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
              return true;
        }
      }
    }

    return false;
  }

  private static Path solveParallel(Instance instance) throws Exception {
    int m = instance.getM();
    if (instance.getA() > instance.getB()) {
      // Find correct d to return path from Proposition 1.
      for (int d = 0; d <= m; d++) {
        Instance critical = Search.criticalWithEmptyIntersection(m, d);
        if (critical.lessThanOrEqualTo(instance))
          return billiardBallPath(instance, d);
      }
      return null;
    }
    ConcurrentLinkedQueue<Path> paths = new ConcurrentLinkedQueue<>();

    initPathsToSolve(instance, paths);
    // logger.trace("{} - Initial number of paths: {}",
    // instance.waitingTimesToString(),
    // paths.size());

    SingleExecutor executor = SingleExecutor.getInstance();
    // int nrTasks = Math.min(paths.size(), executor.getNrThreads());
    int nrTasks = executor.getNrThreads();
    Semaphore available = new Semaphore(1, true);
    AtomicInteger nrBlocked = new AtomicInteger(0);
    ArrayList<Callable<Path>> callables = new ArrayList<>();
    for (int i = 0; i < nrTasks; i++) {
      callables.add(new ParallelInstanceSolver(paths, instance, nrBlocked, nrTasks, available));
    }

    return executor.getExecutor().invokeAny(callables);
  }

  public static Path billiardBallPath(Instance instance, int d) throws Exception {
    int m = instance.getM();
    int slopeXConstant = -1;
    int slopeYConstant = -1;
    if (d == 0)
      slopeYConstant = 0;
    if (d == m - 1)
      slopeXConstant = 0;
    int slopeX = -1 * slopeXConstant;
    int slopeY = -1 * slopeYConstant;
    int x = d + 1;
    int y = 0;
    Position p1 = new Position(x, y);
    Path path = new Path(instance);
    path.addPositionFirst(p1);
    boolean flagX = false;
    boolean flagY = false;

    while (!(flagX && flagY)) {
      x += slopeX;
      y += slopeY;

      Position current = new Position(x, y);
      path.addPositionLast(current);

      if (!flagX && x == m)
        flagX = true;
      if (!flagY && y == d)
        flagY = true;
      if (x == d + 1 || x == m)
        slopeX *= slopeXConstant;
      if (y == d || y == 0)
        slopeY *= slopeYConstant;

    }
    Path copyPath = new Path(path);
    Iterator<Position> reversePathIterator = copyPath.getPath().descendingIterator();
    reversePathIterator.next(); // skip first
    while (reversePathIterator.hasNext()) {
      Position pos = reversePathIterator.next();
      path.addPositionLast(pos);
    }
    return path;
  }

  private static void initPathsToSolve(Instance instance, AbstractCollection<Path> paths)
      throws Exception {
    HashMap<Position, HashSet<Position>> addedPaths = new HashMap<>();
    Property[] properties = instance.getProperties();
    int minNrNeighbours = Integer.MAX_VALUE;
    Property loneliestProperty = null;
    for (Property property : properties) {
      int nrNeighbours = 0;
      for (Position position : property.getPositions()) {
        if (!instance.isValidPos(position))
          continue;
        nrNeighbours += instance.getValidGraph().getNeighbours(position).size();
      }
      if (nrNeighbours < minNrNeighbours)
        loneliestProperty = property;
    }

    for (Position u : loneliestProperty.getPositions()) {
      if (!instance.isValidPos(u))
        continue;
      for (Position v : instance.getValidGraph().getNeighbours(u)) {
        if (addedPaths.get(v) != null && addedPaths.get(v).contains(u))
          continue;
        Path path = new Path(instance);
        path.addPositionLast(u);
        path.addPositionLast(v);
        paths.add(path);
        if (addedPaths.get(u) == null)
          addedPaths.put(u, new HashSet<>());
        addedPaths.get(u).add(v);
        if (addedPaths.get(v) == null)
          addedPaths.put(v, new HashSet<>());
        addedPaths.get(v).add(u);
      }
    }
    // addedPaths.forEach((p1, p2) -> logger.trace("Added: {}->{}", p1, p2));
    // paths.forEach(p -> logger.trace("Path: {}", p));
  }

  private static class ParallelInstanceSolver implements Callable<Path> {
    private final ConcurrentLinkedQueue<Path> paths;
    private final Instance instance;
    private final AtomicInteger nrBlocked;
    private final int nrThreads;
    private final Semaphore semaphore;

    public ParallelInstanceSolver(ConcurrentLinkedQueue<Path> paths, Instance instance,
        AtomicInteger nrBlocked, int nrThreads, Semaphore semaphore) {
      this.paths = paths;
      this.instance = instance;
      this.nrBlocked = nrBlocked;
      this.nrThreads = nrThreads;
      this.semaphore = semaphore;
    }

    @Override
    public Path call() throws Exception {
      while (true) {
        nrBlocked.incrementAndGet();
        if (paths.peek() == null && nrBlocked.get() == nrThreads)
          return null;
        Path p = null;
        while (p == null) {
          semaphore.acquire();
          p = paths.poll();
          if (p != null)
            nrBlocked.decrementAndGet();
          semaphore.release();
        }

        Path solution = extendPath(instance, paths, p);
        if (solution != null)
          return solution;
      }
    }
  }
}

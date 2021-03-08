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
  private final Instance instance;
  private static final Logger logger = LogManager.getLogger(InstanceSolver.class);

  public InstanceSolver(Instance instance) {
    this.instance = instance;
  }

  public Path solve() throws Exception {
    SingleExecutor executor = SingleExecutor.getInstance();
    if (executor == null) {
      return solveSequential();
    }
    return solveParallel();
  }

  private Path solveSequential() throws Exception {
    int m = instance.getM();
    if (instance.getA() > instance.getB()) {
      // Find correct d to return path from Proposition 1.
      for (int d = 0; d <= m; d++) {
        Instance critical = Search.criticalWithEmptyIntersection(m, d);
        if (critical.lessThanOrEqualTo(instance))
          return instance.billiardBallPath(d);
      }
      return null;
    }
    LinkedList<Path> paths = new LinkedList<>();

    initPathsToSolve(paths);
    // logger.trace("{} - Initial number of paths: {}", instance.waitingTimesToString(),
    // paths.size());

    int nrPaths = 0;
    while (!paths.isEmpty()) {
      Path p = paths.pop();
      // logger.trace(p);
      nrPaths++;
      for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
        Path pq = new Path(p);
        pq.addPositionLast(q);
        Position penultimate = p.getPath().get(p.getPath().size() - 2);
        if (Math.abs(penultimate.getX() - q.getX()) == 1
            && Math.abs(penultimate.getY() - q.getY()) == 1)
          continue;
        if (pq.valid()) {
          if (pq.isValidCycle() && pq.visitsAllProperties()) {
            logger.info("{} ({}) visited {} paths.", instance.waitingTimesToString(), "feasible",
                nrPaths);
            return pq;
          } else {
            Path pqp = new Path(pq);
            Iterator<Position> reverseIterator = p.getPath().descendingIterator();
            while (reverseIterator.hasNext()) {
              pqp.addPositionLast(reverseIterator.next());
            }
            // logger.trace("pqp: {}", pqp);
            if (pqp.valid() && pqp.isValidCycle() && pqp.visitsAllProperties()) {
              logger.info("{} ({}) visited {} paths.", instance.waitingTimesToString(), "feasible",
                  nrPaths);
              return pqp;
            }
            paths.add(pq);
          }
        }
      }
    }
    logger.info("{} ({}) visited {} paths.", instance.waitingTimesToString(), "infeasible",
        nrPaths);
    return null;
  }

  private Path solveParallel() throws Exception {
    int m = instance.getM();
    if (instance.getA() > instance.getB()) {
      // Find correct d to return path from Proposition 1.
      for (int d = 0; d <= m; d++) {
        Instance critical = Search.criticalWithEmptyIntersection(m, d);
        if (critical.lessThanOrEqualTo(instance))
          return instance.billiardBallPath(d);
      }
      return null;
    }
    ConcurrentLinkedQueue<Path> paths = new ConcurrentLinkedQueue<>();

    initPathsToSolve(paths);
    // logger.trace("{} - Initial number of paths: {}", instance.waitingTimesToString(),
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

  private void initPathsToSolve(AbstractCollection<Path> paths) throws Exception {
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

  private class ParallelInstanceSolver implements Callable<Path> {
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
          Path temp = paths.peek();
          if (temp != null) {
            nrBlocked.decrementAndGet();
            p = paths.poll();
          }
          semaphore.release();
        }

        for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
          Path pq = new Path(p);
          pq.addPositionLast(q);
          Position penultimate = p.getPath().get(p.getPath().size() - 2);
          if (Math.abs(penultimate.getX() - q.getX()) == 1
              && Math.abs(penultimate.getY() - q.getY()) == 1)
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
      }
    }
  }
}

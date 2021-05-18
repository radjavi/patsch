package wrappers;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import search.Search;
import models.*;
import com.google.common.collect.Sets;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class InstanceSolver {

  private static final Logger logger = LogManager.getLogger(InstanceSolver.class);

  public static Path solve(Instance instance, int[] nrOfSolved) throws Exception {

    return solveSequential(instance, nrOfSolved);
  }

  public static Path solveSequentialBASIC(Instance instance, int[] nrOfSolved) throws Exception {
    int m = instance.getM();
    int a = instance.getA();
    int b = instance.getB();
    if (a > b || (a == 0 && b == m) || (a == 0 && b == 0) || (a == m && b == m)) {
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
    HashSet<ArrayList<Integer>> fingerprints = new HashSet<>();

    while (!paths.isEmpty()) {
      Path p = paths.pop();
      if (nrOfSolved[0] % 1e6 < 10)
        System.out.println(nrOfSolved[0]);
      Path solution = extendPath(instance, fingerprints, paths, p, nrOfSolved);
      if (solution != null) {

        return solution;
      }
    }

    return null;

  }

  public static Path solveSequential(Instance instance, int[] nrOfSolved) throws Exception {
    int m = instance.getM();
    int a = instance.getA();
    int b = instance.getB();
    if (a > b || (a == 0 && b == m) || (a == 0 && b == 0) || (a == m && b == m)) {
      // Find correct d to return path from Proposition 1.
      for (int d = 0; d <= m; d++) {
        Instance critical = Search.criticalWithEmptyIntersection(m, d);
        if (critical.lessThanOrEqualTo(instance))
          return billiardBallPath(instance, d);
      }
      return null;
    }
    LinkedList<Path> paths = new LinkedList<>();
    LinkedList<Path> wildcards = findWildcardPaths(instance, nrOfSolved);
    if (wildcards == null)
      return null;
    else if (!wildcards.isEmpty()) {
      for (Path wildcard : wildcards) {
        if (wildcard.isValidCycle() && wildcard.visitsAllProperties())
          return wildcard;
        paths.add(wildcard);
      }
    } else
      initPathsToSolve(instance, paths);

    // logger.trace("{} - Initial number of paths: {}",
    // instance.waitingTimesToString(),
    // paths.size());
    // Table<Position, Position, Path> bestValidPaths = HashBasedTable.create();
    // for (Path p : paths) {
    // bestValidPaths.put(p.getFirst(), p.getLast(), p);
    // }

    HashSet<ArrayList<Integer>> fingerprints = new HashSet<>();
    // System.out.println(instance.waitingTimesToString());
    // int nrPaths = 0;
    while (!paths.isEmpty()) {
      Path p = paths.pop();
      // nrPaths++;
      Path solution = extendPath(instance, fingerprints, paths, p, nrOfSolved);
      if (solution != null) {
        // logger.trace("Instance: {}, nrPaths: {}", instance.waitingTimesToString(),
        // nrPaths);
        return solution;
      }
    }
    // logger.trace("Instance: {}, nrPaths: {}", instance.waitingTimesToString(),
    // nrPaths);
    return null;
  }

  private static LinkedList<Path> findWildcardPaths(Instance instance, int[] nrOfSolved) throws Exception {
    int a = instance.getA();
    int b = instance.getB();
    int m = instance.getM();
    if (a == 0 && b == m)
      return new LinkedList<Path>();
    int left = a;
    int right = m - b;
    LinkedList<Path> wildcardPathsLeft = new LinkedList<>();
    LinkedList<Path> wildcardPathsRight = new LinkedList<>();
    if (left > right) {
      wildcardPathsLeft = findWildcardPathsLeft(instance, nrOfSolved);
      if (wildcardPathsLeft == null)
        return null;
      wildcardPathsRight = findWildcardPathsRight(instance, nrOfSolved);
      if (wildcardPathsRight == null)
        return null;
    } else {
      wildcardPathsRight = findWildcardPathsRight(instance, nrOfSolved);
      if (wildcardPathsRight == null)
        return null;
      wildcardPathsLeft = findWildcardPathsLeft(instance, nrOfSolved);
      if (wildcardPathsLeft == null)
        return null;
    }
    return left >= right ? wildcardPathsLeft : wildcardPathsRight;
  }

  private static LinkedList<Path> findWildcardPathsLeft(Instance instance, int[] nrOfSolved) throws Exception {
    LinkedList<Path> validPaths = new LinkedList<>();
    int a = instance.getA();
    if (a == 0)
      return validPaths;
    int[] ys = new int[2 * (a + 1) - 1];
    int value = a;
    int step = -1;
    for (int y = 0; y < ys.length; y++) {
      ys[y] = value;
      if (value == 0)
        step = 1;
      value += step;
    }

    LinkedList<Path> paths = new LinkedList<>();
    Property property = instance.getProperty(ys[0]);
    for (Position u : property.getPositions()) {
      if (!instance.isValidPos(u) || u.getY() != ys[0])
        continue;
      for (Position v : instance.getValidGraph().getNeighbours(u)) {
        if (v.getY() != ys[1])
          continue;
        Path path = new Path(instance);
        path.addPositionLast(u);
        path.addPositionLast(v);
        paths.add(path);
      }
    }

    HashSet<ArrayList<Integer>> fingerprints = new HashSet<>();
    while (!paths.isEmpty()) {
      Path p = paths.pop();
      for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
        int length = p.getLength() + 1;
        if (q.getY() != ys[length])
          continue;
        Path pq = new Path(p);
        pq.addPositionLast(q);
        nrOfSolved[0]++;
        if (pq.valid()) {
          ArrayList<Integer> fingerprint = pq.fingerprint();
          if (!fingerprints.contains(fingerprint) && !pq.redundant(nrOfSolved)) {
            fingerprints.add(fingerprint);
            if ((length == ys.length - 1))
              validPaths.add(pq);
            else
              paths.add(pq);
          }
        }
      }
    }

    return validPaths.isEmpty() ? null : validPaths;
  }

  private static LinkedList<Path> findWildcardPathsRight(Instance instance, int[] nrOfSolved) throws Exception {
    LinkedList<Path> validPaths = new LinkedList<>();
    int m = instance.getM();
    int b = instance.getB();
    if (b == m)
      return validPaths;
    int[] xs = new int[2 * (m - b + 1) - 1];
    int value = b;
    int step = 1;
    for (int x = 0; x < xs.length; x++) {
      xs[x] = value;
      if (value == m)
        step = -1;
      value += step;
    }

    LinkedList<Path> paths = new LinkedList<>();
    Property property = instance.getProperty(xs[0]);
    for (Position u : property.getPositions()) {
      if (!instance.isValidPos(u) || u.getX() != xs[0])
        continue;
      for (Position v : instance.getValidGraph().getNeighbours(u)) {
        if (v.getX() != xs[1])
          continue;
        Path path = new Path(instance);
        path.addPositionLast(u);
        path.addPositionLast(v);
        paths.add(path);
      }
    }

    HashSet<ArrayList<Integer>> fingerprints = new HashSet<>();
    while (!paths.isEmpty()) {
      Path p = paths.pop();
      for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
        int length = p.getLength() + 1;
        if (q.getX() != xs[length])
          continue;
        Path pq = new Path(p);
        pq.addPositionLast(q);
        nrOfSolved[0]++;
        if (pq.valid()) {
          ArrayList<Integer> fingerprint = pq.fingerprint();
          if (!fingerprints.contains(fingerprint) && !pq.redundant(nrOfSolved)) {
            fingerprints.add(fingerprint);
            if ((length == xs.length - 1))
              validPaths.add(pq);
            else
              paths.add(pq);
          }
        }
      }
    }
    return validPaths.isEmpty() ? null : validPaths;
  }

  private static Path extendPathBASIC(Instance instance, AbstractCollection<Path> paths, Path p, int[] nrOfSolved)
      throws Exception {
    for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
      Path pq = new Path(p);
      pq.addPositionLast(q);
      nrOfSolved[0]++;
      if (pq.valid()) {
        if (pq.isValidCycle() && pq.visitsAllProperties())
          return pq;

        paths.add(pq);
      }

    }
    return null;
  }

  private static Path extendPath(Instance instance, Set<ArrayList<Integer>> fingerprints,
      AbstractCollection<Path> paths, Path p, int[] nrOfSolved) throws Exception {
    for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
      Path pq = new Path(p);
      pq.addPositionLast(q);
      nrOfSolved[0]++;
      if (pq.valid()) {
        if (pq.isValidCycle() && pq.visitsAllProperties())
          return pq;
        // Path pqp = lookAhead(pq);
        // nrOfSolved[0]++;
        // if (pqp != null)
        // return pqp;
        ArrayList<Integer> fingerprint = pq.fingerprint();
        if (!fingerprints.contains(fingerprint) && !pq.redundant(nrOfSolved)) {
          fingerprints.add(fingerprint);
          paths.add(pq);
        }
      }
    }
    return null;
  }

  private static Path lookAhead(Path pq) throws Exception {
    Path pqp = new Path(pq);
    Iterator<Position> reverseIterator = pq.getPath().descendingIterator();
    reverseIterator.next();
    while (reverseIterator.hasNext()) {
      pqp.addPositionLast(reverseIterator.next());
    }
    if (pqp.valid() && pqp.isValidCycle() && pqp.visitsAllProperties()) {
      return pqp;
    }
    return null;
  }

  public static Path solveParallel(Instance instance) throws Exception {
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
    Set<ArrayList<Integer>> fingerprints = Sets.newConcurrentHashSet();
    for (int i = 0; i < nrTasks; i++) {
      callables.add(new ParallelInstanceSolver(paths, fingerprints, instance, nrBlocked, nrTasks, available));
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

  private static void initPathsToSolveBASIC(Instance instance, AbstractCollection<Path> paths) throws Exception {

    for (Position u : instance.getValidGraph().getPositions()) {
      for (Position v : instance.getValidGraph().getNeighbours(u)) {
        Path path = new Path(instance);
        path.addPositionLast(u);
        path.addPositionLast(v);
        paths.add(path);
      }
    }
  }

  private static void initPathsToSolve(Instance instance, AbstractCollection<Path> paths) throws Exception {
    HashMap<Position, HashSet<Position>> addedPaths = new HashMap<>();
    Property[] properties = instance.getProperties();

    for (Property prop : properties) {
      for (Position u : prop.getPositions()) {
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
    }
    // addedPaths.forEach((p1, p2) -> logger.trace("Added: {}->{}", p1, p2));
    // paths.forEach(p -> logger.trace("Path: {}", p));
  }

  private static class ParallelInstanceSolver implements Callable<Path> {
    private final ConcurrentLinkedQueue<Path> paths;
    private final Set<ArrayList<Integer>> fingerprints;
    private final Instance instance;
    private final AtomicInteger nrBlocked;
    private final int nrThreads;
    private final Semaphore semaphore;

    public ParallelInstanceSolver(ConcurrentLinkedQueue<Path> paths, Set<ArrayList<Integer>> fingerprints,
        Instance instance, AtomicInteger nrBlocked, int nrThreads, Semaphore semaphore) {
      this.paths = paths;
      this.fingerprints = fingerprints;
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

        Path solution = extendPath(instance, fingerprints, paths, p, new int[] { 0 });
        if (solution != null)
          return solution;
      }
    }
  }
}

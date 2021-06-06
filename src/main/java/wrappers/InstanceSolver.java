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

  public static Path solve(Instance instance) throws Exception {
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
    LinkedList<Path> babysittingPaths = findBabysittingPaths(instance);
    if (babysittingPaths == null)
      return null;
    else if (!babysittingPaths.isEmpty()) {
      for (Path babysittingPath : babysittingPaths) {
        if (babysittingPath.isSolutionCycle())
          return babysittingPath;
        paths.add(babysittingPath);
      }
    } else
      initPathsToSolve(instance, paths);

    HashSet<ArrayList<Integer>> fingerprints = new HashSet<>();
    while (!paths.isEmpty()) {
      Path p = paths.pop();
      Path solution = extendPath(instance, fingerprints, paths, p);
      if (solution != null) {
        return solution;
      }
    }
    return null;
  }

  public static Path solveSafe(Instance instance) throws Exception {
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
      Path solution = extendPath(instance, fingerprints, paths, p);
      if (solution != null) {
        return solution;
      }
    }
    return null;
  }

  private static LinkedList<Path> findBabysittingPaths(Instance instance) throws Exception {
    int a = instance.getA();
    int b = instance.getB();
    int m = instance.getM();
    if (a == 0 && b == m)
      return new LinkedList<Path>();
    int left = a;
    int right = m - b;
    LinkedList<Path> babysittingPathsLeft = new LinkedList<>();
    LinkedList<Path> babysittingPathsRight = new LinkedList<>();
    if (left > right) {
      babysittingPathsLeft = findBabysittingPathsLeft(instance);
      if (babysittingPathsLeft == null)
        return null;
      babysittingPathsRight = findBabysittingPathsRight(instance);
      if (babysittingPathsRight == null)
        return null;
    } else {
      babysittingPathsRight = findBabysittingPathsRight(instance);
      if (babysittingPathsRight == null)
        return null;
      babysittingPathsLeft = findBabysittingPathsLeft(instance);
      if (babysittingPathsLeft == null)
        return null;
    }
    LinkedList<Path> allBabysittingPaths = new LinkedList<>();
    allBabysittingPaths.addAll(babysittingPathsLeft);
    allBabysittingPaths.addAll(babysittingPathsRight);
    return allBabysittingPaths;
  }

  private static LinkedList<Path> findBabysittingPathsLeft(Instance instance) throws Exception {
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
        if (pq.valid()) {
          ArrayList<Integer> fingerprint = pq.fingerprint();
          if (!fingerprints.contains(fingerprint) && !pq.redundant()) {
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

  private static LinkedList<Path> findBabysittingPathsRight(Instance instance) throws Exception {
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
        if (pq.valid()) {
          ArrayList<Integer> fingerprint = pq.fingerprint();
          if (!fingerprints.contains(fingerprint) && !pq.redundant()) {
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

  private static Path extendPath(Instance instance, Set<ArrayList<Integer>> fingerprints,
      AbstractCollection<Path> paths, Path p) throws Exception {
    for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
      Path pq = new Path(p);
      pq.addPositionLast(q);
      if (pq.valid()) {
        if (pq.isSolutionCycle())
          return pq;
        ArrayList<Integer> fingerprint = pq.fingerprint();
        if (!fingerprints.contains(fingerprint) && !pq.redundant()) {
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
    if (pqp.isSolutionCycle()) {
      return pqp;
    }
    return null;
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

  private static void initPathsToSolve(Instance instance, AbstractCollection<Path> paths) throws Exception {
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
  }
}

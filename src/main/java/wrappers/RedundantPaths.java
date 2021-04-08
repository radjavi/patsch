package wrappers;

import java.util.Arrays;
import models.*;

public class RedundantPaths {
  private static final int EAST = 0;
  private static final int NORTHEAST = 45;
  private static final int NORTH = 90;
  private static final int NORTHWEST = 135;
  private static final int WEST = 180;
  private static final int SOUTHWEST = 225;
  private static final int SOUTH = 270;
  private static final int SOUTHEAST = 315;

  private static int direction(Position from, Position to) {
    int deltaX = to.getX() - from.getX();
    int deltaY = to.getY() - from.getY();
    return (int) Math.toDegrees(Math.atan2(deltaY, deltaX));
  }

  /**
   * May be improved.
   * @param pq
   * @return
   */
  public static boolean length2(Path pq) {
    Instance instance = pq.getInstance();
    Position antepenultimate = pq.getPath().get(pq.getPath().size() - 3);
    Position penultimate = pq.getPath().get(pq.getPath().size() - 2);
    Position q = pq.getLast();
    PositionGraph validGraph = instance.getValidGraph();

    int direction1 = direction(antepenultimate, penultimate);
    int direction2 = direction(penultimate, q);
    int[] directionPath = new int[] {direction1, direction2};

    // Diagonal
    if (Math.abs(antepenultimate.getX() - q.getX()) == 1
        && Math.abs(antepenultimate.getY() - q.getY()) == 1)
      return true;

    // Square Diamond
    if (Arrays.equals(directionPath, new int[] {NORTH, NORTH})
        || Arrays.equals(directionPath, new int[] {SOUTH, SOUTH})) {
      Position right = new Position(penultimate.getX() + 1, penultimate.getY());
      Position left = new Position(penultimate.getX() - 1, penultimate.getY());
      if (validGraph.hasPosition(right) || validGraph.hasPosition(left))
        return true;
    }
    if (Arrays.equals(directionPath, new int[] {EAST, EAST})
        || Arrays.equals(directionPath, new int[] {WEST, WEST})) {
      Position above = new Position(penultimate.getX(), penultimate.getY() + 1);
      Position under = new Position(penultimate.getX(), penultimate.getY() - 1);
      if (validGraph.hasPosition(above) || validGraph.hasPosition(under))
        return true;
    }

    // Parallelogram
    if (Arrays.equals(directionPath, new int[] {NORTH, NORTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {NORTH, NORTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {EAST, NORTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {EAST, SOUTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {SOUTH, SOUTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {SOUTH, SOUTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {WEST, SOUTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {WEST, NORTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }

    // Hourglass
    if (Arrays.equals(directionPath, new int[] {EAST, NORTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {EAST, SOUTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {WEST, NORTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {WEST, SOUTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
        Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {NORTH, SOUTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {NORTH, SOUTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {SOUTH, NORTHEAST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }
    if (Arrays.equals(directionPath, new int[] {SOUTH, NORTHWEST})) {
      if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
        Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
        if (validGraph.hasPosition(intermediate))
          return true;
      }
    }

    return false;
  }
  
}

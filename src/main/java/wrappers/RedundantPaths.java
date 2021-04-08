package wrappers;

import java.util.Arrays;
import models.*;

public class RedundantPaths {

  private static String direction(Position from, Position to) {
    String dir = "";
    if (to.getY() > from.getY())
      dir += "N";
    else if (to.getY() < from.getY())
      dir += "S";
    if (to.getX() > from.getX())
      dir += "E";
    else if (to.getX() < from.getX())
      dir += "W";
    return dir;
  }

  /**
   * May be improved.
   * 
   * @param pq
   * @return
   */
  public static boolean length2(Path pq) {
    Instance instance = pq.getInstance();
    Position antepenultimate = pq.getPath().get(pq.getPath().size() - 3);
    Position penultimate = pq.getPath().get(pq.getPath().size() - 2);
    Position q = pq.getLast();
    PositionGraph validGraph = instance.getValidGraph();

    String direction1 = direction(antepenultimate, penultimate);
    String direction2 = direction(penultimate, q);
    String directionPath = direction1 + "," + direction2;

    // Diagonal
    if (Math.abs(antepenultimate.getX() - q.getX()) == 1
        && Math.abs(antepenultimate.getY() - q.getY()) == 1)
      return true;

    switch (directionPath) {
      // Square Diamond
      case "N,N":
      case "S,S":
        Position right = new Position(penultimate.getX() + 1, penultimate.getY());
        Position left = new Position(penultimate.getX() - 1, penultimate.getY());
        if (validGraph.hasPosition(right) || validGraph.hasPosition(left))
          return true;
        break;
      case "E,E":
      case "W,W":
        Position above = new Position(penultimate.getX(), penultimate.getY() + 1);
        Position under = new Position(penultimate.getX(), penultimate.getY() - 1);
        if (validGraph.hasPosition(above) || validGraph.hasPosition(under))
          return true;
        break;
      // Parallelogram
      case "N,NE":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "N,NW":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "E,NE":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "E,SE":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "S,SE":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "S,SW":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "W,SW":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "W,NW":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      // Hourglass
      case "E,NW":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "E,SW":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "W,NE":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "W,SE":
        if (instance.getPropertyWaitingTime(penultimate.getY()) > 2) {
          Position intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "N,SE":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "N,SW":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "S,NE":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
      case "S,NW":
        if (instance.getPropertyWaitingTime(penultimate.getX()) > 2) {
          Position intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
          if (validGraph.hasPosition(intermediate))
            return true;
        }
        break;
    }

    return false;
  }

}

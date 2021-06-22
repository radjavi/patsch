package wrappers;

import java.util.*;
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

  public static boolean length2(Path pq) throws Exception {
    if (pq.getLength() < 2)
      return false;

    Instance instance = pq.getInstance();
    LinkedList<Position> path = pq.getPath();
    Position antepenultimate = path.get(path.size() - 3);
    Position penultimate = path.get(path.size() - 2);
    Position q = pq.getLast();
    PositionGraph validGraph = instance.getValidGraph();

    String direction1 = direction(antepenultimate, penultimate);
    String direction2 = direction(penultimate, q);
    String directionPath = direction1 + "," + direction2;

    // Diagonal
    if (Math.abs(antepenultimate.getX() - q.getX()) == 1 && Math.abs(antepenultimate.getY() - q.getY()) == 1)
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
    }

    return false;
  }

  public static boolean length3(Path pq) throws Exception {
    if (pq.getLength() < 3)
      return false;

    LinkedList<Position> path = pq.getPath();
    Position preantepenultimate = path.get(path.size() - 4);
    Position antepenultimate = path.get(path.size() - 3);
    Position penultimate = path.get(path.size() - 2);
    Position q = pq.getLast();

    String direction1 = direction(preantepenultimate, antepenultimate);
    String direction2 = direction(antepenultimate, penultimate);
    String direction3 = direction(penultimate, q);
    String directionPath = direction1 + "," + direction2 + "," + direction3;
    Position intermediate1 = null;
    Position intermediate2 = null;

    switch (directionPath) {
      case "E,NE,E":
      case "W,NW,W":
        intermediate1 = new Position(antepenultimate.getX(), antepenultimate.getY() + 1);
        intermediate2 = new Position(penultimate.getX(), penultimate.getY() - 1);
        break;
      case "E,SE,E":
      case "W,SW,W":
        intermediate1 = new Position(antepenultimate.getX(), antepenultimate.getY() - 1);
        intermediate2 = new Position(penultimate.getX(), penultimate.getY() + 1);
        break;
      case "N,NW,N":
      case "S,SW,S":
        intermediate1 = new Position(antepenultimate.getX() - 1, antepenultimate.getY());
        intermediate2 = new Position(penultimate.getX() + 1, penultimate.getY());
        break;
      case "N,NE,N":
      case "S,SE,S":
        intermediate1 = new Position(antepenultimate.getX() + 1, antepenultimate.getY());
        intermediate2 = new Position(penultimate.getX() - 1, penultimate.getY());
        break;
    }
    if (intermediate1 != null && intermediate2 != null) {
      Instance instance = pq.getInstance();
      PositionGraph validGraph = instance.getValidGraph();
      if (validGraph.hasPosition(intermediate1) && validGraph.hasPosition(intermediate2))
        return true;
    }

    return false;
  }

}

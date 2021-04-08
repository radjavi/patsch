package wrappers;


import java.util.*;
import models.*;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class RedundantPaths {
  // Define a static logger variable so that it references the
  // Logger instance named "App".
  private static final Logger logger = LogManager.getLogger(RedundantPaths.class);

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
  public static boolean length2(Path pq) throws Exception {
    if (pq.getLength() < 2)
      return false;

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
    };

    LinkedList<Position> newPathPositions = (LinkedList<Position>) pq.getPath().clone();
    Position intermediate = null;
    switch (directionPath) {
      // Parallelogram
      case "NE,N": // Parallelogram
      case "SE,S": // Parallelogram
      case "N,SE": // Hourglass
      case "S,NE": // Hourglass
        intermediate = new Position(penultimate.getX() - 1, penultimate.getY());
        break;
      case "NW,N": // Parallelogram
      case "SW,S": // Parallelogram
      case "N,SW": // Hourglass
      case "S,NW": // Hourglass
        intermediate = new Position(penultimate.getX() + 1, penultimate.getY());
        break;
      case "NE,E": // Parallelogram
      case "NW,W": // Parallelogram
      case "E,NW": // Hourglass
      case "W,NE": // Hourglass
        intermediate = new Position(penultimate.getX(), penultimate.getY() - 1);
        break;
      case "SE,E": // Parallelogram
      case "SW,W": // Parallelogram
      case "E,SW": // Hourglass
      case "W,SE": // Hourglass
        intermediate = new Position(penultimate.getX(), penultimate.getY() + 1);
        break;
    }
    if (intermediate != null && validGraph.hasPosition(intermediate)) {
      newPathPositions.set(newPathPositions.size() - 2, intermediate);
      Path newPath = new Path(instance, newPathPositions);
      if (newPath.valid())
        return true;
    }

    return false;
  }

  public static boolean length3(Path pq) throws Exception {
    if (pq.getLength() < 3)
      return false;

    Instance instance = pq.getInstance();
    Position preantepenultimate = pq.getPath().get(pq.getPath().size() - 4);
    Position antepenultimate = pq.getPath().get(pq.getPath().size() - 3);
    Position penultimate = pq.getPath().get(pq.getPath().size() - 2);
    Position q = pq.getLast();
    PositionGraph validGraph = instance.getValidGraph();

    String direction1 = direction(preantepenultimate, antepenultimate);
    String direction2 = direction(antepenultimate, penultimate);
    String direction3 = direction(penultimate, q);
    String directionPath = direction1 + "," + direction2 + "," + direction3;

    switch (directionPath) {
      case "E,NE,E":
        Position intermediate1 = new Position(antepenultimate.getX(), antepenultimate.getY() + 1);
        Position intermediate2 = new Position(penultimate.getX(), penultimate.getY() - 1);
        if (validGraph.hasPosition(intermediate1) && validGraph.hasPosition(intermediate2))
          return true;
        break;
      // TODO: Add more cases
    }

    return false;
  }

}

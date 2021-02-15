package models;
import java.util.*;

public class PositionGraph {
  HashMap<Position, ArrayList<Position>> graph;
    
  public PositionGraph(int[] waitingTimes) {
    int m = waitingTimes.length - 1;
    graph = triangleGraph(m);
  }

  private static HashMap<Position, ArrayList<Position>> triangleGraph(int m) {
    HashMap<Position, ArrayList<Position>> map = new HashMap<>();
    HashSet<Position> vertices = new HashSet<>();
    for (int x=0; x <= m; x++) {
      for (int y=0; y <= x; y++) {
        vertices.add(new Position(x,y));
      }
    }
    vertices.forEach(pos -> {
      ArrayList<Position> neighbours = new ArrayList<>();
      for (int x=pos.getX() - 1; x <= pos.getX() + 1; x++) {
        for (int y=pos.getY() - 1; y <= pos.getY() + 1; y++) {
          Position neighbour = new Position(x,y);
          if (vertices.contains(neighbour)) {
            neighbours.add(neighbour);
          }
        }
      }
      map.put(pos, neighbours);
    });
    return map;
  }

  @Override
  public String toString() {
    String s = "";
    graph.forEach((pos, n) -> {
      s += pos + ": ";
      n.forEach(p -> {
        s += p + ", ";
      });
      s += "\n";
    });
    return s;
  }
}

// (0,0): (0,1), ...
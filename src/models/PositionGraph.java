package models;
import java.util.*;

public class PositionGraph {
  private HashMap<Position, ArrayList<Position>> graph;
    
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
          if (vertices.contains(neighbour) && !pos.equals(neighbour)) {
            neighbours.add(neighbour);
          }
        }
      }
      map.put(pos, neighbours);
    });
    return map;
  }

  public HashMap<Position, ArrayList<Position>> getGraph() {
    return graph;
  }

  public boolean hasPosition(Position pos) {
    return graph.get(pos) != null;
  }

  @Override
  public String toString() {
    String s = "";
    for (Map.Entry<Position, ArrayList<Position>> e : graph.entrySet()) {
      Position pos = e.getKey();
      ArrayList<Position> neighbours = e.getValue();
      s += pos + ": ";
      for (Position n : neighbours) {
        s += n + ", ";
      }
      s += "\n";
    }
    return s;
  }
}
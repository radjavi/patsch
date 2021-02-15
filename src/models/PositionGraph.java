package models;

import java.util.*;

public class PositionGraph {

  private HashMap<Position, ArrayList<Position>> graph;

  public PositionGraph(int[] waitingTimes) {
    int m = waitingTimes.length - 1;
    HashSet<Position> vertices = triangleVertices(m);
    graph = triangleVerticesWithNeighbours(vertices);
  }

  /**
   * Returns the vertices of a triangle graph.
   */
  private static HashSet<Position> triangleVertices(int m) {
    HashSet<Position> vertices = new HashSet<>();
    for (int x = 0; x <= m; x++) {
      for (int y = 0; y <= x; y++) {
        vertices.add(new Position(x, y));
      }
    }
    return vertices;
  }

  /**
   * Returns a mapping of each vertex in a triangle graph to its adjacent vertices.
   */
  private static HashMap<Position, ArrayList<Position>> triangleVerticesWithNeighbours(HashSet<Position> vertices) {
    HashMap<Position, ArrayList<Position>> map = new HashMap<>();
    vertices.forEach(pos -> {
      ArrayList<Position> neighbours = new ArrayList<>();
      for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
        for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
          Position neighbour = new Position(x, y);
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
    SortedSet<Position> keySet = new TreeSet<>(graph.keySet());
    for (Position pos : keySet) {
      ArrayList<Position> neighbours = graph.get(pos);
      s += pos + ": ";
      for (Position n : neighbours) {
        s += n + ", ";
      }
      s += "\n";
    }
    return s;
  }

  public String toStringTriangle() {
    String s = "";
    SortedSet<Position> keySet = new TreeSet<>(Collections.reverseOrder());
    keySet.addAll(graph.keySet());
    System.out.println(keySet);
    int y = keySet.first().getY();
    for (Position pos : keySet) {
     //TODO
    }
    return s;
  }

}

// (0,0): (0,1), ...


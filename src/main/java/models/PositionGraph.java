package models;

import java.util.*;

public class PositionGraph {
  private HashMap<Position, HashSet<Position>> graph;

  public PositionGraph(HashSet<Position> positions) {
    graph = connectVertices(positions);
  }

  /**
   * Returns a mapping of each vertex in a triangle graph to its adjacent
   * vertices.
   */
  private static HashMap<Position, HashSet<Position>> connectVertices(HashSet<Position> positions) {
    HashMap<Position, HashSet<Position>> map = new HashMap<>();
    positions.forEach(pos -> {
      HashSet<Position> neighbours = new HashSet<>();
      for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++) {
        for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++) {
          Position neighbour = new Position(x, y);
          if (positions.contains(neighbour) && !pos.equals(neighbour)) {
            neighbours.add(neighbour);
          }
        }
      }
      map.put(pos, neighbours);
    });
    return map;
  }

  public HashMap<Position, HashSet<Position>> getGraph() {
    return graph;
  }

  public Set<Position> getPositions() {
    return graph.keySet();
  }

  public HashSet<Position> getNeighbours(Position p) {
    return graph.get(p);
  }

  public boolean hasPosition(Position pos) {
    return graph.get(pos) != null;
  }

  @Override
  public String toString() {
    String s = "";
    SortedSet<Position> keySet = new TreeSet<>(graph.keySet());
    for (Position pos : keySet) {
      HashSet<Position> neighbours = graph.get(pos);
      s += pos + ": ";
      for (Position n : neighbours) {
        s += n;
      }
      s += "\n";
    }
    return s;
  }

  public String toStringTriangle() {
    String s = "";
    SortedSet<Position> keySet = new TreeSet<>(Collections.reverseOrder());
    keySet.addAll(graph.keySet());
    int x = keySet.first().getX();
    String[][] matrix = new String[x + 1][x + 1];
    for (String[] row : matrix)
      Arrays.fill(row, " ".repeat(5));

    for (Position pos : keySet) {
      matrix[x - pos.getY()][pos.getX()] = "(" + (pos.getX() + "," + pos.getY() + ")");
    }

    for (int i = 0; i < x + 1; i++) {
      for (int j = 0; j < x + 1; j++) {
        System.out.print(matrix[i][j] + "\t");
      }
      System.out.println();

    }

    return s;
  }

}

// (0,0): (0,1), ...

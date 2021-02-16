package models;

import java.util.*;

public class PositionGraph {

  private HashMap<Position, HashSet<Position>> graph;
  //private ArrayList<Property> properties;

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
  private static HashMap<Position, HashSet<Position>> triangleVerticesWithNeighbours(HashSet<Position> vertices) {
    HashMap<Position, HashSet<Position>> map = new HashMap<>();
    vertices.forEach(pos -> {
      HashSet<Position> neighbours = new HashSet<>();
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

  public HashMap<Position, HashSet<Position>> getGraph() {
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
      HashSet<Position> neighbours = graph.get(pos);
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
    int y = keySet.first().getY();
    String [] [] matrix = new String [y+1][y+1];
    for (String[] row: matrix)
      Arrays.fill(row, " ".repeat(5));

    for (Position pos : keySet) {
      matrix[y-pos.getY()][pos.getX()] = "(" + (pos.getX() + "," + pos.getY() + ")" );
    }

    for(int i=0;i<y+1;i++){    
      for(int j=0;j<y+1;j++){   
        System.out.print(matrix[i][j]+ "\t");
      }
      System.out.println();

    }

    return s;
  }

}

// (0,0): (0,1), ...


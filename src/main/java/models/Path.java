package models;

import java.util.*;

public class Path {

  LinkedList<Position> path;
  Instance instance;
  /**
   * Maps property index to index of first position that has this property.
   */
  int[] s_i;
  /**
   * Maps property index to index of last position that has this property.
   */
  int[] f_i;

  public Path(Instance instance) {
    this.instance = instance;
    path = new LinkedList<Position>();
    s_i = initIntArray(instance.getM() + 1, -1);
    f_i = initIntArray(instance.getM() + 1, -1);
  }

  public Path(Instance instance, LinkedList<Position> path) {
    this.instance = instance;
    this.path = new LinkedList<Position>();
    this.path.addAll(path);
    s_i = initIntArray(instance.getM() + 1, -1);
    f_i = initIntArray(instance.getM() + 1, -1);
    computeIndices();
  }

  public void addPositionLast(Position position) throws Exception {
    PositionGraph validGraph = instance.getValidGraph();
    if (!validGraph.hasPosition(position))
      throw new Exception("Position " + position + " not in valid graph.");
    if (!path.isEmpty() && !validGraph.getNeighbours(this.getLast()).contains(position)) {
      throw new Exception("Position " + position + " is not neighbour to " + this.getLast() + ".");
    }
    path.addLast(position);
    updateIndices(position, path.size() - 1);
  }

  public void addPositionFirst(Position position) throws Exception {
    PositionGraph validGraph = instance.getValidGraph();
    if (!validGraph.hasPosition(position))
      throw new Exception("Position " + position + " not in valid graph.");
    if (!path.isEmpty() && !validGraph.getNeighbours(position).contains(this.getFirst())) {
      throw new Exception("Position " + position + " is not neighbour to " + this.getFirst() + ".");
    }
    path.addFirst(position);
    updateIndices(position, 0);
  }

  public LinkedList<Position> getPath() {
    return path;
  }

  public int getLength() {
    return path.size() - 1;
  }

  public boolean valid() {
    for (Position pos : path) {

    }
    return true;
  }

  private static int[] initIntArray(int size, int initValue) {
    int[] array = new int[size];
    if (initValue != 0)
      Arrays.fill(array, initValue);
    return array;
  }

  /**
   * May be improved.
   */
  private void computeIndices() {
    Property[] properties = instance.getProperties();
    for (int p = 0; p <= instance.getM(); p++) {
      for (int i = 0; i < path.size(); i++) {
        if (properties[p].hasPosition(path.get(i))) {
          s_i[p] = i;
          break;
        }
      }
      for (int i = path.size() - 1; i >= 0; i--) {
        if (properties[p].hasPosition(path.get(i))) {
          f_i[p] = i;
          break;
        }
      }
    }
  }

  /**
   * May be improved.
   */
  private void updateIndices(Position newPosition, int positionIndex) {
    ArrayList<Integer> propertyIndices = new ArrayList<>();
    Property[] properties = instance.getProperties();
    for (int p = 0; p <= instance.getM(); p++) {
      Property property = properties[p];
      if (property.hasPosition(newPosition)) {
        propertyIndices.add(p);
      }
    }
    for (int p = 0; p < s_i.length; p++) {
      if (s_i[p] >= positionIndex)
        s_i[p]++;
      if (f_i[p] >= positionIndex)
        f_i[p]++;
    }
    for (Integer p : propertyIndices) {
      if (s_i[p] < 0 || positionIndex < s_i[p])
        s_i[p] = positionIndex;
      
      if (f_i[p] < 0 || positionIndex > f_i[p])
        f_i[p] = positionIndex;
    }
    
  }

  /**
   * Get s
   */
  private Position getFirst() {
    return path.get(0);
  }

  /**
   * Get f
   */
  private Position getLast() {
    return path.get(path.size() - 1);
  }

  /**
   * @return the s_i
   */
  public int[] getS_i() {
    return s_i;
  }

  /**
   * @return the f_i
   */
  public int[] getF_i() {
    return f_i;
  }

  @Override
  public String toString() {
    String s = "";

    for (Position pos : path) {
      s += pos;
    }

    return s;
  }
}

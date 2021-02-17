package models;

import java.util.*;

public class Path {

  ArrayList<Position> path;
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
    path = new ArrayList<Position>();
    s_i = initIntArray(instance.getM() + 1, -1);
    f_i = initIntArray(instance.getM() + 1, -1);
  }

  public Path(ArrayList<Position> path, Instance instance) {
    this.instance = instance;
    this.path = new ArrayList<Position>();
    this.path.addAll(path);
    s_i = initIntArray(instance.getM() + 1, -1);
    f_i = initIntArray(instance.getM() + 1, -1);
    computeIndices();
  }

  public void addPositionToEnd(Position position) {
    path.add(position);
    computeIndices();
  }

  public ArrayList<Position> getPath() {
    return path;
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
    for (int p = 0; p < instance.getM(); p++) {
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

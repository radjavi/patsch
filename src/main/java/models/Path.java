package models;

import java.util.*;
import wrappers.RedundantPaths;

public class Path {

  private LinkedList<Position> path;
  private Instance instance;
  /**
   * Maps property index to index of first position that has this property.
   */
  private int[] s_i;
  /**
   * Maps property index to index of last position that has this property.
   */
  private int[] f_i;

  private Boolean valid;

  public Path(Instance instance) {
    this.instance = instance;
    path = new LinkedList<Position>();
    s_i = initIntArray(instance.getM() + 1, -1);
    f_i = initIntArray(instance.getM() + 1, -1);
  }

  public Path(Instance instance, ArrayList<Position> path) throws Exception {
    this.instance = instance;
    PositionGraph validGraph = instance.getValidGraph();
    if (!path.isEmpty()) {
      for (int i = 0; i < path.size() - 1; i++) {
        if (!validGraph.getNeighbours(path.get(i)).contains(path.get(i + 1))) {
          throw new Exception("Position " + path.get(i + 1) + " is not neighbour to " + path.get(i) + ".");
        }
      }
    }
    this.path = new LinkedList<Position>(path);
    s_i = initIntArray(instance.getM() + 1, -1);
    f_i = initIntArray(instance.getM() + 1, -1);
    computeIndices();
  }

  public Path(Path path) {
    this.path = new LinkedList<Position>(path.getPath());
    this.instance = path.instance;
    this.s_i = path.s_i.clone();
    this.f_i = path.f_i.clone();
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
    valid = null;
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
    valid = null;
  }

  public LinkedList<Position> getPath() {
    return path;
  }

  public int getLength() {
    return path.size() - 1;
  }

  public boolean valid() throws Exception {
    if (valid != null)
      return valid;

    Property[] properties = instance.getProperties();
    Position s = this.getFirst();
    Position f = this.getLast();
    for (Property property : properties) {
      int p = property.getIndex();
      int waitingTime = property.getWaitingTime();
      if (s_i[p] < 0 && f_i[p] < 0) {
        int propToS = instance.distance(property, s);
        int fToProp = instance.distance(f, property);
        if (propToS + this.getLength() + fToProp > waitingTime) {
          valid = false;
          return false;
        }
      } else {
        int propToSi = instance.distance(property, s) + s_i[p];
        if (propToSi > waitingTime) {
          valid = false;
          return false;
        }
        int fiToProp = ((path.size() - 1) - f_i[p]) + instance.distance(f, property);
        if (fiToProp > waitingTime) {
          valid = false;
          return false;
        }
        if (!consecutivePathsValid(property, waitingTime)) {
          valid = false;
          return false;
        }
      }
    }
    valid = true;
    return true;
  }

  private boolean consecutivePathsValid(Property property, int waitingTime) {
    int propertyIndex = property.getIndex();
    ListIterator<Position> iterator = path.listIterator(s_i[propertyIndex]);
    int iterationCount = f_i[propertyIndex] - s_i[propertyIndex] + 1;
    int time = waitingTime;
    for (int i = 0; i < iterationCount; i++) {
      Position current = iterator.next();
      if (property.hasPosition(current))
        time = waitingTime;
      else {
        time--;
        if (time == 0)
          return false;
      }
    }
    return true;
  }

  private static int[] initIntArray(int size, int initValue) {
    int[] array = new int[size];
    if (initValue != 0)
      Arrays.fill(array, initValue);
    return array;
  }

  private void computeIndices() {
    Property[] properties = instance.getProperties();
    for (Property property : properties) {
      int p = property.getIndex();
      ListIterator<Position> iterator = path.listIterator();
      int i = 0;
      while (iterator.hasNext()) {
        if (property.hasPosition(iterator.next())) {
          s_i[p] = i;
          break;
        }
        i++;
      }
      if (i == path.size())
        return;
      Iterator<Position> iteratorReverse = path.descendingIterator();
      i = path.size() - 1;
      while (iteratorReverse.hasNext()) {
        if (property.hasPosition(iteratorReverse.next())) {
          f_i[p] = i;
          break;
        }
        i--;
      }
    }
  }

  private void updateIndices(Position newPosition, int positionIndex) {
    ArrayList<Integer> propertyIndices = new ArrayList<>();
    Property[] properties = instance.getProperties();
    for (Property property : properties) {
      int p = property.getIndex();
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

  public Position getFirst() {
    return path.get(0);
  }

  public Position getLast() {
    return path.get(path.size() - 1);
  }

  public int[] getS_i() {
    return s_i;
  }

  public int[] getF_i() {
    return f_i;
  }

  public Instance getInstance() {
    return instance;
  }

  public boolean isSolutionCycle() throws Exception {
    if (!this.isCycle())
      return false;
    if (!this.valid())
      return false;
    if (!this.visitsAllProperties())
      return false;
    if (!this.validLoopCycle())
      return false;

    return true;
  }

  private boolean validLoopCycle() {
    Property[] properties = instance.getProperties();
    for (Property property : properties) {
      int p = property.getIndex();
      int waitingTime = property.getWaitingTime();
      int fiToF = ((path.size() - 1) - f_i[p]);
      int sToSi = s_i[p];
      if (fiToF + sToSi > waitingTime)
        return false;
    }
    return true;
  }

  private boolean isCycle() {
    if (this.getFirst().equals(this.getLast()))
      return true;
    return false;
  }

  public boolean visitsAllProperties() {
    for (int i = 0; i <= this.instance.getM(); i++) {
      if (s_i[i] < 0 || f_i[i] < 0)
        return false;
    }
    return true;
  }

  public boolean redundant() throws Exception {
    return RedundantPaths.length2(this) || RedundantPaths.length3(this);
  }

  /**
   * The fingerprint of a path from position s to position f is an array that
   * contains: [s_x, s_y, t_x, t_y, s-s_0, ..., s-s_m, f-f_0, ..., f-f_m]
   * 
   * @return
   */
  public ArrayList<Integer> fingerprint() {
    ArrayList<Integer> fingerprint = new ArrayList<>();
    fingerprint.add(this.getFirst().getX());
    fingerprint.add(this.getFirst().getY());
    fingerprint.add(this.getLast().getX());
    fingerprint.add(this.getLast().getY());
    for (int p = 0; p < this.getS_i().length; p++) {
      fingerprint.add(this.getS_i()[p] < 0 ? this.getLength() : this.getS_i()[p]);
      fingerprint.add(this.getF_i()[p] < 0 ? this.getLength() : this.getLength() - this.getF_i()[p]);
    }
    return fingerprint;
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

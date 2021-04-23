package models;

public class Position implements Comparable<Position> {
  private final int x;
  private final int y;

  /**
   * 
   */
  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * 
   */
  public int getX() {
    return this.x;
  }

  /**
   * 
   */
  public int getY() {
    return this.y;
  }

  public double euclideanDistance(Position p) {
    return Math.sqrt(Math.pow(p.getX() - this.getX(), 2) + Math.pow(p.getY() - this.getY(), 2));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if ((o == null) || (o.getClass() != this.getClass()))
      return false;
    Position pos = (Position) o;
    return x == pos.getX() && y == pos.getY();
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(this.getX());
    result = 31 * result + Integer.hashCode(this.getY());
    return result;
  }

  @Override
  public int compareTo(Position pos) {
    if (this.getX() == pos.getX() && this.getY() == pos.getY())
      return 0;
    if (this.getX() > pos.getX())
      return 1;
    if (this.getX() == pos.getX()) {
      if (this.getY() > pos.getY())
        return 1;
      else
        return -1;
    }
    return -1;
  }

  @Override
  public String toString() {
    return "(" + this.getX() + "," + this.getY() + ")";
  }
}

package models;

public class Position {
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
    int hash = 7;
    hash = 31 * hash + this.getX();
    hash = 31 * hash + this.getY();
    return hash;
  }

  @Override
  public String toString() {
    return "(" + this.getX() + "," + this.getY() + ")";
  }
}
package models;

public class Position {
  private int x;
  private int y;

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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if ((o == null) || (o.getClass() != this.getClass()))
      return false;
    Position pos = (Position) o;
    return x == pos.x && y == pos.y;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + x;
    hash = 31 * hash + y;
    return hash;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
}
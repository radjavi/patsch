package models;

public class Position implements Comparable{
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
  public int compareTo(Object o) {
    
    Position pos = (Position) o;
    if (this.x==pos.x && this.y==pos.y) return 0;
    if (this.x>pos.x) return 1;
    if (this.x==pos.x) {
      if (this.y>pos.y) return 1;
      else return -1;
    }

    return -1;
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
package models;

public class Position {
  private final int x;
  private final int y;

  public static final int EAST = 0;
  public static final int NORTHEAST = 45;
  public static final int NORTH = 90;
  public static final int NORTHWEST = 135;
  public static final int WEST = 180;
  public static final int SOUTHWEST = 225;
  public static final int SOUTH = 270;
  public static final int SOUTHEAST = 315;

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

  public int directionTo(Position to) {
    int deltaX = to.getX() - this.getX();
    int deltaY = to.getY() - this.getY();
    return (int) Math.toDegrees(Math.atan2(deltaY, deltaX));
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

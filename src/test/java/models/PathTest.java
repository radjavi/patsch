package models;

import org.junit.*;
import static org.junit.Assert.*;

public class PathTest {
  @Test
  public void testPropertyIndices() throws Exception {
    int[] times = { 7, 5, 5, 4, 6, 4, 6, 8 };
    Instance instance = new Instance(times);
    Position from = new Position(3, 0);
    Position to = new Position(7, 3);
    Path path = instance.shortestPath(from, to);
    path.addPositionLast(new Position(6, 2));
    path.addPositionLast(new Position(5, 1));
    path.addPositionLast(new Position(4, 0));
    path.addPositionLast(new Position(3, 0));

    int[] expectedS_i = {0, 2, 3, 0, 1, 2, 3, 4};
    int[] expectedF_i = {8, 6, 5, 8, 7, 6, 5, 4};
    int[] actualS_i = path.getS_i();
    int[] actualF_i = path.getF_i();

    assertArrayEquals(expectedS_i, actualS_i);
    assertArrayEquals(expectedF_i, actualF_i);
  }
}

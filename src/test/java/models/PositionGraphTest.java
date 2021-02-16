package models;

import org.junit.*;
import static org.junit.Assert.*;

public class PositionGraphTest {
  @Test
  public void testPositions() {
    int[] waitingTimes = {2, 2, 2};
    Position[] expectedPositions = {
      new Position(0,0),
      new Position(1,0),
      new Position(2,0),
      new Position(1,1),
      new Position(2,1),
      new Position(2,2)
    };
    PositionGraph graph = new PositionGraph(waitingTimes);
    for (Position p : expectedPositions) {
      assertTrue(graph.hasPosition(p));
    }
  }
}

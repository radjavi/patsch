package models;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class PositionGraphTest {
  @Test
  public void testTrianglePositions() {
    HashSet<Position> expectedPositions = new HashSet<>(Arrays.asList(new Position(0, 0), new Position(1, 0), new Position(2, 0), new Position(1, 1),
        new Position(2, 1), new Position(2, 2)));
    PositionGraph graph = new PositionGraph(expectedPositions);
    assertEquals(expectedPositions.size(), graph.getGraph().size());
    for (Position p : expectedPositions) {
      assertTrue(graph.hasPosition(p));
    }
  }
}

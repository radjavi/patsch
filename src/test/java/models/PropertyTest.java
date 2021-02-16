package models;

import org.junit.*;
import static org.junit.Assert.*;

public class PropertyTest {
  @Test
  public void testPropertyPositions() {
    int[] waitingTimes = { 2, 2, 2, 2 };

    Position[][] expectedPositionSets = {
        { new Position(3, 0), new Position(2, 0), new Position(1, 0), new Position(0, 0) },
        { new Position(3, 1), new Position(2, 1), new Position(1, 0), new Position(1, 1) },
        { new Position(2, 0), new Position(3, 2), new Position(2, 1), new Position(2, 2) },
        { new Position(3, 0), new Position(3, 1), new Position(3, 2), new Position(3, 3) }, };

    for (int i = 0; i < waitingTimes.length; i++) {
      Property prop = new Property(waitingTimes.length - 1, waitingTimes[i], i);
      assertEquals(expectedPositionSets[i].length, prop.getPositions().size());
      for (Position p : expectedPositionSets[i]) {
        assertTrue(prop.hasPosition(p));
      }

    }
  }
}

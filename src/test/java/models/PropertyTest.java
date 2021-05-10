package models;

import org.junit.*;
import static org.junit.Assert.*;

public class PropertyTest {
  @Test
  public void testPropertyPositions() {
    int[] waitingTimes = { 2, 2, 2, 2 };
    Instance instance = new Instance(waitingTimes);

    Position[][] expectedPositionSets = {
        { new Position(3, 0), new Position(2, 0), new Position(1, 0) },
        { new Position(3, 1), new Position(2, 1), new Position(1, 0) },
        { new Position(2, 0), new Position(3, 2), new Position(2, 1) },
        { new Position(3, 0), new Position(3, 1), new Position(3, 2) }, };

    for (int i = 0; i < waitingTimes.length; i++) {
      Property prop = instance.getProperty(i);
      assertEquals(expectedPositionSets[i].length, prop.getPositions().size());
      for (Position p : expectedPositionSets[i]) {
        assertTrue(prop.hasPosition(p));
      }

    }
  }
}

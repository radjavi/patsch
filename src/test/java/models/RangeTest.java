package models;

import org.junit.*;
import static org.junit.Assert.*;

public class RangeTest {
  @Test
  public void testRangePositions() {
    int[] waitingTimes = { 5, 4, 1, 2 };
    Instance instance = new Instance(waitingTimes);

    Position[][] expectedPositionSets = {
        {
            new Position(1, 0), 
            new Position(2, 0), 
            new Position(3, 0), 
            new Position(2, 1), 
            new Position(3, 1), 
            new Position(3, 2)
        },
        { 
            new Position(1, 0), 
            new Position(2, 0), 
            new Position(3, 0), 
            new Position(2, 1), 
            new Position(3, 1), 
            new Position(3, 2)
        },
        {  
            new Position(2, 0),
            new Position(2, 1),
            new Position(3, 2)
        },
        {  
            new Position(2, 0),
            new Position(2, 1),
            new Position(3, 0),
            new Position(3, 1),
            new Position(3, 2)
        }
    };

    for (int i = 0; i < waitingTimes.length; i++) {
      Range range = instance.getProperty(i).getRange();
      
      assertEquals(("i:" + i + "," + range.getPositions().toString()), expectedPositionSets[i].length, range.getPositions().size());
      
      for (Position p : expectedPositionSets[i]) {
        assertTrue(range.hasPosition(p));
      }

    }
  }
}

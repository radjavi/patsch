package search;

import models.*;
import java.util.Set;
import java.util.HashSet;
import org.junit.*;
import static org.junit.Assert.*;

public class SearchTest {
  @Test
  public void testSearchForCriticalInstances4() throws Exception {
    HashSet<Instance> expectedInstances = new HashSet<>();
    expectedInstances.add(new Instance(new int[]{2,2,4,2,4}));
    expectedInstances.add(new Instance(new int[]{6,4,4,6,1}));
    expectedInstances.add(new Instance(new int[]{6,1,4,4,6}));
    expectedInstances.add(new Instance(new int[]{4,2,2,2,4}));
    expectedInstances.add(new Instance(new int[]{4,2,4,2,2}));
    expectedInstances.add(new Instance(new int[]{6,4,1,4,6}));
    expectedInstances.add(new Instance(new int[]{1,6,4,4,6}));
    expectedInstances.add(new Instance(new int[]{6,4,4,1,6}));
    Set<Instance> actualInstances = Search.searchForCriticalInstances(4).keySet();

    assertEquals(expectedInstances, actualInstances);
  }
}

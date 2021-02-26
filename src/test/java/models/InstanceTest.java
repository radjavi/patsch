package models;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class InstanceTest {
  // @Test
  // public void testLowerBoundInstance() throws Exception {
  //   Instance[] expectedInstances = {
      
  //   };
  // }

  @Test
  public void testGeqToSomeIn() throws Exception {
    HashSet<Instance> setOfInstances = new HashSet<>();
    setOfInstances.add(new Instance(new int[] {4, 4, 4, 4}));
    setOfInstances.add(new Instance(new int[] {4, 2, 4, 6}));
    Instance[] instances = {
      new Instance(new int[] {1, 1, 1, 1}),
      new Instance(new int[] {6, 6, 6, 6}),
      new Instance(new int[] {4, 3, 4, 6}),
      new Instance(new int[] {3, 3, 3, 3}),
      new Instance(new int[] {4, 1, 4, 6})
    };
    boolean[] expected = {
      false,
      true,
      true,
      false,
      false
    };
    for (int i = 0; i < instances.length; i++) {
      boolean actual = instances[i].geqToSomeIn(setOfInstances) != null;
      assertEquals(instances[i].waitingTimesToString(), expected[i], actual);
    }
  }
}

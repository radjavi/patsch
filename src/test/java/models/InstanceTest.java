package models;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class InstanceTest {
 

  @Test
  public void testEquals() {
    Instance[][] instances = new Instance[][]{
      new Instance[]{new Instance(new int[] {1, 1, 1, 1}), new Instance(new int[] {1, 1, 1, 1})},
      new Instance[]{new Instance(new int[] {1, 1, 1, 1}), new Instance(new int[] {1, 1, 1, 2})},
      new Instance[]{new Instance(new int[] {1, 1, 1, 1}), new Instance(new int[] {1, 1, 2, 1})},
      new Instance[]{new Instance(new int[] {1, 1, 1, 1}), new Instance(new int[] {1, 2, 1, 1})},
      new Instance[]{new Instance(new int[] {1, 1, 1, 1}), new Instance(new int[] {2, 1, 1, 1})},
      new Instance[]{new Instance(new int[] {4, 3, 4, 6}), new Instance(new int[] {6, 4, 3, 4})},
      new Instance[]{new Instance(new int[] {4, 3, 4, 6}), new Instance(new int[] {6, 4, 2, 6})},
      new Instance[]{new Instance(new int[] {5, 3, 4, 6}), new Instance(new int[] {6, 4, 3, 6})},
      new Instance[]{new Instance(new int[] {30, 1, 1, 6, 5, 4, 3, 5, 25, 10, 25, 30, 45, 1}), new Instance(new int[] {1, 45, 30, 25, 10, 25, 5, 3, 4, 5, 6, 1, 1, 30})},
    };
    boolean[] expected = new boolean[]{
      true,
      false,
      false,
      false,
      false,
      true,
      false,
      false,
      true
    };

    for (int i = 0; i < instances.length; i++) {
      Instance[] pair = instances[i];
      assertEquals(pair[0].equals(pair[1]), expected[i]);
    }
  }

  @Test
  public void testHashCode() {
    ArrayList<Instance> instances = new ArrayList<>();
    instances.add(new Instance(new int[] {1, 1, 1, 1}));
    instances.add(new Instance(new int[] {6, 6, 6, 6}));
    instances.add(new Instance(new int[] {4, 3, 4, 6}));
    instances.add(new Instance(new int[] {30, 1, 1, 6, 5, 4, 3, 5, 25, 10, 25, 30, 45, 1}));
    ArrayList<Instance> instancesReversed = new ArrayList<>();
    for (Instance i : instances) {
      instancesReversed.add(i.getReversed());
    }
    instances.addAll(instancesReversed);

    for (Instance i1 : instances) {
      for (Instance i2 : instances) {
        if (i1.equals(i2))
          assertEquals(i1.hashCode(), i2.hashCode());
        else
          assertNotEquals(i1.hashCode(), i2.hashCode());
      }
    }
  }

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
      assertEquals(instances[i].toString(), expected[i], actual);
    }
  }
}

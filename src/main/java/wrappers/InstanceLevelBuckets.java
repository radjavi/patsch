package wrappers;

import models.*;
import java.util.*;
import java.util.concurrent.*;

public class InstanceLevelBuckets {
  private ConcurrentHashMap<Integer, Set<Instance>> buckets;

  public InstanceLevelBuckets() {
    buckets = new ConcurrentHashMap<>();
  }

  public void add(Instance instance, int level) {
    buckets.computeIfAbsent(level, l -> ConcurrentHashMap.newKeySet());
    buckets.get(level).add(instance);
  }

  public Set<Instance> get(int level) {
    return buckets.get(level);
  }

  public Set<Instance> poll(int level) {
    Set<Instance> bucket = buckets.get(level);
    buckets.remove(level);
    return bucket;
  }

  public boolean isEmpty() {
    return buckets.keySet().isEmpty();
  }

  public Set<Instance> allInstances() {
    Set<Instance> allBuckets = new HashSet<>();
    for (Set<Instance> bucket : buckets.values()) {
      allBuckets.addAll(bucket);
    }
    return allBuckets;
  }
}
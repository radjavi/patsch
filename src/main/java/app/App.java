package app;

import models.*;
import search.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        // int[] times = { 4, 2, 2, 2, 4 };
        // int[] times2 = { 5, 2, 2, 2, 4 };
        // Instance instance = new Instance(times);
        // Instance instance2 = new Instance(times2);
        // HashSet<Instance> set = new HashSet<>();
        // set.add(instance);
        // System.out.println(instance2.geqToSomeIn(set));

        HashMap<Instance, Path> criticals = Search.searchForCriticalInstances(5);
        System.out.println("----- " + criticals.size() + " CRITICAL INSTANCES -----");
        for (Instance instance : criticals.keySet()) {
            System.out.println(instance.waitingTimesToString());
        }
        // criticalInstancesForM
        // .forEach((i, p) -> System.out.println("Instance:" + i.waitingTimesToString()
        // + ",Path:" + p));
        // criticalInstanceForM = Search.searchForCriticalInstances(3);
        // System.out.println(instance.getValidGraph().toStringTriangle());
        // Path solution = instance.solve();
        // System.out.println(solution != null ? solution : "Instance is infeasible.");
    }

}

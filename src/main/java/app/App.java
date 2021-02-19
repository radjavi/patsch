package app;

import models.*;
import search.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 2, 2, 4, 2, 4 };

        // int[] times = new int[100];
        // Arrays.fill(times, 1000);
        Instance instance = new Instance(times);

        HashMap<Instance, Path> criticalInstancesForM = Search.searchForCriticalInstances(50);
        criticalInstancesForM
                .forEach((i, p) -> System.out.println("Instance:" + i.waitingTimesToString() + ",Path:" + p));
        // criticalInstanceForM = Search.searchForCriticalInstances(3);
        // System.out.println(instance.getValidGraph().toStringTriangle());
        // Path solution = instance.solve();
        // System.out.println(solution != null ? solution : "Instance is infeasible.");
    }

}

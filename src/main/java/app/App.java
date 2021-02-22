package app;

import models.*;
import search.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        // int[] times = { 2, 2, 4, 2, 4 };
        // int[] times2 = { 2, 2, 5, 2, 4 };

        HashMap<Instance, Path> critcals = Search.searchForCriticalInstances(5);
        for (Instance instance : critcals.keySet()) {
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

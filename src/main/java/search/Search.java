package search;

import models.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Search {

    public static HashMap<Instance, Path> searchForCriticalInstances(int m) throws Exception {
        LinkedList<Instance> U = new LinkedList<>();
        HashMap<Instance, Path> C = new HashMap<>();

        // INIT
        C.putAll(criticalsWithEmptyIntersection(m)); // M_0
        C.putAll(criticalsWithShortWaitingTimes(m)); // M_1
        
        // Generate lower bound instances
        HashSet<Instance> lowerBoundInstances = lowerBoundInstances(C, m);
        for (Instance lowerBoundInstance : lowerBoundInstances) {
            U.add(lowerBoundInstance);
        }
        
        System.out.println("-------- C --------");
        C.keySet().forEach(i -> System.out.println(i.waitingTimesToString()));
        System.out.println("-------- U --------");
        U.forEach(i -> System.out.println(i.waitingTimesToString()));
        System.out.println("-------------------");
        // SEARCH
        HashSet<Instance> visitedInstances = new HashSet<>();
        while (!U.isEmpty())
        {
            //System.out.println(U.size() + " " + visitedInstances.size());
            Instance u = U.pop();
            //System.out.println(u.waitingTimesToString());
            Path solvedU = u.solve();
            System.out.print(u.waitingTimesToString() + ": ");
            System.out.println(solvedU != null ? "feasible" : "infeasible");
            if (solvedU != null && !u.geqToSomeIn(C.keySet())) {
                C.put(u, solvedU);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                System.out.println("[" + dtf.format(LocalDateTime.now()) + "] found critical: " + u.waitingTimesToString());
            }
            else {
                for (int i = 0; i <= m; i++) {
                    if (u.getWaitingTimes()[i] == 2*m)
                        continue;
                    int[] newWaitingTimes = u.getWaitingTimes().clone();
                    newWaitingTimes[i]++;
                    Instance v = new Instance(newWaitingTimes);

                    if (!visitedInstances.contains(v) && !v.geqToSomeIn(C.keySet())) {
                        U.add(v);
                        visitedInstances.add(v);
                    }
                }
            }
        }

        return C;

    }

    private static HashSet<Instance> lowerBoundInstances(HashMap<Instance, Path> C, int m) {
        HashSet<Instance> allLowerBoundInstances = new HashSet<>();
        for (int b = 0; b <= m; b++) {
            for (int a = 0; a <= b; a++) {
                Instance g = Instance.lowerBoundInstance(m, a, b);
                if (!g.geqToSomeIn(C.keySet())) {
                    allLowerBoundInstances.add(g);
                    //System.out.println(a + ", " + b + ": " + g.waitingTimesToString());
                }
            }
        }
        // Remove large lower bound instances
        // May be improved (see mail)
        HashSet<Instance> lowerBoundInstances = new HashSet<>();
        for (Instance lowerBoundInstance : allLowerBoundInstances) {
            HashSet<Instance> comparisonSet = (HashSet<Instance>) allLowerBoundInstances.clone();
            comparisonSet.remove(lowerBoundInstance);
            if (!lowerBoundInstance.geqToSomeIn(comparisonSet)) 
                lowerBoundInstances.add(lowerBoundInstance);
        }
        return lowerBoundInstances;
    }

    private static HashMap<Instance, Path> criticalsWithEmptyIntersection(int m) throws Exception {
        if (m < 4)
            return null; // may be improved.

        HashMap<Instance, Path> C = new HashMap<>();
        for (int d = 0; d <= m - 1; d++) {
            int[] waitingTimes = new int[m + 1];
            for (int i = 0; i <= m; i++) {
                if (i <= d)
                    waitingTimes[i] = Math.max(1, 2 * Math.max(i, d - i));
                else
                    waitingTimes[i] = Math.max(1, 2 * Math.max(i - d - 1, m - i));
            }
            Instance instance = new Instance(waitingTimes);
            Path solution = instance.billiardBallPath(d);
            C.put(instance, solution);
        }
        return C;
    }

    private static HashMap<Instance, Path> criticalsWithShortWaitingTimes(int m) throws Exception {
        HashMap<Instance, Path> C = new HashMap<>();
        if (m < 5) return C;

        // TODO: Theorem 1

        return C;
    }
}

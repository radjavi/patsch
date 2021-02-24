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
        C.forEach((i, s) -> {
            System.out.println(i.waitingTimesToString() + ": " + s);
        });
        System.out.println("-------- U --------");
        U.forEach(i -> System.out.println(i.waitingTimesToString()));
        System.out.println("-------------------");
        // SEARCH
        HashSet<int[]> visitedInstances = new HashSet<>();
        while (!U.isEmpty())
        {
            System.out.println(U.size() + " visited:" + visitedInstances.size());
            Instance u = U.pop();
            //System.out.println(u.waitingTimesToString());
            Path solvedU = u.solve();
            //System.out.print(u.waitingTimesToString() + ": ");
            //System.out.println(solvedU != null ? "feasible" : "infeasible");
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
                    int [] cloned = newWaitingTimes.clone();
                    for(int c = 0; c < cloned.length / 2; c++)
                    {
                        int temp = cloned[c];
                        cloned[c] = cloned[cloned.length - c - 1];
                        cloned[cloned.length - c - 1] = temp;
                    }
                    //Instance vReverse = new Instance(cloned);
                   System.out.println(""+newWaitingTimes.toString() + cloned);
                    if (!visitedInstances.contains(newWaitingTimes) && !v.geqToSomeIn(C.keySet()) && !visitedInstances.contains(cloned) ) {
                        U.add(v);
                        visitedInstances.add(newWaitingTimes);
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
            Instance instance = criticalWithEmptyIntersection(m, d);
            Path solution = instance.billiardBallPath(d);
            C.put(instance, solution);
        }
        return C;
    }

    public static Instance criticalWithEmptyIntersection(int m, int d) {
        int[] waitingTimes = new int[m + 1];
        for (int i = 0; i <= m; i++) {
            if (i <= d)
                waitingTimes[i] = Math.max(1, 2 * Math.max(i, d - i));
            else
                waitingTimes[i] = Math.max(1, 2 * Math.max(i - d - 1, m - i));
        }
        return new Instance(waitingTimes);
    }

    private static HashMap<Instance, Path> criticalsWithShortWaitingTimes(int m) throws Exception {
        HashMap<Instance, Path> C = new HashMap<>();
        if (m < 5) return C;

        // t_k = 1 for 1 <= k <= m-1
        for (int k = 1; k < m; k++) {
            int[] waitingTimes = new int[m + 1];
            for (int i = 0; i <= m; i++) {
                if (i < k)
                    waitingTimes[i] = 2 * Math.max(i, m - i - 1);
                else if (i == k)
                    waitingTimes[i] = 1;
                else
                    waitingTimes[i] = 2 * Math.max(i - 1, m - i);
            }
            Instance instance = new Instance(waitingTimes);
            Path solution = instance.solve();
            C.put(instance, solution);
        }

        // t_k = t_{k+1} = 2 for 1 <= k < k+1 <= m-1
        for (int k = 1; k < m; k++) {
            int[] waitingTimes = new int[m + 1];
            for (int i = 0; i <= m; i++) {
                if (i < k)
                    waitingTimes[i] = 2 * Math.max(i, m - i - 2);
                else if (i == k)
                    waitingTimes[i] = 2;
                else if (i == k + 1)
                    waitingTimes[i] = 2;
                else
                    waitingTimes[i] = 2 * Math.max(i - 2, m - i);
            }
            Instance instance = new Instance(waitingTimes);
            Path solution = instance.solve();
            C.put(instance, solution);
        }

        // t_k = 2 && t_{k+1} = 3 for 1 <= k <= (m-1)/2
        for (int k = 1; k <= (m - 1) / 2; k++) {
            int[] waitingTimes = new int[m + 1];
            if (k == 1) {
                waitingTimes[0] = 2 * (m - 3);
                waitingTimes[1] = 2;
                waitingTimes[2] = 3;
                for (int i = 3; i <= m; i++) {
                    waitingTimes[i] = 2 * Math.max(i - 2, m - i);
                }
            } else {
                for (int i = 0; i <= m; i++) {
                    if (i <= k - 2)
                        waitingTimes[i] = 2 * Math.max(i, m - i - 1);
                    else if (i == k - 1)
                        waitingTimes[i] = 2 * Math.max(k - 1, m - k - 1);
                    else if (i == k)
                        waitingTimes[i] = 2;
                    else if (i == k + 1)
                        waitingTimes[i] = 3;
                    else
                        waitingTimes[i] = 2 * Math.max(i - 1, m - i);
                }
            }
            Instance instance = new Instance(waitingTimes);
            Path solution = instance.solve();
            C.put(instance, solution);
        }

        // TODO:

        // TODO: 

        return C;
    }
}

package search;

import models.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Search {

    /**
     * @param m
     * @return
     * @throws Exception
     */
    public static HashMap<Instance, Path> searchForCriticalInstances(int m) throws Exception {
        LinkedList<Instance> U = new LinkedList<>();
        HashMap<Instance, Path> C = new HashMap<>();
        int r = 2 * m;

        // INIT
        C.putAll(criticalsWithEmptyIntersection(m)); // M_0
        // C.putAll(criticalsWithShortWaitingTimes(m)); // M_1

        // Generate lower bound instances
        HashSet<Instance> lowerBoundInstances = lowerBoundInstances(C, m);
        for (Instance lowerBoundInstance : lowerBoundInstances) {
            if (lowerBoundInstance.geqToSomeIn(C.keySet()) == null)
                U.add(lowerBoundInstance);
        }

        // Generate a stock of instances
        HashSet<Instance> maximalInfeasibleInstances = generateStockOfInstances(m, r);

        System.out.println("-------- C --------");
        C.forEach((i, s) -> {
            System.out.println(i.waitingTimesToString() + ": " + s);
        });
        System.out.println("-------- U --------");
        U.forEach(i -> System.out.println(i.waitingTimesToString()));
        System.out.println("-------------------");
        // SEARCH
        HashSet<Instance> visitedInstances = new HashSet<>();
        while (!U.isEmpty()) {
            System.out.println(U.size() + " visited:" + visitedInstances.size());
            Instance u = new Instance(U.pop().getWaitingTimes());
            if (u.geqToSomeIn(C.keySet()) != null)
                continue;
            // System.out.println(u.waitingTimesToString());
            Path solvedU = u.solve();
            // System.out.print(u.waitingTimesToString() + ": ");
            // System.out.println(solvedU != null ? "feasible" : "infeasible");
            if (solvedU != null) {
                C.put(u, solvedU);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                System.out.println("[" + dtf.format(LocalDateTime.now()) + "] found critical: "
                        + u.waitingTimesToString());
            } else {
                //Instance greaterInfeasible = u.lessThanSomeIn(maximalInfeasibleInstances);
                Instance greaterInfeasible = null;
                for (Instance greater : maximalInfeasibleInstances) {
                    if (u.lessThan(greater)) {
                        greaterInfeasible = greater;
                        break;
                    }
                }
                for (int i = 0; i <= m; i++) {
                    if (greaterInfeasible != null && greaterInfeasible.getWaitingTimes()[i] == r)
                        continue;
                    if (u.getWaitingTimes()[i] == r)
                        continue;
                    int[] newWaitingTimes = u.getWaitingTimes().clone();
                    newWaitingTimes[i]++;
                    Instance v = new Instance(newWaitingTimes);

                    if (!v.inSet(visitedInstances) && v.geqToSomeIn(C.keySet()) == null) {
                        U.add(v);
                        visitedInstances.add(v);
                    }
                }
            }
        }

        // Add reversed critical instances
        HashMap<Instance, Path> CReversed = new HashMap<>();
        for (Instance critical : C.keySet()) {
            Instance criticalReversed = critical.getReversed();
            if (!critical.equals(criticalReversed)) {
                Path solution = criticalReversed.solve();
                CReversed.put(criticalReversed, solution);
            }
        }
        C.putAll(CReversed);

        return C;

    }

    public static HashSet<Instance> generateStockOfInstances(int m, int r) throws Exception {
        HashSet<Instance> maximalInfeasibleInstances = new HashSet<>();
        HashSet<Instance> visitedInstances = new HashSet<>();
        LinkedList<Instance> U = new LinkedList<>();
        // Initialize U with [r,...,r].
        int[] allR = new int[m + 1];
        Arrays.fill(allR, r);
        Instance allRInstance = new Instance(allR);
        U.add(allRInstance);
        visitedInstances.add(allRInstance);

        while (!U.isEmpty()) {
            System.out.println(maximalInfeasibleInstances.size() + ", " + U.size());
            // if (maximalInfeasibleInstances.size() > 10) {
            //     break;
            // }
            Instance u = U.pop();
            Path solution = u.solve();
            if (solution != null) {
                for (int i = 0; i <= m; i++) {
                    if (u.getWaitingTimes()[i] != r)
                        continue;
                    int[] waitingTimes = u.getWaitingTimes().clone();
                    waitingTimes[i] = 1;
                    Instance newInstance = new Instance(waitingTimes);
                    if (!newInstance.inSet(visitedInstances)) {
                        U.add(newInstance);
                        visitedInstances.add(newInstance);
                        //System.out.println(newInstance.waitingTimesToString());
                    }
                }
            } else {
                if (u.lessThanSomeIn(maximalInfeasibleInstances) != null)
                    continue;
                Instance smallerInfeasible = u.geqToSomeIn(maximalInfeasibleInstances);
                while (smallerInfeasible != null) {
                    maximalInfeasibleInstances.remove(smallerInfeasible);
                    smallerInfeasible = u.geqToSomeIn(maximalInfeasibleInstances);
                }
                maximalInfeasibleInstances.add(u);
                for (int i = 0; i <= m; i++) {
                    if (u.getWaitingTimes()[i] == r)
                        continue;
                    int[] waitingTimes = u.getWaitingTimes().clone();
                    waitingTimes[i]++;
                    Instance newInstance = new Instance(waitingTimes);
                    if (!newInstance.inSet(visitedInstances)) {
                        U.add(newInstance);
                        visitedInstances.add(newInstance);
                        //System.out.println(newInstance.waitingTimesToString());
                    }
                }
            }
        }
        maximalInfeasibleInstances.forEach(inf -> System.out.println(inf.waitingTimesToString()));

        return maximalInfeasibleInstances;
    }

    private static HashSet<Instance> lowerBoundInstances(HashMap<Instance, Path> C, int m) {
        HashSet<Instance> allLowerBoundInstances = new HashSet<>();
        for (int b = 0; b <= m; b++) {
            for (int a = 0; a <= b; a++) {
                Instance g = Instance.lowerBoundInstance(m, a, b);
                if (g.geqToSomeIn(C.keySet()) == null && !g.inSet(allLowerBoundInstances)) {
                    allLowerBoundInstances.add(g);
                }
            }
        }
        // Remove large lower bound instances
        // May be improved (see mail)
        HashSet<Instance> lowerBoundInstances = new HashSet<>();
        for (Instance lowerBoundInstance : allLowerBoundInstances) {
            HashSet<Instance> comparisonSet = (HashSet<Instance>) allLowerBoundInstances.clone();
            comparisonSet.remove(lowerBoundInstance);
            if (lowerBoundInstance.geqToSomeIn(comparisonSet) == null)
                lowerBoundInstances.add(lowerBoundInstance);
        }
        return lowerBoundInstances;
    }

    private static HashMap<Instance, Path> criticalsWithEmptyIntersection(int m) throws Exception {
        HashMap<Instance, Path> C = new HashMap<>();

        if (m < 2)
            return C; // may be improved.

        for (int d = 0; d <= m - 1; d++) {
            Instance instance = criticalWithEmptyIntersection(m, d);
            Path solution = instance.billiardBallPath(d);
            if (!instance.inSet(C.keySet()))
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
        if (m < 5)
            return C;

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
                        waitingTimes[i] = 2 * Math.max(k - 1, m == 5 ? m - k - 1 : m - k - 2);
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

        // t_k = t_{k+1} = 3 for 2 <= k < k+1 <= m-2
        for (int k = 2; k <= m - 3; k++) {
            int[] waitingTimes = new int[m + 1];
            if (k == 2) {
                waitingTimes[0] = 2 * (m - 3);
                waitingTimes[1] = 2 * (m - 4);
                waitingTimes[2] = 3;
                waitingTimes[3] = 3;
                for (int i = 3; i <= m; i++) {
                    waitingTimes[i] = 2 * Math.max(i - 2, m - i);
                }
            } else {
                for (int i = 0; i <= m; i++) {
                    if (i <= k - 1)
                        waitingTimes[i] = 2 * Math.max(i, m - i - 3);
                    else if (i == k)
                        waitingTimes[i] = 3;
                    else if (i == k + 1)
                        waitingTimes[i] = 3;
                    else
                        waitingTimes[i] = 2 * Math.max(i - 3, m - i);
                }
            }
            Instance instance = new Instance(waitingTimes);
            Path solution = instance.solve();
            C.put(instance, solution);
        }

        // t_k = 2 && t_{k-1} = t_{k+1} = 4 for 2 <= k <= m-2
        if (m > 5) {
            for (int k = 2; k <= m - 2; k++) {
                int[] waitingTimes = new int[m + 1];
                for (int i = 0; i <= m; i++) {
                    if (i <= k - 2)
                        waitingTimes[i] = 2 * Math.max(i, m - i - 2);
                    else if (i == k - 1)
                        waitingTimes[i] = 4;
                    else if (i == k)
                        waitingTimes[i] = 2;
                    else if (i == k + 1)
                        waitingTimes[i] = 4;
                    else
                        waitingTimes[i] = 2 * Math.max(i - 2, m - i);
                }
                Instance instance = new Instance(waitingTimes);
                Path solution = instance.solve();
                C.put(instance, solution);
            }
        }

        return C;
    }
}

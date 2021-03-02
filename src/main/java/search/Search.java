package search;

import models.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Search {
    private static final Logger logger = LogManager.getLogger(Search.class);

    /**
     * @param m
     * @return
     * @throws Exception
     */
    public static HashMap<Instance, Path> searchForCriticalInstances(int m, ExecutorService executor, int nrThreads) throws Exception {
        if (m < 2)
            return null;
        LinkedList<Instance> U = new LinkedList<>();
        HashMap<Instance, Path> C = new HashMap<>();
        int r = 2 * m;

        // INIT
        logger.info("Generating M_0...");
        HashMap<Instance, Path> m_0 = criticalsWithEmptyIntersection(m);
        logger.debug("Generated M_0 - {} instances", m_0.size());
        C.putAll(m_0); // M_0
        logger.trace("-------- C --------");
        C.forEach((i, s) -> {
            logger.trace(i.waitingTimesToString() + ": " + s);
        });
        logger.trace("-------------------");
        // C.putAll(criticalsWithShortWaitingTimes(m)); // M_1

        // Generate lower bound instances
        logger.info("Generating lower bound instances...");
        HashSet<Instance> lowerBoundInstances = lowerBoundInstances(C, m);
        for (Instance lowerBoundInstance : lowerBoundInstances) {
            if (lowerBoundInstance.geqToSomeIn(C.keySet()) == null)
                U.add(lowerBoundInstance);
        }
        logger.debug("Proceeding with {} lower bound instances", U.size());
        logger.trace("-------- U --------");
        U.forEach(i -> logger.trace(i.waitingTimesToString()));
        logger.trace("-------------------");

        // Generate a stock of instances
        logger.info("Generating maximal infeasible instances...");
        HashSet<Instance> maximalInfeasibleInstances = generateStockOfInstances(m, r, executor, nrThreads);
        logger.debug("Generated {} maximal infeasible instances",
                maximalInfeasibleInstances.size());
        logger.trace("---- Maximal Infeasible Instances ----");
        maximalInfeasibleInstances.forEach(i -> logger.trace(i.waitingTimesToString()));
        logger.trace("--------------------------------------");

        // SEARCH
        logger.info("Searching for critical instances...");
        HashSet<Instance> visitedInstances = new HashSet<>();
        while (!U.isEmpty()) {
            Instance u = new Instance(U.pop().getWaitingTimes());
            Instance uR = u.getReversed();
            if (u.geqToSomeIn(C.keySet()) != null)
                continue;
            Path solvedU = u.solveParallel(executor, nrThreads);
            if (solvedU != null) {
                C.put(u, solvedU);
                logger.info("Found critical instance {}: {}", u.waitingTimesToString(), solvedU);
            } else {
                // Instance greaterInfeasible = u.lessThanSomeIn(maximalInfeasibleInstances);
                Instance greaterInfeasible = null;
                Instance referenceInstance = u;
                for (Instance greater : maximalInfeasibleInstances) {
                    if (u.lessThan(greater)) {
                        greaterInfeasible = greater;
                        break;
                    } else if (uR.lessThan(greater)) {
                        greaterInfeasible = greater;
                        referenceInstance = uR;
                        break;
                    }
                }
                for (int i = 0; i <= m; i++) {
                    if (greaterInfeasible != null && greaterInfeasible.getWaitingTimes()[i] == r)
                        continue;
                    if (referenceInstance.getWaitingTimes()[i] == r)
                        continue;
                    int[] newWaitingTimes = referenceInstance.getWaitingTimes().clone();
                    newWaitingTimes[i]++;
                    Instance v = new Instance(newWaitingTimes);
                    Instance vR = v.getReversed();

                    if (!visitedInstances.contains(v) && !visitedInstances.contains(vR)
                            && v.geqToSomeIn(C.keySet()) == null) {
                        U.add(v);
                        visitedInstances.add(v);
                    }
                }
            }
        }
        logger.info("Found critical instances");

        // Add reversed critical instances
        logger.info("Creating reversed critical instances...");
        HashMap<Instance, Path> CReversed = new HashMap<>();
        for (Instance critical : C.keySet()) {
            Instance criticalReversed = critical.getReversed();
            if (!critical.equals(criticalReversed)) {
                Path solution = criticalReversed.solveParallel(executor, nrThreads);
                CReversed.put(criticalReversed, solution);
            }
        }
        C.putAll(CReversed);

        // Test if critical
        logger.info("Testing if instances are critical...");
        for (Instance i : C.keySet()) {
            assert i.isCritical() : i.waitingTimesToString() + " is NOT critical.";
        }

        logger.info("----- {} CRITICAL INSTANCES -----", C.size());
        C.forEach((i, s) -> {
            logger.info("{}: {}", i.waitingTimesToString(), s);
        });
        logger.info("---------------------------------");

        return C;

    }

    public static HashSet<Instance> generateStockOfInstances(int m, int r, ExecutorService executor, int nrThreads) throws Exception {
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
            //logger.info("maximal size: {}, visited size: {}", maximalInfeasibleInstances.size(), visitedInstances.size());
            Instance u = U.pop();
            //logger.info("Solving {}...", u.waitingTimesToString());
            Path solution = u.solveParallel(executor, nrThreads);
            if (solution != null) {
                for (int i = 0; i <= m; i++) {
                    if (u.getWaitingTimes()[i] != r)
                        continue;
                    int[] waitingTimes = u.getWaitingTimes().clone();
                    waitingTimes[i] = 1;
                    Instance newInstance = new Instance(waitingTimes);
                    Instance newInstanceR = newInstance.getReversed();
                    if (!visitedInstances.contains(newInstance)
                            && !visitedInstances.contains(newInstanceR)) {
                        U.add(newInstance);
                        visitedInstances.add(newInstance);
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
                    Instance newInstanceR = newInstance.getReversed();
                    if (!visitedInstances.contains(newInstance)
                            && !visitedInstances.contains(newInstanceR)) {
                        U.add(newInstance);
                        visitedInstances.add(newInstance);
                    }
                }
            }
        }

        return maximalInfeasibleInstances;
    }

    private static HashSet<Instance> lowerBoundInstances(HashMap<Instance, Path> C, int m) {
        HashSet<Instance> allLowerBoundInstances = new HashSet<>();
        for (int b = 0; b <= m; b++) {
            for (int a = 0; a <= b; a++) {
                Instance g = Instance.lowerBoundInstance(m, a, b);
                Instance gR = g.getReversed();
                if (g.geqToSomeIn(C.keySet()) == null && !allLowerBoundInstances.contains(g)
                        && !allLowerBoundInstances.contains(gR)) {
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
            Instance instanceR = instance.getReversed();
            Path solution = instance.billiardBallPath(d);
            Set<Instance> criticals = C.keySet();
            if (!criticals.contains(instance) && !criticals.contains(instanceR))
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

    private static HashMap<Instance, Path> criticalsWithShortWaitingTimes(int m, ExecutorService executor, int nrThreads) throws Exception {
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
            Path solution = instance.solveParallel(executor, nrThreads);
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
            Path solution = instance.solveParallel(executor, nrThreads);
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
            Path solution = instance.solveParallel(executor, nrThreads);
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
            Path solution = instance.solveParallel(executor, nrThreads);
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
                Path solution = instance.solveParallel(executor, nrThreads);
                C.put(instance, solution);
            }
        }

        return C;
    }
}

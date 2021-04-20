package search;

import models.*;
import wrappers.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.common.primitives.Ints;
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class Search {
    final static Level RESULT = Level.forName("RESULT", 350);
    private static final Logger logger = LogManager.getLogger(Search.class);

    public static Map<Instance, Path> searchForCriticalInstances(int m, int r) throws Exception {
        SingleExecutor executor = SingleExecutor.getInstance();
        if (executor == null) {
            return searchForCriticalInstancesSequential(m, r);
        }
        return searchForCriticalInstancesParallel(m, r);
    }

    /**
     * @param m
     * @return
     * @throws Exception
     */
    public static Map<Instance, Path> searchForCriticalInstancesSequential(int m, int r) throws Exception {
        if (m < 2)
            return null;
        LinkedList<Instance> U = new LinkedList<>();
        HashMap<Instance, Path> C = new HashMap<>();
        HashSet<Instance> visitedInstances = new HashSet<>();

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
            Path solution = new Instance(lowerBoundInstance.getWaitingTimes()).solve();
            if (solution != null)
                C.put(lowerBoundInstance, solution);
            else if (lowerBoundInstance.geqToSomeIn(C.keySet()) == null) {
                U.add(lowerBoundInstance);
            }
        }
        logger.debug("Proceeding with {} lower bound instances", U.size());
        logger.trace("-------- U --------");
        U.forEach(i -> logger.trace(i.waitingTimesToString()));
        logger.trace("-------------------");

        // Generate a stock of instances
        logger.info("Generating maximal infeasible instances...");
        HashSet<Instance> maximalInfeasibleInstances = generateMaximalInfeasible(m, r);
        logger.debug("Generated {} maximal infeasible instances",
                maximalInfeasibleInstances.size());
        logger.trace("---- Maximal Infeasible Instances ----");
        maximalInfeasibleInstances.forEach(i -> logger.trace(i.waitingTimesToString()));
        logger.trace("--------------------------------------");

        // SEARCH
        logger.info("Searching for critical instances...");
        while (!U.isEmpty()) {
            Instance u = U.pop();
            Instance uR = u.getReversed();
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
            ArrayList<Instance> vs = new ArrayList<>(m + 1);
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
                    vs.add(v);
                    visitedInstances.add(v);
                }
            }
            for (Instance v : vs) {
                Path solution = new Instance(v.getWaitingTimes()).solve();
                if (solution != null) {
                    C.put(v, solution);
                    logger.info("Found critical instance {}: {}", v.waitingTimesToString(),
                            solution);
                } else
                    U.add(v);
            }
        }
        logger.info("Found critical instances");

        // Add reversed critical instances
        logger.info("Creating reversed critical instances...");
        HashMap<Instance, Path> CReversed = new HashMap<>();
        for (Instance critical : C.keySet()) {
            Instance criticalReversed = critical.getReversed();
            if (!critical.equals(criticalReversed)) {
                Path solution = criticalReversed.solve();
                CReversed.put(criticalReversed, solution);
            }
        }
        C.putAll(CReversed);

        // Test if critical
        logger.info("Testing if instances are critical...");
        for (Instance i : C.keySet()) {
            boolean critical = i.isCritical();
            if (!critical)
                logger.trace("{} is NOT critical", i.waitingTimesToString());
            assert critical : i.waitingTimesToString() + " is NOT critical.";
        }

        logger.info("----- {} CRITICAL INSTANCES -----", C.size());
        logger.log(RESULT, "m={}", m);
        C.forEach((i, s) -> {
            //logger.info("{}: {}", i.waitingTimesToString(), s);
            logger.log(RESULT, "{} {}", i.waitingTimesToString(), s);
        });
        logger.info("---------------------------------");

        return C;

    }

    public static Map<Instance, Path> searchForCriticalInstancesParallel(int m, int r) throws Exception {
        if (m < 2)
            return null;
        InstanceLevelBuckets U = new InstanceLevelBuckets();
        ConcurrentHashMap<Instance, Path> C = new ConcurrentHashMap<>();
        Set<Instance> visitedInstances = ConcurrentHashMap.newKeySet();

        // INIT
        logger.info("Generating M_0...");
        HashMap<Instance, Path> m_0 = criticalsWithEmptyIntersection(m);
        logger.debug("Generated M_0 - {} instances", m_0.size());
        C.putAll(m_0); // M_0
        // C.putAll(criticalsWithShortWaitingTimes(m)); // M_1

        // Generate lower bound instances
        logger.info("Generating lower bound instances...");
        HashSet<Instance> lowerBoundInstances = lowerBoundInstances(C, m);
        for (Instance lowerBoundInstance : lowerBoundInstances) {
            Path solution = new Instance(lowerBoundInstance.getWaitingTimes()).solve();
            if (solution != null)
                C.put(lowerBoundInstance, solution);
            else if (lowerBoundInstance.geqToSomeIn(C.keySet()) == null) {
                U.add(lowerBoundInstance);
            }
        }
        //logger.debug("Proceeding with {} lower bound instances", U.allInstances().size());
        logger.trace("-------- C --------");
        C.forEach((i, s) -> {
            logger.trace(i.waitingTimesToString() + ": " + s);
        });
        //logger.trace("-------------------");
        logger.trace("-------- U --------");
        U.allInstances().forEach(i -> logger.trace(i.waitingTimesToString()));
        logger.trace("-------------------");

        // Generate a stock of instances
        logger.info("Generating maximal infeasible instances...");
        HashSet<Instance> maximalInfeasibleInstances = generateMaximalInfeasible(m, r);
        logger.debug("Generated {} maximal infeasible instances",
                maximalInfeasibleInstances.size());
        logger.trace("---- Maximal Infeasible Instances ----");
        maximalInfeasibleInstances.forEach(i -> logger.trace(i.waitingTimesToString()));
        logger.trace("--------------------------------------");

        // SEARCH
        SingleExecutor executor = SingleExecutor.getInstance();
        logger.info("Searching for critical instances...");
        int level = 0;
        while (!U.isEmpty()) {
            Set<Instance> levelInstances = U.poll(level);
            if (levelInstances == null) {
                level++;
                continue;
            }
            logger.info("Level: {}, Size of U: {}", level, levelInstances.size());
            ArrayList<Callable<List<Instance>>> callables = new ArrayList<>();
            for (Instance u : levelInstances) {
                callables.add(new ParallelSearchWorker(u, C, maximalInfeasibleInstances,
                        visitedInstances, m, r));
            }
            List<Future<List<Instance>>> futures = executor.getExecutor().invokeAll(callables);
            for (Future<List<Instance>> future : futures) {
                for (Instance i : future.get()) {
                    U.add(i);
                }
            }
            level++;
        }
        logger.info("Found critical instances");

        // Add reversed critical instances
        logger.info("Creating reversed critical instances...");
        HashMap<Instance, Path> CReversed = new HashMap<>();
        for (Instance critical : C.keySet()) {
            Instance criticalReversed = critical.getReversed();
            if (!critical.equals(criticalReversed)) {
                Path solution = criticalReversed.solve();
                CReversed.put(criticalReversed, solution);
            }
        }
        C.putAll(CReversed);

        // Test if critical
        logger.info("Testing if instances are critical...");
        for (Instance i : C.keySet()) {
            boolean critical = i.isCritical();
            if (!critical)
                logger.trace("{} is NOT critical", i.waitingTimesToString());
            assert critical : i.waitingTimesToString() + " is NOT critical.";
        }

        printResults(C, m, r);

        return C;

    }

    public static HashSet<Instance> generateMaximalInfeasible(int m, int r) throws Exception {
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
            // logger.info("maximal size: {}, visited size: {}",
            // maximalInfeasibleInstances.size(), visitedInstances.size());
            Instance u = U.pop();
            //logger.info("{}", u.waitingTimesToString());
            Path solution = u.solve();
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

    private static HashSet<Instance> lowerBoundInstances(Map<Instance, Path> C, int m) {
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
            Path solution = InstanceSolver.billiardBallPath(instance, d);
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

    private static void printResults(Map<Instance, Path> C, int m, int r) {
        Instance[] sortedInstances = C.keySet().toArray(new Instance[0]);
        Arrays.sort(sortedInstances, (i1, i2) -> {
            int min1 = Ints.min(i1.getWaitingTimes());
            int min2 = Ints.min(i2.getWaitingTimes());
            if (min1 < min2) return -1;
            else if (min1 > min2) return 1;
            else {
                int length1 = i1.getB() - i1.getA();
                int length2 = i2.getB() - i2.getA();
                if (length1 < length2) return -1;
                else if (length1 > length2) return 1;
                else return 0;
            }
        });
        logger.log(RESULT, "m={}, r={}", m, r);
        logger.log(RESULT, "----- {} CRITICAL INSTANCES -----", C.size());
        int currentTime = 0;
        for (Instance critical : sortedInstances) {
            int minTime = Ints.min(critical.getWaitingTimes());
            if (minTime > currentTime) {
                currentTime = minTime;
                logger.log(RESULT, "-- Minimum Waiting Time: {} --", currentTime);
            }
            Path solution = C.get(critical);
            int a = critical.getA();
            int b = critical.getB();
            String intervalString = a > b ? "[]" : "[" + a + "," + b + "]";
            logger.log(RESULT, "{} {} {}", critical.waitingTimesToString(), intervalString, solution);
        }
        logger.info("---------------------------------");
    }

    private static class ParallelSearchWorker implements Callable<List<Instance>> {
        private final Instance u;
        private final Map<Instance, Path> C;
        private final Set<Instance> maximalInfeasibleInstances;
        private final Set<Instance> visitedInstances;
        private final int m;
        private final int r;

        public ParallelSearchWorker(Instance u, Map<Instance, Path> C,
                Set<Instance> maximalInfeasibleInstances, Set<Instance> visitedInstances, int m,
                int r) {
            this.u = u;
            this.C = C;
            this.maximalInfeasibleInstances = maximalInfeasibleInstances;
            this.visitedInstances = visitedInstances;
            this.m = m;
            this.r = r;
        }

        @Override
        public List<Instance> call() throws Exception {
            List<Instance> U = new ArrayList<>();
            // Instance u = U.poll();
            Instance uR = u.getReversed();
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
            ArrayList<Instance> vs = new ArrayList<>(m + 1);
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
                    vs.add(v);
                    visitedInstances.add(v);
                }
            }
            for (Instance v : vs) {
                Path solution = new Instance(v.getWaitingTimes()).solve();
                if (solution != null) {
                    C.put(v, solution);
                    logger.info("Found critical instance {}: {}", v.waitingTimesToString(),
                            solution);
                } else
                    U.add(v);
            }
            return U;
        }
    }
}

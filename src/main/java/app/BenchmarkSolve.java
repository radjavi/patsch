package app;

import search.*;
import models.*;
import wrappers.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class BenchmarkSolve {
    final static Level RESULT = Level.forName("RESULT", 350);
    private static final Logger logger = LogManager.getLogger(BenchmarkSolve.class);

    public static void main(String[] args) throws Exception {
        int m = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        if (m < 2) {
            logger.fatal("m must be greater or equal to 2");
            return;
        }

        int r = args.length > 1 ? Integer.parseInt(args[1]) : 2 * m;
        if (r < 1) {
            logger.fatal("Roof value must be greater than 1");
            return;
        }

        int nrOfInstances = args.length > 2 ? Integer.parseInt(args[2]) : 1000;
        if (nrOfInstances < 1) {
            logger.fatal("Number of instances must be greater than 0");
            return;
        }

        int nrThreads = args.length > 3 ? Integer.parseInt(args[3]) : 1;
        if (nrThreads < 1) {
            logger.fatal("Number of threads must be greater than 0");
            return;
        }

        AtomicInteger infeasible = new AtomicInteger(0);
        SingleExecutor executor = SingleExecutor.init(nrThreads);

        ArrayList<Callable<Void>> runnables = new ArrayList<>();
        for (int i = 0; i < nrThreads; i++) {
            runnables.add(new ParallelBenchWorker(infeasible, nrOfInstances, m, r, i));
        }
        executor.getExecutor().invokeAll(runnables);
        if (executor != null)
            executor.shutdown();

    }

    private static class ParallelBenchWorker implements Callable<Void> {
        private AtomicInteger nrOfInfeasible;
        private final int nrOfInstances;
        private final int m;
        private final int r;
        private final int seed;

        public ParallelBenchWorker(AtomicInteger nrOfInfeasible, int nrOfInstances, int m, int r, int seed) {
            this.nrOfInfeasible = nrOfInfeasible;
            this.nrOfInstances = nrOfInstances;
            this.m = m;
            this.r = r;
            this.seed = seed;
        }

        @Override
        public Void call() throws Exception {
            Random random = new Random(seed);
            while (nrOfInfeasible.get() < nrOfInstances) {
                int[] waitingTimes = random.ints(m + 1, 1, r + 1).toArray();
                Instance instance = new Instance(waitingTimes);
                int a = instance.getA();
                int b = instance.getB();
                if (a > b || (a == 0 && b == m) || (a == 0 && b == 0) || (a == m && b == m))
                    continue;
                long before = System.nanoTime();
                AtomicInteger nrOfPaths = new AtomicInteger(0);
                Path sol = instance.solve(nrOfPaths);
                if (sol == null) {
                    nrOfInfeasible.incrementAndGet();
                    logger.log(RESULT, "{} {} {} {}", instance.waitingTimesToString(),
                            sol != null ? "feasible" : "infeasible", (System.nanoTime() - before) * 1E-6, nrOfPaths);

                }
            }
            return null;
        }
    }
}
package app;

import search.*;
import models.*;
import wrappers.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
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

        int nrFeasible = 0;
        int nrInfeasible = 0;
        SingleExecutor executor = SingleExecutor.init(nrThreads);
        Random random = new Random(1);

        while (nrFeasible < nrOfInstances || nrInfeasible < nrOfInstances) {
            ArrayList<Callable<Path>> callables = new ArrayList<>(1000);
            for (int i = 0; i < 1000; i++) {
                int[] waitingTimes = random.ints(m + 1, 1, r + 1).toArray();
                Instance instance = new Instance(waitingTimes);
                int a = instance.getA();
                int b = instance.getB();
                if (a > b || (a == 0 && b == m) || (a == 0 && b == 0) || (a == m && b == m))
                    continue;
                callables.add(new ParallelBenchWorker(instance, nrFeasible, nrInfeasible, nrOfInstances));
            }

            List<Future<Path>> futures = executor.getExecutor().invokeAll(callables);
            for (Future<Path> future : futures) {
                Path sol = future.get();
                if (sol != null)
                    nrFeasible++;
                else
                    nrInfeasible++;
            }
        }

        if (executor != null)
            executor.shutdown();

    }

    private static class ParallelBenchWorker implements Callable<Path> {
        private Instance instance;
        private int nrFeasible;
        private int nrInfeasible;
        private int nrOfInstances;

        public ParallelBenchWorker(Instance instance, int nrFeasible, int nrInfeasible, int nrOfInstances) {
            this.instance = instance;
            this.nrFeasible = nrFeasible;
            this.nrInfeasible = nrInfeasible;
            this.nrOfInstances = nrOfInstances;
        }

        @Override
        public Path call() throws Exception {
            AtomicInteger nrOfPaths = new AtomicInteger(0);
            long before = System.nanoTime();
            Path sol = instance.solve(nrOfPaths);
            long after = System.nanoTime();
            if ((sol != null && nrFeasible < nrOfInstances) || (sol == null && nrInfeasible < nrOfInstances))
                logger.log(RESULT, "{} {} {} {}", instance.waitingTimesToString(),
                        sol != null ? "feasible" : "infeasible", (after - before) * 1E-6, nrOfPaths);

            return sol;
        }
    }
}
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

        int nrFeasible = 0;
        int nrInfeasible = 0;
        ArrayList<LogObject> logObjects = new ArrayList<>(10000);
        Random random = new Random(1);

        while (nrFeasible < nrOfInstances || nrInfeasible < nrOfInstances) {
            int[] waitingTimes = random.ints(m + 1, 1, r + 1).toArray();
            Instance instance = new Instance(waitingTimes);
            int a = instance.getA();
            int b = instance.getB();
            if (a > b || (a == 0 && b == m) || (a == 0 && b == 0) || (a == m && b == m))
                continue;

            AtomicInteger nrOfPaths = new AtomicInteger(0);
            long before = System.nanoTime();
            Path sol = instance.solve(nrOfPaths);
            long after = System.nanoTime();
            if ((sol != null && nrFeasible < nrOfInstances) || (sol == null && nrInfeasible < nrOfInstances)) {
                LogObject objectToLog = new LogObject(instance.getWaitingTimes(), (after - before) * 1E-6,
                        sol != null ? "feasible" : "infeasible", nrOfPaths);
                logObjects.add(objectToLog);
            }

            if (sol == null)
                nrInfeasible++;
            else
                nrFeasible++;

            if (nrFeasible % 200 == 0 || nrInfeasible % 200 == 0)
                logger.info("nrFeasible: {}, nrInfeasible: {}", nrFeasible, nrInfeasible);
        }

        logObjects.sort((l1, l2) -> l1.feasibleInfeasible.compareTo(l2.feasibleInfeasible));

        for (LogObject logObject : logObjects)
            logger.log(RESULT, "{} {} {} {}", Arrays.toString(logObject.waitingTimes), logObject.feasibleInfeasible,
                    logObject.etTime, logObject.nrPaths);

    }

    private static class LogObject {
        int[] waitingTimes;
        double etTime;
        String feasibleInfeasible;
        AtomicInteger nrPaths;

        public LogObject(int[] waitingTimes, double etTime, String feasibleInfeasible, AtomicInteger nrPaths) {
            this.waitingTimes = waitingTimes;
            this.etTime = etTime;
            this.feasibleInfeasible = feasibleInfeasible;
            this.nrPaths = nrPaths;
        }

    }
}

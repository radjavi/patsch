package app;

import models.*;

import java.util.*;
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

        Random random = new Random(1);

        while (nrFeasible < nrOfInstances || nrInfeasible < nrOfInstances) {
            int[] waitingTimes = random.ints(m + 1, 1, r + 1).toArray();
            Instance instance = new Instance(waitingTimes);
            int a = instance.getA();
            int b = instance.getB();
            if (a > b || (a == 0 && b == m) || (a == 0 && b == 0) || (a == m && b == m))
                continue;
            double[] totaltime = new double[11];
            Path sol = null;
            logger.info("solving:{}", instance.waitingTimesToString());
            int[] nrOfPaths = new int[1];
            for (int i = 0; i < 11; i++) {
                nrOfPaths[0] = 0;
                long before = System.nanoTime();
                sol = instance.solveBASIC(nrOfPaths);
                long after = System.nanoTime();
                double time = (after - before) * 1E-6;
                totaltime[i] = time;
            }
            Arrays.sort(totaltime);
            double median = totaltime[5];
            if ((sol != null && nrFeasible < nrOfInstances) || (sol == null && nrInfeasible < nrOfInstances)) {
                logger.log(RESULT, "{} {} {} {}", instance.waitingTimesToString(),
                        sol == null ? "infeasible" : "feasible", median, nrOfPaths);
            }
            if (sol == null)
                nrInfeasible++;
            else
                nrFeasible++;

        }

    }
}
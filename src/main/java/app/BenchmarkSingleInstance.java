package app;

import models.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class BenchmarkSingleInstance {
    final static Level RESULT = Level.forName("RESULT", 350);
    private static final Logger logger = LogManager.getLogger(BenchmarkSingleInstance.class);

    public static void main(String[] args) throws Exception {
        int nrOfTries = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        if (nrOfTries < 1) {
            logger.fatal("nrOfTries must be greater or equal to 1");
            return;
        }

        //INFEASIBLE
        int[] infeasible = new int[] { 16, 16, 7, 16, 2, 16, 16, 16, 16 };
        Instance infeasibleInstance = new Instance(infeasible);
        Path sol = null;
        double[] totaltime = new double[nrOfTries];
        int[] nrOfPaths = new int[1];

        for (int i = 0; i < nrOfTries; i++) {
            nrOfPaths[0] = 0;
            long before = System.nanoTime();
            sol = infeasibleInstance.solveBASIC(nrOfPaths);
            long after = System.nanoTime();
            double time = (after - before) * 1E-6;
            totaltime[i] = time;
        }

        Arrays.sort(totaltime);
        double median = totaltime[nrOfTries/2];
        logger.info("{} {} {} {}", infeasibleInstance.waitingTimesToString(), sol == null ? "infeasible" : "feasible",
                median, nrOfPaths[0]);
       

        // FEASIBLE
        int[] feaisible = new int[] { 16, 16, 7, 16, 3, 16, 16, 16, 16 };
        Instance feasibleInstance = new Instance(feaisible);
        Path solFeasible = null;
        double[] totaltimeFeasible = new double[nrOfTries];
        int[] nrOfPathsFeasible = new int[1];

        for (int i = 0; i < nrOfTries; i++) {
            nrOfPathsFeasible[0] = 0;
            long before = System.nanoTime();
            solFeasible = feasibleInstance.solveBASIC(nrOfPathsFeasible);
            long after = System.nanoTime();
            double time = (after - before) * 1E-6;
            totaltimeFeasible[i] = time;
        }

        Arrays.sort(totaltimeFeasible);
        double medianFeasible = totaltimeFeasible[nrOfTries/2];
        logger.info("{} {} {} {}", feasibleInstance.waitingTimesToString(),
                solFeasible == null ? "infeasible" : "feasible", medianFeasible, nrOfPathsFeasible[0]);

       

    }
}
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

        int[] infeasible = new int[] { 16, 16, 7, 16, 2, 16, 16, 16, 16 };
        int[] feaisible = new int[] { 16 };

        Instance infeasibleInstance = new Instance(infeasible);
        Path sol = null;
        double[] totaltime = new double[1];
        int[] nrOfPaths = new int[1];

        for (int i = 0; i < 1; i++) {
            nrOfPaths[0] = 0;
            long before = System.nanoTime();
            sol = infeasibleInstance.solveBASIC(nrOfPaths);
            long after = System.nanoTime();
            double time = (after - before) * 1E-6;
            totaltime[i] = time;
        }

        Arrays.sort(totaltime);
        double median = totaltime[0];
        logger.info("{} {} {} {}", infeasibleInstance.waitingTimesToString(), sol == null ? "infeasible" : "feasible",
                median, nrOfPaths[0]);

    }
}
package app;

import search.*;
import models.*;
import java.util.*;

// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class App {
    // Define a static logger variable so that it references the
    // Logger instance named "App".
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        int m = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        if (m < 2) {
            logger.fatal("m must be greater or equal to 2");
            return;
        }
        // int[] waitingTimes = new int[m + 1];
        // Arrays.fill(waitingTimes, 2*m);
        // Instance i = new Instance(waitingTimes);
        long startTime = System.nanoTime();
        Search.searchForCriticalInstances(m);
        //logger.info(i.solveParallel(4));
        long stopTime = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime - startTime) * 1e-9);
    }
}

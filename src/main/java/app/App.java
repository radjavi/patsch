package app;

import search.*;
import models.*;
import java.util.*;
import java.util.concurrent.*;

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
        //int[] waitingTimes = new int[]{14,2,14,4,14,14,14,14};
        //Arrays.fill(waitingTimes, 2*m);
        //Instance i = new Instance(waitingTimes);
        //System.out.println(i.getValidGraph().toStringTriangle());
        int nrThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(nrThreads);
        long startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, executor, nrThreads);
        //logger.info(i.solve());
        long stopTime = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime - startTime) * 1e-9);

        // Shutdown executor
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            logger.trace("Executor: Tasks interrupted.");
        }
        finally {
            if (!executor.isTerminated()) {
                logger.trace("Executor: Cancelling non-finished tasks.");
            }
            executor.shutdownNow();
            logger.trace("Executor: Shutdown finished.");
        }
    }
}

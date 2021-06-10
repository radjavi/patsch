package app;

import search.*;
import models.*;
import wrappers.*;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class roofValue3m {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        int nrThreads = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        if (nrThreads < 1) {
            logger.fatal("Number of threads must be greater than 0");
            return;

        }
        SingletonExecutor executor = SingletonExecutor.init(nrThreads);
        
        long startTime = System.nanoTime();
        Search.searchForCriticalInstances(4, 12);
        long stopTime = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime - startTime) * 1e-9);

        long startTime5m = System.nanoTime();
        Search.searchForCriticalInstances(5, 15);
        long stopTime5m = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime5m - startTime5m) * 1e-9);

        long startTime6m = System.nanoTime();
        Search.searchForCriticalInstances(6, 18);
        long stopTime6m = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime6m - startTime6m) * 1e-9);

        long startTime7m = System.nanoTime();
        Search.searchForCriticalInstances(7, 21);
        long stopTime7m = System.nanoTime();
        logger.info("Search took {} seconds.", (startTime7m - stopTime7m) * 1e-9);

        long startTime8m = System.nanoTime();
        Search.searchForCriticalInstances(8, 24);
        long stopTime8m = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime8m - startTime8m) * 1e-9);

        if (executor != null)
            executor.shutdown();
    }
}

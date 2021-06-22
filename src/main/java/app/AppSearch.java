package app;

import search.*;
import wrappers.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AppSearch {
    private static final Logger logger = LogManager.getLogger(AppSearch.class);

    public static void main(String[] args) throws Exception {
        int m = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        if (m < 2) {
            logger.fatal("m must be greater or equal to 2");
            return;
        }
        int r = args.length > 1 ? Integer.parseInt(args[1]) : 4 * m;
        if (r < 1) {
            logger.fatal("Roof value must be greater than 1");
            return;
        }
        int nrThreads = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        if (nrThreads < 1) {
            logger.fatal("Number of threads must be greater than 0");
            return;
        }
        SingletonExecutor executor = SingletonExecutor.init(nrThreads);

        long startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, r);
        long stopTime = System.nanoTime();
        
        logger.info("Search took {} seconds.", (stopTime - startTime) * 1e-9);
        if (executor != null)
            executor.shutdown();
    }
}

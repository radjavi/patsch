package app;

import search.*;
import models.*;
import wrappers.*;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class parallelTest {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        int m = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        if (m < 2) {
            logger.fatal("m must be greater or equal to 2");
            return;
        }
        // 80 THREADS
        SingletonExecutor executor = SingletonExecutor.init(80);
        long startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        long stopTime = System.nanoTime();
        logger.info("80 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);
        

        // 64 THREADS
        SingletonExecutor.getInstance().setThreads(64);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        stopTime = System.nanoTime();
        logger.info("64 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);
        

        // 32 THREADS
        SingletonExecutor.getInstance().setThreads(32);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        stopTime = System.nanoTime();
        logger.info("32 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);
        

        // 16 THREADS
        SingletonExecutor.getInstance().setThreads(16);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        stopTime = System.nanoTime();
        logger.info("16 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);
        

        // 8 THREADS
        SingletonExecutor.getInstance().setThreads(8);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        System.out.println("TEST: " +  SingletonExecutor.getInstance().getNrThreads());
        stopTime = System.nanoTime();
        logger.info("8 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);
        

        // 4 THREADS
        SingletonExecutor.getInstance().setThreads(4);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        stopTime = System.nanoTime();
        logger.info("4 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);
        

        // 2 THREADS
        SingletonExecutor.getInstance().setThreads(2);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        stopTime = System.nanoTime();
        logger.info("2 Threads: Search took {} seconds.", (stopTime - startTime) * 1e-9);

        // 1 THREADS
        SingletonExecutor.getInstance().setThreads(1);
        startTime = System.nanoTime();
        Search.searchForCriticalInstances(m, 2 * m);
        stopTime = System.nanoTime();
        logger.info("1 Thread: Search took {} seconds.", (stopTime - startTime) * 1e-9);

        executor.shutdown();

    }
}

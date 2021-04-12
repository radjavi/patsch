package app;

import search.*;
import models.*;
import wrappers.*;
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
        int nrThreads = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        if (nrThreads < 1) {
            logger.fatal("Number of threads must be greater than 0");
            return;
        }
        SingleExecutor executor = SingleExecutor.init(nrThreads);
        // Path path1 = new Path(new Instance(new int[]{10,5,2,3,4,8,10}));
        // Path path2 = new Path(new Instance(new int[]{10,5,2,3,4,8,10}));

        // path1.addPositionLast(new Position(6,3)); // (6,3)(5,2)(4,1)(3,2)(4,2)
        // path1.addPositionLast(new Position(5,2));
        // path1.addPositionLast(new Position(4,1));
        // path1.addPositionLast(new Position(3,2));
        // path1.addPositionLast(new Position(4,2));

        // path2.addPositionLast(new Position(6,3)); // (6,3)(5,2)(4,2)(3,1)(4,2)
        // path2.addPositionLast(new Position(5,2));
        // path2.addPositionLast(new Position(4,2));
        // path2.addPositionLast(new Position(3,1));
        // path2.addPositionLast(new Position(4,2));

        // System.out.println( "f:" + Arrays.toString( path1.getF_i()) + " s: "+Arrays.toString( path1.getS_i()));
        // System.out.println( "f:" + Arrays.toString(path2.getF_i()) +  " s: "+Arrays.toString( path2.getS_i()));
        // System.out.println(path1.betterThan(path2));

        int[] waitingTimes = new int[]{16,16,7,16,2,16,16,16,16};
        //Arrays.fill(waitingTimes, 2*m);
        Instance i = new Instance(waitingTimes);
        //System.out.println(i.getValidGraph().toStringTriangle());
        long startTime = System.nanoTime();
        //Search.searchForCriticalInstances(m);
        logger.info(i.solve());
        long stopTime = System.nanoTime();
        logger.info("Search took {} seconds.", (stopTime - startTime) * 1e-9);
        if (executor != null)
            executor.shutdown();
    }
}

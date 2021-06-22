package app;

import org.apache.logging.log4j.Logger;

import models.Instance;
import models.Path;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class AppSolve {
    private final static Level RESULT = Level.forName("RESULT", 350);
    private static final Logger logger = LogManager.getLogger(AppSolve.class);

    public static void main(String[] args) throws Exception {
        String argWaitingTimes = args.length > 0 ? args[0] : "";
        if (argWaitingTimes.length() == 0) {
            logger.fatal("The waiting times of an instance must be given!");
            return;
        }
        int[] waitingTimes = parseWaitingTimes(argWaitingTimes);
        Instance instance = new Instance(waitingTimes);

        long startTime = System.nanoTime();
        Path solution = instance.solve();
        long stopTime = System.nanoTime();

        logger.log(RESULT, solution != null ? instance + ": " + solution : instance + " is infeasible");
        logger.info("Solve took {} seconds.", (stopTime - startTime) * 1e-9);
    }

    private static int[] parseWaitingTimes(String argWaitingTimes) {
        ArrayList<Integer> waitingTimesList = new ArrayList<>();
        Matcher m = Pattern.compile("[0-9]+").matcher(argWaitingTimes);
        while (m.find()) {
            waitingTimesList.add(Integer.parseInt(m.group()));
        }
        return Ints.toArray(waitingTimesList);
    }
}

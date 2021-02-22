package search;

import models.*;
import java.util.*;

public class Search {

    public static HashMap<Instance, Path> searchForCriticalInstances(int m) throws Exception {
        LinkedList<Instance> U = new LinkedList<>();
        HashMap<Instance, Path> C = new HashMap<>();

        // INIT
        C = criticalsWithEmptyIntersection(m);
        for (int b = 0; b <= m; b++) {
            for (int a = 0; a <= b; a++) {
                Instance g = Instance.lowerBoundInstance(m, a, b);
                if (!g.geqToSomeIn(C.keySet()))
                    U.add(g);
            }
        }

        // SEARCH
        while (!U.isEmpty())

        {
            Instance u = U.pop();
            Path solvedU = u.solve();
            if (solvedU != null) {
                C.put(u, solvedU);
                System.out.println("found critical: " + u.waitingTimesToString());
            }

            else {
                for (int i = 0; i <= m; i++) {
                    int[] newWaitingTimes = u.getWaitingTimes().clone();
                    newWaitingTimes[i]++;
                    Instance v = new Instance(newWaitingTimes);

                    if (!v.geqToSomeIn(C.keySet()))
                        U.add(v);

                }
            }
        }

        return C;

    }

    private static HashMap<Instance, Path> criticalsWithEmptyIntersection(int m) throws Exception {
        if (m < 4)
            return null; // may be improved.

        HashMap<Instance, Path> C = new HashMap<>();

        for (int d = 1; d <= m - 2; d++) {
            int[] waitingTimes = new int[m + 1];
            for (int i = 0; i < m + 1; i++) {
                if (i <= d)
                    waitingTimes[i] = 2 * Math.max(i, d - i);
                else
                    waitingTimes[i] = 2 * Math.max(i - d - 1, m - i);
            }
            Instance instance = new Instance(waitingTimes);
            Path solution = instance.billiardBallPath(d);
            C.put(instance, solution);

        }

        return C;
    }
}

package app;

import models.*;
import search.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int m = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        if (m < 2) {
            System.out.println("m must be greater or equal to 2");
            return;
        }
        HashMap<Instance, Path> criticals = Search.searchForCriticalInstances(m);
        System.out.println("----- " + criticals.size() + " CRITICAL INSTANCES -----");
        criticals.forEach((i, s) -> {
            System.out.println(i.waitingTimesToString() + ": " + s);
        });
    }
}

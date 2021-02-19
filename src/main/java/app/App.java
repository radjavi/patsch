package app;

import models.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 7, 5, 5, 4, 6, 4, 6, 8 };
        //int[] times = new int[100];
        //Arrays.fill(times, 1000);
        Instance instance = new Instance(times);
        Path solution = instance.solve();
        System.out.println(solution != null ? solution : "Instance is infeasible.");
    }
}

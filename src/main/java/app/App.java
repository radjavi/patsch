package app;

import models.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 7, 5, 5, 4, 6, 4, 6, 8 };
        //int[] times = new int[6];
        Arrays.fill(times, 100);
        Instance instance = new Instance(times);
        System.out.println(instance.solve());
    }
}

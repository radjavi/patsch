package app;

import models.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 7, 5, 5, 4, 6, 4, 6, 8 };
        Instance instance = new Instance(times);
        System.out.println(instance.solve());
    }
}

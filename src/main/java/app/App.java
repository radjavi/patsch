package app;

import models.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 7, 5, 5, 4, 6, 4, 6, 8 };
        // int [] times = {2,2,2,2}

        int m = times.length - 1;
        Instance instance = new Instance(times);
        Property[] properties = instance.getProperties();
        for (Property prop : properties) {
            System.out.println(prop.getIndex() + ":" + prop.getPositions());
        }
        // System.out.println(Arrays.toString(properties));
        // System.out.println(instance.getTriangleGraph().toStringTriangle());
        System.out.println(instance.getValidGraph().toStringTriangle());
        System.out.println("a: " + instance.getA());
        System.out.println("b: " + instance.getB());
        Position from = new Position(5, 0);
        Position to = new Position(7, 1);
        System.out.println("path " + from + " -> " + to + ":\n \t" + instance.shortestPath(from, to));
        System.out.println("Distance:" + instance.distance(from, to));
    }
}

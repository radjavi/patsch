package app;

import models.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 7, 5, 5, 4, 6, 4, 6, 8 };
        // int [] times = {2,2,2,2}

        int m = times.length - 1;
        Instance instance = new Instance(times);
        //instance.removeValidPosition(new Position(6, 1));
        Property[] properties = instance.getProperties();
        for (Property prop : properties) {
            System.out.println(prop.getIndex() + ":" + prop.getPositions());
        }
        // System.out.println(Arrays.toString(properties));
        // System.out.println(instance.getTriangleGraph().toStringTriangle());
        System.out.println(instance.getValidGraph().toStringTriangle());
        System.out.println("a: " + instance.getA());
        System.out.println("b: " + instance.getB());
        Position from = new Position(3, 0);
        Position to = new Position(7, 3);
        Path shortestPath = instance.shortestPath(from, to);
        System.out.println("path " + from + " -> " + to + ":\n \t" + shortestPath);
        System.out.println("Path length: " + shortestPath.getLength());
        int[] s_i = shortestPath.getS_i();
        int[] f_i = shortestPath.getF_i();
        for (int i=0; i < s_i.length; i++) {
            System.out.println("s_" + i + ": " + s_i[i]);
            System.out.println("f_" + i + ": " + f_i[i]);
        }
    }
}

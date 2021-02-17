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

        Path p = new Path(instance);

        p.addPositionToEnd(new Position(0, 0));
        p.addPositionToEnd(new Position(1, 0));
        p.addPositionToEnd(new Position(1, 1));
        System.out.println("path:" + p);

        int[] s_i = p.getS_i();
        int[] f_i = p.getF_i();
        System.out.println("s_i:");
        for (int i = 0; i < s_i.length; i++) {
            System.out.println("s_i : propertyIndex:" + i + ", PathIndex:" + s_i[i]);
            System.out.println("f_i : propertyIndex:" + i + ", PathIndex:" + f_i[i]);

        }

    }
}

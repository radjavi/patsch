package app;

import models.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 7, 5,5,4,6,4,6,8};
        int m = times.length - 1;
        Instance instance = new Instance(times);
        Property[] properties = instance.getProperties();
        //System.out.println(Arrays.toString(properties));
        //System.out.println(instance.getTriangleGraph().toStringTriangle());
        //System.out.println(instance.getValidGraph());
        //System.out.println(instance.getA());
        //System.out.println(instance.getB());
    }
}

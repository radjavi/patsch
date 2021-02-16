package app;

import models.*;

public class App {
    public static void main(String[] args) throws Exception {
        int[] times = { 2, 2, 2, 2 };
        int m = times.length-1;
        PositionGraph graph = new PositionGraph(times);
        System.out.println(graph.toStringTriangle());
        System.out.println(graph);
        Property prop0 = new Property(m,times[0],0);
        System.out.println(prop0);
        Property prop1 = new Property(m,times[1],1);
        System.out.println(prop1);
        Property prop2 = new Property(m,times[2],2);
        System.out.println(prop2);
        Property prop3 = new Property(m,times[3],3);
        System.out.println(prop3);
    }
}

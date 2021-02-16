package models;

import java.util.*;

public class Instance {
    private PositionGraph triangleGraph;
    private Property[] properties;
    private PositionGraph validGraph;
    int a;
    int b;

    public Instance(int [] waitingTimes){
        int m = waitingTimes.length - 1;
        triangleGraph = new PositionGraph(trianglePositions(m));
        properties = createProperties(waitingTimes);
        a = computeA(properties);
        b = computeB(properties);
        validGraph = new PositionGraph(validPositions(properties));
    }

    /**
     * Returns the vertices of a triangle graph.
     */
    private static HashSet<Position> trianglePositions(int m) {
        HashSet<Position> vertices = new HashSet<>();
        for (int x = 0; x <= m; x++) {
            for (int y = 0; y <= x; y++) {
                vertices.add(new Position(x, y));
            }
        }
        return vertices;
    }

    private static int computeA(Property[] properties) {
        int a = 0;
        for (int i=0; i < properties.length; i++) {
            int temp = properties[i].getRange().getA();
            if (temp > a)
                a = temp;
        }
        return a;
    }

    private static int computeB(Property[] properties) {
        int b = Integer.MAX_VALUE;
        for (int i=0; i < properties.length; i++) {
            int temp = properties[i].getRange().getB();
            if (temp < b)
                b = temp;
        }
        return b;
    }

    /**
     * Returns the valid vertices, i.e., the set R.
     * 
     * May be improved.
     */
    private static HashSet<Position> validPositions(Property[] properties) {
        HashSet<Position> vertices = new HashSet<>();
        vertices.addAll(properties[0].getRange().getPositions());
        for (int i=1; i < properties.length; i++) {
        vertices.retainAll(properties[i].getRange().getPositions());
        }
        return vertices;
    }

    public PositionGraph getTriangleGraph() {
        return triangleGraph;
    }

    public PositionGraph getValidGraph() {
        return validGraph;
    }

    public Property[] getProperties() {
        return properties;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    private Property[] createProperties(int[] waitingTimes){
        int m = waitingTimes.length-1;
        Property[] properties = new Property[m+1];
        for (int i=0; i<= m; i++){
            properties[i] = new Property(m,waitingTimes[i],i);
        }
        return properties;
    }

    
}

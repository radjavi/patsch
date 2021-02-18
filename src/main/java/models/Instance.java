package models;

import java.util.*;

public class Instance {
    private int m;
    private PositionGraph triangleGraph;
    private Property[] properties;
    private PositionGraph validGraph;
    private int a;
    private int b;

    public Instance(int[] waitingTimes) {
        m = waitingTimes.length - 1;

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
        for (int i = 0; i < properties.length; i++) {
            int temp = properties[i].getRange().getA();
            if (temp > a)
                a = temp;
        }
        return a;
    }

    private static int computeB(Property[] properties) {
        int b = Integer.MAX_VALUE;
        for (int i = 0; i < properties.length; i++) {
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
        for (int i = 1; i < properties.length; i++) {
            vertices.retainAll(properties[i].getRange().getPositions());
        }
        return vertices;
    }

    /**
     * @return the shortest distance to p
     */
    public <F, T> int distance(F from, T to) {
        return shortestPath(from, to).getLength();
    }

    /**
     * May be improved.
     */
    public <F, T> Path shortestPath(F from, T to) {
        if (from instanceof Position && to instanceof Position)
            return shortestPath((Position) from, (Position) to);
        HashSet<Position> fromSet = new HashSet<>();
        HashSet<Position> toSet = new HashSet<>();
        if (from instanceof Position)
            fromSet.add((Position) from);
        else if (from instanceof Property) {
            Property property = (Property) from;
            for (Position p : property.getPositions()) {
                if (validGraph.hasPosition(p))
                    fromSet.add(p);
            }
        }
        if (to instanceof Position)
            toSet.add((Position) to);
        else if (to instanceof Property) {
            Property property = (Property) to;
            for (Position p : property.getPositions()) {
                if (validGraph.hasPosition(p))
                    toSet.add(p);
            }
        }
        
        int shortestDistance = Integer.MAX_VALUE;
        Path shortestPath = null;
        for (Position f : fromSet) {
            for (Position t : toSet) {
                Path path = shortestPath(f, t);
                if (path.getLength() < shortestDistance) {
                    shortestDistance = path.getLength();
                    shortestPath = path;
                }
            }
        }

        return shortestPath;
    }

    /**
     * May be improved.
     */
    private Path shortestPath(Position from, Position to) {
        // Path path = new Path(instance);
        // path.addPositionToEnd(from);
        // return path;

        HashMap<Position, Integer> gScore = new HashMap<>();
        validGraph.getPositions().forEach(p -> gScore.put(p, Integer.MAX_VALUE));
        gScore.put(from, 0);

        HashMap<Position, Integer> fScore = new HashMap<>();
        validGraph.getPositions().forEach(p -> fScore.put(p, Integer.MAX_VALUE));
        fScore.put(from, from.maxDeltaXY(to));

        PriorityQueue<Position> openSet = new PriorityQueue<>((p1, p2) -> {
            if (fScore.get(p1) < fScore.get(p2))
                return -1;
            if (fScore.get(p1) > fScore.get(p2))
                return 1;
            return 0;
        });
        openSet.add(from);

        HashMap<Position, Position> cameFrom = new HashMap<>();

        while (!openSet.isEmpty()) {
            Position current = openSet.remove();
            if (current.equals(to))
                return reconstructPath(cameFrom, current);

            HashSet<Position> neighbours = validGraph.getNeighbours(current);

            for (Position neighbour : neighbours) {
                int tentativeGScore = gScore.get(current) + 1;
                if (tentativeGScore < gScore.get(neighbour)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentativeGScore);
                    fScore.put(neighbour, tentativeGScore + neighbour.maxDeltaXY(to));
                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
        }

        return null;
    }

    private Path reconstructPath(HashMap<Position, Position> cameFrom, Position end) {
        Path path = new Path(this);
        path.addPositionFirst(end);
        Position current = end;
        while (cameFrom.get(current) != null) {
            Position previous = cameFrom.get(current);
            path.addPositionFirst(previous);
            current = previous;
        }
        return path;
    }

    public Path solve() {
        // TODO

        if (a > b) {
            // return path from Proposition 1
        }
        return null;
        // construct solution cycle
    }

    public int getM() {
        return m;
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

    public void removeValidPosition(Position p) {
        validGraph.removePosition(p);
    }

    private Property[] createProperties(int[] waitingTimes) {
        int m = waitingTimes.length - 1;
        Property[] properties = new Property[m + 1];
        for (int i = 0; i <= m; i++) {
            properties[i] = new Property(m, waitingTimes[i], i);
        }
        return properties;
    }

}

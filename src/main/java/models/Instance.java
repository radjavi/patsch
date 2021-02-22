package models;

import java.util.*;

public class Instance {
    private int m;
    private PositionGraph triangleGraph;
    private Property[] properties;
    private PositionGraph validGraph;
    private int a;
    private int b;
    private boolean critical;
    private int[] waitingTimes;

    public Instance(int[] waitingTimes) {
        m = waitingTimes.length - 1;
        this.waitingTimes = waitingTimes.clone();
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
    public <F, T> int distance(F from, T to) throws Exception {
        return shortestPath(from, to).getLength();
    }

    /**
     * May be improved.
     */
    public <F, T> Path shortestPath(F from, T to) throws Exception {
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

        int shortestHeuristic = Integer.MAX_VALUE;
        Position bestF = null;
        Position bestT = null;
        for (Position f : fromSet) {
            for (Position t : toSet) {
                int heuristic = f.maxDeltaXY(t);
                if (heuristic < shortestHeuristic) {
                    shortestHeuristic = heuristic;
                    bestF = f;
                    bestT = t;
                }
            }
        }

        if (bestF == null || bestT == null)
            return null;
        return shortestPath(bestF, bestT);
    }

    /**
     * May be improved.
     */
    private Path shortestPath(Position from, Position to) throws Exception {
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

    private Path reconstructPath(HashMap<Position, Position> cameFrom, Position end) throws Exception {
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

    public Path solve() throws Exception {
        if (a > b) {
            System.out.println("a: " + a + ", b: " + b);
            // return path from Proposition 1
        }
        LinkedList<Path> paths = new LinkedList<>();

        for (Position u : validGraph.getPositions()) {
            for (Position v : validGraph.getNeighbours(u)) {
                Path path = new Path(this);
                path.addPositionLast(u);
                path.addPositionLast(v);
                paths.add(path);
            }
        }

        while (!paths.isEmpty()) {
            Path p = paths.pop();
            // System.out.println(p);
            for (Position q : validGraph.getNeighbours(p.getLast())) {
                // System.out.println(p + ", " + q);
                Path pq = new Path(p);
                pq.addPositionLast(q);
                if (pq.valid()) {
                    if (pq.isCycle() && pq.visitsAllProperties()) {
                        return pq;
                    } else {
                        paths.add(pq);
                    }
                }
            }
        }

        return null;
    }

    public Path billiardBallPath(int d) throws Exception {
        int slopeX = 1;
        int slopeY = 1;
        int x = d + 1;
        int y = 0;
        Position p1 = new Position(x, y);
        Path path = new Path(this);
        path.addPositionFirst(p1);
        boolean flagX = false;
        boolean flagY = false;

        while (!(flagX && flagY)) {
            x += slopeX;
            y += slopeY;

            Position current = new Position(x, y);
            path.addPositionLast(current);

            if (!flagX && x == m)
                flagX = true;
            if (!flagY && y == d)
                flagY = true;
            if (x == d + 1 || x == m)
                slopeX *= -1;
            if (y == d || y == 0)
                slopeY *= -1;

        }
        Path copyPath = new Path(path);
        Iterator<Position> reversePathIterator = copyPath.getPath().descendingIterator();
        reversePathIterator.next(); // skip first
        while (reversePathIterator.hasNext()) {
            Position pos = reversePathIterator.next();
            path.addPositionLast(pos);
        }
        return path;
    }

    public static Instance lowerBoundInstance(int m, int a, int b) {
        int[] waitingTimes = new int[m + 1];

        for (int i = 0; i < a; i++) {
            waitingTimes[i] = Math.max(2 * i, 2 * (b - i));
        }
        for (int j = b + 1; j <= m; j++) {
            waitingTimes[j] = Math.max(2 * (m - j), 2 * (j - a));
        }

        for (int i = a; i <= b; i++) {
            int waitingtime_a = (i - a) * 2;
            int waitingtime_b = (b - i) * 2;
            int abMax = Math.max(waitingtime_a, waitingtime_b);
            waitingTimes[i] = Math.max(abMax, 1);
        }

        return new Instance(waitingTimes);
    }

    public int getM() {
        return m;
    }

    public String waitingTimesToString() {
        String s = "[";
        for (Integer i : waitingTimes) {
            s += i + ",";
        }
        s = s.substring(0, s.length() - 1);
        s += "]";
        return s;
    }

    public int[] getWaitingTimes() {
        return waitingTimes;
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

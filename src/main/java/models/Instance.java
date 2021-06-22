package models;

import java.util.*;
import wrappers.*;
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Instance {
    private int m;
    private int[] waitingTimes;
    private Integer a;
    private Integer b;
    private Property[] properties;
    private PositionGraph validGraph;
    private DistanceStorage shortestDistances;

    private static final Logger logger = LogManager.getLogger(Instance.class);

    public Instance(int[] waitingTimes) {
        m = waitingTimes.length - 1;
        this.waitingTimes = waitingTimes.clone();
        for (int i = 0; i <= m; i++) {
            assert waitingTimes[i] > 0 : "Waiting time must be greater than 0: " + this.waitingTimesToString();
        }
    }

    private void initProperties() {
        properties = createProperties(waitingTimes);
        // Arrays.sort(properties, (Property p1, Property p2) -> {
        // return Integer.compare(p1.getWaitingTime(), p2.getWaitingTime());
        // });
    }

    private void initA() {
        if (properties == null)
            initProperties();
        a = computeA(properties);
    }

    private void initB() {
        if (properties == null)
            initProperties();
        b = computeB(properties);
    }

    private void initValidGraph() {
        if (properties == null)
            initProperties();
        validGraph = new PositionGraph(validPositions(properties));
    }


    private static int computeA(Property[] properties) {
        int a = 0;
        for (Property property : properties) {
            int temp = property.getRange().getA();
            if (temp > a)
                a = temp;
        }
        return a;
    }

    private static int computeB(Property[] properties) {
        int b = Integer.MAX_VALUE;
        for (Property property : properties) {
            int temp = property.getRange().getB();
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
     * May be improved (check if shortestPath == null)
     * 
     * @return the shortest distance to p
     */
    public <F, T> int distance(F from, T to) throws Exception {
        if ((!(from instanceof Position) && !(from instanceof Property)))
            throw new IllegalArgumentException("'from' is not an instance of Position or Property, but rather: " + from.getClass().getName());
        if ((!(to instanceof Position) && !(to instanceof Property)))
            throw new IllegalArgumentException("'to' is not an instance of Position or Property, but rather: " + to.getClass().getName());

        if (shortestDistances == null)
            shortestDistances = new DistanceStorage();
        Integer cachedDistance = shortestDistances.getDistance(from, to);
        if (cachedDistance != null)
            return cachedDistance;

        int distance = shortestPath(from, to).getLength();
        shortestDistances.setDistance(from, to, distance);
        return distance;
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
                if (this.getValidGraph().hasPosition(p))
                    fromSet.add(p);
            }
        }

        if (to instanceof Position)
            toSet.add((Position) to);
        else if (to instanceof Property) {
            Property property = (Property) to;
            for (Position p : property.getPositions()) {
                if (this.getValidGraph().hasPosition(p))
                    toSet.add(p);
            }
        }

        double shortestHeuristic = Double.MAX_VALUE;
        Position bestF = null;
        Position bestT = null;
        for (Position f : fromSet) {
            for (Position t : toSet) {
                double heuristic = f.euclideanDistance(t);
                if (heuristic < shortestHeuristic) {
                    shortestHeuristic = heuristic;
                    bestF = f;
                    bestT = t;
                }
            }
        }

        return shortestPath(bestF, bestT);
    }

    private Path shortestPath(Position from, Position to) throws Exception {
        if (from == null || to == null)
            return null;

        Path path = new Path(this);
        path.addPositionLast(from);
        Position current = from;

        while (!current.equals(to)) {
            HashSet<Position> neighbours = this.getValidGraph().getNeighbours(current);
            Position closest = null;
            double closestHeuristic = Double.MAX_VALUE;
            for (Position neighbour : neighbours) {
                double heuristic = neighbour.euclideanDistance(to);
                if (heuristic < closestHeuristic) {
                    closest = neighbour;
                    closestHeuristic = heuristic;
                }
            }
            path.addPositionLast(closest);
            current = closest;
        }

        return path;
    }

    public Path solve() throws Exception {
        return InstanceSolver.solve(this);
    }

    public boolean isCritical() throws Exception {
        if (this.solve() == null)
            return false;
        for (int i = 0; i <= m; i++) {
            int[] waitingTimesToTry = this.getWaitingTimes().clone();
            if (waitingTimesToTry[i] == 1)
                continue;
            waitingTimesToTry[i]--;
            Instance instanceToTry = new Instance(waitingTimesToTry);
            if (instanceToTry.solve() != null)
                return false;
        }
        return true;
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

    public int level() {
        int m = this.getM();
        int sum = 0;
        for (int i = 0; i <= m; i++) {
            sum += this.getWaitingTimes()[i] - 1;
        }
        return sum;
    }

    public int getM() {
        return m;
    }

    public String waitingTimesToString() {
        return Arrays.toString(this.getWaitingTimes()).replaceAll("\\s+","").replaceAll("\\[","(").replaceAll("\\]",")");
    }

    public int[] getWaitingTimes() {
        return this.waitingTimes;
    }

    public int getWaitingTime(int index) {
        return this.getWaitingTimes()[index];
    }

    public PositionGraph getValidGraph() {
        if (validGraph == null)
            initValidGraph();
        return validGraph;
    }

    public Property[] getProperties() {
        if (properties == null)
            initProperties();
        return properties;
    }

    public Property getProperty(int index) {
        if (index < 0 || index >= this.getProperties().length)
            return null;
        return this.getProperties()[index];
    }

    public int getPropertyWaitingTime(int index) {
        return getProperty(index).getWaitingTime();
    }

    public Instance getReversed() {
        int[] waitingTimes = this.getWaitingTimes();
        int m = this.m;
        int[] reversedWaitingTimes = new int[m + 1];
        for (int i = 0; i <= m / 2; i++) {
            reversedWaitingTimes[i] = waitingTimes[m - i];
            reversedWaitingTimes[m - i] = waitingTimes[i];
        }
        return new Instance(reversedWaitingTimes);
    }

    public int getA() {
        if (a == null)
            initA();
        return a;
    }

    public int getB() {
        if (b == null)
            initB();
        return b;
    }

    public void removeValidPosition(Position p) {
        this.getValidGraph().removePosition(p);
    }

    private Property[] createProperties(int[] waitingTimes) {
        int m = waitingTimes.length - 1;
        Property[] properties = new Property[m + 1];
        for (int i = 0; i <= m; i++) {
            properties[i] = new Property(this, i);
        }

        return properties;
    }

    public boolean isValidPos(Position pos) {
        return this.getValidGraph().hasPosition(pos);
    }

    /**
     * Checks if this instance is greater than or equal to some instance in
     * `instances`. It also checks the reverse instances in `instances`.
     * 
     * @param instances The set of instances
     * @return The instance in `instances` that is less than or equal to this
     *         instance, otherwise null.
     */
    public Instance geqToSomeIn(Iterable<Instance> instances) {
        Instance thisReversed = this.getReversed();
        Iterator<Instance> iter = instances.iterator();
        while (iter.hasNext()) {
            Instance ins = iter.next();
            if (ins.lessThanOrEqualTo(this))
                return ins;
            else if (ins.lessThanOrEqualTo(thisReversed))
                return ins;
        }
        return null;
    }

    /**
     * Checks if this instance is less than some instance in `instances`. It also
     * checks the reverse instances in `instances`.
     * 
     * @param instances The set of instances
     * @return The instance in `instances` that is greater than this instance,
     *         otherwise null.
     */
    public Instance lessThanSomeIn(Iterable<Instance> instances) {
        Instance thisReversed = this.getReversed();
        Iterator<Instance> iter = instances.iterator();
        while (iter.hasNext()) {
            Instance ins = iter.next();
            if (this.lessThan(ins))
                return ins;
            else if (thisReversed.lessThan(ins))
                return ins;
        }
        return null;
    }

    public boolean lessThanOrEqualTo(Instance ins) {
        for (int i = 0; i <= m; i++) {
            if (!(this.waitingTimes[i] <= ins.waitingTimes[i]))
                return false;
        }
        return true;
    }

    public boolean lessThan(Instance ins) {
        if (!this.lessThanOrEqualTo(ins))
            return false;
        boolean componentLessThan = false;
        for (int i = 0; i <= m; i++) {
            if (this.waitingTimes[i] < ins.waitingTimes[i]) {
                componentLessThan = true;
                break;
            }
        }
        if (!componentLessThan)
            return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Instance))
            return false;
        Instance ins = (Instance) o;
        return Arrays.equals(this.getWaitingTimes(), ins.getWaitingTimes()) || Arrays.equals(this.getWaitingTimes(), ins.getReversed().getWaitingTimes());
    }

    /**
     * An instance and its reversal gets equivalent hash codes.
     */
    @Override
    public int hashCode() {
        Instance reversed = this.getReversed();
        Instance reference = this;
        int diff = Arrays.compare(this.getWaitingTimes(), reversed.getWaitingTimes());
        if (diff < 0)
            reference = reversed;

        int hash = 7;
        for (int t : reference.getWaitingTimes()) {
            hash = 31 * hash + Integer.hashCode(t);
        }
        return hash;
    }
}

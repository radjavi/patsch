package models;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import search.Search;
import singletons.SingleExecutor;
// Import log4j classes.
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Instance {
    private int m;
    private PositionGraph triangleGraph;
    private Property[] properties;
    private PositionGraph validGraph;
    private Integer a;
    private Integer b;
    private boolean critical;
    private int[] waitingTimes;

    private static final Logger logger = LogManager.getLogger(Instance.class);

    public Instance(int[] waitingTimes) {
        m = waitingTimes.length - 1;
        this.waitingTimes = waitingTimes.clone();
        for (int i = 0; i <= m; i++) {
            assert waitingTimes[i] > 0
                    : "Waiting time must be greater than 0: " + this.waitingTimesToString();
        }
    }

    private void initTriangleGraph() {
        triangleGraph = new PositionGraph(trianglePositions(m));
    }

    private void initProperties() {
        properties = createProperties(waitingTimes);
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
     * May be improved (check if shortestPath == null)
     * 
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

        if (bestF == null || bestT == null)
            return null;
        return shortestPath(bestF, bestT);
    }

    private Path shortestPath(Position from, Position to) throws Exception {
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
        SingleExecutor executor = SingleExecutor.getInstance();
        if (executor == null) {
            return solveSequential();
        }
        return solveParallel();
    }

    public Path solveSequential() throws Exception {
        if (this.getA() > this.getB()) {
            // Find correct d to return path from Proposition 1.
            for (int d = 0; d <= m; d++) {
                Instance critical = Search.criticalWithEmptyIntersection(m, d);
                if (critical.lessThanOrEqualTo(this))
                    return billiardBallPath(d);
            }
            return null;
        }
        LinkedList<Path> paths = new LinkedList<>();

        initPathsToSolve(paths);
        // logger.trace("{} - Initial number of paths: {}", this.waitingTimesToString(),
        // paths.size());

        while (!paths.isEmpty()) {
            //logger.info(paths.size());
            Path p = paths.pop();
            for (Position q : this.getValidGraph().getNeighbours(p.getLast())) {
                Path pq = new Path(p);
                pq.addPositionLast(q);
                if (pq.valid()) {
                    if (pq.isValidCycle() && pq.visitsAllProperties()) {
                        return pq;
                    } else {
                        Path pqp = new Path(pq);
                        Iterator<Position> reverseIterator = p.getPath().descendingIterator();
                        while (reverseIterator.hasNext()) {
                            pqp.addPositionLast(reverseIterator.next());
                        }
                        if (pqp.valid() && pq.isValidCycle() && pqp.visitsAllProperties()) {
                            return pqp;
                        }
                        paths.add(pq);
                    }
                }
            }
        }

        return null;
    }

    public Path solveParallel() throws Exception {
        if (this.getA() > this.getB()) {
            // Find correct d to return path from Proposition 1.
            for (int d = 0; d <= m; d++) {
                Instance critical = Search.criticalWithEmptyIntersection(m, d);
                if (critical.lessThanOrEqualTo(this))
                    return billiardBallPath(d);
            }
            return null;
        }
        LinkedBlockingQueue<Path> paths = new LinkedBlockingQueue<>();

        initPathsToSolve(paths);
        // logger.trace("{} - Initial number of paths: {}", this.waitingTimesToString(),
        // paths.size());

        SingleExecutor executor = SingleExecutor.getInstance();
        //int nrTasks = Math.min(paths.size(), executor.getNrThreads());
        int nrTasks = executor.getNrThreads();
        AtomicInteger nrBlocked = new AtomicInteger(0);
        ArrayList<Callable<Path>> callables = new ArrayList<>();
        for (int i = 0; i < nrTasks; i++) {
            callables.add(new ParallelInstanceSolver(paths, this, nrBlocked, nrTasks));
        }

        return executor.getExecutor().invokeAny(callables);
    }

    public boolean isCritical() throws Exception {
        for (int i = 0; i <= m; i++) {
            int[] waitingTimesToTry = this.getWaitingTimes().clone();
            if (waitingTimesToTry[i] == 1)
                continue;
            waitingTimesToTry[i]--;
            Instance instanceToTry = new Instance(waitingTimesToTry);
            if (instanceToTry.solve() != null) {
                logger.trace("{} is critical", instanceToTry.waitingTimesToString());
                return false;
            }

        }
        return true;
    }

    private void initPathsToSolve(AbstractCollection<Path> paths) throws Exception {
        HashMap<Position, HashSet<Position>> addedPaths = new HashMap<>();
        Property[] properties = this.getProperties();
        int minNrNeighbours = Integer.MAX_VALUE;
        Property loneliestProperty = null;
        for (Property property : properties) {
            int nrNeighbours = 0;
            for (Position position : property.getPositions()) {
                if (!this.isValidPos(position))
                    continue;
                nrNeighbours += this.getValidGraph().getNeighbours(position).size();
            }
            if (nrNeighbours < minNrNeighbours)
                loneliestProperty = property;
        }

        for (Position u : loneliestProperty.getPositions()) {
            if (!this.isValidPos(u))
                continue;
            for (Position v : this.getValidGraph().getNeighbours(u)) {
                if (addedPaths.get(v) != null && addedPaths.get(v).contains(u))
                    continue;
                Path path = new Path(this);
                path.addPositionLast(u);
                path.addPositionLast(v);
                paths.add(path);
                if (addedPaths.get(u) == null)
                    addedPaths.put(u, new HashSet<>());
                addedPaths.get(u).add(v);
                if (addedPaths.get(v) == null)
                    addedPaths.put(v, new HashSet<>());
                addedPaths.get(v).add(u);
            }
        }
        // addedPaths.forEach((p1, p2) -> logger.trace("Added: {}->{}", p1, p2));
        // paths.forEach(p -> logger.trace("Path: {}", p));
    }

    public Path billiardBallPath(int d) throws Exception {
        int slopeXConstant = -1;
        int slopeYConstant = -1;
        if (d == 0)
            slopeYConstant = 0;
        if (d == m - 1)
            slopeXConstant = 0;
        int slopeX = -1 * slopeXConstant;
        int slopeY = -1 * slopeYConstant;
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
                slopeX *= slopeXConstant;
            if (y == d || y == 0)
                slopeY *= slopeYConstant;

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
        if (triangleGraph == null)
            initTriangleGraph();
        return triangleGraph;
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
            properties[i] = new Property(m, waitingTimes[i], i);
        }
        return properties;
    }

    public boolean isValidPos(Position pos) {
        return this.getValidGraph().hasPosition(pos);
    }

    /**
     * Checks if this instance is greater than or equal to some instance in `instances`. It also
     * checks the reverse instances in `instances`.
     * 
     * @param instances The set of instances
     * @return The instance in `instances` that is less than or equal to this instance, otherwise
     *         null.
     */
    public Instance geqToSomeIn(Iterable<Instance> instances) {
        Iterator<Instance> iter = instances.iterator();
        while (iter.hasNext()) {
            Instance ins = iter.next();
            Instance insR = ins.getReversed();
            if (ins.lessThanOrEqualTo(this))
                return ins;
            else if (insR.lessThanOrEqualTo(this))
                return ins;
        }
        return null;
    }

    /**
     * Checks if this instance is less than some instance in `instances`. It also checks the reverse
     * instances in `instances`.
     * 
     * @param instances The set of instances
     * @return The instance in `instances` that is greater than this instance, otherwise null.
     */
    public Instance lessThanSomeIn(Iterable<Instance> instances) {
        Iterator<Instance> iter = instances.iterator();
        while (iter.hasNext()) {
            Instance ins = iter.next();
            Instance insR = ins.getReversed();
            if (this.lessThan(ins))
                return ins;
            else if (this.lessThan(insR))
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
        boolean componentLessThan = false;
        for (int i = 0; i <= m; i++) {
            if (this.waitingTimes[i] < ins.waitingTimes[i]) {
                componentLessThan = true;
                break;
            }
        }
        if (!componentLessThan)
            return false;
        for (int i = 0; i <= m; i++) {
            if (!(this.waitingTimes[i] == ins.waitingTimes[i]
                    || this.waitingTimes[i] < ins.waitingTimes[i]))
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if ((o == null) || (o.getClass() != this.getClass()))
            return false;
        Instance ins = (Instance) o;
        return Arrays.equals(this.getWaitingTimes(), ins.getWaitingTimes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getWaitingTimes());
    }

    private class ParallelInstanceSolver implements Callable<Path> {
        private final LinkedBlockingQueue<Path> paths;
        private final Instance instance;
        private final AtomicInteger nrBlocked;
        private final int nrThreads;

        public ParallelInstanceSolver(LinkedBlockingQueue<Path> paths, Instance instance,
                AtomicInteger nrBlocked, int nrThreads) {
            this.paths = paths;
            this.instance = instance;
            this.nrBlocked = nrBlocked;
            this.nrThreads = nrThreads;
        }

        @Override
        public Path call() throws Exception {
            while (true) {
                nrBlocked.incrementAndGet();
                if (paths.peek() == null && nrBlocked.get() == nrThreads)
                    return null;
                Path p = paths.take();
                //logger.info(p);
                nrBlocked.decrementAndGet();
                for (Position q : instance.getValidGraph().getNeighbours(p.getLast())) {
                    Path pq = new Path(p);
                    pq.addPositionLast(q);
                    if (pq.valid()) {
                        if (pq.isValidCycle() && pq.visitsAllProperties()) {
                            return pq;
                        } else {
                            Path pqp = new Path(pq);
                            Iterator<Position> reverseIterator = p.getPath().descendingIterator();
                            while (reverseIterator.hasNext()) {
                                pqp.addPositionLast(reverseIterator.next());
                            }
                            if (pqp.valid() && pq.isValidCycle() && pqp.visitsAllProperties()) {
                                return pqp;
                            }
                            paths.add(pq);
                        }
                    }
                }
            }
        }
    }
}

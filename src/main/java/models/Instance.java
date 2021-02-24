package models;

import java.util.*;
import search.Search;

public class Instance {
    private int m;
    private PositionGraph triangleGraph;
    private Property[] properties;
    private PositionGraph validGraph;
    private Integer a;
    private Integer b;
    private boolean critical;
    private int[] waitingTimes;

    public Instance(int[] waitingTimes) {
        m = waitingTimes.length - 1;
        this.waitingTimes = waitingTimes.clone();
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

     private String getCoordinate(Position from, Position to){
        String  dir = "";
        if (to.getY()>from.getY()) 
            dir += "N";
        else if (to.getY()<from.getY())
            dir += "S";
        if (to.getX()>from.getX()) 
            dir += "E";
        else if (to.getX()<from.getX()) 
            dir  += "W";
        return dir;
     }
    private Path shortestPath(Position from, Position to) throws Exception {
        Path path1 = new Path(this);
        path1.addPositionLast(from);
        Position currentPos = new Position (from.getX(), from.getY());
        
        while (!(currentPos.getX() == to.getX() && currentPos.getY() == to.getY())){
            
            //NE
            if (getCoordinate(currentPos, to).equals("NE") ){
                Position posToCheck = new Position (currentPos.getX()+1, currentPos.getY()+1);
                if (validGraph.hasPosition(posToCheck)) {
                    path1.addPositionLast(posToCheck);
                    currentPos = posToCheck;
                }
                else {
                    posToCheck = new Position (currentPos.getX()+1, currentPos.getY());
                    if (validGraph.hasPosition(posToCheck)) {
                        path1.addPositionLast(posToCheck);
                        currentPos = posToCheck;
                    }
                    else {
                        Position posToadd = new Position (currentPos.getX(), currentPos.getY()+1);
                        path1.addPositionLast(posToadd);
                        currentPos = posToadd;
                    }
                }

            }
             //NW
            if (getCoordinate(currentPos, to).equals("NW") ){
                 Position posToCheck = new Position (currentPos.getX()-1, currentPos.getY()+1);
                 if (validGraph.hasPosition(posToCheck)) {
                     path1.addPositionLast(posToCheck);
                     currentPos = posToCheck;
                 }
                 else {
                     posToCheck = new Position (currentPos.getX()-1, currentPos.getY());
                     if (validGraph.hasPosition(posToCheck)) {
                         path1.addPositionLast(posToCheck);
                         currentPos = posToCheck;
                     }
                     else {
                         Position posToAdd = new Position (currentPos.getX(), currentPos.getY()+1);
                         path1.addPositionLast(posToAdd);
                         currentPos = posToAdd;
                     }    
                 }
             }
             

            //N
            if (getCoordinate(currentPos, to).equals("N") ){
                Position posToCheck = new Position (currentPos.getX(), currentPos.getY()+1);
                path1.addPositionLast(posToCheck);
                currentPos = posToCheck; 
            }

            //W
            if (getCoordinate(currentPos, to).equals("W") ){
                Position posToCheck = new Position (currentPos.getX()-1, currentPos.getY());
                path1.addPositionLast(posToCheck);
                currentPos = posToCheck; 
            }
             //E
             if (getCoordinate(currentPos, to).equals("E") ){
                Position posToCheck = new Position (currentPos.getX()+1, currentPos.getY());
                path1.addPositionLast(posToCheck);
                currentPos = posToCheck; 
            }

            //SW
            if (getCoordinate(currentPos, to).equals("SW") ){
                Position posToCheck = new Position (currentPos.getX()-1, currentPos.getY()-1);
                if (validGraph.hasPosition(posToCheck)) {
                    path1.addPositionLast(posToCheck);
                    currentPos = posToCheck;
                }
                else {
                    posToCheck = new Position (currentPos.getX()-1, currentPos.getY());
                    if (validGraph.hasPosition(posToCheck)) {
                        path1.addPositionLast(posToCheck);
                        currentPos = posToCheck;
                    }
                        
                    else {
                        Position posToAdd = new Position (currentPos.getX(), currentPos.getY()-1);
                        path1.addPositionLast(posToAdd);
                        currentPos = posToAdd;
                    } 
                }
                
            }
             //SE
             if (getCoordinate(currentPos, to).equals("SE") ){
                Position posToCheck = new Position (currentPos.getX()+1, currentPos.getY()-1);
                if (validGraph.hasPosition(posToCheck)) {
                    path1.addPositionLast(posToCheck);
                    currentPos = posToCheck;
                }
                else {
                    posToCheck = new Position (currentPos.getX()+1, currentPos.getY());
                    if (validGraph.hasPosition(posToCheck)) {
                        path1.addPositionLast(posToCheck);
                        currentPos = posToCheck;
                    }
                        
                    else {
                        Position posToAdd = new Position (currentPos.getX(), currentPos.getY()-1);
                        path1.addPositionLast( posToAdd);
                        currentPos =posToAdd;
                    }     
                }
                
            }
             //S
             if (getCoordinate(currentPos, to).equals("S") ){
                Position posToCheck = new Position (currentPos.getX(), currentPos.getY()-1);
                path1.addPositionLast(posToCheck);
                currentPos = posToCheck; 
            }
            

        }
       
            

        return (path1);
        
    }

        /*___________---------------------
        HashMap<Position, Integer> gScore = new HashMap<>();
        this.getValidGraph().getPositions().forEach(p -> gScore.put(p, Integer.MAX_VALUE));
        gScore.put(from, 0);

        HashMap<Position, Integer> fScore = new HashMap<>();
        this.getValidGraph().getPositions().forEach(p -> fScore.put(p, Integer.MAX_VALUE));
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

            HashSet<Position> neighbours = this.getValidGraph().getNeighbours(current);

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
*/
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
        if (this.getA() > this.getB()) {
            System.out.println("a > b: " + this.getA() + " > " + this.getB());
            // Find correct d to return path from Proposition 1.
            for (int d = 0; d <= m; d++) {
                Instance critical = Search.criticalWithEmptyIntersection(m, d);
                if (critical.lessThanOrEqualTo(this))
                    return billiardBallPath(d);
            }
            return null;
        }
        LinkedList<Path> paths = new LinkedList<>();

        for (Position u : this.getValidGraph().getPositions()) {
            for (Position v : this.getValidGraph().getNeighbours(u)) {
                Path path = new Path(this);
                path.addPositionLast(u);
                path.addPositionLast(v);
                paths.add(path);
            }
        }

        while (!paths.isEmpty()) {
            Path p = paths.pop();
            // System.out.println(p);
            for (Position q : this.getValidGraph().getNeighbours(p.getLast())) {
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

    /**
     * Checks if this instance is greater than or equal to some instance in
     * `instances`.
     * 
     * @param instances The set of instances
     * @return `true` if this instance is greater than or equal to some instance in
     *         `instances`, and `false` otherwise.
     */
    public boolean geqToSomeIn(Iterable<Instance> instances) {
        Iterator<Instance> iter = instances.iterator();
        while (iter.hasNext()) {
            Instance ins = iter.next();
            if (ins.lessThanOrEqualTo(this))
                return true;
        }
        return false;
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
            if (!(this.waitingTimes[i] == ins.waitingTimes[i] || this.waitingTimes[i] < ins.waitingTimes[i]))
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
        if (this.m != ins.m) // Must have same length
            return false;
        for (int i = 0; i <= this.m; i++) {
            if (this.waitingTimes[i] != ins.waitingTimes[i])
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (Integer waitingTime : this.waitingTimes) {
            hash = 31 * hash + waitingTime;
        }
        return hash;
    }
}

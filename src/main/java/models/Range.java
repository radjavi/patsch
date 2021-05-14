package models;

import java.util.HashSet;

public class Range {
    private final Property property;
    private final int a;
    private final int b;
    private final HashSet<Position> positions;

    public Range(Property property) {
        this.property = property;
        a = computeA();
        b = computeB();
        positions = createPositions();
    }

    private int computeA() {
        int index = this.getProperty().getIndex();
        int waitingTime = this.getProperty().getWaitingTime();
        return Math.max(0, index - (waitingTime / 2));
    }

    private int computeB() {
        int index = this.getProperty().getIndex();
        int waitingTime = this.getProperty().getWaitingTime();
        int m = this.getProperty().getInstance().getM();
        return Math.min(index + (waitingTime / 2), m);
    }

    private HashSet<Position> createPositions() {
        int m = this.getProperty().getInstance().getM();
        
        HashSet<Position> positions = new HashSet<>();
        for (int x = a; x <= b; x++) {
            for (int y = 0; y <= x; y++) {
                if (x == y) // Remove H 
                    continue;  
                positions.add(new Position(x, y));
            }
        }
        for (int y = a; y <= b; y++) {
            for (int x = 0; x <= m; x++) {
                if (y < x) // Maybe improve
                    positions.add(new Position(x, y));
            }
        }
        return positions;
    }

    public Property getProperty() {
        return this.property;
    }

    public HashSet<Position> getPositions() {
        return this.positions;
    }

    public boolean hasPosition(Position p) {
        return this.getPositions().contains(p);
    }

    public int getA() {
        return this.a;
    }

    public int getB() {
        return this.b;
    }
}

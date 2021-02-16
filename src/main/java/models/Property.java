package models;

import java.util.HashSet;

public class Property {
    private HashSet<Position> pos;
    private int waitingTime;
    private int propertyIndex;
    private Range range;

    public Property(int m, int waitingTime, int index) {
        pos = positions(m, index);
        propertyIndex = index;
        this.waitingTime = waitingTime;
        range = new Range(m, waitingTime, index);
    }

    private HashSet<Position> positions(int m, int index) {
        HashSet<Position> positions = new HashSet<>();
        for (int x = index; x <= m; x++) {
            positions.add(new Position(x, index));
        }
        for (int y = 0; y <= index; y++) {
            positions.add(new Position(index, y));
        }
        return positions;
    }

    public HashSet<Position> getPositions() {
        return pos;
    }

    public boolean hasPosition(Position p) {
        return pos.contains(p);
    }

    public Range getRange() {
        return range;
    }

    @Override
    public String toString() {
        String s = "Index:" + propertyIndex + ", " +  "WatingTime:" + waitingTime + ", Positions: ";
        for (Position position : pos) {
            s +=  position ;
            
        }
        s +=" \n\n";
        return s;
    }
}

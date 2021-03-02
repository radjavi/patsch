package models;

import java.util.HashSet;

public class Property {
    private HashSet<Position> pos;
    private int waitingTime;
    private int index;
    private Range range;

    public Property(int m, int waitingTime, int index) {
        pos = positions(m, index);
        this.index = index;
        this.waitingTime = waitingTime;
        range = new Range(m, waitingTime, index);
    }

    private HashSet<Position> positions(int m, int index) {
        HashSet<Position> positions = new HashSet<>();
        for (int x = index + 1; x <= m; x++) {
            positions.add(new Position(x, index));
        }
        for (int y = 0; y < index; y++) {
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

    public int getIndex(){
        return index;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    @Override
    public String toString() {
        String s = "Index:" + index + ", " +  "WatingTime:" + waitingTime + ", Positions: ";
        for (Position position : pos) {
            s +=  position ;
            
        }
        s +=" \n\n";
        return s;
    }
}

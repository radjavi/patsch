package models;

import java.util.HashSet;

public class Property {
    private final int index;
    private final int waitingTime;
    private final HashSet<Position> positions;
    private final Range range;

    public Property(int m, int waitingTime, int index) {
        this.positions = this.positions(m, index);
        this.index = index;
        this.waitingTime = waitingTime;
        this.range = new Range(m, waitingTime, index);
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
        return this.positions;
    }

    public boolean hasPosition(Position p) {
        return this.positions.contains(p);
    }

    public Range getRange() {
        return this.range;
    }

    public int getIndex() {
        return this.index;
    }

    public int getWaitingTime() {
        return this.waitingTime;
    }

    @Override
    public String toString() {
        String s = "Index:" + this.getIndex() + ", " + "WatingTime:" + this.getWaitingTime() + ", Positions: ";
        for (Position position : this.getPositions()) {
            s += position;

        }
        s += " \n\n";
        return s;
    }

    // May be improved (same instance?).
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        Property prop = (Property) obj;
        return this.getIndex() == prop.getIndex();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.getIndex();
        return hash;
    }
}

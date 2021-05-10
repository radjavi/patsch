package models;

import java.util.HashSet;

public class Property {
    private final int index;
    private final Instance instance;
    private final HashSet<Position> positions;
    private final Range range;

    public Property(Instance instance, int index) {
        this.index = index;
        this.instance = instance;
        this.positions = this.createPositions();
        this.range = new Range(this);
    }

    private HashSet<Position> createPositions() {
        int index = this.getIndex();
        int m = this.instance.getM();

        HashSet<Position> positions = new HashSet<>();
        for (int x = index + 1; x <= m; x++) {
            positions.add(new Position(x, index));
        }
        for (int y = 0; y < index; y++) {
            positions.add(new Position(index, y));
        }
        return positions;
    }

    public Instance getInstance() {
        return this.instance;
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
        return this.getInstance().getWaitingTime(this.getIndex());
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Property))
            return false;
        Property prop = (Property) obj;
        return this.getIndex() == prop.getIndex();
    }

    @Override
    public int hashCode() {
        int hash = Integer.hashCode(this.getIndex());
        return hash;
    }
}

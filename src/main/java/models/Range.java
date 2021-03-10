package models;

import java.util.HashSet;

public class Range {
    private final int a;
    private final int b;
    private final HashSet<Position> positions;

    public Range(int m, int waitingTime, int index) {
        a = Math.max(0, index - (waitingTime / 2));
        b = Math.min(index + (waitingTime / 2), m);
        positions = createPositions(m, index);
    }

    private HashSet<Position> createPositions(int m, int index) {
        HashSet<Position> positions = new HashSet<>();
        for (int x = a; x <= b; x++) {
            for (int y = 0; y <= x; y++) {
                if (x == y)
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

    public HashSet<Position> getPositions() {
        return positions;
    }

    public boolean hasPosition(Position p) {
        return positions.contains(p);
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}

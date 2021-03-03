package models;

import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

public class PropertyPositionDistances {

    private Table<Position, Position, Integer> shortestDistancesPosition;
    private Table<Property, Position, Integer> shortestDistancesPropertytoPosition;
    private Table<Position, Property, Integer> shortestDistancesPositiontoProperty;

    public PropertyPositionDistances() {
        shortestDistancesPosition = HashBasedTable.create();
        shortestDistancesPropertytoPosition = HashBasedTable.create();
        shortestDistancesPositiontoProperty = HashBasedTable.create();
    }

    public <F, T> void setDistance(F from, T to, int distance) {
        if (from instanceof Position && to instanceof Position)
            shortestDistancesPosition.put((Position) from, (Position) to, distance);

        if (from instanceof Property && to instanceof Position)
            shortestDistancesPropertytoPosition.put((Property) from, (Position) to, distance);

        if (from instanceof Position && to instanceof Property)
            shortestDistancesPositiontoProperty.put((Position) from, (Property) to, distance);

    }

    public <F, T> Integer getDistance(F from, T to) {
        if (from instanceof Position && to instanceof Position)
            return shortestDistancesPosition.get((Position) from, (Position) to);

        if (from instanceof Property && to instanceof Position)
            return shortestDistancesPropertytoPosition.get((Property) from, (Position) to);

        if (from instanceof Position & to instanceof Property)
            return shortestDistancesPositiontoProperty.get((Position) from, (Property) to);

        return null;
    }

}

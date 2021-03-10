package wrappers;

import models.*;
import com.google.common.collect.Table;
import com.google.common.collect.HashBasedTable;

public class DistanceCache {

    private Table<Property, Position, Integer> cache;

    public DistanceCache() {
        cache = HashBasedTable.create();
    }

    public <F, T> void setDistance(F from, T to, int distance) {

        if (from instanceof Property && to instanceof Position)
            cache.put((Property) from, (Position) to, distance);

        if (from instanceof Position && to instanceof Property)
            cache.put((Property) to, (Position) from, distance);

    }

    public <F, T> Integer getDistance(F from, T to) {

        if (from instanceof Property && to instanceof Position)
            return cache.get((Property) from, (Position) to);

        if (from instanceof Position && to instanceof Property)
            return cache.get((Property) to, (Position) from);

        return null;
    }

}

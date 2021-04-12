package wrappers;

import models.*;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyPositionDistances {

    private ConcurrentHashMap<PropertyPosition, Integer> cache;

    public PropertyPositionDistances() {
        cache = new ConcurrentHashMap<>();
    }

    public <F, T> void setDistance(F from, T to, int distance) {
        if (from instanceof Property && to instanceof Position) {
            PropertyPosition propPos = new PropertyPosition((Property) from, (Position) to);
            cache.put(propPos, distance);
        }

        if (from instanceof Position && to instanceof Property) {
            PropertyPosition propPos = new PropertyPosition((Property) to, (Position) from);
            cache.put(propPos, distance);
        }
    }

    public <F, T> Integer getDistance(F from, T to) {

        if (from instanceof Property && to instanceof Position) {
            PropertyPosition propPos = new PropertyPosition((Property) from, (Position) to);
            return cache.get(propPos);
        }

        if (from instanceof Position && to instanceof Property) {
            PropertyPosition propPos = new PropertyPosition((Property) to, (Position) from);
            return cache.get(propPos);
        }
        return null;
    }

    public class PropertyPosition {
        Property prop;
        Position pos;

        public PropertyPosition(Property prop, Position pos) {
            this.prop = prop;
            this.pos = pos;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            if ((o == null) || (o.getClass() != this.getClass()))
                return false;
            PropertyPosition propPos = (PropertyPosition) o;
            return prop.getIndex() == (propPos.prop.getIndex()) && pos.equals(propPos.pos);
        }

        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + pos.getX();
            hash = 31 * hash + pos.getY();
            hash = 31 * hash + prop.getIndex();
            return hash;

        }
    }

}
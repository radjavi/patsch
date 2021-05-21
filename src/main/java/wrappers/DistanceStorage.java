package wrappers;

import models.*;
import java.util.concurrent.ConcurrentHashMap;

public class DistanceStorage {

    private ConcurrentHashMap<PropertyPosition, Integer> cache;

    public DistanceStorage() {
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

    private class PropertyPosition {
        Property property;
        Position position;

        public PropertyPosition(Property property, Position position) {
            this.property = property;
            this.position = position;
        }

        public Property getProperty() {
            return this.property;
        }

        public Position getPosition() {
            return this.position;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof PropertyPosition))
                return false;
            PropertyPosition propPos = (PropertyPosition) o;
            return this.getProperty().equals(propPos.getProperty()) && this.getPosition().equals(propPos.getPosition());
        }

        public int hashCode() {
            int hash = Integer.hashCode(this.getPosition().getX());
            hash = 31 * hash + Integer.hashCode(this.getPosition().getY());
            hash = 31 * hash + Integer.hashCode(this.getProperty().getIndex());
            return hash;
        }
    }

}
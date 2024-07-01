package org.jetbrains.station;

public abstract class Station {
    private int id;
    private double location;

    public Station(int id, int location) {
        this.id = id;
        this.location = location;
    }

    public double getLocation() {
        return location;
    }
}

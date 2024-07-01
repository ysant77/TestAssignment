package org.jetbrains.station;

import org.jetbrains.car.Car;

import java.util.ArrayList;
import java.util.List;

public  class StationsPool {

    private static StationsPool stations;

    private List<Station> gasStations = new ArrayList<>();
    private List<Station> chargingStations = new ArrayList<>();;

    public static StationsPool getInstance(){
        if (stations == null){
            stations = new StationsPool();
        }

        return stations;
    }

    private StationsPool() {
        // Add gas stations
        gasStations.add(new GasStation(1,10));
        gasStations.add(new GasStation(2,25));
        gasStations.add(new GasStation(3,45));
        gasStations.add(new GasStation(4,67));
        gasStations.add(new GasStation(5,77));
        gasStations.add(new GasStation(6,89));
        gasStations.add(new GasStation(7,97));
        // Add charging stations
        chargingStations.add(new ChargingStation(8,15));
        chargingStations.add(new ChargingStation(9,35));
        chargingStations.add(new ChargingStation(10,47));
        chargingStations.add(new ChargingStation(11,59));
        chargingStations.add(new ChargingStation(12,70));
        chargingStations.add(new ChargingStation(13,86));
        chargingStations.add(new ChargingStation(14,96));
    }

    public ChargingStation getClosestChargingStation(Car car) {
        return (ChargingStation) getClosestStation(car,this.chargingStations);
    }

    public GasStation getClosestGasStation(Car car) {
        return (GasStation) getClosestStation(car,this.gasStations);
    }

    private Station getClosestStation(Car car, List<Station> stations){

        double minDestination = 100;
        Station closestChargingStation = null;

        for (Station chargingStation : stations){
            double destination = Math.abs(car.getLocation() - chargingStation.getLocation());
            if( destination < minDestination){
                closestChargingStation = chargingStation;
                minDestination = destination;
            }
        }

        return closestChargingStation;
    }
}

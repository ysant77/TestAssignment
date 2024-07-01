package org.jetbrains.car;

public class ElectricCar extends Car{

    public ElectricCar(double location, double energyUsageRate) {
        super(location,energyUsageRate);
        energyThreshold=40;
    }

}

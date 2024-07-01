package org.jetbrains.car;

public class PetrolCar extends Car{
    public PetrolCar(double location, double energyUsageRate) {
        super(location,energyUsageRate);
        energyThreshold=20;
    }
}

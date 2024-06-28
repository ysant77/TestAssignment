package org.jetbrains.testing;

import org.jetbrains.car.*;
import org.jetbrains.person.Person;
import org.junit.jupiter.api.Test;

class TestResult {
    double location;
    double energy;

    public TestResult(double location, double energy) {
        this.location = location;
        this.energy = energy;
    }

    @Override
    public String toString() {
        return "Location: " + location + ", Energy: " + energy;
    }
}

public class TestTemplates {

    @Test
    public TestResult fixedPetrolCarTest(double location, double energyRate, int age, double homeLocation, double workLocation) {
        Car car = new PetrolCar(location, energyRate);
        Person person = new Person(age, homeLocation, workLocation, car);
        person.goToWork();
        person.goToHome();
        person.goToWork();
        person.goToHome();
        return new TestResult(car.getLocation(), car.getEnergyValue());
    }

    @Test
    public TestResult fixedPetrolCarTest(Chromosome chromosome) {
        Car car = new PetrolCar(chromosome.location, chromosome.energyUsageRate);
        Person person = new Person(chromosome.age, chromosome.homeLocation, chromosome.workLocation, car);
        person.goToWork();
        person.goToHome();
        person.goToWork();
        person.goToHome();
        return new TestResult(car.getLocation(), car.getEnergyValue());
    }

    @Test
    public void variableTripPetrolCarTest(double location, double energyRate, int age, double homeLocation, double workLocation, int numTrips) {
        Car car = new PetrolCar(location, energyRate);
        Person person = new Person(age, homeLocation, workLocation, car);
        for (int i = 0; i < numTrips; i++) {
            person.goToWork();
            person.goToHome();
        }
    }

    @Test
    public void fixedElectricCarTest(double location, double energyRate, int age, double homeLocation, double workLocation) {
        Car car = new ElectricCar(location, energyRate);
        Person person = new Person(age, homeLocation, workLocation, car);
        person.goToWork();
        person.goToHome();
        person.goToWork();
        person.goToHome();
    }

    @Test
    public void variableCarTest(String carType, double location, double energyRate, int age, double homeLocation, double workLocation) {
        Car car = (carType.equals("PETROL")) ? new PetrolCar(location, energyRate) : new ElectricCar(location, energyRate);
        Person person = new Person(age, homeLocation, workLocation, car);
        person.goToWork();
        person.goToHome();
        person.goToWork();
        person.goToHome();
    }

    @Test
    public void carChangeTest(String initialCarType, double location, double energyRate, int age, double homeLocation, double workLocation) {
        Car car = (initialCarType.equals("PETROL")) ? new PetrolCar(location, energyRate) : new ElectricCar(location, energyRate);
        Person person = new Person(age, homeLocation, workLocation, car);
        person.goToWork();
        person.goToHome();
        Car newCar = (initialCarType.equals("PETROL")) ? new ElectricCar(location, energyRate) : new PetrolCar(location, energyRate);
        person.changeCar(newCar);
        person.goToWork();
        person.goToHome();
    }
}

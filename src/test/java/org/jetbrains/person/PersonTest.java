package org.jetbrains.person;


import org.jetbrains.car.Car;
import org.jetbrains.car.PetrolCar;
import org.junit.jupiter.api.Test;

class PersonTest {

    @Test
    void testPerson(){
        Car car = new PetrolCar(10,2);
        Person person = new Person(19,10.1,46.10, car);
        person.goToWork();
        person.goToHome();
        person.goToWork();
        person.goToHome();
        person.goToWork();

        assert (car.getEnergyValue() > 0 && car.getEnergyValue() <=100);
    }

}
package org.jetbrains.testing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FuzzerConfig {
    public static Map<String, Object[]> generateTestParameters() {
        Map<String, Object[]> testParameters = new HashMap<>();
        Random random = new Random();

        // Using random values for the "fixed" test as well to introduce variability
        testParameters.put("fixedPetrolCarTest", new Object[]{
            random.nextDouble() * 100,   // location
            1 + random.nextDouble() * 9, // energyRate, ensuring it's greater than 0
            18 + random.nextInt(50),     // age
            random.nextDouble() * 100,   // homeLocation
            random.nextDouble() * 100    // workLocation
        });

        testParameters.put("variableTripPetrolCarTest", new Object[]{
            random.nextDouble() * 100,   
            random.nextDouble() * 10,    
            18 + random.nextInt(50),     
            random.nextDouble() * 100,   
            random.nextDouble() * 100,   
            1 + random.nextInt(10)       // numTrips
        });

        testParameters.put("fixedElectricCarTest", new Object[]{
            random.nextDouble() * 100,
            random.nextDouble() * 10,
            18 + random.nextInt(50),
            random.nextDouble() * 100,
            random.nextDouble() * 100
        });

        testParameters.put("variableCarTest", new Object[]{
            random.nextBoolean() ? "PETROL" : "ELECTRIC",
            random.nextDouble() * 100,
            random.nextDouble() * 10,
            18 + random.nextInt(50),
            random.nextDouble() * 100,
            random.nextDouble() * 100
        });

        testParameters.put("carChangeTest", new Object[]{
            random.nextBoolean() ? "PETROL" : "ELECTRIC",
            random.nextDouble() * 100,
            random.nextDouble() * 10,
            18 + random.nextInt(50),
            random.nextDouble() * 100,
            random.nextDouble() * 100
        });

        return testParameters;
    }
}

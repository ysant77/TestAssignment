package org.jetbrains.testing;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        boolean useJson = false; // Default is not to use JSON
        for (String arg : args) {
            if (arg.startsWith("useJson=")) {
                useJson = Boolean.parseBoolean(arg.split("=")[1]);
            }
        }
        System.out.println("useJson: " + useJson);
        List<RunResult> results = new ArrayList<>();
        GeneticAlgorithm ga = new GeneticAlgorithm();
        for(int i = 0; i < 20; i++){
            ga.initialize(useJson);
            ga.run();
            results.add(new RunResult(ga.getBestChromosomeEver(), ga.getBestFitnessEver(), ga.getBestGeneration()));
            System.out.println(i+"/20 completed" );
        }
        ga.saveAllResults(results);
    }
}

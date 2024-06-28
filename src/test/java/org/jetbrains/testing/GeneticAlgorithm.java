package org.jetbrains.testing;

import org.jetbrains.car.*;
import org.jetbrains.person.Person;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class Chromosome {
    double location;
    double energyUsageRate;
    int age;
    double homeLocation;
    double workLocation;
    double fitness;
    TestResult testResult;

    public Chromosome(double location, double energyUsageRate, int age, double homeLocation, double workLocation) {
        this.location = location;
        this.energyUsageRate = energyUsageRate;
        this.age = age;
        this.homeLocation = homeLocation;
        this.workLocation = workLocation;
        this.fitness = Double.MAX_VALUE;  // Initialize with a large value
    }
    public String toString() {
        return "Location: " + location + ", Energy Usage Rate: " + energyUsageRate + ", Age: " + age + ", Home Location: " + homeLocation + ", Work Location: " + workLocation;
    }
}

class GeneticAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private List<Chromosome> population = new ArrayList<>();
    private Random random = new Random();
    private double bestFitnessEver = Double.MIN_VALUE;
    private Chromosome bestChromosomeEver;
    private int bestGeneration = 0;
    private int generationCount = 0;
    private TestTemplates testTemplates = new TestTemplates();
    public void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double location = Math.min(100, Math.max(0, random.nextDouble() * 100));
            double energyUsageRate = 0.1 + random.nextDouble() * 0.9;  // Keep between 1 and 10
            int age = 18 + random.nextInt(50);  // Age between 18 and 67
            double homeLocation = random.nextDouble() * 100;
            double workLocation;
            
            do {
                workLocation = random.nextDouble() * 100;
            } while (Math.abs(homeLocation - workLocation) < 1);  // Ensure at least some distance between home and work
    
            population.add(new Chromosome(location, energyUsageRate, age, homeLocation, workLocation));
        }
    }
    

    public Chromosome tournamentSelection(int tournamentSize) {
        List<Chromosome> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }
    
        // Select the best chromosome from the tournament based on highest fitness
        return tournament.stream()
                         .max(Comparator.comparingDouble(c -> calculateFitness(c)))
                         .orElse(null);
    }
    
    // public double calculateFitness(Chromosome chromosome) {
    //     TestResult result = testTemplates.fixedPetrolCarTest(chromosome);
    //     double fitness = 0;
    //     if (result.location < 0 || result.location > 100) fitness += Math.abs(result.location - 50) - 50;
    //     if (result.energy < 0 || result.energy > 100) fitness += Math.abs(result.energy - 50) - 50;
    //     return fitness;
    // }
    
    public double calculateFitness(Chromosome chromosome) {
        try {
            TestResult result = testTemplates.fixedPetrolCarTest(chromosome);
            double fitness = 0;
            if (result.location < 0 || result.location > 100) {
                fitness += Math.abs(result.location - 50) - 50;
            }
            if (result.energy < 0 || result.energy > 100) {
                fitness += Math.abs(result.energy - 50) - 50;
            }
            chromosome.testResult = result;
            //System.out.println("Calculated fitness: " + fitness);  // Debugging output
            return fitness;
        } catch (Exception e) {
            System.err.println("Error calculating fitness: " + e.getMessage());
            System.err.println("Chromosome: " + chromosome);
            System.err.println("TestTemplates: " + testTemplates.fixedPetrolCarTest(chromosome));
            return Double.MAX_VALUE;  // Ensure a fallback value that indicates error
        }
    }
    
    
    public Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        double location = (parent1.location + parent2.location) / 2;
        double energyUsageRate = (parent1.energyUsageRate + parent2.energyUsageRate) / 2;
        int age = (parent1.age + parent2.age) / 2;
        double homeLocation = (parent1.homeLocation + parent2.homeLocation) / 2;
        double workLocation = (parent1.workLocation + parent2.workLocation) / 2;
    
        // Clamping the values to ensure they remain within realistic bounds
        location = Math.min(Math.max(location, 0), 100);
        energyUsageRate = Math.min(Math.max(energyUsageRate, 0.1), 1.0); // Assuming 0.1 to 1.0 is the realistic range for energy usage rate
        homeLocation = Math.min(Math.max(homeLocation, 0), 100);
        workLocation = Math.min(Math.max(workLocation, 0), 100);
    
        return new Chromosome(location, energyUsageRate, age, homeLocation, workLocation);
    }
    
    
    public void mutate(Chromosome chromosome) {
        chromosome.location += random.nextGaussian();
        chromosome.energyUsageRate += random.nextGaussian() * 0.1;  // Small mutation
    
        // Clamping to ensure the values remain within valid ranges
        chromosome.location = Math.min(Math.max(chromosome.location, 0), 100);
        chromosome.energyUsageRate = Math.min(Math.max(chromosome.energyUsageRate, 0.1), 1.0);
    }
    
    

    public void evaluatePopulation() {
        for (Chromosome chromosome : population) {
            double fitness = calculateFitness(chromosome);
            chromosome.fitness = fitness;
            if (fitness > bestFitnessEver) {  // Change to checking for greater fitness
                bestFitnessEver = fitness;
                bestChromosomeEver = chromosome;
                bestGeneration = generationCount;
            }
        }
    
        // Find the chromosome with the highest fitness in the current generation
        Chromosome bestOfGeneration = population.stream()
                                                .max(Comparator.comparing(c -> c.fitness))  // Change to max to find the highest fitness
                                                .orElse(null);
    
        if (bestOfGeneration != null) {
            //System.out.println("Generation " + generationCount + " - Best Fitness: " + bestOfGeneration.fitness);
            //System.out.println("Best Chromosome: " + bestOfGeneration);
        }
    }
    

    public void run() {
        initializePopulation();
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < 30000) { // 30 seconds
            evaluatePopulation();  // Evaluate each new generation
            List<Chromosome> newPopulation = new ArrayList<>();
            while (newPopulation.size() < POPULATION_SIZE) {
                Chromosome parent1 = tournamentSelection(5);
                Chromosome parent2 = tournamentSelection(5);
                Chromosome child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
            }
            population = newPopulation;
            generationCount++;
        }

        System.out.println("Best Ever Fitness: " + bestFitnessEver + " from Generation " + bestGeneration);
        System.out.println("Best Ever Chromosome: " + bestChromosomeEver);
        System.out.println("Test Result: " + bestChromosomeEver.testResult);
    }

}
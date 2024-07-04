package org.jetbrains.testing;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import com.google.gson.Gson; // Google JSON library
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

 class RunResult {
    private Chromosome bestChromosome;
    private double bestFitness;
    private int bestGeneration;

    public RunResult(Chromosome bestChromosome, double bestFitness, int bestGeneration) {
        this.bestChromosome = bestChromosome;
        this.bestFitness = bestFitness;
        this.bestGeneration = bestGeneration;
    }
}


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
        this.fitness = Double.MAX_VALUE;  // Initialize fitness with a large value
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
    private String resultFilename;

    public double getBestFitnessEver() {
        return bestFitnessEver;
    }
    public int getBestGeneration() {
        return bestGeneration;
    }
    public Chromosome getBestChromosomeEver() {
        return bestChromosomeEver;
    }

    public void initialize(boolean useJson) {
        if (useJson) {
            try {
                // Call the API to generate the JSON file
                callApiToGenerateJson();
                // Initialize the population from the updated JSON file
                System.out.println("Initializing population from JSON");
                initializePopulationFromJson();
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to random initialization if there's an error
                initializePopulation();
            }
        } else {
            // Regular random initialization
            initializePopulation();
        }
    }

    private void callApiToGenerateJson() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://127.0.0.1:8000/generate-data"))
        .GET()  // GET request to trigger JSON generation
        .build();
    
    client.send(request, HttpResponse.BodyHandlers.ofString());
    
}

   private void initializePopulationFromJson() {
    try {
        // Read JSON content from file
        //Change the path to the correct JSON path here (refer to README.md for more details)
        String jsonContent = new String(Files.readAllBytes(Paths.get("/home/yatharth/TestAssignment/app/initial_population.json")));

        // Create Gson instance
        Gson gson = new Gson();
        
        // Deserialize JSON to an array of JsonElement
        JsonElement jsonElement = gson.fromJson(jsonContent, JsonElement.class);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        // Clear existing population
        population = new ArrayList<>();
        
        // Iterate over JSON array to create Chromosome instances
        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();

            // Read each property from the JSON object
            double location = obj.get("initial_car_location").getAsDouble();
            double energyUsageRate = obj.get("car_energy_usage_rate").getAsDouble();
            int age = obj.get("person_age").getAsInt();
            double homeLocation = obj.get("home_location").getAsDouble();
            double workLocation = obj.get("work_location").getAsDouble();

            // Create a new Chromosome instance and add to population
            Chromosome chromosome = new Chromosome(location, energyUsageRate, age, homeLocation, workLocation);
            population.add(chromosome);
        }
    } catch (Exception e) {
        e.printStackTrace();
        // Fallback to random initialization if any error occurs
        initializePopulation(); 
    }
}

    public void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double location = Math.min(100, Math.max(0, random.nextDouble() * 100));
            double energyUsageRate = 0.1 + random.nextDouble() * 0.9;  // Clipping the value between 0 and 1
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
    
        location = Math.min(Math.max(location, 0), 100); // Clipping value between 0 and 100
        energyUsageRate = Math.min(Math.max(energyUsageRate, 0.1), 1.0); // Clipping value between 0.0 and 1.0
        homeLocation = Math.min(Math.max(homeLocation, 0), 100); // Clipping value between 0 and 100
        workLocation = Math.min(Math.max(workLocation, 0), 100); // Clipping value between 0 and 100
    
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
    }
    

    public void run() {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < 30000) { // 30 seconds time limit
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
    }

    public void saveAllResults(List<RunResult> results){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Saving all results...");
        System.out.println(results);
        String allResultsJson = gson.toJson(results);
        try {
            Files.write(Paths.get(resultFilename), allResultsJson.getBytes());
            System.out.println("All results saved to " + resultFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
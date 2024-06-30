import json
from fastapi import FastAPI
import requests

app = FastAPI()

# prompt = """
# I'm testing a Java project that simulates commuting in a 1D world where locations are represented as doubles between 0 and 100. The project has three packages: car, person, and station. The 'Car' package includes classes for petrol or electric cars. The 'Person' package includes a class representing the person commuting between home and work. The 'Station' package contains a singleton class 'StationsPool' simulating charging and fuel stations.

# A person driving a car must commute between their home and work without running out of energy. If the car's energy is less than a required threshold, the person must refuel at the nearest station. I need to generate test inputs that could potentially cause the system to fail, such as the car's location or energy level falling outside the expected range (0-100 for energy, 0-100 for location).

# Please generate 50 sets of test inputs considering the following scenarios:
# 1. Car energy dropping below 0 or going above 100.
# 2. Car location moving outside the 0 to 100 range.

# The test inputs should consist of five values:
# - initial car location (double, 0-100)
# - car energy usage rate (double, small positive value to simulate realistic energy depletion)
# - person's age (int, within a realistic human lifespan)
# - home location (double, 0-100, not equal to work location)
# - work location (double, 0-100, not equal to home location)

# Each input set should be a feasible starting point to explore the boundaries of the system's behavior under stress or unusual conditions. The aim is to discover vulnerabilities through these generated inputs by breaking the main commuting rule or exhausting the car's energy improperly.

# Here are examples of Java class structures you may find useful to understand the inputs and their relationships:

# ```java
# public class Car {
#     double location;
#     double energyUsageRate; // Energy used per unit distance
# }

# public class Person {
#     int age;
#     double homeLocation;
#     double workLocation;
#     Car car; // Associated car object
# }

# public class StationsPool {
#     // Manages refueling stations within the system
# }

# public void simulateCommute() {
#     Car car = new Car(50, 0.05); // Mid-point location, modest energy use
#     Person person = new Person(30, 10, 90, car); // Age, home and work far apart
#     if (car.needsEnergy(person.workLocation)) {
#         car.driveTo(StationsPool.getNearestStation(car.location));
#         car.refuel();
#     }
#     car.driveTo(person.workLocation); // Drive to work
#     // Test if energy levels and location are within valid ranges
# }
# ```

# Use this detailed scenario to generate initial test inputs for a genetic algorithm-based fuzzer aiming to uncover flaws in a transportation 
# simulation system. The inputs should be creatively challenging to the system's ability to handle edge cases.

# """




prompt = """
Generate five distinct test inputs for a Java simulation where cars and persons commute in a 1D world, each in a JSON-like key-value pair format. Ensure each input potentially causes simulation failures by making the car's energy drop below 0 or rise above 100, or by moving the car's location outside the 0 to 100 range. Each input should include:
- "initial_car_location": a double between 0 and 100,
- "car_energy_usage_rate": a small positive double,
- "person_age": an integer from 18 to 70,
- "home_location": a double between 0 and 100, distinctly different from work location,
- "work_location": a double between 0 and 100, distinctly different from home location.

Please provide the results as a list of JSON objects, each representing a set of parameters for a test case. Provide only JSON as output and no explanation or reasoning.
"""


# prompt = """Generate a single test input for a Java simulation where cars and persons commute in a 1D world, in a JSON-like key-value pair format. The input should potentially cause simulation failures by making the car's energy drop below 0 or rise above 100, or by moving the car's location outside the 0 to 100 range. Each input should include:
# - "initial_car_location": a double between 0 and 100,
# - "car_energy_usage_rate": a small positive double,
# - "person_age": an integer from 18 to 70,
# - "home_location": a double between 0 and 100, distinctly different from work location,
# - "work_location": a double between 0 and 100, distinctly different from home location.

# Focus on generating intelligent yet random edge cases that challenge the simulation's ability to handle extreme commuting scenarios without running out of energy. Do not include the input prompt in the output.
# """

import google.generativeai as genai
GOOGLE_API_KEY = 'API_KEY_HERE'
genai.configure(api_key=GOOGLE_API_KEY)
model = genai.GenerativeModel('gemini-1.5-pro')


@app.get("/generate-data")
def generate_data():
    results = []
    for _ in range(10):
        response = model.generate_content(prompt)
        if response:
            cleaned_response = response.text.replace('\n', '').replace('```', '').replace('json', '') # Remove new lines
            print(cleaned_response)
            try:
                json_data = json.loads(cleaned_response)  # Convert string to JSON
                results.extend(json_data)
            except json.JSONDecodeError as e:
                print(f"Error parsing JSON: {e}")
                break
    with open("/home/yatharth/TestAssignment/app/initial_population.json", "w") as f:
        json.dump(results, f, indent=4)
    return results
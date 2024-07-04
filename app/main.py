import json
from fastapi import FastAPI
import requests

app = FastAPI()



prompt = """
Generate five distinct test inputs for a Java simulation where cars and persons commute in a 1D world, each in a JSON-like key-value pair format. Ensure each input potentially causes simulation failures by making the car's energy drop below 0 or rise above 100, or by moving the car's location outside the 0 to 100 range. Each input should include:
- "initial_car_location": a double between 0 and 100,
- "car_energy_usage_rate": a small positive double,
- "person_age": an integer from 18 to 70,
- "home_location": a double between 0 and 100, distinctly different from work location,
- "work_location": a double between 0 and 100, distinctly different from home location.

Please provide the results as a list of JSON objects, each representing a set of parameters for a test case. Provide only JSON as output and no explanation or reasoning.
"""

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
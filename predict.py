import sys
import base64
import ipdb

#ipdb.set_trace()

with open("pie.jpg", "rb") as image_file:
    encoded_string = base64.b64encode(image_file.read())

#TODO(developer): Uncomment and set the following variables
project_id = 'pixelfood'
compute_region = 'us-central1'
model_id = 'ICN4832559605394156485'
score_threshold = '0.7'
key = 'pixelfood-e2b93b66a82f.json'

if __name__ == '__main__':
  file_path = sys.argv[1]

from google.cloud import automl_v1beta1 as automl

automl_client = automl.AutoMlClient()

# Get the full path of the model.
model_full_id = automl_client.model_path(
    project_id, compute_region, model_id
)

print(model_full_id)


# Create client for prediction service.
prediction_client = automl.PredictionServiceClient().from_service_account_file(key)

# Read the image and assign to payload.
with open(file_path, "rb") as image_file:
    content = image_file.read()
payload = {"image": {"image_bytes": content}}

# params is additional domain-specific parameters.
# score_threshold is used to filter the result
# Initialize params
params = {}
if score_threshold:
    params = {"score_threshold": score_threshold}

response = prediction_client.predict(model_full_id, payload, params)

# test1 = automl_client.from_service_account_file(key)
# ipdb.set_trace()
# response2 = test1.predict(model_full_id,payload,params)

response = prediction_client.predict(model_full_id, payload, params)
print("Prediction results:")
for result in response.payload:
    print("Predicted class name: {}".format(result.display_name))
    print("Predicted class score: {}".format(result.classification.score))

    if __name__ == "__main__":
      retStr = result.display_name
      #sys.exit(retStr)

import requests
from pprint import pprint
import json

name_of_food = "pizza";

url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title="
name_of_food = "+".join(name_of_food.split())
url = url + name_of_food

nutritionData = requests.get(url, headers={"X-RapidAPI-Key": "55564aab52msh6f13532902474b5p1fd958jsn58bfff5d2ece"})

with open('testFile.json', 'w') as outfile:  
    json.dump(nutritionData.json(), outfile, indent = 4)

pprint(nutritionData.json())



ID = 757090

url1 = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + str(ID) + "/information"
allergenData = requests.get(url1, headers={"X-RapidAPI-Key": "55564aab52msh6f13532902474b5p1fd958jsn58bfff5d2ece"})

with open('allergenFile.json', 'w') as outfile:  
    json.dump(allergenData.json(), outfile, indent = 4)

pprint(allergenData.json())





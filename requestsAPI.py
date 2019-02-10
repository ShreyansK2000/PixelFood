import requests
from pprint import pprint
import json

def foodFacts(name_of_food):
    url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title="
    name_of_food = "+".join(name_of_food.split())
    url = url + name_of_food

    #print(url)

    nutritionData = requests.get(url, headers={"X-RapidAPI-Key": "b47efebac2msha9ffb9228793e7cp158af4jsn6a95620e7043"})

    '''
    Writes json to testFile in the same directory as the py file
    '''
    
    with open('testFile.json', 'w') as outfile:  
        json.dump(nutritionData.json(), outfile, indent = 4)
    
    #pprint(nutritionData.json())

foodFacts("lasagna")

import requests
from pprint import pprint

r = requests.get('https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title=Lasagna', headers={
    "X-RapidAPI-Key": "b47efebac2msha9ffb9228793e7cp158af4jsn6a95620e7043"
  })
pprint(r.json())
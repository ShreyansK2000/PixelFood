from django.http import HttpResponse
import requests
from pprint import pprint
import json

#from PIL import Image

import random

def hello_world(request):
    return HttpResponse("Hello World")

def root_page(request):
    print("++++++++++++++++++++++++++++++++++")
    print('Root_Page')
    print("++++++++++++++++++++++++++++++++++")
    return HttpResponse("Root Home Page")

def random_number(request, max_rand=100):
    random_number = random.randrange(0, int(max_rand))

    msg = "Random Number Between 0 and %s : %d" %(max_rand, random_number)
    print("++++++++++++++++++++++++++++++++++")
    print(random_number)
    print("++++++++++++++++++++++++++++++++++")
    return HttpResponse(msg)

def imageProcessing(image):
    return "apple"


def foodFacts(name_of_food):
    foodID = {}
    url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title="
    name_of_food = "+".join(name_of_food.split())
    url = url + name_of_food

    # print(url)

    nutritionData = requests.get(url, headers={"X-RapidAPI-Key": ""})

    return nutritionData.json()


def foodDetection(request,image):
    #preprocess the image
    foodName = imageProcessing(image)
    jsonResponse = foodFacts(foodName)




#def imageRequest(request, image):
 #   print(image.size)


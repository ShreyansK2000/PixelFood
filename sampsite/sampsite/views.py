from django.http import HttpResponse
import requests
#from pprint import pprint
import json
import urllib2
import simplejson

#from PIL import Image


def imageProcessing(request):
    return HttpResponse("apple")


def foodFacts(request):
    #foodID = {}
    response = urllib2.urlopen("http://172.0.0.1:8000/custom/get/")
    data = simplejson.load(response)

    url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title="
    name_of_food = "pizza" #"+".join(name_of_food.split())
    url = url + name_of_food

    # print(url)

    nutritionData = requests.get(url, headers={"X-RapidAPI-Key": "55564aab52msh6f13532902474b5p1fd958jsn58bfff5d2ece"})

    return HttpResponse(str(nutritionData.json()))


def getAllergenInfo(ID):
    url = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + str(ID) + "/information"
    allergenData = requests.get(url, headers={"X-RapidAPI-Key": "55564aab52msh6f13532902474b5p1fd958jsn58bfff5d2ece"})

    with open('allergenFile.json', 'w') as outfile:
        json.dump(allergenData.json(), outfile, indent=4)

    return HttpResponse(str(allergenData.json()))




#def imageRequest(request, image):
 #   print(image.size)


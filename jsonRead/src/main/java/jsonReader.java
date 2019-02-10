import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.parser.JSONParser;

public class jsonReader {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(new FileReader("d:\\xdHacks PixelFood\\testFile.json"));
            JSONObject jsonObject = (JSONObject) obj;

            HashMap<Object, Object> calorieMap = (HashMap<Object, Object>) jsonObject.get("calories");
            double calorieCount = (double) calorieMap.get("value");

            HashMap<String, Double> rangeMap = (HashMap<String, Double>) calorieMap.get("confidenceRange95Percent");
            double minVal = rangeMap.get("min");
            double maxVal = rangeMap.get("max");

            HashMap<Object, Object> carbMap = (HashMap<Object, Object>) jsonObject.get("carbs");
            HashMap<String, Double> carbRangeMap = (HashMap<String, Double>) carbMap.get("confidenceRange95Percent");
            double carbCount = (double) carbMap.get("value");
            double carbMin = carbRangeMap.get("min");
            double carbMax = carbRangeMap.get("max");

            HashMap<Object, Object> fatMap = (HashMap<Object, Object>) jsonObject.get("fat");
            HashMap<String, Double> fatRangeMap = (HashMap<String, Double>) fatMap.get("confidenceRange95Percent");
            double fatCount = (double) fatMap.get("value");
            double fatMin = fatRangeMap.get("min");
            double fatMax = fatRangeMap.get("max");

            HashMap<Object, Object> proteinMap = (HashMap<Object, Object>) jsonObject.get("protein");
            HashMap<String, Double> proteinRangeMap = (HashMap<String, Double>) proteinMap.get("confidenceRange95Percent");
            double proteinCount = (double) proteinMap.get("value");
            double proteinMin = proteinRangeMap.get("min");
            double proteinMax = proteinRangeMap.get("max");
            System.out.println();

            System.out.println("Nutrient      " + "Calories (Range)\nCarbohydrate: " + carbCount + " (" + carbMin + "-" + carbMax + ") \nFat:          " + fatCount + " (" + fatMin + "-" + fatMax + ") \nProteins:     " + proteinCount + " (" + proteinMin + "-" + proteinMax + ") \nTotal:        " + calorieCount + " (" + minVal + "-" + maxVal + ") ");

        } catch (Exception e) {
            e.printStackTrace();
        }

        //////////////////////////////////////

        try{
            Object obj = parser.parse(new FileReader("d:\\xdHacks PixelFood\\allergenFile.json"));
            JSONObject allergenObj = (JSONObject) obj;

            boolean vegetarian = (boolean) allergenObj.get("vegetarian");
            boolean vegan = (boolean) allergenObj.get("vegan");
            boolean glutenFree = (boolean) allergenObj.get("glutenFree");
            boolean dairyFree = (boolean) allergenObj.get("dairyFree");
            boolean veryHealthy = (boolean) allergenObj.get("veryHealthy");
            boolean cheap = (boolean) allergenObj.get("cheap");
            boolean veryPopular = (boolean) allergenObj.get("veryPopular");
            boolean ketogenic = (boolean) allergenObj.get("ketogenic");
            boolean whole30 = (boolean) allergenObj.get("whole30");
            boolean lowFodmap = (boolean) allergenObj.get("lowFodmap");

            ArrayList<HashMap<String, Object>> mapList = (ArrayList<HashMap<String, Object>>) allergenObj.get("extendedIngredients");
            ArrayList<String> ingredients= new ArrayList<String>();
            for(HashMap<String, Object> map : mapList){
                ingredients.add((String) map.get("name"));
            }

            System.out.println(ingredients);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
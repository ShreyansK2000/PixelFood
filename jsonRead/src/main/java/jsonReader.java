import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.FileReader;
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


//            System.out.println(jsonObject);
//            System.out.println(carbMap);
//            System.out.println(fatMap);
//            System.out.println(proteinMap);

            System.out.println("Calories count: " + calorieCount);
            System.out.println("Minimum calories: " + minVal);
            System.out.println("Maximum calories: " + maxVal);
            System.out.println();

            System.out.println("Carb calories count: " + carbCount);
            System.out.println("Carb Minimum calories: " + carbMin);
            System.out.println("Carb Maximum calories: " + carbMax);
            System.out.println();

            System.out.println("Fat calories count: " + fatCount);
            System.out.println("Fat Minimum calories: " + fatMin);
            System.out.println("Fat Maximum calories: " + fatMax);
            System.out.println();

            System.out.println("Proteins calories count: " + proteinCount);
            System.out.println("Proteins Minimum calories: " + proteinMin);
            System.out.println("Proteins Maximum calories: " + proteinMax);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) throws FileNotFoundException {
    //assume we are dealing with text files
    HashMap<Integer, Object> rawTable = new HashMap<>();
    LinkedHashMap<Integer, Object> reverseSortedMap = new LinkedHashMap<>();
    FileReader fileReader = new FileReader(args[0]);
    try (BufferedReader br = new BufferedReader(fileReader)) {
      String line = br.readLine();
      while (line != null) {
        try {
          String[] idWithPayload = line.split(":", 2);
          //Assume Scores are non-negative 32-bit integers.
          if (idWithPayload[0] != null) {
            //re-order map to maintain top x records
            updateMap(rawTable, idWithPayload, Integer.valueOf(args[1]));
          }
        } catch (JsonSyntaxException e) {
          System.out.println("invalid json format No JSON object could be decoded\n"
              + " THIS IS NOT JSON");
          System.exit(2);
        }
        // Read next line
        line = br.readLine();
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }

    rawTable.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
        .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

    LOGGER.debug(reverseSortedMap.toString());
    List<Map<String, Object>> res = generatedResult(reverseSortedMap);
    Gson gsonOut = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gsonOut.toJson(res));

  }

  public static HashMap<Integer, Object> updateMap(HashMap<Integer, Object> existing,
      String[] incomingPayload, Integer requireSize) throws JsonSyntaxException  {

    Integer mapSmallest = Integer.MAX_VALUE;
    JsonObject result;

    if (existing.size() == 0) {
      result=JsonParser.parseString(incomingPayload[1])
          .getAsJsonObject();
      existing.put(Integer.valueOf(incomingPayload[0]), result);
      return existing;
    } else if (existing.size() <= requireSize) {
      //find smallest key
      for (Integer key : existing.keySet()) {
        if (key.compareTo(mapSmallest) <= 0) {
          mapSmallest = key;
        }
      }
      if (existing.size() < requireSize) {
          //keep adding
        result=JsonParser.parseString(incomingPayload[1])
            .getAsJsonObject();
        existing.put(Integer.valueOf(incomingPayload[0]), result);
        return existing;
      } else if (existing.size() == requireSize) {
          if(mapSmallest.compareTo(Integer.valueOf(incomingPayload[0]))<= 0){
            //remove map smallest with this larger one
            existing.remove(mapSmallest);
            result=JsonParser.parseString(incomingPayload[1])
                .getAsJsonObject();
            existing.put(Integer.valueOf(incomingPayload[0]), result);
          }
      }
    }

    return existing;
  }

  public static List<Map<String, Object>> generatedResult(
      LinkedHashMap<Integer, Object> sortedInput) {
    //given map with number of highest score, we validate if the id exist and generate correct output
    boolean idAtRootLevel = false;
    int numOfAddedRecord = 0;
    List resultList = new ArrayList<Map<String, Object>>();

    for (Entry<Integer, Object> row : sortedInput.entrySet()) {
      try {
        Map<String, Object> record = new Gson().fromJson((JsonElement) row.getValue(), Map.class);
        LOGGER.debug(
            "Current record:" + record + " numOfAddedRecord " + numOfAddedRecord + "entry size: "
                + sortedInput.size());
        //The `id` must be at the root level of the JSON object,
        for (String nodeKey : record.keySet()) {
          //traverse root level node to determine if `id` is present
            if (nodeKey.equalsIgnoreCase("id")) {
                idAtRootLevel = true;
            }
        }
        if (idAtRootLevel) {
          //valid payload so generate a result
          HashMap<String, Object> output = generateIdScoreMap(row.getKey(), record);
          resultList.add(output);
          numOfAddedRecord++;
        }
        //reset idAtRootLevel flag
        idAtRootLevel = false;
      } catch (Exception e) {
        LOGGER.error("ERROR :" + e.getLocalizedMessage());
      }
    }
    //reverse list because highest score is pushed last
    LOGGER.debug("Collected Results:" + resultList);
    return resultList;
  }

  public static HashMap<String, Object> generateIdScoreMap(Integer score, Map<String, Object> map) {

    HashMap<String, Object> result = new HashMap<>();
    result.put("score", score);
    if (map.containsKey("id")) {
      result.put("id", map.get("id"));
    }
    return result;
  }
}

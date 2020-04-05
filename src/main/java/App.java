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
import java.util.Collections;
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
            Map<Integer, Object> rawTable = new HashMap<>();
            LinkedHashMap<Integer, Object> reverseSortedMap = new LinkedHashMap<>();
            FileReader fileReader = new FileReader(args[0]);
            try(BufferedReader br = new BufferedReader(fileReader)) {
                String line = br.readLine();
                while (line != null) {
                    try {
                        String[] idWithPayload = line.split(":", 2);
                        //Assume Scores are non-negative 32-bit integers.
                        if (idWithPayload[0] != null) {
                            JsonObject result = JsonParser.parseString(idWithPayload[1])
                                .getAsJsonObject();
                            //Scores can repeat, but you should only count the `id` of the _last_ line processed as the "winning" `id`, override with latest entry
                            rawTable.put(Integer.valueOf(idWithPayload[0]), result);
                        }

                    } catch (JsonSyntaxException e) {
                        System.out.println("invalid json format No JSON object could be decoded\n"
                            + " THIS IS NOT JSON");
                        System.exit(2);
                    }
                    // Read next line
                    line = br.readLine();
                }
            }
            catch (Exception e){
                LOGGER.error(e.getMessage());
            }
            rawTable.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
            LOGGER.debug(reverseSortedMap.toString());
            List<Map<String, Object>> res = returndResultByHighest(reverseSortedMap, Integer.valueOf(args[1]));
            Gson gsonOut = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gsonOut.toJson(res));

    }

    public static List<Map<String,Object>> returndResultByHighest(Map<Integer, Object> sortedInput, int highest) {

        boolean idAtRootLevel = false;
        int numOfAddedRecord = 0;
        List resultList = new ArrayList<Map<String,Object>>();

        for(Entry<Integer, Object> row : sortedInput.entrySet()){
            try {
                Map<String,Object> record = new Gson().fromJson((JsonElement) row.getValue(), Map.class);
                LOGGER.debug("Current record:" +record + " numOfAddedRecord "+numOfAddedRecord+ "entry size: "+sortedInput.size());
                //The `id` must be at the root level of the JSON object,
                //traverse each root nodes
                for(String nodeKey:record.keySet()){
                    //traverse root level node to determine if `id` is present
                    if(nodeKey.equalsIgnoreCase("id")) idAtRootLevel=true;
                }
                if(idAtRootLevel && numOfAddedRecord<highest){
                    //add from the highest score
                    HashMap<String, Object> output = generateIdScoreMap(row.getKey(),record);
                    resultList.add(output);
                    numOfAddedRecord++;
                }
                //reset idAtRootLevel flag
                idAtRootLevel=false;
            }
            catch (Exception e){
               LOGGER.error("ERROR :"+e);
            }
        }
        //reverse list because highest score is pushed last
        LOGGER.debug("Collected Results:" + resultList);
        return resultList;
    }

    public static HashMap<String, Object> generateIdScoreMap(Integer score, Map<String, Object> map){

            HashMap<String, Object> result = new HashMap<>();
            result.put("score",score);
            if(map.containsKey("id")){
                result.put("id",map.get("id"));
            }
            return result;
    }
}

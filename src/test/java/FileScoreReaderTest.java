import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class FileScoreReaderTest {


  private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String NUMBER = "0123456789";

  private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + NUMBER;
  private static SecureRandom random = new SecureRandom();

  @Test
  public void testReturndResultHWithHighest2HappyPath() {

    HashMap<Integer, Object> record = new HashMap<Integer, Object>();

    //insert determined scores and its payload
    GsonBuilder gsonMapBuilder = new GsonBuilder();
    Gson gsonObject = gsonMapBuilder.create();
    String json1 = gsonObject.toJson(generateRandomPayload());
    String json2 = gsonObject.toJson(generateRandomPayload());
    String json3 = gsonObject.toJson(generateRandomPayload());
    JsonObject jsonObject1 = JsonParser.parseString(json1)
        .getAsJsonObject();
    JsonObject jsonObject2 = JsonParser.parseString(json2)
        .getAsJsonObject();
    JsonObject jsonObject3 = JsonParser.parseString(json3)
        .getAsJsonObject();

    record.put(14027069,jsonObject1);
    record.put(15027069,jsonObject2);
    record.put(16027069,jsonObject3);

    List<Map<String,Object>> result = App.returndResultByHighest(record,2);
    Collections.reverse(result);
    Map<String, Object> t1 = result.get(0);
    Assert.assertEquals(jsonObject3.get("id").getAsString(), t1.get("id"));

  }

  @Test
  public void testGenerateIdScoreMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", UUID.randomUUID());
    Map<String, Object> result = App.generateIdScoreMap(12345,map);
    Assert.assertEquals(result.get("id"),map.get("id"));
    Assert.assertEquals(result.get("score"),12345);

  }

  private HashMap generateRandomPayload() {

    HashMap<String, Object> map = new HashMap<>();
    SecureRandom random = new SecureRandom();
//    String uuid = UUID.randomUUID().toString();
    int umbrella = random.nextInt(10000);
    int value = random.nextInt(100000);

    int minDay = (int) LocalDate.of(1900, 1, 1).toEpochDay();
    int maxDay = (int) LocalDate.of(2020, 1, 1).toEpochDay();

    int millisInDay = 24 * 60 * 60 * 1000;

    long randomDay = minDay + random.nextInt(maxDay - minDay);

    long randomTime = random.nextInt(millisInDay);

    map.put("umbrella", umbrella);
    map.put("name", generateRandomString(40));
    map.put("value", value);
    map.put("payload", "........");
    map.put("date_stamp", randomDay);
    map.put("time", randomTime);
    map.put("id", generateRandomString(32));

    return map;

  }

  public String generateRandomString(int length) {
    if (length < 1) {
      throw new IllegalArgumentException();
    }

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {

      // 0-62 (exclusive), random returns 0-61
      int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
      char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

      // debug
      // System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

      sb.append(rndChar);

    }
    return sb.toString();
  }


}

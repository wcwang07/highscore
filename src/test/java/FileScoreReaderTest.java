import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

public class FileScoreReaderTest {


  private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String NUMBER = "0123456789";

  private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + NUMBER;
  private static SecureRandom random = new SecureRandom();



  @Test
  public void testUpdateMapHappyPath() {

    Integer integer1 = Integer.valueOf("14027069");
    Integer integer2 = Integer.valueOf("15027069");
    Integer integer3 = Integer.valueOf("16027069");

   HashMap<Integer, Object> result = new HashMap<>();

    String[] arr1 = {"14027069",generateRandomPayload().toString()};
    String[] arr2 = {"15027069",generateRandomPayload().toString()};
    String[] arr3 = {"16027069",generateRandomPayload().toString()};
    String[] arr4 = {"17027069",generateRandomPayload().toString()};
    String[] arr5 = {"18027069",generateRandomPayload().toString()};
    String[] arr6 = {"19027069",generateRandomPayload().toString()};
    String[] arr7 = {"24027069",generateRandomPayload().toString()};
    String[] arr8 = {"25027069",generateRandomPayload().toString()};
    String[] arr9 = {"26027069",generateRandomPayload().toString()};
    String[] arr10 = {"27027069",generateRandomPayload().toString()};

    App.updateMap(result,arr1,2);
    App.updateMap(result,arr2,2);
    App.updateMap(result,arr3,2);
    App.updateMap(result,arr4,2);
    App.updateMap(result,arr5,2);
    App.updateMap(result,arr6,2);
    App.updateMap(result,arr7,2);
    App.updateMap(result,arr8,2);
    App.updateMap(result,arr9,2);
    App.updateMap(result,arr10,2);

    Assert.assertEquals(result.size(),2);
    Assert.assertTrue(result.containsKey(26027069));
    Assert.assertTrue(result.containsKey(27027069));

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

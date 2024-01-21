package utils.fileutils;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import lombok.Data;

@Data
public class TestData {

  private String loginUrl;
  private String buyHostingPageUrl;

  private static final String TEST_DATA_FILE = "src/main/resources/test_data.yaml";
  private static final String TEST_DATA_CAN_NOT_BE_NULL_MESSAGE = "TestData cannot be null";

  public static TestData getTestData() {
    try {
      ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
      return objectMapper.readValue(new File(TEST_DATA_FILE), TestData.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getLoginUrlFromTestData(){
    return requireNonNull(getTestData(), TEST_DATA_CAN_NOT_BE_NULL_MESSAGE).getLoginUrl();
  }

  public static String getBuyHostingPageUrlFromTestData(){
    return requireNonNull(getTestData(), TEST_DATA_CAN_NOT_BE_NULL_MESSAGE).getBuyHostingPageUrl();
  }
}

package utils.fileutils;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import lombok.Data;

@Data
public class Secrets {

  private String email;
  private String password;

  private static final String SECRETS_FILE = "secrets.yaml";
  private static final String SECRETS_CAN_NOT_BE_NULL_MESSAGE = "Secrets cannot be null";

  public static Secrets getSecrets() {
    try {
      ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
      return objectMapper.readValue(new File(SECRETS_FILE), Secrets.class);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String getEmailFromSecrets() {
    return requireNonNull(getSecrets(), SECRETS_CAN_NOT_BE_NULL_MESSAGE).getEmail();
  }

  public static String getPasswordFromSecrets() {
    return requireNonNull(getSecrets(), SECRETS_CAN_NOT_BE_NULL_MESSAGE).getPassword();
  }
}

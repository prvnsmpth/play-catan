package models.constants;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.Optional;


public enum Color {
  WHITE,
  BLUE,
  RED,
  ORANGE;

  public static Color fromValue(String colorName) {
    Optional<Color> color = Arrays.stream(Color.values())
        .filter(c -> c.name().equalsIgnoreCase(colorName))
        .findAny();
    return color.orElse(null);
  }

  public static Color fromPlayerPos(int pos) {
    return Arrays.stream(Color.values())
        .filter(c -> c.ordinal() == pos)
        .findAny().orElse(null);
  }
}

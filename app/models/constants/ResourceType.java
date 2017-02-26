package models.constants;

import java.util.Arrays;
import java.util.Optional;


public enum ResourceType {
  LUMBER,
  WOOL,
  BRICK,
  GRAIN,
  ORE;

  public static ResourceType fromValue(String val) {
    Optional<ResourceType> maybe = Arrays.stream(ResourceType.values())
        .filter(type -> type.name().equalsIgnoreCase(val))
        .findFirst();
    return maybe.orElse(null);
  }
}

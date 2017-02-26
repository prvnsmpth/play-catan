package models;

import java.util.Arrays;


public enum BuildingType {
  ROAD,
  SETTLEMENT,
  CITY;

  public static BuildingType fromValue(String val) {
    return Arrays.stream(BuildingType.values())
        .filter(type -> type.name().equalsIgnoreCase(val))
        .findFirst().orElse(null);
  }
}

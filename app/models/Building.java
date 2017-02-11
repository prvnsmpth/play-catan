package models;

public class Building {
  private final BuildingType _type;

  public Building(BuildingType type) {
    _type = type;
  }

  public static Building ofType(BuildingType type) {
    return new Building(type);
  }
}

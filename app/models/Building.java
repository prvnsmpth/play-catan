package models;

import com.google.common.base.Objects;
import models.constants.Color;
import models.constants.GameConstants;


public class Building {

  private final BuildingType type;
  private final Color color;

  public Building(BuildingType type, Color color) {
    this.type = type;
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  public BuildingType getType() {
    return type;
  }

  public Stockpile getBuildingCost() {
    return GameConstants.BUILDING_COSTS.get(type);
  }

  @Override
  public String toString() {
    return String.format("(%s, %s)", type.name(), color.name());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Building)) {
      return false;
    }

    Building that = (Building) obj;
    return that.type.equals(this.type) && that.color.equals(this.color);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, color);
  }
}

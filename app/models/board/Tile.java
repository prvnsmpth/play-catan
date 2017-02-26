package models.board;

import com.fasterxml.jackson.annotation.JsonCreator;
import models.board.coords.TileCoords;
import models.constants.ResourceType;


public class Tile {

  private final TileCoords coords;
  private ResourceType resourceType;
  private int number;

  public Tile(TileCoords coords) {
    this.coords = coords;
  }

  public Tile(TileCoords coords, ResourceType resourceType, int number) {
    this.coords = coords;
    this.resourceType = resourceType;
    this.number = number;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public TileCoords getCoords() {
    return coords;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", coords.x, coords.y);
  }
}

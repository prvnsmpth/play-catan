package models.board;

import com.fasterxml.jackson.annotation.JsonCreator;
import models.Building;
import models.board.coords.EdgeCoords;


public class Edge {

  private final EdgeCoords coords;

  private Building road;

  public Edge(EdgeCoords coords) {
    this.coords = coords;
  }

  public Edge(EdgeCoords coords, Building road) {
    this.coords = coords;
    this.road = road;
  }

  public void placeRoad(Building road) {
    this.road = road;
  }

  public Building getRoad() {
    return road;
  }

  public EdgeCoords getCoords() {
    return coords;
  }
}

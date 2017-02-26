package models.board;

import models.Building;
import models.Harbour;
import models.board.coords.VertexCoords;


public class Vertex {

  private final VertexCoords coords;
  private Building building;
  private Harbour harbour;

  public Vertex(VertexCoords coords) {
    this.coords = coords;
    building = null;
    harbour = null;
  }

  public Vertex(VertexCoords coords, Building building, Harbour harbour) {
    this.coords = coords;
    this.building = building;
    this.harbour = harbour;
  }

  public void setBuilding(Building building) {
    this.building = building;
  }

  public Building getBuilding() {
    return building;
  }

  public Harbour getHarbour() {
    return harbour;
  }

  public VertexCoords getCoords() {
    return coords;
  }

  @Override
  public String toString() {
    return coords.toString();
  }
}

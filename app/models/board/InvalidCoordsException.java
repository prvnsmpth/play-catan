package models.board;

import models.board.coords.Coords;


public class InvalidCoordsException extends Exception {

  private String message;
  private Coords coords;

  public InvalidCoordsException() {
    super();
  }

  public InvalidCoordsException(Coords coords) {
    super();
    this.coords = coords;
  }

  public Coords getCoords() {
    return coords;
  }

}

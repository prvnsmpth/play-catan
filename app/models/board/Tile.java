package models.board;

public class Tile {
  private final TileCoords _coords;

  public Tile(TileCoords coords) {
    _coords = coords;
  }

  public TileCoords getCoords() {
    return _coords;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", _coords.x, _coords.y);
  }
}

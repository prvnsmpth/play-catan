package models.board.coords;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;


public class TileCoords extends Coords {

  @JsonCreator
  public TileCoords(@JsonProperty("x") int x, @JsonProperty("y") int y) {
    super(x, y);
  }

  public TileCoords getRight() {
    return new TileCoords(x, y+1);
  }

  public TileCoords getLeft() {
    return new TileCoords(x, y-1);
  }

  public TileCoords getTopLeft() {
    return new TileCoords(x-1, y - (x % 2));
  }

  public TileCoords getTopRight() {
    return new TileCoords(x-1, y + (x % 2));
  }

  public TileCoords getBottomLeft() {
    return new TileCoords(x+1, y - (x % 2));
  }

  public TileCoords getBottomRight() {
    return new TileCoords(x+1, y + (x % 2));
  }

  public VertexCoords getTopVertex() {
    return new VertexCoords(x, y, VertexCoords.T);
  }

  public VertexCoords getBottomVertex() {
    return new VertexCoords(x, y, VertexCoords.B);
  }

  public EdgeCoords getNorthEdge() {
    return new EdgeCoords(x, y, EdgeCoords.N);
  }

  public EdgeCoords getEastEdge() {
    return new EdgeCoords(x, y, EdgeCoords.E);
  }

  public EdgeCoords getSouthEdge() {
    return new EdgeCoords(x, y, EdgeCoords.S);
  }

  public List<VertexCoords> getVertices() {
    List<VertexCoords> coords = new ArrayList<>();
    coords.add(getTopVertex());
    coords.add(getBottomVertex());

    TileCoords leftTile = getLeft();
    coords.add(leftTile.getTopVertex());
    coords.add(leftTile.getBottomVertex());

    TileCoords topLeftTile = getTopLeft();
    coords.add(topLeftTile.getBottomVertex());
    TileCoords bottomLeftTile = getBottomLeft();
    coords.add(bottomLeftTile.getTopVertex());
    return coords;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }
}

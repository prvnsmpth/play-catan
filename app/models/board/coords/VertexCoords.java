package models.board.coords;

import java.util.ArrayList;
import java.util.List;


public class VertexCoords extends Coords {
  public static final int T = 0;
  public static final int B = 1;

  public VertexCoords(int x, int y, int z) {
    super(x, y, z);
  }

  public List<TileCoords> getSurroundingTiles() {
    List<TileCoords> sTiles = new ArrayList<>();

    // The vertex's home tile
    TileCoords homeTile = new TileCoords(x, y);
    sTiles.add(homeTile);

    // Tile to the right
    TileCoords rightTileCoords = homeTile.getRight();
    if (rightTileCoords.y + 1 <= 6) {
      sTiles.add(rightTileCoords);
    }

    // Tile above or below
    TileCoords adjTileCoords;
    if (z == VertexCoords.T) {
      adjTileCoords = homeTile.getTopRight();
    } else if (z == VertexCoords.B) {
      adjTileCoords = homeTile.getBottomRight();
    } else {
      throw new IllegalStateException("Bad vertex coords");
    }
    sTiles.add(adjTileCoords);
    return sTiles;
  }

  public List<VertexCoords> getAdjacent() {
    List<VertexCoords> adjVertices = new ArrayList<>();

    // The home tile
    TileCoords homeTile = new TileCoords(x, y);

    // Vertically adjacent (above or below)
    adjVertices.add(new VertexCoords(x, y, 1 - z));

    // Left and right adjacent vertices
    VertexCoords right;
    VertexCoords left;
    if (z == VertexCoords.T) {
      left = homeTile.getTopLeft().getBottomVertex();
      right = homeTile.getTopRight().getBottomVertex();
    } else if (z == VertexCoords.B) {
      left = homeTile.getBottomLeft().getTopVertex();
      right = homeTile.getBottomRight().getTopVertex();
    } else {
      throw new IllegalStateException("Bad vertex coords");
    }

    adjVertices.add(left);
    adjVertices.add(right);
    return adjVertices;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d, %d)", x, y, z);
  }
}

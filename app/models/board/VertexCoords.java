package models.board;

import java.util.ArrayList;
import java.util.List;


public class VertexCoords {
  public static final int T = 0;
  public static final int B = 1;

  public final int x;
  public final int y;
  public final int s;

  public VertexCoords(int x, int y, int s) {
    this.x = x;
    this.y = y;
    this.s = s;
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
    if (s == VertexCoords.T) {
      adjTileCoords = homeTile.getTopRight();
    } else if (s == VertexCoords.B) {
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
    adjVertices.add(new VertexCoords(x, y, 1 - s));

    // Left and right adjacent vertices
    VertexCoords right;
    VertexCoords left;
    if (s == VertexCoords.T) {
      left = homeTile.getTopLeft().getBottomVertex();
      right = homeTile.getTopRight().getBottomVertex();
    } else if (s == VertexCoords.B) {
      left = homeTile.getBottomLeft().getTopVertex();
      right = homeTile.getBottomRight().getTopVertex();
    } else {
      throw new IllegalStateException("Bad vertex coords");
    }

    adjVertices.add(left);
    adjVertices.add(right);
    return adjVertices;
  }
}

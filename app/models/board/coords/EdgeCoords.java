package models.board.coords;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;


public class EdgeCoords extends Coords {
  public static final int N = 0;
  public static final int E = 1;
  public static final int S = 2;

  public EdgeCoords(int x, int y, int z) {
    super(x, y, z);
  }

  public List<EdgeCoords> getAdjacent() {

    // Home tile, and some surrounding tiles
    TileCoords homeTile = new TileCoords(x, y);
    TileCoords topRightTile = homeTile.getTopRight();
    TileCoords topLeftTile = homeTile.getTopLeft();
    TileCoords bottomRightTile = homeTile.getBottomRight();
    TileCoords bottomLeftTile = homeTile.getBottomLeft();

    EdgeCoords topLeft;
    EdgeCoords topRight;
    EdgeCoords bottomLeft;
    EdgeCoords bottomRight;

    switch (z) {
      case EdgeCoords.N:
        topLeft = topLeftTile.getSouthEdge();
        topRight = topLeftTile.getEastEdge();
        bottomLeft = homeTile.getEastEdge();
        bottomRight = topRightTile.getSouthEdge();
        break;
      case EdgeCoords.E:
        topLeft = homeTile.getNorthEdge();
        topRight = topRightTile.getSouthEdge();
        bottomLeft = homeTile.getSouthEdge();
        bottomRight = bottomRightTile.getNorthEdge();
        break;
      case EdgeCoords.S:
        topLeft = homeTile.getEastEdge();
        topRight = bottomRightTile.getNorthEdge();
        bottomLeft = bottomLeftTile.getNorthEdge();
        bottomRight = bottomLeftTile.getEastEdge();
        break;
      default:
        throw new IllegalStateException("Bad edge coords");
    }

    return Arrays.asList(topLeft, topRight, bottomLeft, bottomRight);
  }
}

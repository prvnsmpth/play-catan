package models.board;

public class TileCoords {
  public final int x;
  public final int y;

  public TileCoords(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public TileCoords getRight() {
    return new TileCoords(x, y+1);
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
}

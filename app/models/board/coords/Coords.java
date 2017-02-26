package models.board.coords;

public abstract class Coords {
  public final int x;
  public final int y;
  public final int z;

  protected Coords(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  protected Coords(int x, int y) {
    this.x = x;
    this.y = y;
    this.z = -1; // Don't care
  }
}

package models.board;

public class Edge {
  public static final int N = 0;
  public static final int E = 1;
  public static final int S = 2;

  public final int x;
  public final int y;
  public final int s;

  public Edge(int x, int y, int s) {
    this.x = x;
    this.y = y;
    this.s = s;
  }
}

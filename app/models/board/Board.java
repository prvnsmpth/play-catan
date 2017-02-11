package models.board;

public class Board {

  private final Tile[][] _tiles;
  private final Edge[][][] _edges;
  private final Vertex[][][] _vertices;

  public Board() {
    _tiles = initTiles();
    _edges = initEdges();
    _vertices = initVertices();
  }

  private Tile[][] initTiles() {
    Tile[][] tiles = new Tile[7][7];
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        boolean valid = true;
        if (i == 0 || i == 6) {
          valid = false;
        } else {
          int distToMidRow = Math.abs(i - 3);
          int min = (distToMidRow + 1) / 2 + 1;
          int max = min + (5 - distToMidRow) - 1;
          if (j < min || j > max) {
            valid = false;
          }
        }
        if (valid) {
          tiles[i][j] = new Tile(new TileCoords(i, j));
        }
      }
    }
    return tiles;
  }

  private Edge[][][] initEdges() {
    Edge[][][] edges = new Edge[7][7][3];
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        for (int k = 0; k < 3; k++) {
          edges[i][j][k] = new Edge(i, j, k);
        }
      }
    }
    return edges;
  }

  private Vertex[][][] initVertices() {
    Vertex[][][] vertices = new Vertex[7][7][2];
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        for (int k = 0; k < 2; k++) {
          vertices[i][j][k] = new Vertex(new VertexCoords(i, j, k));
        }
      }
    }
    return vertices;
  }

  public void print() {
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        System.out.print((_tiles[i][j] != null ? 1 : 0) + " ");
      }
      System.out.println();
    }
  }

  public Tile[][] getTiles() {
    return _tiles;
  }

  public static void main(String[] args) {
    Board b = new Board();
    b.print();

    Vertex v = new Vertex(new VertexCoords(3, 0, VertexCoords.T));

    System.out.println(BoardUtils.getSurroundingTiles(b, v));
  }

}

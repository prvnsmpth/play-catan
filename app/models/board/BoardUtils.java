package models.board;

import java.util.List;
import java.util.stream.Collectors;


public class BoardUtils {

  private static boolean between(int val, int a, int b) {
    return val >= a && val <= b;
  }

  private static boolean checkTileCoords(TileCoords p) {
    return between(p.x, 0, 6) && between(p.y, 0, 6);
  }

  private static boolean checkVertexCoords(VertexCoords p) {
    return between(p.x, 0, 6) && between(p.y, 0, 6) && between(p.s, 0, 1);
  }

  public static List<Tile> getSurroundingTiles(Board board, Vertex vertex) {
    Tile[][] boardTiles = board.getTiles();
    VertexCoords coords = vertex.getCoords();
    return coords.getSurroundingTiles().stream()
        .filter(BoardUtils::checkTileCoords)
        .map(tileCoords -> boardTiles[tileCoords.x][tileCoords.y])
        .filter(t -> t != null)
        .collect(Collectors.toList());
  }
}

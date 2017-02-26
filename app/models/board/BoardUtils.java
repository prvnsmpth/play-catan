package models.board;

import java.util.List;
import java.util.stream.Collectors;
import models.board.coords.EdgeCoords;
import models.board.coords.TileCoords;
import models.board.coords.VertexCoords;


public class BoardUtils {

  private static boolean between(int val, int a, int b) {
    return val >= a && val <= b;
  }

  public static boolean checkTileCoords(TileCoords p) {
    return between(p.x, 0, 6) && between(p.y, 0, 6);
  }

  public static boolean checkVertexCoords(VertexCoords p) {
    return between(p.x, 0, 6) && between(p.y, 0, 6) && between(p.z, 0, 1);
  }

  public static boolean checkEdgeCoords(EdgeCoords p) {
    return between(p.x, 0, 6) && between(p.y, 0, 6) && between(p.z, 0, 2);
  }

  public static List<Tile> getSurroundingTiles(Board board, Vertex vertex) {
    VertexCoords coords = vertex.getCoords();
    return coords.getSurroundingTiles().stream()
        .filter(BoardUtils::checkTileCoords)
        .map(board::getTileAt)
        .filter(t -> t != null)
        .collect(Collectors.toList());
  }

  public static List<Vertex> getAdjacentVertices(Board board, Vertex vertex) {
    VertexCoords coords = vertex.getCoords();
    return coords.getAdjacent().stream()
        .filter(BoardUtils::checkVertexCoords)
        .map(board::getVertexAt)
        .filter(v -> v != null)
        .collect(Collectors.toList());
  }

  public static List<Edge> getAdjacentEdges(Board board, Edge edge) {
    EdgeCoords coords = edge.getCoords();
    return coords.getAdjacent().stream()
        .filter(BoardUtils::checkEdgeCoords)
        .map(board::getEdgeAt)
        .filter(e -> e != null)
        .collect(Collectors.toList());
  }

  public static List<Vertex> getVertices(Board board, Tile tile) {
    TileCoords coords = tile.getCoords();
    return coords.getVertices().stream()
        .filter(BoardUtils::checkVertexCoords)
        .map(board::getVertexAt)
        .filter(e -> e != null)
        .collect(Collectors.toList());
  }
}

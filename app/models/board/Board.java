package models.board;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import models.Building;
import models.BuildingType;
import models.Stockpile;
import models.board.coords.Coords;
import models.board.coords.EdgeCoords;
import models.board.coords.TileCoords;
import models.board.coords.VertexCoords;
import models.constants.Color;
import models.constants.ResourceType;
import org.apache.commons.lang3.tuple.Pair;


public class Board {

  private final Tile[][] tiles;

  private final Edge[][][] edges;

  private final Vertex[][][] vertices;

  private final Map<Integer, List<Tile>> rollHexTable;

  public Board() {
    tiles = initTiles();
    edges = initEdges();
    vertices = initVertices();
    rollHexTable = new HashMap<>();
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
          edges[i][j][k] = new Edge(new EdgeCoords(i, j, k));
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

  public void setResource(TileCoords tileCoords, ResourceType resourceType) throws InvalidCoordsException {
    Tile tile = getTileAt(tileCoords);
    if (tile == null) {
      throw new InvalidCoordsException(tileCoords);
    }
    tile.setResourceType(resourceType);
  }

  public void placeNumber(TileCoords tileCoords, int number) throws InvalidCoordsException {
    Tile tile = getTileAt(tileCoords);
    if (tile == null) {
      throw new InvalidCoordsException(tileCoords);
    }
    tile.setNumber(number);
    if (!rollHexTable.containsKey(number)) {
      rollHexTable.put(number, Lists.newArrayList(tile));
    } else {
      rollHexTable.get(number).add(tile);
    }
  }

  public Map<Color, Stockpile> produce(int number) {
    return rollHexTable.get(number).stream()
        .flatMap(tile -> getBuildings(tile).stream().map(b -> Pair.of(tile, b)))
        .map(p -> getProduce(p.getLeft(), p.getRight()))
        .filter(p -> p != null)
        .collect(Collectors.groupingBy(p -> p.getLeft().getColor(),
            Collectors.reducing(new Stockpile(), Pair::getRight, (a, b) -> a.add(b))));
  }

  private List<Building> getBuildings(Tile tile) {
    return BoardUtils.getVertices(this, tile).stream()
        .map(Vertex::getBuilding)
        .filter(b -> b != null)
        .collect(Collectors.toList());
  }

  private Pair<Building, Stockpile> getProduce(Tile tile, Building building) {
    if (building.getType() == BuildingType.SETTLEMENT) {
      return Pair.of(building, Stockpile.of(tile.getResourceType()));
    } else if (building.getType() == BuildingType.CITY) {
      return Pair.of(building, Stockpile.of(tile.getResourceType(), 2));
    }
    return null;
  }

  public void print() {
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        System.out.print((tiles[i][j] != null ? 1 : 0) + " ");
      }
      System.out.println();
    }
  }

  public Tile getTileAt(TileCoords coords) {
    if (!BoardUtils.checkTileCoords(coords)) {
      throw new IllegalArgumentException("Bad tile coords");
    }
    return tiles[coords.x][coords.y];
  }

  public Vertex getVertexAt(VertexCoords coords) {
    if (!BoardUtils.checkVertexCoords(coords)) {
      throw new IllegalArgumentException("Bad vertex coords: " + coords);
    }
    return vertices[coords.x][coords.y][coords.z];
  }

  public Edge getEdgeAt(EdgeCoords coords) {
    if (!BoardUtils.checkEdgeCoords(coords)) {
      throw new IllegalArgumentException("Bad edge coords: " + coords);
    }
    return edges[coords.x][coords.y][coords.z];
  }

  public void build(Building building, Coords coords) {
    switch (building.getType()) {
      case ROAD:
        placeRoad(building, (EdgeCoords) coords);
        break;
      case SETTLEMENT:
        placeSettlement(building, (VertexCoords) coords);
        break;
      case CITY:
        upgradeSettlement(building, (VertexCoords) coords);
        break;
      default:
        throw new IllegalArgumentException("Invalid building type");
    }
  }

  private void upgradeSettlement(Building city, VertexCoords coords) {
    Vertex vertex = getVertexAt(coords);
    Building settlement = vertex.getBuilding();
    if (settlement == null) {
      throw new IllegalStateException("No settlement to upgrade.");
    }
    if (settlement.getColor().equals(city.getColor())) {
      throw new IllegalStateException("That's not your settlement.");
    }
    vertex.setBuilding(city);
  }

  private void placeSettlement(Building settlement, VertexCoords coords) {
    Vertex vertex = getVertexAt(coords);
    if (vertex.getBuilding() != null) {
      throw new IllegalStateException("Vertex already has a building.");
    }
    long numEmptyAdjSpots = coords.getAdjacent().stream()
        .map(this::getVertexAt)
        .map(Vertex::getBuilding)
        .filter(Objects::isNull)
        .count();
    if (numEmptyAdjSpots < 3) {
      throw new IllegalStateException("Can't build here!");
    }
    vertex.setBuilding(settlement);
  }

  private void placeRoad(Building building, EdgeCoords coords) {
    Edge edge = getEdgeAt(coords);
    if (edge.getRoad() != null) {
      throw new IllegalStateException("Edge already has a road.");
    }
    long numEnemyRoads = BoardUtils.getAdjacentEdges(this, edge).stream()
        .filter(e -> e.getRoad() != null && e.getRoad().getColor() != building.getColor())
        .count();
    if (numEnemyRoads > 0) {
      throw new IllegalStateException("Can't build here");
    }
    edge.placeRoad(building);
  }

  public static void main(String[] args) {
    Board b = new Board();
    b.print();

    Vertex v = new Vertex(new VertexCoords(0, 2, VertexCoords.B));

    System.out.println(BoardUtils.getSurroundingTiles(b, v));
    System.out.println(BoardUtils.getAdjacentVertices(b, v));
  }

}

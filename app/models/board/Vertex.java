package models.board;

public class Vertex {
  private VertexCoords _coords;

  public Vertex(VertexCoords coords) {
    _coords = coords;
  }

  public VertexCoords getCoords() {
    return _coords;
  }
}

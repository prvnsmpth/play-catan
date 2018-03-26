class Vertex {

  /**
   * Make a vertex object, providing the logical board coordinates (i, j, k) and physical canvas coordinates (x, y),
   * in an object.
   */
  constructor(props) {
    this.i = props.i;
    this.j = props.j;
    this.k = props.k;
    this.x = props.x;
    this.y = props.y;
  }

  /**
   * Returns the three vertices adjacent to this vertex.
   */
  get adjVertices() {
    var vertices = [{i: this.i, j: this.j, k: 1 - this.k}];
    vertices.push({
      i: this.i + (2*this.k - 1),
      j: this.j - (1 - this.i % 2),
      k: 1 - this.k
    });
    vertices.push({
      i: this.i + (2*this.k - 1),
      j: this.j + this.i % 2,
      k: 1 - this.k
    });
    return vertices.map(v => new Vertex(v));
  }

  get surroundingHexCoords() {
    var hexes = [{i: this.i, j: this.j}, {i: this.i, j: this.j+1}];
    hexes.push({
      i: this.i + (2*this.k - 1),
      j: this.j + this.i % 2
    });
    return hexes;
  }

  isWithinBounds(x, y, threshold) {
    return Math.abs(this.x - x) + Math.abs(this.y - y) <= threshold;
  }

  /**
   * Distance to a canvas point (x, y).
   *
   * @param x
   * @param y
   */
  dist(x, y) {
    return Math.abs(this.x - x) + Math.abs(this.y - y);
  }

}
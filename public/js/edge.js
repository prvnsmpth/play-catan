class Edge {

  /**
   * Make an edge object, providing the logical board coordinates (i, j, k) and physical canvas coordinates (v1, v2),
   * in an object.
   *
   * v1 and v2 are the two endpoints of the edge.
   */
  constructor(props) {
    this.i = props.i;
    this.j = props.j;
    this.k = props.k;
    this.v1 = props.v1;
    this.v2 = props.v2;
  }

  /**
   * Returns the four edges connected to this edge.
   */
  get connectedEdges() {
    var edges = [];
  }

  get midX() {
    return (this.v1.x + this.v2.x) / 2;
  }

  get midY() {
    return (this.v1.y + this.v2.y) / 2;
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
    return this.v1.dist(x, y) + this.v2.dist(x, y);
  }

}
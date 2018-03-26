var Hex = (function () {

  function Hex(props) {
    this.Shape_constructor();

    this.i = props.i;
    this.j = props.j;
    this.x = props.x;
    this.y = props.y;
    this.alt = Math.sqrt(3)/2 * props.radius;
    this.radius = props.radius;
    this.ghost = props.ghost || false; // Ghost hexes are off-board hexes that only help with vertex coordinate calculations

    this.graphics.s('black').f(props.color || 'white').ss(4).dp(0, 0, props.radius, 6, 0, 90);
  }

  var h = createjs.extend(Hex, createjs.Shape);

  h.getVertices = function () {
    if (!this.vertices) {
      this.vertices = [
        this.leftHex().topVertex(),
        this.topLeftHex().bottomVertex(),
        this.topVertex(), this.bottomVertex(),
        this.bottomLeftHex().topVertex(),
        this.leftHex().bottomVertex()
      ];
    }
    return this.vertices;
  };

  h.getEdges = function () {
    if (!this.edges) {
      this.edges = [
          this.topEdge(), this.rightEdge(), this.bottomEdge(),
          this.bottomLeftHex().topEdge(), this.leftHex().rightEdge(), this.topLeftHex().bottomEdge()
      ];
    }
    return this.edges;
  };

  h.getNearestVertex = function(mouseX, mouseY) {
    return this.getVertices().reduce((v1, v2) =>
      v1.dist(mouseX, mouseY) < v2.dist(mouseX, mouseY) ? v1 : v2);
  };

  h.getNearestEdge = function(mouseX, mouseY) {
    return this.getEdges().reduce((e1, e2) =>
      e1.dist(mouseX, mouseY) < e2.dist(mouseX, mouseY) ? e1 : e2);
  };

  h.topRightHex = function () {
    return new Hex({
      i: this.i-1, j: this.j + this.i%2,
      x: this.x + this.alt, y: this.y - 1.5 * this.radius,
      radius: this.radius
    });
  };
  h.rightHex = function () {
    return new Hex({
      i: this.i, j: this.j+1,
      x: this.x + 2 * this.alt, y: this.y,
      radius: this.radius
    });
  };
  h.bottomRightHex = function () {
    return new Hex({
      i: this.i+1, j: this.j + this.i%2,
      x: this.x + this.alt, y: this.y + 1.5 * this.radius,
      radius: this.radius
    });
  };
  h.bottomLeftHex = function () {
    return new Hex({
      i: this.i+1, j: this.j-1 + this.i%2,
      x: this.x - this.alt, y: this.y + 1.5 * this.radius,
      radius: this.radius
    });
  };
  h.leftHex = function () {
    return new Hex({
      i: this.i, j: this.j-1,
      x: this.x - 2 * this.alt, y: this.y,
      radius: this.radius
    });
  };
  h.topLeftHex = function () {
    return new Hex({
      i: this.i-1, j: this.j-1 + this.i%2,
      x: this.x - this.alt, y: this.y - 1.5 * this.radius,
      radius: this.radius
    });
  };

  h.topEdge = function () {
    return new Edge({
      i: this.i, j: this.j, k: 0,
      v1: this.topLeftHex().bottomVertex(),
      v2: this.topVertex()
    });
  };
  h.rightEdge = function () {
    return new Edge({
      i: this.i, j: this.j, k: 1,
      v1: this.topVertex(), v2: this.bottomVertex()
    });
  };
  h.bottomEdge = function () {
    return new Edge({
      i: this.i, j: this.j, k: 2,
      v1: this.bottomVertex(),
      v2: this.bottomLeftHex().topVertex()
    });
  };

  h.topVertex = function () {
    return new Vertex({
      i: this.i, j: this.j, k: 0,
      x: this.x + this.alt, y: this.y - this.radius / 2
    });
  };
  h.bottomVertex = function () {
    return new Vertex({
      i: this.i, j: this.j, k: 1,
      x: this.x + this.alt, y: this.y + this.radius / 2
    });
  };

  return createjs.promote(Hex, "Shape");
}());


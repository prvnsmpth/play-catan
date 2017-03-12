var Hex = (function () {

  function getHexCenterCoordinates(boardX, boardY, hexRadius, i, j) {
    var alt = Math.sqrt(3)/2 * hexRadius;
    return {
      x: boardX + (j - 3) * 2 * alt - (i % 2 == 0 ? alt : 0),
      y: boardY + (i - 3) * 3 * hexRadius/2
    };
  }

  function getVertices(hex) {
    var alt = hex.alt;
    var radius = hex.radius;
    var centerX = hex.x;
    var centerY = hex.y;
    return [{
      i: hex.row, j: hex.col - 1, k: 0,
      x: centerX - alt, y: centerY - radius/2
    }, {
      i: hex.row - 1, j: (hex.row % 2 == 0) ? hex.col-1 : hex.col, k: 1,
      x: centerX, y: centerY - radius
    }, {
      i: hex.row, j: hex.col, k: 0,
      x: centerX + alt, y: centerY - radius/2
    }, {
      i: hex.row, j: hex.col, k: 1,
      x: centerX + alt, y: centerY + radius/2
    }, {
      i: hex.row + 1, j: (hex.row % 2 == 0) ? hex.col-1 : hex.col, k: 0,
      x: centerX, y: centerY + radius
    }, {
      i: hex.row, j: hex.col -1, k: 1,
      x: centerX - alt, y: centerY + radius/2
    }].map(obj => new Vertex(obj));
  }

  function Hex(row, col, props) {
    this.Shape_constructor();
    var center = getHexCenterCoordinates(props.boardX, props.boardY, props.radius, row, col);
    this.row = row;
    this.col = col;
    this.x = center.x;
    this.y = center.y;
    this.alt = Math.sqrt(3)/2 * props.radius;
    this.radius = props.radius;
    this.vertices = getVertices(this);
    this.ghost = props.ghost || false; // Ghost hexes are off-board hexes that only help with vertex coordinate calculations

    this.graphics.s('black').f(props.color || 'white').ss(4).dp(0, 0, props.radius, 6, 0, 90);
  }

  var h = createjs.extend(Hex, createjs.Shape);

  h.getNearestVertex = function(mouseX, mouseY) {
    var dist = (x1, y1, x2, y2) => Math.abs(x1 - x2) + Math.abs(y1 - y2);
    return this.vertices.reduce((v1, v2) =>
      dist(v1.x, v1.y, mouseX, mouseY) < dist(v2.x, v2.y, mouseX, mouseY) ? v1 : v2);
  };

  h.getNearestEdge = function(mouseX, mouseY) {
    var vertices = this.vertices.map(v => ({
      x: v.x,
      y: v.y,
      dist: Math.abs(v.x - mouseX) + Math.abs(v.y - mouseY)
    })).sort((a, b) => a.dist - b.dist);
    return {
      v1: vertices[0],
      v2: vertices[1]
    };
  };

  return createjs.promote(Hex, "Shape");
}());


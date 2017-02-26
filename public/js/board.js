var Board = (function () {

  var settings = {
    hexRadius: 75,
    tokenRadius: 20,

    settlementSize: 25,
    roadLength: 45,
    roadThickness: 12,

    vertexSelectionDistThreshold: 40,

    hexColors: {
      GRAIN: 'yellow',
      ORE: 'grey',
      BRICK: 'red',
      LUMBER: '#05c40f',
      WOOL: '#9bffa0'
    }
  };

  var currentEl;

  var hexes = makeArray([7, 7]);

  function makeArray(dimensions) {
    var arr = new Array(dimensions[0]);
    if (dimensions.length == 1) {
      return arr;
    }
    for (var i = 0; i < dimensions[0]; i++) {
      arr[i] = makeArray(dimensions.slice(1));
    }
    return arr;
  }

  function makeHex(x, y, radius, color) {
    var alt = Math.sqrt(3)/2 * radius;
    var hex = new createjs.Shape();
    hex.graphics.s('black').f(color || 'white').ss(5).dp(alt, radius, radius, 6, 0, 90);
    hex.x = x - alt;
    hex.y = y - radius;
    return hex;
  }

  function makeCircle(x, y, radius) {
    var circle = new createjs.Shape();
    circle.graphics.s('black').f('#fffdb5').ss(2).dc(x, y, radius);
    return circle;
  }

  function makeText(x, y, text) {
    var textObj = new createjs.Text(text, 'bold 16px Arial', 'black');
    var bounds = textObj.getBounds();
    textObj.x = x - bounds.width/2; textObj.y = y - bounds.height/2;
    return textObj;
  }

  function makeSettlement(x, y, color) {
    var side = settings.settlementSize;
    var settlement = new createjs.Shape();
    settlement.graphics.s('black').f(color || 'white').dr(0, 0, side, side);
    settlement.x = x - side/2;
    settlement.y = y - side/2;
    return settlement;
  }

  function makeRoad(v1, v2, color) {
    var len = settings.roadLength;
    var thickness = settings.roadThickness;
    var road = new createjs.Shape();
    road.graphics.s('black').f(color).dr(0, 0, len, thickness);
    road.x = (v1.x + v2.x)/2;
    road.y = (v1.y + v2.y)/2;
    road.regX = len/2;
    road.regY = thickness/2;
    road.rotation = 90 + ((v1.x > v2.x) - (v1.x < v2.x)) * 60;
    return road;
  }

  function getVerticesOfHex(hex) {
    var radius = settings.hexRadius;
    var alt = Math.sqrt(3)/2 * radius;
    var centerX = hex.x + alt;
    var centerY = hex.y + radius;
    var vertices = [];
    vertices.push({x: centerX - alt, y: centerY - radius/2});
    vertices.push({x: centerX, y: centerY - radius});
    vertices.push({x: centerX + alt, y: centerY - radius/2});
    vertices.push({x: centerX - alt, y: centerY + radius/2});
    vertices.push({x: centerX, y: centerY + radius});
    vertices.push({x: centerX + alt, y: centerY + radius/2});
    return vertices;
  }

  function getVertexNearMousePtr(mouseX, mouseY) {
    var dist = ((x1, y1, x2, y2) => Math.abs(x2-x1) + Math.abs(y2-y1));
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        if (!hexes[i][j]) continue;
        var pt = hexes[i][j].globalToLocal(mouseX, mouseY);
        if (hexes[i][j].hitTest(pt.x, pt.y)) {
          var closest = getVerticesOfHex(hexes[i][j]).map(vertex => ({
            x: vertex.x, y: vertex.y, dist: dist(vertex.x, vertex.y, mouseX, mouseY)
          })).reduce((a, b) => a.dist < b.dist ? a : b);
          return closest.dist < settings.vertexSelectionDistThreshold ? closest : null;
        }
      }
    }
  }

  /**
   * Get canvas coordinates of a hex's center, given its coordinates in the hex grid, (i, j).
   *
   * This is required because of the ghost tiles on the periphery of the board, which are not currently being
   * returned as tiles in the board state.
   */
  function getHexCenterCoordinates(boardX, boardY, hexRadius, i, j) {
    var alt = Math.sqrt(3)/2 * hexRadius;
    return {
      x: boardX + (j - 3) * 2 * alt - (i % 2 == 0 ? alt : 0),
      y: boardY + (i - 3) * 3 * hexRadius/2
    };
  }

  function drawBoard(stage, x, y, hexRadius, boardState) {
    var tiles = boardState['tiles'];
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        if (!tiles[i][j]) continue;

        // Place hex
        var hexCenter = getHexCenterCoordinates(x, y, hexRadius, i, j);
        var resourceType = tiles[i][j]['resourceType'];
        var hex = makeHex(hexCenter.x, hexCenter.y, hexRadius, settings.hexColors[resourceType]);
        hexes[i][j] = hex;
        stage.addChild(hex);

        // Place the number token
        if (tiles[i][j]['number'] > 0) {
          var numberToken = makeCircle(hexCenter.x, hexCenter.y, settings.tokenRadius);
          stage.addChild(numberToken);
          var number = makeText(hexCenter.x, hexCenter.y, tiles[i][j]['number']);
          stage.addChild(number);
        }

        stage.update();
      }
    }

    // Place settlements
    var vertices = boardState['vertices'];
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        for (var k = 0; k < 1; k++) {
          var vertexObj = vertices[i][j][k]['building'];
          if (!vertexObj) continue;
          var hexCenter = getHexCenterCoordinates(x, y, hexRadius, i, j);
          var alt = Math.sqrt(3)/2 * hexRadius;
          var vertex = {
            x: hexCenter.x + alt,
            y: hexCenter.y + (2*k - 1) * hexRadius/2
          };
          var building = makeSettlement(vertex.x, vertex.y, vertexObj['color']);
          stage.addChild(building);
        }
      }
    }
    stage.update();

    // Place roads
    var edges = boardState['edges'];
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        for (var k = 0; k < 3; k++) {
          if (edges[i][j][k]['road']) {
            var roadObj = edges[i][j][k]['road'];
            var hexCenter = getHexCenterCoordinates(x, y, hexRadius, i, j);
            var alt = Math.sqrt(3)/2 * hexRadius;
            var v1 = {
              x: k > 0 ? hexCenter.x + alt : hexCenter.x,
              y: k > 0 ? hexCenter.y + (2*k - 3) * hexRadius/2 : hexCenter.y - hexRadius
            };
            var v2 = {
              x: k < 2 ? hexCenter.x + alt : hexCenter.x,
              y: k < 2 ? hexCenter.y + (2*k - 1) * hexRadius/2 : hexCenter.y + hexRadius
            };
            var roadShape = makeRoad(v1, v2, roadObj['color']);
            stage.addChild(roadShape);
          }
        }
      }
    }
    stage.update();
  }

  return {
    init: function(boardState) {
      var canvas = $("#game-canvas")[0];
      this.draw(canvas, boardState);
    },

    draw: function(canvas, boardState) {
      var stage = new createjs.Stage(canvas);
      stage.enableMouseOver(10);

      stage.on('stagemousemove', evt => {
        if (currentEl !== undefined) {
          currentEl.x = evt.stageX - settings.settlementSize/2;
          currentEl.y = evt.stageY - settings.settlementSize/2;
          stage.update();
        }
      });

      drawBoard(stage, canvas.width/2, canvas.height/2, 75, boardState);

      var vertexSelector = new createjs.Shape();
      vertexSelector.visible = false;
      vertexSelector.alpha = 0.5;
      var size = settings.settlementSize;
      vertexSelector.graphics.f('black').dr(0, 0, size, size);
      stage.addChild(vertexSelector);
      stage.on('stagemousemove', evt => {
        var pt = getVertexNearMousePtr(stage.mouseX, stage.mouseY);
        if (!pt) {
          vertexSelector.visible = false;
        } else {
          vertexSelector.x = pt.x - size/2;
          vertexSelector.y = pt.y - size/2;
          vertexSelector.visible = true;
        }
        stage.update();
      });

      stage.update();
    }
  }
}());
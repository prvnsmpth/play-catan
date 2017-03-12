var Board = (function () {

  return Backbone.Model.extend({
    initialize: function (options) {
      this.parent = options.parent;
    },

    buildingAt: function(vertex) {
      if (!this.isValidVertex(vertex)) {
        return null;
      }
      return this.get('vertices')[vertex.i][vertex.j][vertex.k].building;
    },

    /**
     * Checks if the given vertex coordinates correspond to a vertex that is part of the board.
     *
     * @param vertex An object containing vertex coordinates.
     */
    isValidVertex: function(vertex) {
      var tiles = this.get('tiles');
      var validSurroundingTiles = vertex.surroundingHexCoords
          .filter(c => this.isValidHexCoord(c));
      return validSurroundingTiles.length > 0;
    },

    isValidHexCoord: function(hexCoords) {
      try {
        return this.get('tiles')[hexCoords.i][hexCoords.j] !== null;
      } catch (e) {
        return false;
      }
    },

    canPlace: function(vertex, buildingType) {
      return this.parent.canPlace(vertex, buildingType);
    }
  });

})();

var BoardView = (function () {
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

  function makeSettlement(x, y, color, size) {
    var settlement = new createjs.Bitmap('/assets/images/settlement-red.png');
    settlement.x = x - size/2;
    settlement.y = y - size/2;
    return settlement;
  }

  function makeRoad(v1, v2, color, len, thickness) {
    var road = new createjs.Shape();
    road.graphics.s('black').f(color).dr(0, 0, len, thickness);
    road.x = (v1.x + v2.x)/2;
    road.y = (v1.y + v2.y)/2;
    road.regX = len/2;
    road.regY = thickness/2;
    road.rotation = 90 + ((v1.x > v2.x) - (v1.x < v2.x)) * 60;
    return road;
  }

  function getHexUnderCursor(mouseX, mouseY) {
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        if (!hexes[i][j].ghost) {
          var pt = hexes[i][j].globalToLocal(mouseX, mouseY);
          if (hexes[i][j].hitTest(pt.x, pt.y)) {
            return hexes[i][j];
          }
        }
      }
    }
  }

  function getNearestVertex(mouseX, mouseY) {
    var hex = getHexUnderCursor(mouseX, mouseY);
    if (hex) {
      return hex.getNearestVertex(mouseX, mouseY);
    }
  }

  function getNearestEdge(mouseX, mouseY) {
    var hex = getHexUnderCursor(mouseX, mouseY);
    if (hex) {
      return hex.getNearestEdge(mouseX, mouseY);
    }
  }

  function placeHexes(stage, boardX, boardY, boardState) {
    var settings = boardState.settings;
    var tiles = boardState.tiles;
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        delete hexes[i][j];
        if (tiles[i][j]) {
          var resourceType = tiles[i][j].resourceType;
          var color = settings.hexColors[resourceType];
          var hex = new Hex(i, j, {
            boardX: boardX,
            boardY: boardY,
            radius: settings.hexRadius,
            color: color
          });
          hexes[i][j] = hex;
          stage.addChild(hex);
          if (tiles[i][j]['number'] > 0) {
            var numberToken = makeCircle(hex.x, hex.y, settings.tokenRadius);
            var number = makeText(hex.x, hex.y, tiles[i][j]['number']);
            stage.addChild(numberToken);
            stage.addChild(number);
          }
        } else {
          // Make a dummy hex to help with calculations of vertices at the left edge of the board
          hexes[i][j] = new Hex(i, j, {
            boardX: boardX,
            boardY: boardY,
            radius: settings.hexRadius,
            color: 'white',
            ghost: true
          });
        }
      }
    }
  }

  function placeSettlements(stage, boardState) {
    var settings = boardState.settings;
    var vertices = boardState.vertices;
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        for (var k = 0; k < 2; k++) {
          var vertexObj = vertices[i][j][k]['building'];
          if (!vertexObj) continue;
          var hex = hexes[i][j];
          var vertex = {
            x: hex.x + hex.alt,
            y: hex.y + (2*k - 1) * hex.radius/2
          };
          var building = makeSettlement(vertex.x, vertex.y, vertexObj['color'], settings.settlementSize);
          stage.addChild(building);
        }
      }
    }
  }

  function placeRoads(stage, boardState) {
    var settings = boardState.settings;
    var edges = boardState.edges;
    var hexRadius = settings.hexRadius;
    for (var i = 0; i < 7; i++) {
      for (var j = 0; j < 7; j++) {
        for (var k = 0; k < 3; k++) {
          if (edges[i][j][k]['road']) {
            var roadObj = edges[i][j][k]['road'];
            var hex = hexes[i][j];
            var alt = hex.alt;
            var v1 = {
              x: k > 0 ? hex.x + alt : hex.x,
              y: k > 0 ? hex.y + (2*k - 3) * hexRadius/2 : hex.y - hexRadius
            };
            var v2 = {
              x: k < 2 ? hex.x + alt : hex.x,
              y: k < 2 ? hex.y + (2*k - 1) * hexRadius/2 : hexCenter.y + hexRadius
            };
            var roadShape = makeRoad(v1, v2, roadObj['color'], settings.roadLength, settings.roadThickness);
            stage.addChild(roadShape);
          }
        }
      }
    }
  }

  function makeVertexSelector(settings) {
    var size = settings.settlementSize;
    var vertexSelector = new createjs.Bitmap('/assets/images/settlement-red.png');
    vertexSelector.visible = false;
    vertexSelector.alpha = 0.5;
    return vertexSelector;
  }

  function addEdgeSelector(stage, settings) {
    var length = settings.roadLength;
    var thickness = settings.roadThickness;

    var edgeSelector = new createjs.Shape();
    edgeSelector.visible = false;
    edgeSelector.alpha = 0.5;

    edgeSelector.graphics.f('black').dr(0, 0, length, thickness);
    stage.addChild(edgeSelector);
    stage.on('stagemousemove', () => {

    });
  }

  function drawBoard(stage, x, y, boardState) {
    placeHexes(stage, x, y, boardState);
    placeSettlements(stage, boardState);
    placeRoads(stage, boardState);
    stage.update();
  }

  return Backbone.View.extend({
    initialize: function(options) {
      this.listenTo(this.model, 'change', this.render);
      this.parent = options.parent;
      this.canvas = this.el;
      this.stage = new createjs.Stage(this.canvas);

      var settings = this.model.get('settings');
      var moveListener = this.stage.on('stagemousemove', (evt) => {
        var vertex = getNearestVertex(evt.stageX, evt.stageY);
        if (!vertex) return;
        if (vertex.isWithinBounds(evt.stageX, evt.stageY, settings.distThreshold) &&
            this.model.canPlace(vertex, 'SETTLEMENT')) {
          this.vertexSelector.x = vertex.x - settings.settlementSize/2;
          this.vertexSelector.y = vertex.y - settings.settlementSize/2;
          this.vertexSelector.vertex = vertex;
          this.vertexSelector.visible = true;
        } else {
          this.vertexSelector.visible = false;
        }
        this.stage.update();
      });

      var clickListener = this.stage.on('click', () => {
        if (this.vertexSelector.visible) {
          var v = this.vertexSelector.vertex;
          var vertices = this.model.get('vertices');
          vertices[v.i][v.j][v.k].building = {
            type: 'SETTLEMENT',
            color: 'red'
          };
          console.log(this.vertexSelector.vertex);
          this.model.trigger('change');
        }
      });
    },

    render: function () {
      console.log('Rendering board...');
      var board = this.model;
      var settings = board.get('settings');
      this.stage.removeAllChildren();
      this.stage.clear();
      this.stage.enableMouseOver(10);
      drawBoard(this.stage, this.canvas.width/2, this.canvas.height/2, board.toJSON());
      this.vertexSelector = makeVertexSelector(settings);
      this.stage.addChild(this.vertexSelector);
      this.stage.update();
    }
  });
})();

